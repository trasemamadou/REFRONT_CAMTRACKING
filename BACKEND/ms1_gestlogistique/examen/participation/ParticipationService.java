package com.groupeisi.ms1_gestschool.examen.participation;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ParticipationService {
    ParticipationDtoResponse create(ParticipationDtoRequest participationDtoRequest);
    ParticipationDtoResponse update(String id, ParticipationDtoRequest participationDtoRequest);
    void delete(String id);
    ParticipationDtoResponse getById(String id);
    List<ParticipationDtoResponse> getAll();
    List<ParticipationDtoResponse> getByClasseId(String classeId);
    List<ParticipationDtoResponse> getBySessionId(String sessionId);
    Resource generateSaisieNotesExcel(String sessionClasseId);
}