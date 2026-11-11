package com.groupeisi.ms1_gestschool.examen.epreuve;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereDtoResponse;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationDtoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveDtoResponse {

    private String id;
    private MatiereDtoResponse matiere;
    private ParticipationDtoResponse participation;
    private Double coef;
}