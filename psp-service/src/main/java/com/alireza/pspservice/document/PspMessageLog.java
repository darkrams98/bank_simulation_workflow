package com.alireza.pspservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "psp_message_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PspMessageLog {

    @Id
    private String id;

    @Indexed
    private String transactionId;

    private String direction;
    private String counterparty;
    private String payload;
    private Instant createdAt;
}
