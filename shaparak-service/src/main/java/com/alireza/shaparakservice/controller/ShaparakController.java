package com.alireza.shaparakservice.controller;

import com.alireza.shaparakservice.document.ShaparakTransaction;
import com.alireza.shaparakservice.dto.ShaparakRequest;
import com.alireza.shaparakservice.dto.ShaparakResponse;
import com.alireza.shaparakservice.service.ShaparakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shaparak")
@RequiredArgsConstructor
public class ShaparakController {

    private final ShaparakService shaparakService;

    @PostMapping("/transactions")
    public ResponseEntity<ShaparakResponse> handle(@Valid @RequestBody ShaparakRequest request) {
        return ResponseEntity.ok(shaparakService.process(request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<ShaparakTransaction>> list() {
        return ResponseEntity.ok(shaparakService.history());
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ShaparakTransaction> get(@PathVariable String transactionId) {
        ShaparakTransaction transaction = shaparakService.findByTransactionId(transactionId);
        return transaction == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(transaction);
    }
}
