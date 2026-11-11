package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEnseignantDtoResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EnseignantDtoResponse {

    private String id;
    private String matricule;
    private String role;        // ajouté

    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String sexe;
    private String nationalite;
    private String situationMatrimoniale;
    private String numeroCNI;
    private String adresse;
    private String telephone;
    private String email;
    private String photoProfil;
    private boolean archive;
    private StatutPerson statut;
    private LocalDateTime dateCreation;
    private DossierEnseignantDtoResponse dossier;
}