package com.groupeisi.ms1_gestschool.scolarite.annee;

import java.util.List;
import java.util.Optional;

public interface AnneeService {

    AnneeDtoResponse save(AnneeDtoRequest request);

    List<AnneeDtoResponse> findAll();

    Optional<AnneeDtoResponse> findById(String id);

    AnneeDtoResponse update(String id, AnneeDtoRequest request);

    void delete(String id);
}