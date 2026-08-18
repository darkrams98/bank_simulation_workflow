package com.alireza.pspservice.client;

import com.alireza.pspservice.dto.ShaparakRequest;
import com.alireza.pspservice.dto.ShaparakResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ShaparakClient {

    private final RestTemplate shaparakRestTemplate;

    @Value("${app.shaparak.base-url}")
    private String shaparakBaseUrl;

    @Value("${app.shaparak.transaction-path}")
    private String transactionPath;

    public ShaparakResponse send(ShaparakRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ShaparakResponse> response = shaparakRestTemplate.exchange(
                shaparakBaseUrl + transactionPath,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ShaparakResponse.class);

        return response.getBody();
    }
}
