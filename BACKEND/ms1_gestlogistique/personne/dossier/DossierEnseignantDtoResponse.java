package com.groupeisi.ms1_gestschool.personne.dossier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.groupeisi.ms1_gestschool.personne.TypeContrat;
import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoResponse;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierEnseignantDtoResponse {
    private String id;
    private String photoProfil;
    private String fileCni;
    private String fileCv;
    private String fileDiplome;
    private String fileContrat;
    private String nomUrgence;
    private String telUrgence;
    private String lienUrgence;
    private String antecedents;
    private String allergies;
    private String experience;
    private String adresse;
    private String diplome;
    private LocalDate dateEmbauche;
    private TypeContrat typeContrat;
    private BigDecimal salaire;
    private String banque;
    private String numeroCompte;
    private String specialite;
    @JsonIgnoreProperties("dossier")
    private EnseignantDtoResponse enseignant;

}