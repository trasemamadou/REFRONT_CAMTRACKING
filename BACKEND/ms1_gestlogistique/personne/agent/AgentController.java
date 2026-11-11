package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
@RequestMapping("/api/agents")

@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AgentController {


    private final AgentService tuteurService;



    @GetMapping
    public ResponseEntity<List<AgentDtoResponse>> getAllTuteurs() {
        return tuteurService.getAllTuteurs()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<?>> getAllTuteursPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<AgentDtoResponse> result =
                tuteurService.getAllTuteursPaged(pageable)
                        .orElse(Page.empty());

        if (result.isEmpty()) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun tuteur n'a été trouvé",
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
                "Tuteurs trouvés avec succès",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDtoResponse> getTuteurById(@PathVariable String id) {
        Optional<AgentDtoResponse> tuteur = tuteurService.getTuteurById(id);
        if (tuteur.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return tuteur.map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Une erreur s'est produite lors de la récupération du tuteur"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDtoResponse> createTuteur(
            @Valid @RequestBody AgentDtoRequest request
    ) {
        Optional<AgentDtoResponse> savedTuteur = tuteurService.saveTuteur(request);

        return savedTuteur
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }



    @PutMapping("/{id}")
    public ResponseEntity<AgentDtoResponse> updateTuteur(
            @PathVariable String id,
            @Valid @RequestBody AgentDtoRequest tuteur) {
        return tuteurService.updateTuteur(id, tuteur)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Le tuteur n'existe pas"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuteur(@PathVariable String id) {
        tuteurService.deleteTuteurById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<AgentDtoResponse> getTuteurByMatricule(@PathVariable String matricule) {
        return tuteurService.getTuteurByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}