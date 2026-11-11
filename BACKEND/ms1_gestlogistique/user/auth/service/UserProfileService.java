package com.groupeisi.ms1_gestschool.user.auth.service;

import com.groupeisi.ms1_gestschool.user.auth.dto.UserProfileDto;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final KeycloakService keycloakService;

    public UserProfileService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public UserProfileDto getCurrentUserProfile(Jwt jwt) {
        String userId = jwt.getSubject(); // ou "sub"

        UserRepresentation user = keycloakService.getUserById(userId);

        // 🔹 Récupération des rôles realm
        List<String> realmRoles = keycloakService.getUserRealmRoles(userId)
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());


        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                realmRoles,
                List.of(), // clientRoles (optionne
                user.isEnabled()
        );
    }
}
