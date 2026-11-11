package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEnseignantDtoResponse;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnseignantWrapperDtoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("enseignant")
    private EnseignantDtoResponse enseignantResponse;

    @JsonProperty("dossier")
    private DossierEnseignantDtoResponse dossierResponse;

    @JsonProperty("message")
    private String message;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("statusCode")
    private Integer statusCode;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    // Constructeur pour réponse simple
    public EnseignantWrapperDtoResponse(EnseignantDtoResponse enseignantResponse, String message) {
        this.enseignantResponse = enseignantResponse;
        this.message = message;
        this.success = true;
        this.timestamp = LocalDateTime.now();
        this.statusCode = 200;
    }

    // Constructeur pour réponse complète
    public EnseignantWrapperDtoResponse(EnseignantDtoResponse enseignantResponse,
                                        DossierEnseignantDtoResponse dossierResponse,
                                        String message) {
        this.enseignantResponse = enseignantResponse;
        this.dossierResponse = dossierResponse;
        this.message = message;
        this.success = true;
        this.timestamp = LocalDateTime.now();
        this.statusCode = 201;
    }

    // Factory methods pour différents cas d'usage

    public static EnseignantWrapperDtoResponse success(EnseignantDtoResponse enseignant) {
        return EnseignantWrapperDtoResponse.builder()
                .enseignantResponse(enseignant)
                .success(true)
                .message("Opération réussie sur l'enseignant")
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }

    public static EnseignantWrapperDtoResponse success(EnseignantDtoResponse enseignant,
                                                       DossierEnseignantDtoResponse dossier) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("dossierId", dossier != null ? dossier.getId() : null);
        metadata.put("dateCreation", LocalDateTime.now());

        return EnseignantWrapperDtoResponse.builder()
                .enseignantResponse(enseignant)
                .dossierResponse(dossier)
                .success(true)
                .message("Enseignant et dossier créés avec succès")
                .timestamp(LocalDateTime.now())
                .statusCode(201)
                .metadata(metadata)
                .build();
    }

    public static EnseignantWrapperDtoResponse success(EnseignantDtoResponse enseignant,
                                                       String customMessage) {
        return EnseignantWrapperDtoResponse.builder()
                .enseignantResponse(enseignant)
                .success(true)
                .message(customMessage)
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }

    public static EnseignantWrapperDtoResponse error(String errorMessage) {
        return EnseignantWrapperDtoResponse.builder()
                .success(false)
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .statusCode(400)
                .build();
    }

    public static EnseignantWrapperDtoResponse error(String errorMessage, int statusCode) {
        return EnseignantWrapperDtoResponse.builder()
                .success(false)
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .build();
    }

    public static EnseignantWrapperDtoResponse notFound(String resourceType, Object identifier) {
        String message = String.format("%s non trouvé avec l'identifiant : %s", resourceType, identifier);
        return EnseignantWrapperDtoResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .statusCode(404)
                .build();
    }

    public static EnseignantWrapperDtoResponse validationError(Map<String, String> errors) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("validationErrors", errors);

        return EnseignantWrapperDtoResponse.builder()
                .success(false)
                .message("Erreur de validation des données")
                .timestamp(LocalDateTime.now())
                .statusCode(422)
                .metadata(metadata)
                .build();
    }

    // Méthodes utilitaires

    public boolean hasDossier() {
        return dossierResponse != null;
    }

    public String getEnseignantId() {
        return enseignantResponse != null ? enseignantResponse.getId() : null;
    }

    public String getEnseignantMatricule() {
        return enseignantResponse != null ? enseignantResponse.getMatricule() : null;
    }

    public String getFullName() {
        if (enseignantResponse != null) {
            return enseignantResponse.getPrenom() + " " + enseignantResponse.getNom();
        }
        return null;
    }

    // Méthode pour ajouter des métadonnées
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}