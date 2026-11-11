package com.groupeisi.ms1_gestschool.user.auth.service;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import com.groupeisi.ms1_gestschool.user.auth.dto.CreateKeycloakUserRequest;
import com.groupeisi.ms1_gestschool.user.auth.dto.PersonneWithKeycloakDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserManagementService {

    private final KeycloakService keycloakService;
    private final PersonneRepository personneRepository;

    /**
     * Crée un compte Keycloak et le lie à une personne existante
     */
    @Transactional
    public String createUser(CreateKeycloakUserRequest request) {
        // Validation de base
        if (request == null) {
            log.error("Requête null reçue");
            throw new IllegalArgumentException("La requête ne peut pas être null");
        }

        if (request.personneId() == null) {
            log.error("ID personne null dans la requête: {}", request);
            throw new IllegalArgumentException("L'ID de la personne est obligatoire");
        }

        log.info("Tentative de création de compte Keycloak pour la personne ID: {}", request.personneId());
        log.debug("Requête complète reçue: {}", request);

        // 1. Vérifier que la personne existe
        PersonneEntity personne = personneRepository.findById(request.personneId())
                .orElseThrow(() -> {
                    log.error("Personne non trouvée avec l'ID: {}", request.personneId());
                    return new RuntimeException(
                            String.format("Personne introuvable avec l'ID: %d", request.personneId())
                    );
                });

        log.info("Personne trouvée: {} {} (ID: {}), Email: {}, Rôle: {}",
                personne.getPrenom(), personne.getNom(), personne.getId(), personne.getEmail(), personne.getRole());

        // 2. Vérifier qu'elle n'a pas déjà un compte Keycloak
        if (personne.getKeycloakId() != null) {
            log.error("La personne ID: {} a déjà un compte Keycloak avec l'ID: {}",
                    request.personneId(), personne.getKeycloakId());
            throw new IllegalStateException(
                    String.format("Cette personne (ID: %d) possède déjà un compte Keycloak avec l'ID: %s",
                            request.personneId(), personne.getKeycloakId())
            );
        }

        // 3. Validation des données de la personne
        validatePersonneData(personne);
        validatePassword(request.password());

        try {
            // 4. Créer l'utilisateur dans Keycloak avec les infos de la personne
            UserRepresentation user = new UserRepresentation();
            user.setUsername(personne.getEmail()); // Utilisation de l'email comme username
            user.setEmail(personne.getEmail());
            user.setFirstName(personne.getPrenom());
            user.setLastName(personne.getNom());
            user.setEnabled(true);
            user.setEmailVerified(true);

            log.info("Création de l'utilisateur Keycloak avec username: {}, email: {}",
                    user.getUsername(), user.getEmail());

            // 5. Création dans Keycloak
            String keycloakId;
            try {
                keycloakId = keycloakService.createUser(user, request.password());
                log.info("Compte Keycloak créé avec succès - ID: {}, Personne ID: {}", keycloakId, request.personneId());
            } catch (Exception e) {
                log.error("Erreur lors de l'appel à keycloakService.createUser: {}", e.getMessage(), e);
                throw new RuntimeException("Erreur Keycloak: " + e.getMessage(), e);
            }

            // 6. Assigner les rôles si fournis
            if (request.roles() != null && !request.roles().isEmpty()) {
                try {
                    keycloakService.assignRealmRoles(keycloakId, request.roles());
                    log.info("Rôles Keycloak assignés à l'utilisateur {}: {}", keycloakId, request.roles());
                } catch (Exception e) {
                    log.error("Erreur lors de l'assignation des rôles: {}", e.getMessage(), e);
                    // On continue même si les rôles échouent, le compte est créé
                }
            } else {
                log.info("Aucun rôle fourni, création sans rôles");
            }

            // 7. Lier le compte Keycloak à la personne
            personne.setKeycloakId(keycloakId);
            personneRepository.save(personne);
            log.info("Compte Keycloak lié à la personne ID: {}", request.personneId());

            return keycloakId;

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Erreurs de validation, on les laisse passer
            log.error("Erreur de validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du compte Keycloak pour la personne ID: {}",
                    request.personneId(), e);
            throw new RuntimeException("Échec de la création du compte Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un utilisateur Keycloak par ID personne
     */
    public UserRepresentation getUser(String personneId) {
        validatePersonneId(personneId);
        log.info("Récupération de l'utilisateur Keycloak pour la personne ID: {}", personneId);

        PersonneEntity personne = getPersonneWithKeycloakAccount(personneId);

        try {
            UserRepresentation user = keycloakService.getUserById(personne.getKeycloakId());
            log.info("Utilisateur Keycloak récupéré: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur Keycloak pour la personne ID: {}",
                    personneId, e);
            throw new RuntimeException("Impossible de récupérer l'utilisateur Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour les informations Keycloak à partir des données de la personne
     */
    @Transactional
    public void updateUser(String personneId) {
        validatePersonneId(personneId);
        log.info("Mise à jour de l'utilisateur Keycloak pour la personne ID: {}", personneId);

        PersonneEntity personne = getPersonneWithKeycloakAccount(personneId);

        try {
            UserRepresentation user = keycloakService.getUserById(personne.getKeycloakId());

            // Mise à jour des informations
            user.setFirstName(personne.getPrenom());
            user.setLastName(personne.getNom());
            user.setEmail(personne.getEmail());

            keycloakService.updateUser(personne.getKeycloakId(), user);
            log.info("Utilisateur Keycloak mis à jour - ID: {}, Personne ID: {}",
                    personne.getKeycloakId(), personneId);

        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'utilisateur Keycloak pour la personne ID: {}",
                    personneId, e);
            throw new RuntimeException("Impossible de mettre à jour l'utilisateur Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime le compte Keycloak d'une personne
     */
    @Transactional
    public void deleteUser(String personneId) {
        validatePersonneId(personneId);
        log.info("Suppression du compte Keycloak pour la personne ID: {}", personneId);

        PersonneEntity personne = getPersonneWithKeycloakAccount(personneId);
        String keycloakId = personne.getKeycloakId();

        try {
            keycloakService.deleteUser(keycloakId);
            log.info("Utilisateur Keycloak supprimé - ID: {}", keycloakId);

            // Dissocier le compte Keycloak de la personne
            personne.setKeycloakId(null);
            personneRepository.save(personne);
            log.info("Compte Keycloak dissocié de la personne ID: {}", personneId);

        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'utilisateur Keycloak pour la personne ID: {}",
                    personneId, e);
            throw new RuntimeException("Impossible de supprimer l'utilisateur Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Réinitialise le mot de passe Keycloak d'une personne
     */
    @Transactional
    public void resetPassword(String personneId, String password) {
        validatePersonneId(personneId);
        validatePassword(password);
        log.info("Réinitialisation du mot de passe pour la personne ID: {}", personneId);

        PersonneEntity personne = getPersonneWithKeycloakAccount(personneId);

        try {
            keycloakService.resetPassword(personne.getKeycloakId(), password);
            log.info("Mot de passe réinitialisé pour l'utilisateur - Personne ID: {}", personneId);

        } catch (Exception e) {
            log.error("Erreur lors de la réinitialisation du mot de passe pour la personne ID: {}",
                    personneId, e);
            throw new RuntimeException("Impossible de réinitialiser le mot de passe: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour les rôles Keycloak d'une personne
     */
    @Transactional
    public void updateRoles(String personneId, List<String> roles) {
        validatePersonneId(personneId);

        if (roles == null) {
            throw new IllegalArgumentException("La liste des rôles ne peut pas être null");
        }

        log.info("Mise à jour des rôles pour la personne ID: {}, rôles: {}", personneId, roles);

        PersonneEntity personne = getPersonneWithKeycloakAccount(personneId);

        try {
            keycloakService.assignRealmRoles(personne.getKeycloakId(), roles);
            log.info("Rôles mis à jour pour l'utilisateur {}: {}", personne.getKeycloakId(), roles);

        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour des rôles pour la personne ID: {}", personneId, e);
            throw new RuntimeException("Impossible de mettre à jour les rôles: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les détails d'une personne avec ses informations Keycloak
     */
    public PersonneWithKeycloakDetails getPersonneWithKeycloakDetails(String personneId) {
        validatePersonneId(personneId);
        log.info("Récupération des détails complets pour la personne ID: {}", personneId);

        PersonneEntity personne = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Personne introuvable avec l'ID: %d", personneId)
                ));

        List<String> roles = List.of();
        Boolean enabled = false;

        if (personne.getKeycloakId() != null) {
            try {
                UserRepresentation user = keycloakService.getUserById(personne.getKeycloakId());
                enabled = user.isEnabled();

                // Récupérer les rôles Keycloak
                List<RoleRepresentation> roleRepresentations = keycloakService.getUserRealmRoles(personne.getKeycloakId());
                roles = roleRepresentations.stream()
                        .map(RoleRepresentation::getName)
                        .collect(Collectors.toList());

                log.info("Détails Keycloak récupérés pour {} - Enabled: {}, Rôles: {}",
                        personne.getEmail(), enabled, roles);

            } catch (Exception e) {
                log.warn("Impossible de récupérer les détails Keycloak pour la personne ID: {}", personneId, e);
            }
        } else {
            log.info("Personne ID: {} n'a pas de compte Keycloak", personneId);
        }

        return new PersonneWithKeycloakDetails(
                personne.getId(),
                personne.getNom(),
                personne.getPrenom(),
                personne.getEmail(),
                personne.getTelephone() != null ? personne.getTelephone() : "",
                personne.getDateNaissance() != null ? personne.getDateNaissance() : null,
                personne.getSexe(),
                personne.getRole(),
                personne.getKeycloakId(),
                enabled,
                roles
        );
    }

    /**
     * Vérifie si une personne a un compte Keycloak
     */
    public boolean hasKeycloakAccount(String personneId) {
        validatePersonneId(personneId);

        return personneRepository.findById(personneId)
                .map(p -> p.getKeycloakId() != null)
                .orElse(false);
    }

    /**
     * Dissocie le compte Keycloak d'une personne sans le supprimer
     */
    @Transactional
    public void unlinkKeycloakAccount(String personneId) {
        validatePersonneId(personneId);
        log.info("Dissociation du compte Keycloak pour la personne ID: {}", personneId);

        PersonneEntity personne = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Personne introuvable avec l'ID: %d", personneId)
                ));

        if (personne.getKeycloakId() == null) {
            log.warn("Tentative de dissociation pour une personne sans compte Keycloak: {}", personneId);
            throw new IllegalStateException("Cette personne n'a pas de compte Keycloak");
        }

        personne.setKeycloakId(null);
        personneRepository.save(personne);
        log.info("Compte Keycloak dissocié de la personne ID: {}", personneId);
    }

    // ===================== MÉTHODES PRIVÉES =====================

    private void validatePersonneId(String personneId) {
        if (personneId == null) {
            throw new IllegalArgumentException("L'ID de la personne ne peut pas être null");
        }
    }

    private void validatePersonneData(PersonneEntity personne) {
        if (personne.getEmail() == null || personne.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email de la personne est requis pour créer un compte Keycloak");
        }
        if (personne.getPrenom() == null || personne.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de la personne est requis pour créer un compte Keycloak");
        }
        if (personne.getNom() == null || personne.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la personne est requis pour créer un compte Keycloak");
        }
    }

    private void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }

    private PersonneEntity getPersonneWithKeycloakAccount(String personneId) {
        PersonneEntity personne = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Personne introuvable avec l'ID: %d", personneId)
                ));

        if (personne.getKeycloakId() == null) {
            throw new IllegalStateException(
                    String.format("Cette personne (ID: %d) n'a pas de compte Keycloak associé", personneId)
            );
        }

        return personne;
    }
}