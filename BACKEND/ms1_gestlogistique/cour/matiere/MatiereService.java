package com.groupeisi.ms1_gestschool.cour.matiere;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MatiereService {
    MatiereDtoResponse createMatiere(MatiereDtoRequest request);
    List<MatiereDtoResponse> getAllMatieres();
    Page<MatiereDtoResponse> filterMatieres(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
    Optional<MatiereDtoResponse> getMatiereByName(String name);
    Optional<MatiereDtoResponse> updateMatiere(String name, MatiereDtoRequest request);
    boolean deleteMatiere(String name);
}

