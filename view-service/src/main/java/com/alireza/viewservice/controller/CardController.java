package com.alireza.viewservice.controller;

import com.alireza.viewservice.dto.CardRequest;
import com.alireza.viewservice.dto.CardResponse;
import com.alireza.viewservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> list(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cardService.findAll(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long id) {
        cardService.deactivate(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
