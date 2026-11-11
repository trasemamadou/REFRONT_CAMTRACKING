package com.groupeisi.ms1_gestschool.cour.classe;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClasseDtoRequest {
    private Long id;
    @NotBlank(message = "Le nom ne peux pas être vide")
    private String nom;
    @NotBlank(message = "La description ne peut pas être vide")
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
    private String ecole;
}
