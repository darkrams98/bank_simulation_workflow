package com.alireza.bankservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "bank")
public class BankProperties {

    private String code;
    private String name;
    private String queue;
    private List<String> supportedBins = new ArrayList<>();
}
