// File: CompositionInstanceResponseDTO.java
package com.groupeisi.ms1_gestschool.examen.parametrage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionInstanceResponseDTO {
    private String id;
    private String name;
    private String referenceFieldName;
    private String referenceFormula;
    private List<CompositionFieldResponseDTO> fields;
}