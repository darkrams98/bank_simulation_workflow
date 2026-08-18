package com.alireza.viewservice.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
    private Long id;
    private String maskedCardNumber;
    private String cardHolderName;
    private String bin;
    private boolean active;
    private Instant createdAt;
}
