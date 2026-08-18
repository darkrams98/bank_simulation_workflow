package com.alireza.shaparakservice.service;

import com.alireza.payment.common.dto.BankTransactionRequest;
import com.alireza.payment.common.dto.BankTransactionResponse;
import com.alireza.payment.common.dto.PaymentStatus;
import com.alireza.shaparakservice.config.ShaparakProperties;
import com.alireza.shaparakservice.document.ShaparakTransaction;
import com.alireza.shaparakservice.dto.ShaparakRequest;
import com.alireza.shaparakservice.dto.ShaparakResponse;
import com.alireza.shaparakservice.repository.ShaparakTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShaparakService {

    private final RabbitTemplate rabbitTemplate;
    private final ShaparakProperties properties;
    private final BinRoutingService binRoutingService;
    private final ShaparakTransactionRepository transactionRepository;
    private final ShaparakLogService logService;

    public ShaparakResponse process(ShaparakRequest request) {
        long start = System.currentTimeMillis();
        String correlationId = UUID.randomUUID().toString();
        String bin = binRoutingService.extractBin(request.getCardNumber());
        String routingKey = binRoutingService.resolveRoutingKey(request.getCardNumber());

        ShaparakTransaction transaction = ShaparakTransaction.builder()
                .transactionId(request.getTransactionId())
                .correlationId(correlationId)
                .maskedCardNumber(mask(request.getCardNumber()))
                .bin(bin)
                .cardHolderName(request.getCardHolderName())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .pspCode(request.getPspCode())
                .routingKey(routingKey)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        transactionRepository.findByTransactionId(request.getTransactionId())
                .ifPresent(existing -> transaction.setId(existing.getId()));

        logService.log(request.getTransactionId(), correlationId, "INBOUND_REQUEST", "HTTP", "PSP", sanitize(request));

        if (routingKey == null) {
            transaction.setStatus(PaymentStatus.BANK_NOT_FOUND.name());
            transaction.setMessage("No bank is registered for BIN " + bin);
            transaction.setCompletedAt(Instant.now());
            transaction.setDurationMs(System.currentTimeMillis() - start);
            transactionRepository.save(transaction);

            ShaparakResponse failure = ShaparakResponse.builder()
                    .transactionId(request.getTransactionId())
                    .status(PaymentStatus.BANK_NOT_FOUND.name())
                    .message("No bank is registered for BIN " + bin)
                    .build();

            logService.log(request.getTransactionId(), correlationId, "OUTBOUND_RESPONSE", "HTTP", "PSP", failure);
            return failure;
        }

        String bankCode = binRoutingService.resolveBankCode(routingKey);
        transaction.setBankCode(bankCode);
        transactionRepository.save(transaction);

        BankTransactionRequest bankRequest = new BankTransactionRequest();
        bankRequest.setTransactionId(request.getTransactionId());
        bankRequest.setCorrelationId(correlationId);
        bankRequest.setCardNumber(request.getCardNumber());
        bankRequest.setCardHolderName(request.getCardHolderName());
        bankRequest.setBin(bin);
        bankRequest.setAmount(request.getAmount());
        bankRequest.setMerchantId(request.getMerchantId());
        bankRequest.setPspCode(request.getPspCode());
        bankRequest.setDescription(request.getDescription());

        logService.log(request.getTransactionId(), correlationId, "OUTBOUND_REQUEST", "AMQP", routingKey,
                sanitize(bankRequest));

        MessagePostProcessor postProcessor = message -> {
            message.getMessageProperties().setCorrelationId(correlationId);
            message.getMessageProperties().setHeader("x-transaction-id", request.getTransactionId());
            message.getMessageProperties().setHeader("x-bin", bin);
            return message;
        };

        BankTransactionResponse bankResponse = null;
        try {
            Object reply = rabbitTemplate.convertSendAndReceive(
                    properties.getExchange(), routingKey, bankRequest, postProcessor);
            bankResponse = (BankTransactionResponse) reply;
        } catch (Exception e) {
            log.error("Bank call failed for {} on {}: {}", request.getTransactionId(), routingKey, e.getMessage());
        }

        if (bankResponse == null) {
            transaction.setStatus(PaymentStatus.TIMEOUT.name());
            transaction.setMessage("No reply received from " + bankCode + " within timeout");
            transaction.setCompletedAt(Instant.now());
            transaction.setDurationMs(System.currentTimeMillis() - start);
            transactionRepository.save(transaction);

            ShaparakResponse timeout = ShaparakResponse.builder()
                    .transactionId(request.getTransactionId())
                    .status(PaymentStatus.TIMEOUT.name())
                    .message("No reply received from " + bankCode + " within timeout")
                    .bankCode(bankCode)
                    .routingKey(routingKey)
                    .build();

            logService.log(request.getTransactionId(), correlationId, "OUTBOUND_RESPONSE", "HTTP", "PSP", timeout);
            return timeout;
        }

        logService.log(request.getTransactionId(), correlationId, "INBOUND_RESPONSE", "AMQP",
                properties.getReplyQueue(), bankResponse);

        transaction.setStatus(bankResponse.getStatus());
        transaction.setMessage(bankResponse.getMessage());
        transaction.setReferenceCode(bankResponse.getReferenceCode());
        transaction.setRemainingBalance(bankResponse.getRemainingBalance());
        transaction.setBankCode(bankResponse.getBankCode() != null ? bankResponse.getBankCode() : bankCode);
        transaction.setCompletedAt(Instant.now());
        transaction.setDurationMs(System.currentTimeMillis() - start);
        transactionRepository.save(transaction);

        ShaparakResponse response = ShaparakResponse.builder()
                .transactionId(bankResponse.getTransactionId())
                .status(bankResponse.getStatus())
                .message(bankResponse.getMessage())
                .referenceCode(bankResponse.getReferenceCode())
                .remainingBalance(bankResponse.getRemainingBalance())
                .bankCode(transaction.getBankCode())
                .routingKey(routingKey)
                .build();

        logService.log(request.getTransactionId(), correlationId, "OUTBOUND_RESPONSE", "HTTP", "PSP", response);

        return response;
    }

    public List<ShaparakTransaction> history() {
        return transactionRepository.findAll();
    }

    public ShaparakTransaction findByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId).orElse(null);
    }

    private ShaparakRequest sanitize(ShaparakRequest request) {
        return ShaparakRequest.builder()
                .transactionId(request.getTransactionId())
                .cardNumber(mask(request.getCardNumber()))
                .cardHolderName(request.getCardHolderName())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .pspCode(request.getPspCode())
                .description(request.getDescription())
                .build();
    }

    private BankTransactionRequest sanitize(BankTransactionRequest request) {
        BankTransactionRequest copy = new BankTransactionRequest();
        copy.setTransactionId(request.getTransactionId());
        copy.setCorrelationId(request.getCorrelationId());
        copy.setCardNumber(mask(request.getCardNumber()));
        copy.setCardHolderName(request.getCardHolderName());
        copy.setBin(request.getBin());
        copy.setAmount(request.getAmount());
        copy.setMerchantId(request.getMerchantId());
        copy.setPspCode(request.getPspCode());
        copy.setDescription(request.getDescription());
        return copy;
    }

    private String mask(String cardNumber) {
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }
}
