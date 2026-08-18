package com.alireza.bankservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = "card_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false, length = 120)
    private String cardHolderName;

    @Column(name = "account_number", nullable = false, length = 32)
    private String accountNumber;

    @Column(name = "bin", nullable = false, length = 6)
    private String bin;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
