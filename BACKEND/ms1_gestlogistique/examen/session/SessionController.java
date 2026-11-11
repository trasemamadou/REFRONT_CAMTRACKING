package com.groupeisi.ms1_gestschool.examen.session;

import com.groupeisi.ms1_gestschool.config.global.APIResponse;
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
@RequestMapping("/api/compositions")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @PostMapping
    public SessionDtoResponse create(@RequestBody SessionDtoRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SessionDtoResponse update(@PathVariable String id,
                                         @RequestBody SessionDtoRequest request) {
        return service.update(id, request);
    }

    @GetMapping("/{id}")
    public SessionDtoResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping
    public List<SessionDtoResponse> findAll() {
        return service.findAll();
    }
    @GetMapping(path = "/")
    public ResponseEntity<APIResponse<?>> getAllCompostions() {

        List<SessionDtoResponse> result = service.findAll();

        if (result.isEmpty()) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun élève n'a été trouvé",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        APIResponse<?> response = new APIResponse<>(
                "success",
                200,
                "Compositions trouvés avec succès",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(path = "/search")
    public ResponseEntity<APIResponse<?>> getAllCompostionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SessionDtoResponse> result = service.getAllCompostionPaged(pageable);

        if (result.isEmpty()) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun élève n'a été trouvé",
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
                "Compositions trouvés avec succès",
                data
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
