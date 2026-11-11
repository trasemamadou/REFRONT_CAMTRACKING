package com.groupeisi.ms1_gestschool.examen.epreuve;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/epreuves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EpreuveController {

    private final EpreuveService epreuveService;

    @PostMapping
    public ResponseEntity<EpreuveDtoResponse> create(@RequestBody EpreuveDtoRequest dtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(epreuveService.create(dtoRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpreuveDtoResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(epreuveService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EpreuveDtoResponse>> getAll() {
        return ResponseEntity.ok(epreuveService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        epreuveService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participation/{participationId}")
    public ResponseEntity<List<EpreuveDtoResponse>> getByParticipation(@PathVariable String participationId) {
        return ResponseEntity.ok(epreuveService.getByParticipationId(participationId));
    }
}