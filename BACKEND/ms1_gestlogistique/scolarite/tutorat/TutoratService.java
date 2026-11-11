package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import java.util.List;

public interface TutoratService {
    TutoratDtoResponse createTutorat(TutoratDtoRequest request);
    TutoratDtoResponse getTutoratById(Long id);
    List<TutoratDtoResponse> getAllTutorats();
    TutoratDtoResponse updateTutorat(Long id, TutoratDtoRequest request);
    void deleteTutorat(Long id);
    List<TutoratDtoResponse> getTutoratsByEleve(Long eleveId);
    List<TutoratDtoResponse> getTutoratsByTuteur(Long tuteurId);
}