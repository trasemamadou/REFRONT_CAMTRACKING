package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "parametrage")
public class ParametrageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private Date dateDebut;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacturationObject object;
    //@Column(nullable = false)
    private String libelle;
    @Column(nullable = false)
    private Date dateFin;
    @ManyToOne
    //@JoinColumn(name = "ecole_nom", nullable = false)
    @JoinColumn(name = "ecole_nom")
    private EcoleEntity ecole;


}
