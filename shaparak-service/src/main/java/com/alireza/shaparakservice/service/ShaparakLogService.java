package com.alireza.shaparakservice.service;

import com.alireza.shaparakservice.document.ShaparakMessageLog;
import com.alireza.shaparakservice.repository.ShaparakMessageLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShaparakLogService {

    private final ShaparakMessageLogRepository messageLogRepository;
    private final ObjectMapper objectMapper;

    public void log(String transactionId, String correlationId, String direction,
                    String channel, String destination, Object payload) {
        String serialized;
        try {
            serialized = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            serialized = String.valueOf(payload);
        }

        messageLogRepository.save(ShaparakMessageLog.builder()
                .transactionId(transactionId)
                .correlationId(correlationId)
                .direction(direction)
                .channel(channel)
                .destination(destination)
                .payload(serialized)
                .createdAt(Instant.now())
                .build());
    }
}
