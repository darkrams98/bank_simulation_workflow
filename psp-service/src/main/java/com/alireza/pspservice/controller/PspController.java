package com.alireza.pspservice.controller;

import com.alireza.pspservice.document.PspTransaction;
import com.alireza.pspservice.dto.PspPaymentRequest;
import com.alireza.pspservice.dto.PspPaymentResponse;
import com.alireza.pspservice.service.PspService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psp")
@RequiredArgsConstructor
public class PspController {

    private final PspService pspService;

    @PostMapping("/payments")
    public ResponseEntity<PspPaymentResponse> pay(@Valid @RequestBody PspPaymentRequest request) {
        return ResponseEntity.ok(pspService.process(request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<PspTransaction>> transactions() {
        return ResponseEntity.ok(pspService.history());
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<PspTransaction> transaction(@PathVariable String transactionId) {
        PspTransaction transaction = pspService.findByTransactionId(transactionId);
        return transaction == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(transaction);
    }
}
