package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseService;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveService;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageService;
import com.groupeisi.ms1_gestschool.scolarite.enseignement.EnseignementDtoResponse;
import com.groupeisi.ms1_gestschool.scolarite.enseignement.EnseignementService;
import com.groupeisi.ms1_gestschool.templates.DocumentFormat;
import com.groupeisi.ms1_gestschool.templates.DocumentType;
import com.groupeisi.ms1_gestschool.templates.TemplateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;

import java.util.*;

@RestController
@RequestMapping("/api/classes")
@AllArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ClasseController {
    @Autowired
    private ClasseService classeService;
    @Autowired
    private EnseignementService enseignementService;
    @Autowired
    private ParametrageclasseService parametrageService;
    @Autowired
    private ApprentissageService apprentissageService;
    @Autowired
    private EleveService eleveService;
    @Autowired
    private TemplateService templateService;
    @GetMapping
    public ResponseEntity<Optional<List<ClasseDtoResponse>>> getAllClasses(){
        return ResponseEntity.ok(classeService.getAllClasses());
    }
    @GetMapping(path = "/search")
    public ResponseEntity<APIResponse<?>> getAllClassesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<ClasseDtoResponse> result = classeService.getAllClassesPaged(pageable).get() ;
        if (result.isEmpty()){
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    404,
                    "Aucune classe n'a ete trouvee",
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
                "Classe trouvee avec success",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterClasses(
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

            Page<ClasseDtoResponse> result = classeService.filterClasses(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucune classe ne correspond aux critères de recherche",
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
                    "Classes trouvées avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
         //   log.error("Erreur lors du filtrage des classes", e);
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
    public ResponseEntity<Optional<ClasseDtoResponse>> getClasseById(@PathVariable String id){
        return ResponseEntity.ok(classeService.getClasseById(id));
    }
    @GetMapping("/parametrages/{id}")
    public ResponseEntity<Optional<List<ParametrageclasseDtoResponse>>> getParamClasseById(@PathVariable String id){
        Optional<List<ParametrageclasseDtoResponse>> result = Optional.of(parametrageService.getByClasseId(id));
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<Optional<ClasseDtoResponse>> addClasse(@RequestBody @Valid ClasseDtoRequest classeDtoRequest){
        return ResponseEntity.ok(classeService.addClasse(classeDtoRequest));
    }
    @PutMapping("/{id}")
        public ResponseEntity<Optional<ClasseDtoResponse>> updateClasse(@PathVariable String id, @RequestBody ClasseDtoRequest classeDtoRequest){
        return ResponseEntity.ok(classeService.updateClasse(id, classeDtoRequest));
    }
    @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteClasse(@PathVariable String id){
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }
        @GetMapping("enseignements/{idClasse}")
        public ResponseEntity<List<EnseignementDtoResponse>> getEnseignementsByClasseId(@PathVariable String idClasse) {
            return ResponseEntity.ok(enseignementService.getAllByClasseId(idClasse));
        }
        // POUR LA GESTION DES CARTES SCOLAIRES //
        @GetMapping("/{id}/cartes-scolaires/pdf")
        public ResponseEntity<Resource> generateCartesScolaires(@PathVariable String id) {
            // Récupérer la classe
            ClasseDtoResponse classe = classeService.getClasseById(id)
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

            // Récupérer les élèves de la classe via apprentissage
            List<EleveDtoResponse> eleves = apprentissageService.getStudentsByClasseId(id).get();

            if (eleves.isEmpty()) {
                throw new RuntimeException("Aucun élève trouvé pour cette classe");
            }

            // Construire les données pour le template
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> elevesData = new ArrayList<>();

            for (EleveDtoResponse eleve : eleves) {
                Map<String, Object> eleveData = new HashMap<>();
                eleveData.put("id", eleve.getId());
                eleveData.put("matricule", eleve.getMatricule());
                eleveData.put("nom", eleve.getNom());
                eleveData.put("prenom", eleve.getPrenom());
                eleveData.put("classe", classe.getNom());
                eleveData.put("anneeScolaire", classe.getAnneeScolaire());
                eleveData.put("photoUrl", eleve.getPhotoProfil());
                elevesData.add(eleveData);
            }

            data.put("eleves", elevesData);
            data.put("classe", classe.getNom());
            data.put("schoolName", "ÉCOLE INTERNATIONALE");
            data.put("cardType", "CARTE SCOLAIRE");
            data.put("anneeScolaire", classe.getAnneeScolaire());

            // Générer les cartes
            Resource document = templateService.generateDocument(
                    DocumentType.CARTE_SCOLAIRE,
                    DocumentFormat.PDF,
                    data
            );

            String filename = "cartes_classe_" + classe.getNom() + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(document);
        }

    /**
     * Génère une carte scolaire individuelle pour un élève
     */
    @GetMapping("/{classeId}/eleve/{eleveId}/carte-pdf")
    public ResponseEntity<Resource> generateCarteScolaire(
            @PathVariable String classeId,
            @PathVariable String eleveId) {

        // Récupérer la classe
        ClasseDtoResponse classe = classeService.getClasseById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

        // Récupérer l'élève
        EleveDtoResponse eleve = eleveService.getStudentById(eleveId).get();

        // Construire les données
        Map<String, Object> eleveData = new HashMap<>();
        eleveData.put("id", eleve.getId());
        eleveData.put("matricule", eleve.getMatricule());
        eleveData.put("nom", eleve.getNom());
        eleveData.put("prenom", eleve.getPrenom());
        eleveData.put("classe", classe.getNom());
        eleveData.put("anneeScolaire", classe.getAnneeScolaire());
        eleveData.put("photoUrl", eleve.getPhotoProfil());

        Map<String, Object> data = new HashMap<>();
        data.put("eleve", eleveData);
        data.put("schoolName", "ÉCOLE INTERNATIONALE");
        data.put("cardType", "CARTE SCOLAIRE");

        // Générer la carte
        Resource document = templateService.generateDocument(
                DocumentType.CARTE_SCOLAIRE,
                DocumentFormat.PDF,
                data
        );

        String filename = "carte_" + eleve.getMatricule() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\"")
                .body(document);
    }

}
