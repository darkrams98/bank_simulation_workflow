package com.alireza.viewservice.client;

import com.alireza.viewservice.dto.PspPaymentRequest;
import com.alireza.viewservice.dto.PspPaymentResponse;
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
public class PspClient {

    private final RestTemplate pspRestTemplate;

    @Value("${app.psp.base-url}")
    private String pspBaseUrl;

    @Value("${app.psp.payment-path}")
    private String paymentPath;

    public PspPaymentResponse pay(PspPaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<PspPaymentResponse> response = pspRestTemplate.exchange(
                pspBaseUrl + paymentPath,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                PspPaymentResponse.class);

        return response.getBody();
    }
}
