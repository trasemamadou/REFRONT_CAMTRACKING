package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoRequest;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoRequest;
import lombok.Data;

@Data
public class RegistrationWraperRequest {
    private EleveDtoRequest eleveDtoRequest;
    private FacturationDtoRequest facturationDtoRequest;
    private DossierEleveDtoRequest dossierEleveDtoRequest;
}
