package com.groupeisi.ms1_gestschool.scolarite.niveau;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NiveauDtoRequest {
    private String id;
    private String libelle;
    private String ecole;
    private boolean actif=true;
}
