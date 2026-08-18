package com.alireza.pspservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "psp_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PspTransaction {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionId;

    private String maskedCardNumber;
    private String bin;
    private String cardHolderName;
    private BigDecimal amount;
    private String merchantId;
    private String description;
    private String status;
    private String message;
    private String referenceCode;
    private String bankCode;
    private BigDecimal remainingBalance;
    private long durationMs;
    private Instant createdAt;
    private Instant completedAt;
}
