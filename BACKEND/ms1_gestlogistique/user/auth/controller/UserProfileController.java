package com.groupeisi.ms1_gestschool.user.auth.controller;

import com.groupeisi.ms1_gestschool.user.auth.dto.UserProfileDto;
import com.groupeisi.ms1_gestschool.user.auth.service.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Récupère les informations de profil de l'utilisateur connecté
     */
    @GetMapping("/profile")
    public UserProfileDto getProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            // Retourner null ou lever une exception si token manquant
            throw new RuntimeException("Token JWT manquant ou invalide !");
        }
        return userProfileService.getCurrentUserProfile(jwt);
    }

    /**
     * Récupère la liste des rôles de l'utilisateur connecté depuis le JWT
     */
    @GetMapping("/roles")
    public List<String> getCurrentUserRoles(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new RuntimeException("Token JWT manquant ou invalide !");
        }
        // Récupérer l'objet realm_access
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of(); // Pas de roles trouvés
        }
        // Convertir en List<String> et retourner
        return (List<String>) realmAccess.get("roles");
    }
}