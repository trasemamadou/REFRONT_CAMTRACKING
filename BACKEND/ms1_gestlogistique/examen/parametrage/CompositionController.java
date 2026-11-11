// File: CompositionController.java (updated)
package com.groupeisi.ms1_gestschool.examen.parametrage;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationRequestDTO;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/composition-instances")
@RequiredArgsConstructor
public class CompositionController {

    private final CompositionService service;

    @PostMapping
    public ResponseEntity<CompositionInstanceResponseDTO> createInstance(
            @Valid @RequestBody CompositionInstanceRequestDTO request) {
        return ResponseEntity.ok(service.saveInstance(request));
    }

    @GetMapping("/instance/{id}")
    public ResponseEntity<CompositionInstanceResponseDTO> getInstance(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CompositionInstanceResponseDTO>> getAllInstances() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/instance/{id}/evaluate")
    public ResponseEntity<?> evaluate(
            @PathVariable String id,
            @Valid @RequestBody EvaluationRequestDTO request) {
        try {
            EvaluationResponseDTO response = service.evaluate(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Erreur d'évaluation");
        }
    }
}