package com.groupeisi.ms1_gestschool.personne.dossier;

import java.util.List;

public interface DossierService {
    // Eleve
    DossierEleveDtoResponse createEleveDossier(DossierEleveDtoRequest request);
    DossierEleveDtoResponse getEleveDossierById(String id);
    DossierEleveDtoResponse updateEleveDossier(String id, DossierEleveDtoRequest request);
    void deleteEleveDossier(String id);
    List<DossierEleveDtoResponse> getAllEleveDossiers();

    // Enseignant
    DossierEnseignantDtoResponse createEnseignantDossier(DossierEnseignantDtoRequest request);
    DossierEnseignantDtoResponse getEnseignantDossierById(String id);
    DossierEnseignantDtoResponse updateEnseignantDossier(String id, DossierEnseignantDtoRequest request);
    void deleteEnseignantDossier(String id);
    List<DossierEnseignantDtoResponse> getAllEnseignantDossiers();
}