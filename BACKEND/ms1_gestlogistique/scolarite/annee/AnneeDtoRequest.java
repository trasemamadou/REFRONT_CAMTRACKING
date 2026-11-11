package com.groupeisi.ms1_gestschool.scolarite.annee;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnneeDtoRequest {
    private String id;
    private String libelle;
    private String ecole;
    private boolean actif=true;
}
