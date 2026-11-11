// File: CompositionService.java (updated interface)
package com.groupeisi.ms1_gestschool.examen.parametrage;

import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationRequestDTO;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CompositionService {

     CompositionInstanceResponseDTO saveInstance(CompositionInstanceRequestDTO requestDTO);

     Optional<CompositionInstanceResponseDTO> findById(String id);

     List<CompositionInstanceResponseDTO> findAll();

     EvaluationResponseDTO evaluate(String instanceId, EvaluationRequestDTO request);

     void validateInstance(CompositionInstance instance);
}