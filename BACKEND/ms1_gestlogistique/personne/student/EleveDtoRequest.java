package com.groupeisi.ms1_gestschool.personne.student;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EleveDtoRequest implements Serializable {
    private String id;
    @NotBlank(message = "Le prenom est obligatoire ")
    private String prenom;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    private String email;
    @NotBlank(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;
    private String matricule;
    @NotBlank(message = "La photo de profil est obligatoire")
    private String photoProfil;
    private String telephone;
    private String adresse;
    private String sexe;
    private String lieuNaissance;
    private String nationalite;

    private boolean archive;
    private String role;
    private String dossierId;
}