package com.alireza.viewservice.repository;

import com.alireza.viewservice.entity.RefreshToken;
import com.alireza.viewservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
