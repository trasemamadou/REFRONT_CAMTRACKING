package com.groupeisi.ms1_gestschool.scolarite.niveau;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@Table(name = "niveau")
public class NiveauEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String libelle;
    private boolean actif;
    @ManyToOne
    //@JoinColumn(name = "ecole_nom", nullable = false)
    @JoinColumn(name = "ecole_nom")
    private EcoleEntity ecole;
}
