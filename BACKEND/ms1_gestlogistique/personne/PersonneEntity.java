package com.groupeisi.ms1_gestschool.personne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personne")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 30)
    private String matricule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 100)
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sexe sexe;

    @Column(length = 50)
    private String nationalite;

    @Column(name = "situation_matrimoniale", length = 30)
    private String situationMatrimoniale;

    @Column(name = "numerocni", length = 30)
    private String numeroCNI;

    @Column(length = 30)
    private String telephone;

    @Column(length = 150)
    private String email;
    @Column(nullable = true, length = 255)
    private String password;
    @Column(columnDefinition = "TEXT", length = 100)
    private String adresse;

    private boolean archive;

    @Column(name = "photo_profil", columnDefinition = "TEXT")
    private String photoProfil;

    /* =========================
       STATUT & AUDIT
       ========================= */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatutPerson statut;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id")
    @JsonIgnore
    private DossierEntity dossier;
    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;
    @Column(name = "username", unique = true)
    private String username;

    /* =========================
       LIFECYCLE
       ========================= */
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutPerson.ACTIF;
        }
        if (this.dateNaissance == null) {
            this.dateNaissance = LocalDate.now(); // ou LocalDate.of(2000, 1, 1) selon votre besoin
        }
    }
}