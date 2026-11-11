package com.groupeisi.ms1_gestschool.examen.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDTO {
    private String instanceId;
    private Map<String, Double> values;
    private List<String> order;
}