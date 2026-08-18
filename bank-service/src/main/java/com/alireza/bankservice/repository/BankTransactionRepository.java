package com.alireza.bankservice.repository;

import com.alireza.bankservice.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    Optional<BankTransaction> findByTransactionId(String transactionId);
}
