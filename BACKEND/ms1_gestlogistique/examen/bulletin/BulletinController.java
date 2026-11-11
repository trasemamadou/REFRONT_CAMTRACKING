package com.groupeisi.ms1_gestschool.examen.bulletin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bulletin")
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinService bulletinService;

    @PostMapping
    public ResponseEntity<BulletinDtoResponse> createBulletin(@RequestBody BulletinDtoRequest request) {
        BulletinDtoResponse response = bulletinService.createBulletin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BulletinDtoResponse> getBulletin(@PathVariable String id) {
        BulletinDtoResponse response = bulletinService.getBulletinById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BulletinDtoResponse>> getAllBulletins() {
        List<BulletinDtoResponse> responses = bulletinService.getAllBulletins();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BulletinDtoResponse> updateBulletin(
            @PathVariable String id,
            @RequestBody BulletinDtoRequest request) {
        BulletinDtoResponse response = bulletinService.updateBulletin(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBulletin(@PathVariable String id) {
        bulletinService.deleteBulletin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eleve/{eleveId}")
    public ResponseEntity<List<BulletinDtoResponse>> getBulletinsByEleve(@PathVariable String eleveId) {
        List<BulletinDtoResponse> responses = bulletinService.getBulletinsByEleve(eleveId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/classe/{classe}")
    public ResponseEntity<List<BulletinDtoResponse>> getBulletinsByClasse(@PathVariable String sessionclasseId) {
        List<BulletinDtoResponse> responses = bulletinService.getBulletinsByClasseId(sessionclasseId);
        return ResponseEntity.ok(responses);
    }
}