package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enseignements")
@CrossOrigin(origins = "*")
public class EnseignementController {

    private final EnseignementService enseignementService;

    @PostMapping
    public ResponseEntity<EnseignementDtoResponse> create(@RequestBody EnseignementDtoRequest request) {
        return enseignementService.save(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnseignementDtoResponse> update(@PathVariable String id,
                                                          @RequestBody EnseignementDtoRequest request) {
        return enseignementService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnseignementDtoResponse> getById(@PathVariable String id) {
        return enseignementService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EnseignementDtoResponse>> getAll() {
        return ResponseEntity.ok(enseignementService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return enseignementService.delete(id)
                .filter(Boolean::booleanValue)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
