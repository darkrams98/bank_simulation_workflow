package com.alireza.viewservice.service;

import com.alireza.viewservice.client.PspClient;
import com.alireza.viewservice.dto.*;
import com.alireza.viewservice.entity.PaymentRecord;
import com.alireza.viewservice.entity.Product;
import com.alireza.viewservice.entity.PurchaseCard;
import com.alireza.viewservice.repository.PaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductService productService;
    private final CardService cardService;
    private final PspClient pspClient;
    private final PaymentRecordRepository paymentRecordRepository;

    @Value("${app.merchant.id}")
    private String merchantId;

    @Transactional
    public PurchaseResponse purchase(String username, PurchaseRequest request) {
        Product product = productService.requireProduct(request.getProductId());
        PurchaseCard card = cardService.requireCard(username, request.getCardId());

        String transactionId = UUID.randomUUID().toString().replace("-", "");

        PspPaymentRequest pspRequest = PspPaymentRequest.builder()
                .transactionId(transactionId)
                .cardNumber(card.getCardNumber())
                .cardHolderName(card.getCardHolderName())
                .amount(product.getPrice())
                .merchantId(merchantId)
                .description("Purchase of " + product.getName())
                .build();

        PspPaymentResponse pspResponse;
        try {
            pspResponse = pspClient.pay(pspRequest);
        } catch (Exception e) {
            log.error("PSP call failed for transaction {}: {}", transactionId, e.getMessage());
            pspResponse = PspPaymentResponse.builder()
                    .transactionId(transactionId)
                    .status("FAILED")
                    .message("PSP is unreachable")
                    .build();
        }

        if (pspResponse == null) {
            pspResponse = PspPaymentResponse.builder()
                    .transactionId(transactionId)
                    .status("FAILED")
                    .message("Empty response from PSP")
                    .build();
        }

        paymentRecordRepository.save(PaymentRecord.builder()
                .transactionId(transactionId)
                .username(username)
                .productId(product.getId())
                .maskedCard(CardService.mask(card.getCardNumber()))
                .amount(product.getPrice())
                .status(pspResponse.getStatus())
                .message(pspResponse.getMessage())
                .referenceCode(pspResponse.getReferenceCode())
                .build());

        if ("SUCCESS".equals(pspResponse.getStatus()) && product.getStock() > 0) {
            product.setStock(product.getStock() - 1);
        }

        return PurchaseResponse.builder()
                .transactionId(transactionId)
                .status(pspResponse.getStatus())
                .message(pspResponse.getMessage())
                .referenceCode(pspResponse.getReferenceCode())
                .maskedCardNumber(CardService.mask(card.getCardNumber()))
                .amount(product.getPrice())
                .productName(product.getName())
                .timestamp(Instant.now())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentRecord> history(String username) {
        return paymentRecordRepository.findAllByUsernameOrderByIdDesc(username);
    }
}
