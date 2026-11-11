package com.groupeisi.ms1_gestschool.facturation.parametrage;


import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ParametrageService {
    ParametrageDtoResponse create(ParametrageDtoRequest request);
    ParametrageDtoResponse createWithParamSalaire(ParametragesalaireWrapper request);
    ParametrageDtoResponse update(String id, ParametrageDtoRequest request);
    ParametrageDtoResponse getById(String id);
    List<ParametrageDtoResponse> getAll();
    Optional<ParametrageDtoResponse> getByObject( FacturationObject object);
    Page<ParametrageDtoResponse> filterParametrages(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
    void delete(String id);
}
