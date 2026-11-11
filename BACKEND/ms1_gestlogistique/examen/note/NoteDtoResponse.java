package com.groupeisi.ms1_gestschool.examen.note;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDtoResponse {
    private String id;
    private Double valeur;
//    private String eleveNom;
//    private String elevePrenom;
    private String sessionmatiereId;
    private String matiereNom;
    private LocalDateTime dateCreation;
    private String commentaire;
    private Double coefficient;
    private String typeEvaluation;
    private Double valeurPonderee;
}