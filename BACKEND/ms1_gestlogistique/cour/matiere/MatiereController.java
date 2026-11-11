package com.groupeisi.ms1_gestschool.cour.matiere;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/matieres")
@AllArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class MatiereController {

    private final MatiereService matiereService;
    private static final Logger LOG = LogManager.getLogger(MatiereController.class);

    @PostMapping
    public ResponseEntity<MatiereDtoResponse> createMatiere(@RequestBody @Valid MatiereDtoRequest request) {
        LOG.info("Creating new matiere: {}", request.getName());
        return ResponseEntity.ok(matiereService.createMatiere(request));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMatieres() {
        List<MatiereDtoResponse> matieres = matiereService.getAllMatieres();
        Map<String, Object> response = new HashMap<>();
        response.put("count", matieres.size());
        response.put("matieres", matieres);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMatiereById(@PathVariable String id) {
        Optional<MatiereDtoResponse> matiere = matiereService.getMatiereByName(id);
        Map<String, Object> response = new HashMap<>();
        if (matiere.isPresent()) {
            response.put("message", "Matière trouvée");
            response.put("matiere", matiere.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Matière non trouvée");
            return ResponseEntity.status(404).body(response);
        }
    }
    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterMatieres(
            @RequestBody @Valid List<FilterRequest> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "true") boolean sortAsc) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            SorterRequest sorter = null;

            if (sortField != null && !sortField.isEmpty()) {
                sorter = new SorterRequest();
                sorter.setChamp(sortField);
                sorter.setAsc(sortAsc);
            }

            Page<MatiereDtoResponse> result = matiereService.filterMatieres(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucune matière ne correspond aux critères de recherche",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("content", result.getContent());
            data.put("totalElements", result.getTotalElements());
            data.put("totalPages", result.getTotalPages());
            data.put("currentPage", result.getNumber());
            data.put("pageSize", result.getSize());

            APIResponse<?> response = new APIResponse<>(
                    "success",
                    200,
                    "Matières trouvées avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    400,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMatiere(@PathVariable String id, @RequestBody @Valid MatiereDtoRequest request) {
        Optional<MatiereDtoResponse> updatedMatiere = matiereService.updateMatiere(id, request);
        Map<String, Object> response = new HashMap<>();
        if (updatedMatiere.isPresent()) {
            response.put("message", "Matière mise à jour avec succès");
            response.put("matiere", updatedMatiere.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Matière non trouvée");
            return ResponseEntity.status(404).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMatiere(@PathVariable String id) {
        boolean deleted = matiereService.deleteMatiere(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Matière supprimée avec succès");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Matière non trouvée");
            return ResponseEntity.status(404).body(response);
        }
    }
}
