package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apprentissages")
@CrossOrigin(origins = "*")
public class ApprentissageController {

    private final ApprentissageService enseignementService;

    @PostMapping
    public ResponseEntity<ApprentissageDtoResponse> create(@RequestBody ApprentissageDtoRequest request) {
        return enseignementService.save(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApprentissageDtoResponse> update(@PathVariable String id,
                                                          @RequestBody ApprentissageDtoRequest request) {
        return enseignementService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprentissageDtoResponse> getById(@PathVariable String id) {
        return enseignementService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ApprentissageDtoResponse>> getAll() {
        return ResponseEntity.ok(enseignementService.getAll());
    }
    @GetMapping(path = "/search")
    public ResponseEntity<APIResponse<?>> getAllApprentissagesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") String idClasse
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<ApprentissageDtoResponse> result =   enseignementService.getAllByClasseIdPaged(idClasse, pageable);
        if (result.isEmpty()){
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun apprentissage n'a ete trouvee",
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
                "Apprentissages trouvee avec success",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return enseignementService.delete(id)
                .filter(Boolean::booleanValue)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
