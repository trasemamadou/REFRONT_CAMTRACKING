package com.groupeisi.ms1_gestschool.scolarite.annee;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annees")
@RequiredArgsConstructor
public class AnneeController {

    private final AnneeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnneeDtoResponse create(@RequestBody AnneeDtoRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<AnneeDtoResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AnneeDtoResponse getOne(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new RuntimeException("Année introuvable"));
    }

    @PutMapping("/{id}")
    public AnneeDtoResponse update(@PathVariable String id, @RequestBody AnneeDtoRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}