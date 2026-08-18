package com.alireza.shaparakservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "shaparak_message_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShaparakMessageLog {

    @Id
    private String id;

    @Indexed
    private String transactionId;

    private String correlationId;
    private String direction;
    private String channel;
    private String destination;
    private String payload;
    private Instant createdAt;
}
