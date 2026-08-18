package com.alireza.pspservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShaparakRequest {
    private String transactionId;
    private String cardNumber;
    private String cardHolderName;
    private BigDecimal amount;
    private String merchantId;
    private String pspCode;
    private String description;
}
