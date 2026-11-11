package com.groupeisi.ms1_gestschool.scolarite.ecole;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EcoleDtoResponse {
    private String nom;
    private String adresse;
    private String telephone;
    private String emailPrescripteur;
    private String directeur;
    private LocalDate dateCreation;
    private String photoLogo;
    private boolean active;
}