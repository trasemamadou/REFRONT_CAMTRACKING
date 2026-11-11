package com.groupeisi.ms1_gestschool.cour.seance;

import java.util.List;
import java.util.Optional;

public interface SeanceService {
    SeanceDtoResponse createSession(SeanceDtoRequest request);
    List<SeanceDtoResponse> getAllSessions();
    List<SeanceDtoResponse> getSessionsByCourseId(String matiereName);
    Optional<SeanceDtoResponse> getSessionById(Long id);
    Optional<SeanceDtoResponse> updateSession(Long id, SeanceDtoRequest request);
    boolean deleteSession(Long id);
    SeanceDtoResponse endSession(Long id);
}
