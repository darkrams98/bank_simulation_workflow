package com.alireza.bankservice.controller;

import com.alireza.bankservice.config.BankProperties;
import com.alireza.bankservice.entity.Account;
import com.alireza.bankservice.entity.BankTransaction;
import com.alireza.bankservice.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;
    private final BankProperties bankProperties;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "code", bankProperties.getCode(),
                "name", bankProperties.getName(),
                "queue", bankProperties.getQueue(),
                "supportedBins", bankProperties.getSupportedBins()));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> accounts() {
        return ResponseEntity.ok(bankService.accounts());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<BankTransaction>> transactions() {
        return ResponseEntity.ok(bankService.transactions());
    }
}
