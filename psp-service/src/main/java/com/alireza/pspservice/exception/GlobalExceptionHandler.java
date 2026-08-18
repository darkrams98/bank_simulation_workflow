package com.alireza.pspservice.exception;

import com.alireza.pspservice.dto.PspPaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PspPaymentResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(PspPaymentResponse.builder()
                .status("FAILED")
                .message(message)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PspPaymentResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PspPaymentResponse.builder()
                .status("FAILED")
                .message(ex.getMessage())
                .build());
    }
}
