package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEnseignantDtoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EnseignantWrapperDtoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Les informations de l'enseignant sont obligatoires")
    @Valid
    @JsonProperty("enseignant")
    private EnseignantDtoRequest enseignantRequest;

    @Valid
    @JsonProperty("dossier")
    private DossierEnseignantDtoRequest dossierRequest;

    @JsonProperty("avecDossier")
    private boolean avecDossier;

    // Constructeur pratique pour création sans dossier
    public EnseignantWrapperDtoRequest(EnseignantDtoRequest enseignantRequest) {
        this.enseignantRequest = enseignantRequest;
        this.avecDossier = false;
    }

    // Constructeur pratique pour création avec dossier
    public EnseignantWrapperDtoRequest(EnseignantDtoRequest enseignantRequest,
                                       DossierEnseignantDtoRequest dossierRequest) {
        this.enseignantRequest = enseignantRequest;
        this.dossierRequest = dossierRequest;
        this.avecDossier = true;
    }

    // Méthode utilitaire pour vérifier si le dossier est présent
    public boolean hasDossier() {
        return dossierRequest != null && avecDossier;
    }

    // Méthode pour obtenir l'email de l'enseignant (utile pour les validations)
    public String getEnseignantEmail() {
        return enseignantRequest != null ? enseignantRequest.getEmail() : null;
    }

    // Méthode pour obtenir le téléphone de l'enseignant
    public String getEnseignantTelephone() {
        return enseignantRequest != null ? enseignantRequest.getTelephone() : null;
    }
}