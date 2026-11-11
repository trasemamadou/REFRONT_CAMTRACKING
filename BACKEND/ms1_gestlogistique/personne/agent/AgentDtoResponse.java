package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDtoResponse {

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