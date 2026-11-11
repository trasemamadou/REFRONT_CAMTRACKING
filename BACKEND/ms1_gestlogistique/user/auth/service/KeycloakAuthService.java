package com.groupeisi.ms1_gestschool.user.auth.service;

import com.groupeisi.ms1_gestschool.user.auth.dto.KeycloakTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakAuthService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth.client-id}")
    private String clientId;

    @Value("${keycloak.auth.client-secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KeycloakAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KeycloakTokenResponse login(String username, String password) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add("client_secret", clientSecret);
        }
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                tokenUrl, request, KeycloakTokenResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Authentication failed: " + response.getStatusCode());
        }

        return response.getBody();
    }

    public KeycloakTokenResponse refreshToken(String refreshToken) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add("client_secret", clientSecret);
        }
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                tokenUrl, request, KeycloakTokenResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Refresh failed: " + response.getStatusCode());
        }

        return response.getBody();
    }
}