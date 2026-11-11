package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutoratDtoResponse {
    private Long id;
    private Long eleveId;
    private String eleveNom;
    private String elevePrenom;
    private Long tuteurId;
    private String tuteurNom;
    private String tuteurPrenom;
}

