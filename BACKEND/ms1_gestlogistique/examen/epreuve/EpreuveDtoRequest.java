package com.groupeisi.ms1_gestschool.examen.epreuve;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveDtoRequest {

    @NotNull(message = "L'ID de la matière est obligatoire")
    private String matiereId;

    @NotNull(message = "L'ID de la participation est obligatoire")
    private String participationId;

    @Positive(message = "Le coefficient doit être positif")
    private Double coef = 1.0;
}