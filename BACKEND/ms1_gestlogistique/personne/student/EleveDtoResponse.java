package com.groupeisi.ms1_gestschool.personne.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoResponse;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EleveDtoResponse implements Serializable {
    private String id;
    private String prenom;
    private String nom;
    private Date dateNaissance;
    private String matricule;
    private String photoProfil;
    private String telephone;
    private String adresse;
    private String sexe;
    private String lieuNaissance;
    private String nationalite;
    private boolean archive;
    private String role;
    @JsonIgnoreProperties("eleve")
    private DossierEleveDtoResponse dossier;
}