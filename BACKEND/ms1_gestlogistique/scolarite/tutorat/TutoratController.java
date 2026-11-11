package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutorats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TutoratController {

    private final TutoratService tutoratService;

    @PostMapping
    public ResponseEntity<APIResponse<TutoratDtoResponse>> createTutorat(@Valid @RequestBody TutoratDtoRequest request) {
        TutoratDtoResponse created = tutoratService.createTutorat(request);
        APIResponse<TutoratDtoResponse> response = new APIResponse<>(
                "success",
                HttpStatus.CREATED.value(),
                "Tutorat créé avec succès",
                created
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TutoratDtoResponse>> getTutoratById(@PathVariable Long id) {
        TutoratDtoResponse tutorat = tutoratService.getTutoratById(id);
        APIResponse<TutoratDtoResponse> response = new APIResponse<>(
                "success",
                HttpStatus.OK.value(),
                "Tutorat trouvé",
                tutorat
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<TutoratDtoResponse>>> getAllTutorats() {
        List<TutoratDtoResponse> tutorats = tutoratService.getAllTutorats();
        if (tutorats.isEmpty()) {
            APIResponse<List<TutoratDtoResponse>> response = new APIResponse<>(
                    "error",
                    HttpStatus.NO_CONTENT.value(),
                    "Aucun tutorat trouvé",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        APIResponse<List<TutoratDtoResponse>> response = new APIResponse<>(
                "success",
                HttpStatus.OK.value(),
                "Tutorats récupérés avec succès",
                tutorats
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TutoratDtoResponse>> updateTutorat(
            @PathVariable Long id,
            @Valid @RequestBody TutoratDtoRequest request) {
        TutoratDtoResponse updated = tutoratService.updateTutorat(id, request);
        APIResponse<TutoratDtoResponse> response = new APIResponse<>(
                "success",
                HttpStatus.OK.value(),
                "Tutorat mis à jour avec succès",
                updated
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteTutorat(@PathVariable Long id) {
        tutoratService.deleteTutorat(id);
        APIResponse<Void> response = new APIResponse<>(
                "success",
                HttpStatus.NO_CONTENT.value(),
                "Tutorat supprimé avec succès",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-eleve/{eleveId}")
    public ResponseEntity<APIResponse<List<TutoratDtoResponse>>> getTutoratsByEleve(@PathVariable Long eleveId) {
        List<TutoratDtoResponse> tutorats = tutoratService.getTutoratsByEleve(eleveId);
        APIResponse<List<TutoratDtoResponse>> response = new APIResponse<>(
                "success",
                HttpStatus.OK.value(),
                "Tutorats de l'élève récupérés",
                tutorats
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-tuteur/{tuteurId}")
    public ResponseEntity<APIResponse<List<TutoratDtoResponse>>> getTutoratsByTuteur(@PathVariable Long tuteurId) {
        List<TutoratDtoResponse> tutorats = tutoratService.getTutoratsByTuteur(tuteurId);
        APIResponse<List<TutoratDtoResponse>> response = new APIResponse<>(
                "success",
                HttpStatus.OK.value(),
                "Tutorats du tuteur récupérés",
                tutorats
        );
        return ResponseEntity.ok(response);
    }
}