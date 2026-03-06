package com.kushalsg.urlshortener.domain.services;

import com.kushalsg.urlshortener.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TurnstileService {

    private static final Logger log = LoggerFactory.getLogger(TurnstileService.class);
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final String secretKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TurnstileService(ApplicationProperties properties) {
        this.secretKey = properties.turnstileSecretKey();
    }

    public boolean verify(String token) {
        if (token == null || token.isBlank()) {
            log.warn("Turnstile token is missing");
            return false;
        }
        try {
            String body = "secret=" + secretKey + "&response=" + token;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERIFY_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            log.debug("Turnstile response: {}", responseBody);

            return responseBody.contains("\"success\":true");

        } catch (IOException | InterruptedException e) {
            log.error("Turnstile verification failed", e);
            return false;
        }
    }
}