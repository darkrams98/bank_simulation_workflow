package com.alireza.pspservice.repository;

import com.alireza.pspservice.document.PspTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PspTransactionRepository extends MongoRepository<PspTransaction, String> {
    Optional<PspTransaction> findByTransactionId(String transactionId);
}
