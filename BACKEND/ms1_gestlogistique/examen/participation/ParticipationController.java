package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessionclasses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ParticipationController {

    private final ParticipationService participationService;
    private final EpreuveService epreuveService;

//    @PostMapping
//    public ResponseEntity<SessionclasseDtoResponse> create(
//            @Valid @RequestBody SessionclasseDtoRequest request) {
//        SessionclasseDtoResponse response = sessionclasseService.create(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
    @PostMapping
    public ResponseEntity<ParticipationDtoResponse> createAvecMatieres(
            @Valid @RequestBody ParticipationDtoRequest request) {

        // 1️⃣ Créer la session classe
        ParticipationDtoResponse response = participationService.create(request);
        if (response != null && request.getMatieres() != null && !request.getMatieres().isEmpty()) {
            epreuveService.createList(
                    response.getId(),
                    request.getMatieres()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ParticipationDtoResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ParticipationDtoRequest request) {
        ParticipationDtoResponse response = participationService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        participationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipationDtoResponse> getById(@PathVariable String id) {
        ParticipationDtoResponse response = participationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationDtoResponse>> getAll() {
        List<ParticipationDtoResponse> responses = participationService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<ParticipationDtoResponse>> getByClasseId(
            @PathVariable String classeId) {
        List<ParticipationDtoResponse> responses = participationService.getByClasseId(classeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ParticipationDtoResponse>> getBySessionId(
            @PathVariable String sessionId) {
        List<ParticipationDtoResponse> responses = participationService.getBySessionId(sessionId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}/saisie-notes/excel")
    public ResponseEntity<Resource> downloadSaisieNotesExcel(@PathVariable String id) {

        Resource file = participationService.generateSaisieNotesExcel(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=saisie_notes_sessionclasse_" + id + ".xlsx")
                .body(file);
    }
//    @GetMapping("/search")
//    public ResponseEntity<SessionclasseDtoResponse> getByClasseAndSession(
//            @RequestParam Long classeId,
//            @RequestParam Long sessionId) {
//        SessionclasseDtoResponse response = sessionclasseService
//                .getByClasseAndSession(classeId, sessionId);
//        return ResponseEntity.ok(response);
//    }
}