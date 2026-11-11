package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleDtoResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClasseDtoResponse {
    private String id;
    private String nom;
    private String description;
    private boolean archive;
    private String niveau;
    private String anneeScolaire;
    // Effectif & capacité
    private Integer effectifActuel;
    private Integer capaciteMax;
    private Double tauxOccupation;
    // Organisation
    private String regime;
    private String langueEnseignement;
    private String regimeDescription;
    private EcoleDtoResponse ecole;
}
