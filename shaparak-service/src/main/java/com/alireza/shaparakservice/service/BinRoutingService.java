package com.alireza.shaparakservice.service;

import com.alireza.shaparakservice.config.ShaparakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinRoutingService {

    private final ShaparakProperties properties;

    public String extractBin(String cardNumber) {
        return cardNumber.substring(0, 6);
    }

    public String resolveRoutingKey(String cardNumber) {
        return properties.getBinRouting().get(extractBin(cardNumber));
    }

    public String resolveBankCode(String routingKey) {
        return properties.getBankCodes().getOrDefault(routingKey, "UNKNOWN");
    }
}
