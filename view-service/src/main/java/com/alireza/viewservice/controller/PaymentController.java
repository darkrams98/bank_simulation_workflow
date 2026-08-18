package com.alireza.viewservice.controller;

import com.alireza.viewservice.dto.PurchaseRequest;
import com.alireza.viewservice.dto.PurchaseResponse;
import com.alireza.viewservice.entity.PaymentRecord;
import com.alireza.viewservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> purchase(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(paymentService.purchase(userDetails.getUsername(), request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentRecord>> history(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.history(userDetails.getUsername()));
    }
}
