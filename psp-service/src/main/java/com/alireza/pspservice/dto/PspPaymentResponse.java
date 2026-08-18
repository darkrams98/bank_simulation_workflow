package com.alireza.pspservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PspPaymentResponse {
    private String transactionId;
    private String status;
    private String message;
    private String referenceCode;
    private BigDecimal remainingBalance;
    private String bankCode;
}
