package com.groupeisi.ms1_gestschool.examen.parametrage;
import com.groupeisi.ms1_gestschool.examen.parametrage.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionFieldResponseDTO {
    private String id;
    private String name;
    private String label;
    private FieldType type;
    private String formule;
    private boolean actif;
    private String instanceId;
}