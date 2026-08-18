package com.alireza.viewservice.repository;

import com.alireza.viewservice.entity.PurchaseCard;
import com.alireza.viewservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseCardRepository extends JpaRepository<PurchaseCard, Long> {
    List<PurchaseCard> findAllByUserAndActiveTrue(User user);
    Optional<PurchaseCard> findByIdAndUser(Long id, User user);
    boolean existsByUserAndCardNumber(User user, String cardNumber);
}
