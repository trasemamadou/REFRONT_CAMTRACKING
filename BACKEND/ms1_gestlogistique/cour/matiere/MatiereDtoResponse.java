package com.groupeisi.ms1_gestschool.cour.matiere;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatiereDtoResponse {
    private String id;
    private String name;
    private String description;
    private boolean  archive;
}