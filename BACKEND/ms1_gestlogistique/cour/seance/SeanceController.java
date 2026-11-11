package com.groupeisi.ms1_gestschool.cour.seance;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class SeanceController {

    private final SeanceService sessionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSession(@RequestBody @Valid  SeanceDtoRequest request) {
        SeanceDtoResponse session = sessionService.createSession(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Session created successfully");
        response.put("session", session);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSessions() {
        List<SeanceDtoResponse> sessions = sessionService.getAllSessions();
        Map<String, Object> response = new HashMap<>();
        response.put("count", sessions.size());
        response.put("sessions", sessions);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/courseSessions/{id}")
    public ResponseEntity<Map<String, Object>> getAllSessionsByCoursesId(@PathVariable  String matiereName) {
        List<SeanceDtoResponse> sessions = sessionService.getSessionsByCourseId(matiereName);
        Map<String, Object> response = new HashMap<>();
        response.put("count", sessions.size());
        response.put("sessions", sessions);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSessionById(@PathVariable Long id) {
        Optional<SeanceDtoResponse> session = sessionService.getSessionById(id);
        if (session.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session found");
            response.put("session", session.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Session not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSession(@PathVariable Long id, @RequestBody @Valid SeanceDtoRequest request) {
        Optional<SeanceDtoResponse> updatedSession = sessionService.updateSession(id, request);
        if (updatedSession.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session updated successfully");
            response.put("session", updatedSession.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Session not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    @PutMapping("/endsession/{id}")
    public ResponseEntity<?> endSession(@PathVariable Long id) {
        try {
            SeanceDtoResponse session = sessionService.endSession(id);
            return ResponseEntity.ok(session);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session non trouvée avec l'ID : " + id);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La session ne peut pas être terminée : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne s'est produite.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSession(@PathVariable Long id) {
        boolean deleted = sessionService.deleteSession(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Session deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Session not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
