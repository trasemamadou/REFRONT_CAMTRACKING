package com.groupeisi.ms1_gestschool.personne.dossier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierEleveDtoResponse {
    private String id;
    private String photoProfil;
    private String fileNaissance;
    private String fileRadiation;
    private String fileSante;
    private String fileCni;
    private String nomUrgence;
    private String telUrgence;
    private String lienUrgence;
    private String antecedents;
    private String allergies;
    private String adresse;
    private String ecolePrecedente;
    private String identifiantScolaire;
    private String langueEtude;
    private String groupeSanguin;
    private String type;
    @JsonIgnoreProperties("dossier")
    private EleveDtoResponse eleve;
}