package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseDtoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/parametrageFacturation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParametrageController {
 @Autowired
    private ParametrageService service;

    @PostMapping
    public ResponseEntity<ParametrageDtoResponse> create(
            @RequestBody ParametrageDtoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametrageDtoResponse> update(
            @PathVariable String id,
            @RequestBody ParametrageDtoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametrageDtoResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParametrageDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @GetMapping("/object/{object}")
    public ResponseEntity<ParametrageDtoResponse> getByClasseIdAndObject(
            @PathVariable String object) {

        try {
            FacturationObject objectEnum = FacturationObject.valueOf(object.toUpperCase());
            Optional<ParametrageDtoResponse> result = service.getByObject(objectEnum);
            return ResponseEntity.ok(result.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Enum invalide
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/salaire")
    public ResponseEntity<ParametrageDtoResponse> createWithParamSalaire(
            @Valid @RequestBody ParametragesalaireWrapper request) {

        ParametrageDtoResponse response = service.createWithParamSalaire(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterParametrages(
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

            Page<ParametrageDtoResponse> result = service.filterParametrages(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucun paramétrage ne correspond aux critères de recherche",
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
                    "Paramétrages trouvés avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // log.error("Erreur lors du filtrage des paramétrages", e);
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    400,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
