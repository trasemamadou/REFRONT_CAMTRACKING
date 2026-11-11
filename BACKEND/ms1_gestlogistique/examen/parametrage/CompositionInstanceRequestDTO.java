package com.groupeisi.ms1_gestschool.examen.parametrage;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionInstanceRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String referenceFieldName;

    private String referenceFormula;

    @NotEmpty(message = "L'instance doit contenir au moins un champ")
    private List<CompositionFieldRequestDTO> fields;
}