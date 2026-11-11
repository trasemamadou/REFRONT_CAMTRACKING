package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametrageclasse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametrageclasseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /* ===================== RELATIONS ===================== */

    @ManyToOne(fetch = FetchType.LAZY)
   // @JoinColumn(name = "classe_id", nullable = false)
    @JoinColumn(name = "classe_id")
    private ClasseEntity classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parametrage_id", nullable = false)
    private ParametrageEntity parametrage;

    /* ===================== ATTRIBUTS ===================== */

    private Double montant;

    @Column(nullable = false)
    private boolean actif = true;
}
