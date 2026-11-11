package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApprentissageService {
    Optional<ApprentissageDtoResponse> save(ApprentissageDtoRequest dtoRequest);
    Optional<ApprentissageDtoResponse> update(String id, ApprentissageDtoRequest dtoRequest);
    Optional<ApprentissageDtoResponse> getById(String id);
    List<ApprentissageDtoResponse> getAll();
    List<ApprentissageDtoResponse> getAllByClasseId(String idClasse);
    Page<ApprentissageDtoResponse> getAllByClasseIdPaged(String idClasse, Pageable pageable);
    Optional<Page<EleveDtoResponse>> getStudentsByClasseIdPaged(String classeId, Pageable pageable);
    Optional<List<EleveDtoResponse>> getStudentsByClasseId(String classeId);
    Optional<Boolean> delete(String id);
}
