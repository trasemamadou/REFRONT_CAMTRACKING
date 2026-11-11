package com.groupeisi.ms1_gestschool.personne.dossier;

import com.groupeisi.ms1_gestschool.personne.TypeContrat;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierEnseignantDtoRequest {

    @NotBlank(message = "La photo de profil est obligatoire")
    private String photoProfil;

    @NotBlank(message = "Le fichier CNI est obligatoire")
    private String fileCni;
    @NotBlank(message = "Le fichier CV est obligatoire")
    private String fileCv;
    @NotBlank(message = "Le fichier Diplome est obligatoire")
    private String fileDiplome;
    @NotBlank(message = "Le fichier Contrat est obligatoire")
    private String fileContrat;
    private String nomUrgence;
    private String telUrgence;
    private String lienUrgence;

    private String antecedents;
    private String allergies;
    private String experience;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le diplôme est obligatoire")
    private String diplome;

    @NotNull(message = "La date d'embauche est obligatoire")
    private LocalDate dateEmbauche;

    @NotNull(message = "Le type de contrat est obligatoire")
    private TypeContrat typeContrat;

    @NotNull(message = "Le salaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le salaire doit être positif")
    private BigDecimal salaire;

    @NotBlank(message = "La banque est obligatoire")
    private String banque;

    @NotBlank(message = "Le numéro de compte est obligatoire")
    private String numeroCompte;

    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    private String eleveId;
}