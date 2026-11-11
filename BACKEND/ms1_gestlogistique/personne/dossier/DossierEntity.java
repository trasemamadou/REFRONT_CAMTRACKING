package com.groupeisi.ms1_gestschool.personne.dossier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.TypeContrat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dossiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /* =========================
       TYPE DOSSIER
    ========================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDossier type;

    /* =========================
       FICHIERS
    ========================= */
    @Column(columnDefinition = "TEXT")
    private String photoProfil;

    @Column(columnDefinition = "TEXT")
    private String fileNaissance;

    @Column(columnDefinition = "TEXT")
    private String fileRadiation;

    @Column(columnDefinition = "TEXT")
    private String fileSante;

    @Column(columnDefinition = "TEXT")
    private String fileCni;
    @Column(columnDefinition = "TEXT")
    private String fileCv;
    @Column(columnDefinition = "TEXT")
    private String fileDiplome;
    @Column(columnDefinition = "TEXT")
    private String fileContrat;

    /* =========================
       CONTACT D'URGENCE
    ========================= */
    @Column(columnDefinition = "TEXT")
    private String nomUrgence;

    @Column(columnDefinition = "TEXT")
    private String telUrgence;

    @Column(columnDefinition = "TEXT")
    private String lienUrgence;

    /* =========================
       INFOS PERSONNELLES
    ========================= */
    @Column(columnDefinition = "TEXT")
    private String antecedents;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String ecolePrecedente;

    @Column(unique = true, columnDefinition = "TEXT")
    private String identifiantScolaire;

    @Column(columnDefinition = "TEXT")
    private String langueEtude;

    @Column(columnDefinition = "TEXT")
    private String groupeSanguin;

    /* =========================
       INFOS PROFESSIONNELLES
    ========================= */
    @Column(columnDefinition = "TEXT")
    private String diplome;

    private LocalDate dateEmbauche;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaire;

    @Column(columnDefinition = "TEXT")
    private String banque;

    @Column(columnDefinition = "TEXT")
    private String numeroCompte;

    @Column(columnDefinition = "TEXT")
    private String specialite;

    /* =========================
       AUDIT
    ========================= */
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @OneToOne(mappedBy = "dossier", fetch = FetchType.LAZY)
    @JsonIgnore
    private PersonneEntity personne;

    @PrePersist
    public void prePersist() {

        this.dateCreation = LocalDateTime.now();
        if (this.type == null) {
            this.type = TypeDossier.ELEVE;
        }

    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

}