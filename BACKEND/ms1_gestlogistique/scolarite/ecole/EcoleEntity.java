package com.groupeisi.ms1_gestschool.scolarite.ecole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ecoles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcoleEntity {

    @Id
    @Column(name = "nom", nullable = false, unique = true, length = 100)
    private String nom;

    @Column(name = "adresse", length = 200)
    private String adresse;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "email", length = 100)
    private String emailPrescripteur;

    @Column(name = "directeur", length = 100)
    private String directeur;

    @Column(name = "date_creation")
    private LocalDate dateCreation;
    @Column(name = "photo_logo", columnDefinition = "TEXT")
    private String photoLogo;
    @Column(name = "active")
    private boolean active = true;
}