package com.groupeisi.ms1_gestschool.examen.bulletin;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinDtoResponse {
    private String id;
    private String eleveId;
    private String classeId;
    private String trimestre;
    private Double moyenneGenerale;
    private String appreciation;
    private LocalDateTime dateCreation;
}