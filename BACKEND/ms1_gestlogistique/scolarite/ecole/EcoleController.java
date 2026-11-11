package com.groupeisi.ms1_gestschool.scolarite.ecole;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ecoles")
@RequiredArgsConstructor
public class EcoleController {

    private final EcoleService ecoleService;

    @PostMapping
    public ResponseEntity<EcoleDtoResponse> createEcole(@Valid @RequestBody EcoleDtoRequest ecoleDtoRequest) {
        EcoleDtoResponse response = ecoleService.createEcole(ecoleDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{nom}")
    public ResponseEntity<EcoleDtoResponse> updateEcole(
            @PathVariable String nom,
            @Valid @RequestBody EcoleDtoRequest ecoleDtoRequest) {
        EcoleDtoResponse response = ecoleService.updateEcole(nom, ecoleDtoRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{nom}")
    public ResponseEntity<Void> deleteEcole(@PathVariable String nom) {
        ecoleService.deleteEcole(nom);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nom}")
    public ResponseEntity<EcoleDtoResponse> getEcoleByNom(@PathVariable String nom) {
        EcoleDtoResponse response = ecoleService.getEcoleByNom(nom);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EcoleDtoResponse>> getAllEcoles() {
        List<EcoleDtoResponse> response = ecoleService.getAllEcoles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/actives")
    public ResponseEntity<List<EcoleDtoResponse>> getActiveEcoles() {
        List<EcoleDtoResponse> response = ecoleService.getActiveEcoles();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{nom}/status")
    public ResponseEntity<EcoleDtoResponse> toggleEcoleStatus(
            @PathVariable String nom,
            @RequestParam boolean active) {
        EcoleDtoResponse response = ecoleService.toggleEcoleStatus(nom, active);
        return ResponseEntity.ok(response);
    }
}