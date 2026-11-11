package com.groupeisi.ms1_gestschool.user.auth.controller;

import com.groupeisi.ms1_gestschool.user.auth.dto.CreateKeycloakUserRequest;
import com.groupeisi.ms1_gestschool.user.auth.dto.PersonneWithKeycloakDetails;
import com.groupeisi.ms1_gestschool.user.auth.service.KeycloakUserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users/v2")
@RequiredArgsConstructor
@Validated
@Tag(name = "Gestion des comptes Keycloak", description = "API pour gérer les comptes Keycloak liés aux personnes")
public class KeycloakUserManagementController {

    private final KeycloakUserManagementService service;

    /**
     * Crée un compte Keycloak et le lie à une personne existante
     */
    @PostMapping
    @Operation(summary = "Créer un compte Keycloak",
            description = "Crée un compte Keycloak et l'associe à une personne existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compte Keycloak créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "404", description = "Personne non trouvée"),
            @ApiResponse(responseCode = "409", description = "La personne a déjà un compte Keycloak"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateKeycloakUserRequest request) {
        String keycloakId = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(keycloakId);
    }

    /**
     * Récupère l'utilisateur Keycloak associé à une personne
     */
    @GetMapping("/{personneId}")
    @Operation(summary = "Récupérer un utilisateur Keycloak",
            description = "Récupère les informations Keycloak d'une personne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Personne ou compte Keycloak non trouvé"),
            @ApiResponse(responseCode = "400", description = "ID invalide")
    })
    public ResponseEntity<UserRepresentation> getUser(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId) {
        return ResponseEntity.ok(service.getUser(personneId));
    }

    /**
     * Récupère les détails complets d'une personne (infos locales + Keycloak)
     */
    @GetMapping("/{personneId}/details")
    @Operation(summary = "Récupérer les détails complets",
            description = "Récupère les informations de la personne et de son compte Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Détails récupérés"),
            @ApiResponse(responseCode = "404", description = "Personne non trouvée"),
            @ApiResponse(responseCode = "400", description = "ID invalide")
    })
    public ResponseEntity<PersonneWithKeycloakDetails> getPersonneWithKeycloakDetails(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId) {
        return ResponseEntity.ok(service.getPersonneWithKeycloakDetails(personneId));
    }

    /**
     * Met à jour les informations Keycloak à partir des données de la personne
     */
    @PutMapping("/{personneId}")
    @Operation(summary = "Synchroniser avec Keycloak",
            description = "Met à jour les informations Keycloak avec les données actuelles de la personne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Synchronisation réussie"),
            @ApiResponse(responseCode = "404", description = "Personne ou compte Keycloak non trouvé"),
            @ApiResponse(responseCode = "400", description = "ID invalide")
    })
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId) {
        service.updateUser(personneId);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime le compte Keycloak d'une personne
     */
    @DeleteMapping("/{personneId}")
    @Operation(summary = "Supprimer le compte Keycloak",
            description = "Supprime le compte Keycloak et dissocie de la personne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Compte supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Personne ou compte Keycloak non trouvé"),
            @ApiResponse(responseCode = "400", description = "ID invalide")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId) {
        service.deleteUser(personneId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Réinitialise le mot de passe Keycloak d'une personne
     */
    @PostMapping("/{personneId}/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe",
            description = "Réinitialise le mot de passe Keycloak d'une personne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé"),
            @ApiResponse(responseCode = "400", description = "Mot de passe invalide"),
            @ApiResponse(responseCode = "404", description = "Personne ou compte Keycloak non trouvé")
    })
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId,

            @Parameter(description = "Nouveau mot de passe (min 6 caractères)", required = true)
            @RequestParam @NotBlank(message = "Le mot de passe est obligatoire")
            @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
            String password) {
        service.resetPassword(personneId, password);
        return ResponseEntity.ok().build();
    }

    /**
     * Met à jour les rôles Keycloak d'une personne
     */
    @PostMapping("/{personneId}/roles")
    @Operation(summary = "Mettre à jour les rôles Keycloak",
            description = "Remplace tous les rôles Keycloak d'une personne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôles mis à jour"),
            @ApiResponse(responseCode = "400", description = "Liste de rôles invalide"),
            @ApiResponse(responseCode = "404", description = "Personne ou compte Keycloak non trouvé")
    })
    public ResponseEntity<Void> updateRoles(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId,

            @Parameter(description = "Liste des rôles Keycloak", required = true)
            @RequestBody @NotNull List<@NotBlank String> roles) {
        service.updateRoles(personneId, roles);
        return ResponseEntity.ok().build();
    }

    /**
     * Vérifie si une personne a un compte Keycloak
     */
    @GetMapping("/{personneId}/exists")
    @Operation(summary = "Vérifier l'existence d'un compte",
            description = "Vérifie si une personne possède un compte Keycloak")
    public ResponseEntity<Boolean> hasKeycloakAccount(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull String personneId) {
        try {
            service.getUser(personneId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Dissocie le compte Keycloak d'une personne sans le supprimer
     */
    @PostMapping("/{personneId}/unlink")
    @Operation(summary = "Dissocier le compte Keycloak",
            description = "Dissocie le compte Keycloak de la personne sans le supprimer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compte dissocié"),
            @ApiResponse(responseCode = "404", description = "Personne non trouvée")
    })
    public ResponseEntity<Void> unlinkKeycloakAccount(
            @Parameter(description = "ID de la personne", required = true)
            @PathVariable @NotNull Long personneId) {
        // Cette méthode nécessite une implémentation dans le service
        // service.unlinkKeycloakAccount(personneId);
        return ResponseEntity.ok().build();
    }
}