package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoResponse;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoRequest;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoResponse;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEnseignantDtoResponse;
import lombok.Data;

@Data
public class RegistrationWraperResponse {
    private EleveDtoResponse eleveDtoResponse;
    private FacturationDtoResponse facturationDtoResponse;
    private DossierEleveDtoResponse dossierEleveDtoResponse;
}
