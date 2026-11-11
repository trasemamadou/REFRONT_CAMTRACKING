package com.groupeisi.ms1_gestschool.scolarite.annee;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Table(name = "annee")
@Entity
@Getter
@Setter
@Builder
public class AnneeEntity {
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
