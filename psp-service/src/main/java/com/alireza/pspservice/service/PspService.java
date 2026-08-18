package com.alireza.pspservice.service;

import com.alireza.pspservice.client.ShaparakClient;
import com.alireza.pspservice.document.PspTransaction;
import com.alireza.pspservice.dto.*;
import com.alireza.pspservice.repository.PspTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PspService {

    private final ShaparakClient shaparakClient;
    private final PspTransactionRepository transactionRepository;
    private final TransactionLogService logService;

    @Value("${app.psp.code}")
    private String pspCode;

    public PspPaymentResponse process(PspPaymentRequest request) {
        long start = System.currentTimeMillis();
        Instant createdAt = Instant.now();

        logService.log(request.getTransactionId(), "INBOUND_REQUEST", "VIEW", sanitize(request));

        PspTransaction transaction = PspTransaction.builder()
                .transactionId(request.getTransactionId())
                .maskedCardNumber(mask(request.getCardNumber()))
                .bin(request.getCardNumber().substring(0, 6))
                .cardHolderName(request.getCardHolderName())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .description(request.getDescription())
                .status("PENDING")
                .createdAt(createdAt)
                .build();

        transactionRepository.findByTransactionId(request.getTransactionId())
                .ifPresent(existing -> transaction.setId(existing.getId()));

        transactionRepository.save(transaction);

        ShaparakRequest shaparakRequest = ShaparakRequest.builder()
                .transactionId(request.getTransactionId())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .pspCode(pspCode)
                .description(request.getDescription())
                .build();

        logService.log(request.getTransactionId(), "OUTBOUND_REQUEST", "SHAPARAK", sanitize(shaparakRequest));

        ShaparakResponse shaparakResponse;
        try {
            shaparakResponse = shaparakClient.send(shaparakRequest);
        } catch (Exception e) {
            log.error("Shaparak call failed for {}: {}", request.getTransactionId(), e.getMessage());
            shaparakResponse = ShaparakResponse.builder()
                    .transactionId(request.getTransactionId())
                    .status(PaymentStatus.FAILED.name())
                    .message("Shaparak is unreachable")
                    .build();
        }

        if (shaparakResponse == null) {
            shaparakResponse = ShaparakResponse.builder()
                    .transactionId(request.getTransactionId())
                    .status(PaymentStatus.FAILED.name())
                    .message("Empty response from Shaparak")
                    .build();
        }

        logService.log(request.getTransactionId(), "INBOUND_RESPONSE", "SHAPARAK", shaparakResponse);

        transaction.setStatus(shaparakResponse.getStatus());
        transaction.setMessage(shaparakResponse.getMessage());
        transaction.setReferenceCode(shaparakResponse.getReferenceCode());
        transaction.setBankCode(shaparakResponse.getBankCode());
        transaction.setRemainingBalance(shaparakResponse.getRemainingBalance());
        transaction.setCompletedAt(Instant.now());
        transaction.setDurationMs(System.currentTimeMillis() - start);
        transactionRepository.save(transaction);

        PspPaymentResponse response = PspPaymentResponse.builder()
                .transactionId(shaparakResponse.getTransactionId())
                .status(shaparakResponse.getStatus())
                .message(shaparakResponse.getMessage())
                .referenceCode(shaparakResponse.getReferenceCode())
                .remainingBalance(shaparakResponse.getRemainingBalance())
                .bankCode(shaparakResponse.getBankCode())
                .build();

        logService.log(request.getTransactionId(), "OUTBOUND_RESPONSE", "VIEW", response);

        return response;
    }

    public List<PspTransaction> history() {
        return transactionRepository.findAll();
    }

    public PspTransaction findByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId).orElse(null);
    }

    private PspPaymentRequest sanitize(PspPaymentRequest request) {
        return PspPaymentRequest.builder()
                .transactionId(request.getTransactionId())
                .cardNumber(mask(request.getCardNumber()))
                .cardHolderName(request.getCardHolderName())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .description(request.getDescription())
                .build();
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

    private String mask(String cardNumber) {
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }
}
