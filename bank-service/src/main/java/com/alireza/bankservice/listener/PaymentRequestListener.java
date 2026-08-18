package com.alireza.bankservice.listener;

import com.alireza.bankservice.config.BankProperties;
import com.alireza.bankservice.service.BankService;
import com.alireza.payment.common.dto.BankTransactionRequest;
import com.alireza.payment.common.dto.BankTransactionResponse;
import com.alireza.payment.common.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final BankService bankService;
    private final BankProperties bankProperties;

    @RabbitListener(queues = "${bank.queue}")
    public BankTransactionResponse onPaymentRequest(BankTransactionRequest request) {
        log.info("{} received transaction {} for BIN {}",
                bankProperties.getCode(), request.getTransactionId(), request.getBin());
        try {
            return bankService.process(request);
        } catch (Exception e) {
            log.error("{} failed to process transaction {}: {}",
                    bankProperties.getCode(), request.getTransactionId(), e.getMessage());
            BankTransactionResponse response = new BankTransactionResponse();
            response.setTransactionId(request.getTransactionId());
            response.setCorrelationId(request.getCorrelationId());
            response.setStatus(PaymentStatus.FAILED.name());
            response.setMessage("Internal bank error: " + e.getMessage());
            response.setBankCode(bankProperties.getCode());
            return response;
        }
    }
}
