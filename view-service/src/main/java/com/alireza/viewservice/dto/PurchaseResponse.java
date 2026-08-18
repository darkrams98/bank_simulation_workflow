package com.alireza.viewservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {
    private String transactionId;
    private String status;
    private String message;
    private String referenceCode;
    private String maskedCardNumber;
    private BigDecimal amount;
    private String productName;
    private Instant timestamp;
}
