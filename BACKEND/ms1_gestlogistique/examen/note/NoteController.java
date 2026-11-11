package com.groupeisi.ms1_gestschool.examen.note;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDtoResponse> createNote(@RequestBody NoteDtoRequest request) {
        NoteDtoResponse response = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDtoResponse> getNote(@PathVariable String id) {
        NoteDtoResponse response = noteService.getNoteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteDtoResponse>> getAllNotes() {
        List<NoteDtoResponse> responses = noteService.getAllNotes();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDtoResponse> updateNote(
            @PathVariable String id,
            @RequestBody NoteDtoRequest request) {
        NoteDtoResponse response = noteService.updateNote(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/composition-eleve/{compositionEleveId}")
    public ResponseEntity<List<NoteDtoResponse>> getNotesByCompositionEleve(
            @PathVariable String compositionEleveId) {
        List<NoteDtoResponse> responses = noteService.getNotesByBulletin(compositionEleveId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/matiere/{sessionmatiereId}") // Changé le paramètre
    public ResponseEntity<List<NoteDtoResponse>> getNotesByMatiere(@PathVariable String sessionmatiereId) {
        List<NoteDtoResponse> responses = noteService.getNotesByMatiere(sessionmatiereId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/eleve/{eleveId}")
    public ResponseEntity<List<NoteDtoResponse>> getNotesByEleve(@PathVariable String eleveId) {
        List<NoteDtoResponse> responses = noteService.getNotesByEleve(eleveId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/composition/{compositionId}")
    public ResponseEntity<List<NoteDtoResponse>> getNotesByComposition(@PathVariable String compositionId) {
        List<NoteDtoResponse> responses = noteService.getNotesByBulletin(compositionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/moyenne")
    public ResponseEntity<Double> getMoyenneByEleveAndMatiere(
            @RequestParam String eleveId,
            @RequestParam String sessionmatiereId) { // Changé le paramètre
        Double moyenne = noteService.calculateMoyenneByEleveAndMatiere(eleveId, sessionmatiereId);
        return ResponseEntity.ok(moyenne);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NoteDtoResponse>> getNotesByTypeEvaluation(@PathVariable String type) {
        List<NoteDtoResponse> responses = noteService.getNotesByTypeEvaluation(type);
        return ResponseEntity.ok(responses);
    }
}