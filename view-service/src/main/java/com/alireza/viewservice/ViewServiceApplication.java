package com.alireza.viewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ViewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViewServiceApplication.class, args);
    }
}
