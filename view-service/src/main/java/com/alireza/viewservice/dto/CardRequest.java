package com.alireza.viewservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String cardNumber;

    @NotBlank
    @Size(min = 3, max = 120)
    private String cardHolderName;
}
