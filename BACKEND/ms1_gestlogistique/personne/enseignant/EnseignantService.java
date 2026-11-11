package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnseignantService {
     Optional<List<EnseignantDtoResponse>> getAllEnseignants();
     Optional<Page<EnseignantDtoResponse>> getAllEnseignantsPaged(Pageable pageable);
     Optional<EnseignantDtoResponse> saveEnseignant(EnseignantDtoRequest enseignant);
     Optional<EnseignantDtoResponse> saveEnseignantWithDossier(EnseignantWrapperDtoRequest wrapperRequest);
     Optional<EnseignantDtoResponse> getEnseignantById(String id);
     Optional<EnseignantDtoResponse> getEnseignantByMatricule(String matricule);
     Optional<EnseignantDtoResponse> updateEnseignant(String id, EnseignantDtoRequest enseignant);
     void deleteEnseignantById(String id);
     Optional<EnseignantDtoResponse> changeStatus(String id, String statut);
     Optional<Page<EnseignantDtoResponse>> searchEnseignants(String search, Pageable pageable);
     Page<EnseignantDtoResponse> filterTeacherss(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
}