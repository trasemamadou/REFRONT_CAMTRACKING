package com.groupeisi.ms1_gestschool.examen.bulletin;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinDtoRequest {
    private String eleveId;
    private String classe;
    private String trimestre;
    private Double moyenneGenerale;
    private String appreciation;
    private String sessionclasseId;
}