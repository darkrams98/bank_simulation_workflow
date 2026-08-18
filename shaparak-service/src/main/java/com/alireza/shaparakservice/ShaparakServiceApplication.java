package com.alireza.shaparakservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ShaparakServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShaparakServiceApplication.class, args);
    }
}
