package com.alireza.bankservice.service;

import com.alireza.bankservice.config.BankProperties;
import com.alireza.bankservice.entity.Account;
import com.alireza.bankservice.entity.BankTransaction;
import com.alireza.bankservice.repository.AccountRepository;
import com.alireza.bankservice.repository.BankTransactionRepository;
import com.alireza.payment.common.dto.BankTransactionRequest;
import com.alireza.payment.common.dto.BankTransactionResponse;
import com.alireza.payment.common.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final BankProperties bankProperties;

    @Transactional
    public BankTransactionResponse process(BankTransactionRequest request) {
        Optional<BankTransaction> existing = bankTransactionRepository.findByTransactionId(request.getTransactionId());
        if (existing.isPresent()) {
            BankTransaction stored = existing.get();
            return build(request, stored.getStatus(), stored.getMessage(),
                    stored.getReferenceCode(), stored.getBalanceAfter());
        }

        String bin = request.getCardNumber().substring(0, 6);

        if (!bankProperties.getSupportedBins().contains(bin)) {
            return persistAndBuild(request, null, PaymentStatus.INVALID_CARD,
                    "BIN " + bin + " does not belong to " + bankProperties.getCode(), null);
        }

        Optional<Account> accountOptional = accountRepository.findByCardNumber(request.getCardNumber());
        if (accountOptional.isEmpty()) {
            return persistAndBuild(request, null, PaymentStatus.INVALID_CARD,
                    "Card not found in " + bankProperties.getCode(), null);
        }

        Account account = accountOptional.get();

        if (!account.isActive()) {
            return persistAndBuild(request, account, PaymentStatus.CARD_INACTIVE,
                    "Card is blocked or inactive", account.getBalance());
        }

        if (!account.getCardHolderName().equalsIgnoreCase(request.getCardHolderName())) {
            return persistAndBuild(request, account, PaymentStatus.INVALID_CARD,
                    "Cardholder name does not match the account owner", account.getBalance());
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return persistAndBuild(request, account, PaymentStatus.INSUFFICIENT_FUNDS,
                    "Insufficient funds. Available balance: " + account.getBalance(), account.getBalance());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        log.info("{} debited {} from card {} for transaction {}",
                bankProperties.getCode(), request.getAmount(), mask(request.getCardNumber()), request.getTransactionId());

        return persistAndBuild(request, account, PaymentStatus.SUCCESS, "Transaction completed successfully", newBalance);
    }

    @Transactional(readOnly = true)
    public List<Account> accounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> transactions() {
        return bankTransactionRepository.findAll();
    }

    private BankTransactionResponse persistAndBuild(BankTransactionRequest request, Account account,
                                                    PaymentStatus status, String message, BigDecimal balanceAfter) {
        String referenceCode = status == PaymentStatus.SUCCESS ? generateReference() : null;

        bankTransactionRepository.save(BankTransaction.builder()
                .transactionId(request.getTransactionId())
                .correlationId(request.getCorrelationId())
                .maskedCard(mask(request.getCardNumber()))
                .cardHolderName(account != null ? account.getCardHolderName() : request.getCardHolderName())
                .amount(request.getAmount())
                .balanceAfter(balanceAfter)
                .status(status.name())
                .message(message)
                .referenceCode(referenceCode)
                .merchantId(request.getMerchantId())
                .build());

        return build(request, status.name(), message, referenceCode, balanceAfter);
    }

    private BankTransactionResponse build(BankTransactionRequest request, String status, String message,
                                          String referenceCode, BigDecimal balanceAfter) {
        BankTransactionResponse response = new BankTransactionResponse();
        response.setTransactionId(request.getTransactionId());
        response.setCorrelationId(request.getCorrelationId());
        response.setStatus(status);
        response.setMessage(message);
        response.setReferenceCode(referenceCode);
        response.setRemainingBalance(balanceAfter);
        response.setBankCode(bankProperties.getCode());
        return response;
    }

    private String generateReference() {
        return bankProperties.getCode() + "-" + ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
    }

    private String mask(String cardNumber) {
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }
}
