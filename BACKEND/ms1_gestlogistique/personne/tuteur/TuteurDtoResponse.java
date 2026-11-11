package com.groupeisi.ms1_gestschool.personne.tuteur;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import com.groupeisi.ms1_gestschool.personne.TypeContrat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuteurDtoResponse {

    /* =========================
       IDENTITÉ
       ========================= */
    private String id;
    private String matricule;


    /* =========================
       Infos personnelles
       ========================= */
    private String prenom;
    private String nom;
    private String sexe;
    private String adresse;
    private String telephone;
    private String email;
    private String role;

    /* =========================
       Infos professionnelles
       ========================= */
    private StatutPerson statut;


    /* =========================
       Bancaire & Urgence
       ========================= */


    /* =========================
       MÉTADONNÉES
       ========================= */
    private String photoProfil;
    private LocalDateTime dateCreation;





}