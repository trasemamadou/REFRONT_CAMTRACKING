package com.groupeisi.ms1_gestschool.scolarite.niveau;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
@RequiredArgsConstructor
public class NiveauController {

    private final NiveauService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NiveauDtoResponse create(@RequestBody NiveauDtoRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<NiveauDtoResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public NiveauDtoResponse getOne(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau introuvable"));
    }

    @PutMapping("/{id}")
    public NiveauDtoResponse update(@PathVariable String id, @RequestBody NiveauDtoRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}