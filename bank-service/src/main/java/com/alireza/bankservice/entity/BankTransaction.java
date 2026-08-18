package com.alireza.bankservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bank_transactions", uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, length = 64)
    private String transactionId;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "masked_card", nullable = false, length = 20)
    private String maskedCard;

    @Column(name = "card_holder_name", length = 120)
    private String cardHolderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 300)
    private String message;

    @Column(name = "reference_code", length = 64)
    private String referenceCode;

    @Column(name = "merchant_id", length = 64)
    private String merchantId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
