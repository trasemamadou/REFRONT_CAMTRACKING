package com.groupeisi.ms1_gestschool.personne.dossier;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierEleveDtoRequest {

    @NotBlank(message = "La photo de profil est obligatoire")
    private String photoProfil;

    @NotBlank(message = "Le fichier de naissance est obligatoire")
    private String fileNaissance;

    private String fileRadiation;

    private String fileSante;

    private String fileCni;

    @NotBlank(message = "Le nom du contact d'urgence est obligatoire")
    private String nomUrgence;

    @NotBlank(message = "Le téléphone du contact d'urgence est obligatoire")
    private String telUrgence;

    @NotBlank(message = "Le lien du contact d'urgence est obligatoire")
    private String lienUrgence;

    private String antecedents;
    private String allergies;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    private String ecolePrecedente;

    @NotBlank(message = "L'identifiant scolaire est obligatoire")
    private String identifiantScolaire;

    @NotBlank(message = "La langue d'étude est obligatoire")
    private String langueEtude;

    @NotBlank(message = "Le groupe sanguin est obligatoire")
    private String groupeSanguin;
    @NotBlank(message = "Le type de dossier est obligatoire")
    private String type;
    private String eleveId;
}