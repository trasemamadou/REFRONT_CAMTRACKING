package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;

import java.util.List;

public interface ParametrageclasseService {
    ParametrageclasseDtoResponse create(ParametrageclasseDtoRequest request);
    ParametrageclasseDtoResponse update(String id, ParametrageclasseDtoRequest request);
    void delete(String id);
    ParametrageclasseDtoResponse getById(String id);
     ParametrageclasseDtoResponse getByClasseIdAndObject(String classeId, FacturationObject parametrageObject);
    List<ParametrageclasseDtoResponse> getAll();
    List<ParametrageclasseDtoResponse> getByClasseId(String classeId);
    List<ParametrageclasseDtoResponse> getByParametrageId(String parametrageId);
    boolean existsByClasseAndParametrage(String classeId, String parametrageId);
}