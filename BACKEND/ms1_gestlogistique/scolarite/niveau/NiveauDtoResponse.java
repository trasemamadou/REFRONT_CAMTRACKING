package com.groupeisi.ms1_gestschool.scolarite.niveau;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleDtoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class NiveauDtoResponse {
    private String id;
    private String libelle;
    private boolean actif;
    private EcoleDtoResponse ecole;
}
