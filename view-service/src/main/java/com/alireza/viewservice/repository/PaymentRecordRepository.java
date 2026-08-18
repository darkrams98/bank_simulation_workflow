package com.alireza.viewservice.repository;

import com.alireza.viewservice.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findAllByUsernameOrderByIdDesc(String username);
}
