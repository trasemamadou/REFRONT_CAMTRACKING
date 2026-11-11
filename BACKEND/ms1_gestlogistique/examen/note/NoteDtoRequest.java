package com.groupeisi.ms1_gestschool.examen.note;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDtoRequest {

    private Double valeur;
    private String bulletinId;
    private String sessionmatiereId;
    private String commentaire;
    private Double coefficient;
    private String typeEvaluation;
}