package com.alireza.pspservice.repository;

import com.alireza.pspservice.document.PspMessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PspMessageLogRepository extends MongoRepository<PspMessageLog, String> {
    List<PspMessageLog> findAllByTransactionIdOrderByCreatedAtAsc(String transactionId);
}
