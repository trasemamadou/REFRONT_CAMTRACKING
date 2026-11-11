package com.groupeisi.ms1_gestschool.examen.parametrage;
import com.groupeisi.ms1_gestschool.examen.parametrage.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionFieldRequestDTO {

    private String id;

    @NotBlank(message = "Le nom du champ est obligatoire")
    private String name;

    @NotBlank(message = "Le label du champ est obligatoire")
    private String label;

    @NotNull(message = "Le type du champ est obligatoire")
    private FieldType type;

    private String formule;

    private boolean actif = true;
}