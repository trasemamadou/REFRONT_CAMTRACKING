package com.groupeisi.ms1_gestschool.examen.evaluation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDTO {

    @NotNull(message = "Les valeurs d'entrée sont obligatoires")
    private Map<String, Double> inputs;
}