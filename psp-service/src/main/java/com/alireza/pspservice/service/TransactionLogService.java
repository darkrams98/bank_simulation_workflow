package com.alireza.pspservice.service;

import com.alireza.pspservice.document.PspMessageLog;
import com.alireza.pspservice.repository.PspMessageLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final PspMessageLogRepository messageLogRepository;
    private final ObjectMapper objectMapper;

    public void log(String transactionId, String direction, String counterparty, Object payload) {
        String serialized;
        try {
            serialized = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            serialized = String.valueOf(payload);
        }

        messageLogRepository.save(PspMessageLog.builder()
                .transactionId(transactionId)
                .direction(direction)
                .counterparty(counterparty)
                .payload(serialized)
                .createdAt(Instant.now())
                .build());
    }
}
