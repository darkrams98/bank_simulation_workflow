package com.alireza.shaparakservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "shaparak")
public class ShaparakProperties {

    private String code;
    private String exchange;
    private String replyQueue;
    private String replyRoutingKey;
    private long replyTimeoutMs = 30000L;
    private boolean userCorrelationId = false;
    private Map<String, String> binRouting = new HashMap<>();
    private Map<String, String> bankCodes = new HashMap<>();
}
