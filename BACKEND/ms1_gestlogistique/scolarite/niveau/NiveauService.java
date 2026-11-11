package com.groupeisi.ms1_gestschool.scolarite.niveau;

import java.util.List;
import java.util.Optional;

public interface NiveauService {
    NiveauDtoResponse save(NiveauDtoRequest request);
    List<NiveauDtoResponse> findAll();
    Optional<NiveauDtoResponse> findById(String id);
    NiveauDtoResponse update(String id, NiveauDtoRequest request);
    void delete(String id);
}