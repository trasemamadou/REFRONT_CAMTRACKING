package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseEntity;
import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import com.groupeisi.ms1_gestschool.scolarite.enseignement.EnseignementEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classe")
@Getter
@Setter
public class ClasseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nom;
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean archive;

    /* ===================== RELATIONS ===================== */

    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametrageclasseEntity> parametrageClasses = new ArrayList<>();


    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnseignementEntity> enseignements = new ArrayList<>();

    /* ===================== INFOS CLASSE ===================== */

    private String niveau;
    private String anneeScolaire;

    private Integer effectifActuel;
    private Integer capaciteMax;
    private Double tauxOccupation;

    private String regime;
    private String langueEnseignement;
    private String regimeDescription;
    @ManyToOne
    //@JoinColumn(name = "ecole_nom", nullable = false)
    @JoinColumn(name = "ecole_nom")
    private EcoleEntity ecole;
}
