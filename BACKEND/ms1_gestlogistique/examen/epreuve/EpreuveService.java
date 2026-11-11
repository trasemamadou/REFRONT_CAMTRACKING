package com.groupeisi.ms1_gestschool.examen.epreuve;

import java.util.List;

public interface EpreuveService {

    EpreuveDtoResponse create(EpreuveDtoRequest dtoRequest);

    List<EpreuveDtoResponse> createList(String participationId, List<EpreuveDtoRequest> dtoRequestList);

    EpreuveDtoResponse update(String id, EpreuveDtoRequest dtoRequest);

    void delete(String id);

    EpreuveDtoResponse getById(String id);

    List<EpreuveDtoResponse> getAll();

    List<EpreuveDtoResponse> getByMatiereId(String matiereId);

    List<EpreuveDtoResponse> getByParticipationId(String participationId);
}