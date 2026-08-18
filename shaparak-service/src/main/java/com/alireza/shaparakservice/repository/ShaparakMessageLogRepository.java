package com.alireza.shaparakservice.repository;

import com.alireza.shaparakservice.document.ShaparakMessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShaparakMessageLogRepository extends MongoRepository<ShaparakMessageLog, String> {
    List<ShaparakMessageLog> findAllByTransactionIdOrderByCreatedAtAsc(String transactionId);
}
