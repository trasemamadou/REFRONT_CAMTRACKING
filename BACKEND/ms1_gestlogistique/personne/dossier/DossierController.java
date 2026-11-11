package com.groupeisi.ms1_gestschool.personne.dossier;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dossier")
@RequiredArgsConstructor
public class DossierController {

    private final DossierService dossierService;

    // ========== ÉLÈVE ENDPOINTS ==========
    @PostMapping("/eleve")
    public ResponseEntity<DossierEleveDtoResponse> createEleve(@Valid @RequestBody DossierEleveDtoRequest request) {
        return new ResponseEntity<>(dossierService.createEleveDossier(request), HttpStatus.CREATED);
    }

    @GetMapping("/eleve/{id}")
    public ResponseEntity<DossierEleveDtoResponse> getEleveById(@PathVariable String id) {
        return ResponseEntity.ok(dossierService.getEleveDossierById(id));
    }

    @PutMapping("/eleve/{id}")
    public ResponseEntity<DossierEleveDtoResponse> updateEleve(@PathVariable String id,
                                                               @Valid @RequestBody DossierEleveDtoRequest request) {
        return ResponseEntity.ok(dossierService.updateEleveDossier(id, request));
    }

    @DeleteMapping("/eleve/{id}")
    public ResponseEntity<Void> deleteEleve(@PathVariable String id) {
        dossierService.deleteEleveDossier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eleve")
    public ResponseEntity<List<DossierEleveDtoResponse>> getAllEleve() {
        return ResponseEntity.ok(dossierService.getAllEleveDossiers());
    }

    // ========== ENSEIGNANT ENDPOINTS ==========
    @PostMapping("/enseignant")
    public ResponseEntity<DossierEnseignantDtoResponse> createEnseignant(@Valid @RequestBody DossierEnseignantDtoRequest request) {
        return new ResponseEntity<>(dossierService.createEnseignantDossier(request), HttpStatus.CREATED);
    }

    @GetMapping("/enseignant/{id}")
    public ResponseEntity<DossierEnseignantDtoResponse> getEnseignantById(@PathVariable String id) {
        return ResponseEntity.ok(dossierService.getEnseignantDossierById(id));
    }

    @PutMapping("/enseignant/{id}")
    public ResponseEntity<DossierEnseignantDtoResponse> updateEnseignant(@PathVariable String id,
                                                                         @Valid @RequestBody DossierEnseignantDtoRequest request) {
        return ResponseEntity.ok(dossierService.updateEnseignantDossier(id, request));
    }

    @DeleteMapping("/enseignant/{id}")
    public ResponseEntity<Void> deleteEnseignant(@PathVariable String id) {
        dossierService.deleteEnseignantDossier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enseignant")
    public ResponseEntity<List<DossierEnseignantDtoResponse>> getAllEnseignant() {
        return ResponseEntity.ok(dossierService.getAllEnseignantDossiers());
    }
}