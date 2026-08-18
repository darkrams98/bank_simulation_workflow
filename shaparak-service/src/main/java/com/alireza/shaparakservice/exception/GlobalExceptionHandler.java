package com.alireza.shaparakservice.exception;

import com.alireza.payment.common.dto.PaymentStatus;
import com.alireza.shaparakservice.dto.ShaparakResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ShaparakResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(ShaparakResponse.builder()
                .status(PaymentStatus.FAILED.name())
                .message(message)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ShaparakResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ShaparakResponse.builder()
                .status(PaymentStatus.FAILED.name())
                .message(ex.getMessage())
                .build());
    }
}
