package com.groupeisi.ms1_gestschool.facturation.facturation;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "facture")
@Getter
@Setter
public class FacturationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String libelle;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacturationObject object;
    private String description;
    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double montant = 0.0;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacturationStatus statut;
    @ManyToOne
    @JoinColumn(name = "parametrageclasse_id", nullable = true)
    private ParametrageclasseEntity parametrageclasse;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private PersonneEntity student;

    private String responsableFinancier;
    private String modePaiement;
    // Dans FacturationEntity.java
    @Column(name = "date_generation")
    private LocalDateTime dateGeneration;

}
