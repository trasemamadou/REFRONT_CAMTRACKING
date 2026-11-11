package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoRequest;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationService;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoRequest;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoResponse;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierService;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageService;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoRequest;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/students")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class EleveController {

    private final EleveService studentService;
    private final ApprentissageService apprentissageService;
    @Value("${student.profile.dir}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<EleveDtoResponse>> getAllStudent() {
        return studentService.getAllStudents()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping(path = "/search")
    public ResponseEntity<APIResponse<?>> getAllElevesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EleveDtoResponse> result = studentService.getAllElevePaged(pageable).get();

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
                "Élèves trouvés avec succès",
                data
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(path = "/classe/{classeId}")
    public ResponseEntity<APIResponse<?>> getAllElevesPagedByClasseId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String classeId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Optional<Page<EleveDtoResponse>> result = apprentissageService.getStudentsByClasseIdPaged(classeId, pageable);

        if (result.isEmpty() || result.get().isEmpty()) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucun élève n'a été trouvé pour cette classe",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        Page<EleveDtoResponse> pageResult = result.get();
        Map<Object, Object> data = new HashMap<>();
        data.put("content", pageResult.getContent());
        data.put("totalElements", pageResult.getTotalElements());
        data.put("totalPages", pageResult.getTotalPages());

        APIResponse<?> response = new APIResponse<>(
                "success",
                200,
                "Élèves trouvés avec succès",
                data
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterStudents(
            @RequestBody @Valid List<FilterRequest> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "true") boolean sortAsc) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            SorterRequest sorter = null;
            if (sortField != null && !sortField.isEmpty()) {
                sorter = new SorterRequest();
                sorter.setChamp(sortField);
                sorter.setAsc(sortAsc);
            }


            Page<EleveDtoResponse> result = studentService.filterStudents(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucun élève ne correspond aux critères de recherche",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("content", result.getContent());
            data.put("totalElements", result.getTotalElements());
            data.put("totalPages", result.getTotalPages());
            data.put("currentPage", result.getNumber());
            data.put("pageSize", result.getSize());

            APIResponse<?> response = new APIResponse<>(
                    "success",
                    200,
                    "Élèves trouvés avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des étudiants", e);
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    400,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<EleveDtoResponse> getStudentById(@PathVariable String id) {
        Optional<EleveDtoResponse> student = studentService.getStudentById(id);
        if (student.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return student.map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<?>> createStudent(
            @RequestBody @Valid RegistrationWraperRequest registrationWrapper) {

        try {
            RegistrationWraperResponse wrapperResponse = studentService.registerStudent(registrationWrapper);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new APIResponse<>(
                            "success",
                            201,
                            "Étudiant, dossier et facturation créés avec succès",
                            wrapperResponse
                    ));
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription de l'étudiant", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(
                            "error",
                            400,
                            "Erreur lors de l'inscription: " + e.getMessage(),
                            null
                    ));
        }
    }

    @PostMapping(path = "/tutorat")
    public ResponseEntity<TutoratDtoResponse> saveTutorat(
            @RequestBody TutoratDtoRequest request) {

        TutoratDtoResponse response = studentService.saveTutorat(request).get();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile/{filename:.+}")
    public ResponseEntity<UrlResource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            UrlResource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'image", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EleveDtoResponse> updateStudent(@PathVariable String id, @RequestBody EleveDtoRequest request) {

        return studentService.updateStudent(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("L'étudiant n'existe pas"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build();
    }
}