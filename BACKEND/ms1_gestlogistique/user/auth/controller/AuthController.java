package com.groupeisi.ms1_gestschool.user.auth.controller;

import com.groupeisi.ms1_gestschool.user.auth.dto.KeycloakTokenResponse;
import com.groupeisi.ms1_gestschool.user.auth.dto.LoginRequest;
import com.groupeisi.ms1_gestschool.user.auth.service.KeycloakAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Dev uniquement, prod : mettre Angular URL exacte
public class AuthController {

    private final KeycloakAuthService authService;

    public AuthController(KeycloakAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<KeycloakTokenResponse> login(@RequestBody LoginRequest request) {
        KeycloakTokenResponse tokenResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<KeycloakTokenResponse> refresh(@RequestBody String refreshToken) {
        KeycloakTokenResponse tokenResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}