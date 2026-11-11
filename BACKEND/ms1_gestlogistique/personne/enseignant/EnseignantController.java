package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enseignants")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EnseignantController {

    private final EnseignantService enseignantService;

    @GetMapping
    public ResponseEntity<List<EnseignantDtoResponse>> getAllEnseignants() {
        return enseignantService.getAllEnseignants()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<?>> getAllEnseignantsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EnseignantDtoResponse> result =
                enseignantService.getAllEnseignantsPaged(pageable)
                        .orElse(Page.empty());

        if (result.isEmpty()) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun enseignant n'a été trouvé",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        Map<Object, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());

        APIResponse<?> response = new APIResponse<>(
                "success",
                200,
                "Enseignants trouvés avec succès",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnseignantDtoResponse> getEnseignantById(@PathVariable String id) {
        Optional<EnseignantDtoResponse> enseignant = enseignantService.getEnseignantById(id);
        return enseignant.map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID : " + id));
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<EnseignantDtoResponse> getEnseignantByMatricule(@PathVariable String matricule) {
        return enseignantService.getEnseignantByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec le matricule : " + matricule));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnseignantDtoResponse> createEnseignant(
            @Valid @RequestBody EnseignantDtoRequest request
    ) {
        Optional<EnseignantDtoResponse> savedEnseignant = enseignantService.saveEnseignant(request);

        return savedEnseignant
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    @PostMapping(value = "/with-dossier", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<EnseignantWrapperDtoResponse>> createEnseignantWithDossier(
            @Valid @RequestBody EnseignantWrapperDtoRequest wrapperRequest
    ) {
        try {
            EnseignantWrapperDtoResponse result = new EnseignantWrapperDtoResponse();
             result.setEnseignantResponse(enseignantService.saveEnseignantWithDossier(wrapperRequest)
                    .orElseThrow(() -> new RuntimeException("Erreur lors de la création")));

            APIResponse<EnseignantWrapperDtoResponse> response = new APIResponse<>(
                    "success",
                    201,
                    result.getMessage(),
                    result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            APIResponse<EnseignantWrapperDtoResponse> response = new APIResponse<>(
                    "error",
                    400,
                    e.getMessage(),
                    EnseignantWrapperDtoResponse.error(e.getMessage())
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            APIResponse<EnseignantWrapperDtoResponse> response = new APIResponse<>(
                    "error",
                    500,
                    "Erreur interne du serveur: " + e.getMessage(),
                    EnseignantWrapperDtoResponse.error("Erreur interne du serveur")
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterTeacherss(
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

            Page<EnseignantDtoResponse> result = enseignantService.filterTeacherss(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucun élève ne correspond aux critères de recherche",
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
                    "Élèves trouvés avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
           // log.error("Erreur lors du filtrage des étudiants", e);
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
    public ResponseEntity<EnseignantDtoResponse> updateEnseignant(
            @PathVariable String id,
            @Valid @RequestBody EnseignantDtoRequest enseignant
    ) {
        return enseignantService.updateEnseignant(id, enseignant)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("L'enseignant n'existe pas avec l'ID : " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnseignant(@PathVariable String id) {
        enseignantService.deleteEnseignantById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EnseignantDtoResponse> changeStatus(
            @PathVariable String id,
            @RequestParam String statut
    ) {
        return enseignantService.changeStatus(id, statut)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Impossible de changer le statut de l'enseignant"));
    }
}