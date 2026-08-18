package com.alireza.shaparakservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShaparakRequest {

    @NotBlank
    private String transactionId;

    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String cardNumber;

    @NotBlank
    private String cardHolderName;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String merchantId;

    private String pspCode;

    private String description;
}
