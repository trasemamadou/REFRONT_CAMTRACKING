package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnseignementDtoRequest {
    private String id;
    private String classeId;
    private String enseignantId;
    private String matiereName;
}
