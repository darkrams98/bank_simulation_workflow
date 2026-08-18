package com.alireza.shaparakservice.repository;

import com.alireza.shaparakservice.document.ShaparakTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShaparakTransactionRepository extends MongoRepository<ShaparakTransaction, String> {
    Optional<ShaparakTransaction> findByTransactionId(String transactionId);
}
