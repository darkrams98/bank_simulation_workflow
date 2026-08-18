package com.alireza.pspservice.dto;

public enum PaymentStatus {
    SUCCESS,
    FAILED,
    INSUFFICIENT_FUNDS,
    INVALID_CARD,
    CARD_INACTIVE,
    BANK_NOT_FOUND,
    TIMEOUT
}
