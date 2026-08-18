package com.alireza.viewservice.service;

import com.alireza.viewservice.dto.CardRequest;
import com.alireza.viewservice.dto.CardResponse;
import com.alireza.viewservice.entity.PurchaseCard;
import com.alireza.viewservice.entity.User;
import com.alireza.viewservice.exception.BusinessException;
import com.alireza.viewservice.exception.ResourceNotFoundException;
import com.alireza.viewservice.repository.PurchaseCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final PurchaseCardRepository purchaseCardRepository;
    private final AuthService authService;

    @Transactional
    public CardResponse create(String username, CardRequest request) {
        User user = authService.requireUser(username);

        if (purchaseCardRepository.existsByUserAndCardNumber(user, request.getCardNumber())) {
            throw new BusinessException("Card already registered for this user", HttpStatus.CONFLICT);
        }

        PurchaseCard card = PurchaseCard.builder()
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .bin(request.getCardNumber().substring(0, 6))
                .user(user)
                .active(true)
                .build();

        return toResponse(purchaseCardRepository.save(card));
    }

    @Transactional(readOnly = true)
    public List<CardResponse> findAll(String username) {
        User user = authService.requireUser(username);
        return purchaseCardRepository.findAllByUserAndActiveTrue(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deactivate(String username, Long cardId) {
        PurchaseCard card = requireCard(username, cardId);
        card.setActive(false);
        purchaseCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public PurchaseCard requireCard(String username, Long cardId) {
        User user = authService.requireUser(username);
        PurchaseCard card = purchaseCardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));
        if (!card.isActive()) {
            throw new BusinessException("Card is not active", HttpStatus.BAD_REQUEST);
        }
        return card;
    }

    public static String mask(String cardNumber) {
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }

    private CardResponse toResponse(PurchaseCard card) {
        return CardResponse.builder()
                .id(card.getId())
                .maskedCardNumber(mask(card.getCardNumber()))
                .cardHolderName(card.getCardHolderName())
                .bin(card.getBin())
                .active(card.isActive())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
