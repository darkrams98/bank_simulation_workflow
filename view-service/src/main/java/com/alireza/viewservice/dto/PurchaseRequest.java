package com.alireza.viewservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Long cardId;
}
