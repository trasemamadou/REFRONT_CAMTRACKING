package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametragesalaireWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametrage-classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParametrageclasseController {

    private final ParametrageclasseService service;

    @PostMapping
    public ResponseEntity<ParametrageclasseDtoResponse> create(
            @Valid @RequestBody ParametrageclasseDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametrageclasseDtoResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ParametrageclasseDtoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametrageclasseDtoResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParametrageclasseDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<ParametrageclasseDtoResponse>> getByClasseId(
            @PathVariable String classeId) {
        return ResponseEntity.ok(service.getByClasseId(classeId));
    }

    @GetMapping("/classe/{classeId}/object/{parametrageObject}")
    public ResponseEntity<ParametrageclasseDtoResponse> getByParametrageId(
            @PathVariable String classeId,
            @PathVariable FacturationObject parametrageObject) {  // ← enum directement

        ParametrageclasseDtoResponse response = service.getByClasseIdAndObject(classeId, parametrageObject);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByClasseAndParametrage(
            @RequestParam String classeId,
            @RequestParam String parametrageId) {
        return ResponseEntity.ok(service.existsByClasseAndParametrage(classeId, parametrageId));
    }
}