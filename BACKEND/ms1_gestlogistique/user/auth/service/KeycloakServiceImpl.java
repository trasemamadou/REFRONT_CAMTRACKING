package com.groupeisi.ms1_gestschool.user.auth.service;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private Keycloak keycloak;
    private RestTemplate restTemplate;

    public KeycloakServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @PostConstruct
    public void init() {
        try {
            if (serverUrl == null || clientId == null || clientSecret == null || realm == null) {
                throw new IllegalStateException("Configuration Keycloak manquante : " +
                        "serverUrl=" + serverUrl + ", clientId=" + clientId +
                        ", realm=" + realm + ", secret=" + clientSecret);
            }

            this.keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            // Test de connexion
            keycloak.tokenManager().getAccessToken();
            System.out.println("✅ Keycloak connecté avec succès sur : " + serverUrl + " / " + realm);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Erreur lors de l'initialisation de Keycloak : " + e.getMessage(), e);
        }
    }

    @Override
    public String createUser(UserRepresentation user, String password) {
        try {
            // 🔐 Credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);

            user.setEnabled(true);
            user.setCredentials(List.of(credential));

            // 🔥 Normalisation des rôles (minuscule + role_)
            List<String> normalizedRoles = new ArrayList<>();
            if (user.getRealmRoles() != null) {
                normalizedRoles = user.getRealmRoles().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .map(role -> role.startsWith("role_") ? role : "role_" + role)
                        .distinct()
                        .toList();
            }

            // ⚠️ On enlève temporairement les rôles du user (Keycloak les ignore souvent à la création)
            user.setRealmRoles(null);

            // 🚀 Création utilisateur
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() == 201) {

                    String userId = CreatedResponseUtil.getCreatedId(response);

                    // 🔥 Assignation des rôles APRÈS création
                    if (!normalizedRoles.isEmpty()) {
                        List<RoleRepresentation> rolesToAdd = normalizedRoles.stream()
                                .map(roleName -> keycloak.realm(realm)
                                        .roles()
                                        .get(roleName)
                                        .toRepresentation())
                                .toList();

                        keycloak.realm(realm)
                                .users()
                                .get(userId)
                                .roles()
                                .realmLevel()
                                .add(rolesToAdd);
                    }

                    return userId;

                } else {
                    String err = "Erreur création utilisateur Keycloak : HTTP " + response.getStatus();
                    System.err.println(err);
                    throw new RuntimeException(err);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur createUser : " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserRepresentation> getUsers() {
        try {
            return keycloak.realm(realm).users().list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getUsers : " + e.getMessage(), e);
        }
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getUserById : " + e.getMessage(), e);
        }
    }
    @Override
    public void updateUser(String userId, UserRepresentation user) {
        try {

            // 🔥 Normalisation des rôles
            List<String> normalizedRoles = new ArrayList<>();
            if (user.getRealmRoles() != null) {
                normalizedRoles = user.getRealmRoles().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .map(role -> role.startsWith("role_") ? role : "role_" + role)
                        .distinct()
                        .toList();
            }

            // ⚠️ On enlève les rôles avant update
            user.setRealmRoles(null);

            // 🚀 Update user (infos de base)
            keycloak.realm(realm).users().get(userId).update(user);

            // 🔥 Gestion des rôles après update
            if (!normalizedRoles.isEmpty()) {

                // 1. Récupérer les rôles actuels
                List<RoleRepresentation> currentRoles =
                        keycloak.realm(realm)
                                .users()
                                .get(userId)
                                .roles()
                                .realmLevel()
                                .listAll();

                // 2. Supprimer tous les rôles actuels
                if (!currentRoles.isEmpty()) {
                    keycloak.realm(realm)
                            .users()
                            .get(userId)
                            .roles()
                            .realmLevel()
                            .remove(currentRoles);
                }

                // 3. Ajouter les nouveaux rôles
                List<RoleRepresentation> rolesToAdd = normalizedRoles.stream()
                        .map(roleName -> keycloak.realm(realm)
                                .roles()
                                .get(roleName)
                                .toRepresentation())
                        .toList();

                keycloak.realm(realm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(rolesToAdd);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur updateUser : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        try {
            keycloak.realm(realm).users().get(userId).remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur deleteUser : " + e.getMessage(), e);
        }
    }

    // ---------- NOUVELLES FONCTIONNALITÉS ----------

    @Override
    public void resetPassword(String userId, String newPassword) {
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            keycloak.realm(realm).users().get(userId).resetPassword(credential);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur resetPassword : " + e.getMessage(), e);
        }
    }

    @Override
    public void assignRealmRoles(String userId, List<String> roles) {
        try {
            debugAvailableRoles(); // Add this to see available roles

            if (roles == null || roles.isEmpty()) {
                return;
            }

            System.out.println("📥 Rôles reçus: " + roles);

            // 🔥 Normalisation des rôles - plus robuste
            List<String> normalizedRoles = roles.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .map(role -> {
                        // Si le rôle commence déjà par "role_", on le garde tel quel
                        if (role.startsWith("role_")) {
                            return role;
                        }
                        // Sinon, on ajoute "role_"
                        return "role_" + role;
                    })
                    .distinct()
                    .toList();

            System.out.println("📤 Rôles normalisés: " + normalizedRoles);

            // 🔥 Récupérer les rôles depuis Keycloak avec gestion d'erreur améliorée
            List<RoleRepresentation> rolesToAdd = new ArrayList<>();

            for (String roleName : normalizedRoles) {
                try {
                    System.out.println("🔍 Recherche du rôle: '" + roleName + "'");
                    RoleRepresentation role = keycloak.realm(realm)
                            .roles()
                            .get(roleName)
                            .toRepresentation();
                    System.out.println("✅ Rôle trouvé: " + role.getName());
                    rolesToAdd.add(role);
                } catch (Exception e) {
                    System.err.println("❌ Rôle introuvable dans Keycloak: '" + roleName + "'");
                    System.err.println("   Erreur: " + e.getMessage());
                }
            }

            if (rolesToAdd.isEmpty()) {
                System.err.println("⚠️ Aucun rôle valide trouvé pour assignation !");
                return;
            }

            // 🔥 Récupérer les rôles existants de l'utilisateur
            List<RoleRepresentation> existingRoles = keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .listAll();

            System.out.println("📋 Rôles existants de l'utilisateur: " +
                    existingRoles.stream().map(RoleRepresentation::getName).toList());

            // 🔥 Supprimer les anciens rôles si nécessaire
            if (!existingRoles.isEmpty()) {
                System.out.println("🗑️ Suppression des rôles existants...");
                keycloak.realm(realm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .remove(existingRoles);
            }

            // 🔥 Ajouter les nouveaux rôles
            System.out.println("➕ Ajout des nouveaux rôles: " +
                    rolesToAdd.stream().map(RoleRepresentation::getName).toList());

            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(rolesToAdd);

            System.out.println("✅ Rôles assignés avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur assignRealmRoles : " + e.getMessage(), e);
        }
    }
    private void debugAvailableRoles() {
        try {
            System.out.println("🔍 Vérification des rôles disponibles dans Keycloak:");
            List<RoleRepresentation> allRoles = keycloak.realm(realm).roles().list();
            for (RoleRepresentation role : allRoles) {
                System.out.println("   - Rôle trouvé: '" + role.getName() + "' (avec guillemets pour voir les espaces)");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des rôles: " + e.getMessage());
        }
    }

    @Override
    public List<RoleRepresentation> getUserRealmRoles(String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur getUserRealmRoles : " + e.getMessage(), e);
        }
    }

}