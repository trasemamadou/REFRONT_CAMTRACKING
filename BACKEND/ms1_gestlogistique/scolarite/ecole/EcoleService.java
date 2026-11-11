package com.groupeisi.ms1_gestschool.scolarite.ecole;

import java.util.List;

public interface EcoleService {
    EcoleDtoResponse createEcole(EcoleDtoRequest ecoleDtoRequest);
    EcoleDtoResponse updateEcole(String nom, EcoleDtoRequest ecoleDtoRequest);
    void deleteEcole(String nom);
    EcoleDtoResponse getEcoleByNom(String nom);
    List<EcoleDtoResponse> getAllEcoles();
    List<EcoleDtoResponse> getActiveEcoles();
    EcoleDtoResponse toggleEcoleStatus(String nom, boolean active);
}