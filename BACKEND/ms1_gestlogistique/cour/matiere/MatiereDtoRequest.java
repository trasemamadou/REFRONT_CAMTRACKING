package com.groupeisi.ms1_gestschool.cour.matiere;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatiereDtoRequest {
    private String id;
    @NotBlank(message = "Le nom est requis")
    private String name;
    @NotBlank(message = "La description est requise")
    private String description;
    private boolean  archive;
    private String ecole;
}
