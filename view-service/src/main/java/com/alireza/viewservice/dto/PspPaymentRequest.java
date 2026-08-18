package com.alireza.viewservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PspPaymentRequest {
    private String transactionId;
    private String cardNumber;
    private String cardHolderName;
    private BigDecimal amount;
    private String merchantId;
    private String description;
}
