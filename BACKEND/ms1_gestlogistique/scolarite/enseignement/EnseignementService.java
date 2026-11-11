package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import java.util.List;
import java.util.Optional;

public interface EnseignementService {
    Optional<EnseignementDtoResponse> save(EnseignementDtoRequest dtoRequest);
    Optional<EnseignementDtoResponse> update(String id, EnseignementDtoRequest dtoRequest);
    Optional<EnseignementDtoResponse> getById(String id);
    List<EnseignementDtoResponse> getAll();
    List<EnseignementDtoResponse> getAllByClasseId(String idClasse);
    Optional<Boolean> delete(String id);
}
