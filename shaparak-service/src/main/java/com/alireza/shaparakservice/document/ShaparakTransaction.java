package com.alireza.shaparakservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "shaparak_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShaparakTransaction {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionId;

    private String correlationId;
    private String maskedCardNumber;
    private String bin;
    private String cardHolderName;
    private BigDecimal amount;
    private String merchantId;
    private String pspCode;
    private String routingKey;
    private String bankCode;
    private String status;
    private String message;
    private String referenceCode;
    private BigDecimal remainingBalance;
    private long durationMs;
    private Instant createdAt;
    private Instant completedAt;
}
