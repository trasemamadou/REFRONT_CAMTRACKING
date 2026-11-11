package com.groupeisi.ms1_gestschool.bibliotheques;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentNumeriqueController {

    private final DocumentService documentService;
    private final DocumentFileStorageService documentFileStorageService;
    private final ObjectMapper objectMapper;

    @GetMapping("/check-hash")
    public ResponseEntity<Map<String, Boolean>> checkDocumentExists(@RequestParam String hash) {
        boolean exists = documentService.checkDocumentExistsByHash(hash);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDocumentsStats() {
        Map<String, Object> stats = documentService.getDocumentsStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<DocumentDtoResponse> createDocument(@Valid @RequestBody DocumentDtoRequest request) {
        DocumentDtoResponse response = documentService.createDocument(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentDtoResponse>> getAllDocuments() {
        List<DocumentDtoResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDtoResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("metadata") String metadataJson) {
        try {
            log.info("Réception d'un fichier: {}", file.getOriginalFilename());
            DocumentDtoRequest metadata = objectMapper.readValue(metadataJson, DocumentDtoRequest.class);
            DocumentDtoResponse response = documentService.uploadDocument(file, metadata);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload du document", e);
            throw new RuntimeException("Erreur lors de l'upload: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentDtoResponse>> uploadMultipleDocuments(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("metadata") String metadataJson) {
        try {
            log.info("Réception de {} fichiers", files.size());
            DocumentDtoRequest baseMetadata = objectMapper.readValue(metadataJson, DocumentDtoRequest.class);
            List<DocumentDtoResponse> responses = documentService.uploadMultipleDocuments(files, baseMetadata);
            return new ResponseEntity<>(responses, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload multiple", e);
            throw new RuntimeException("Erreur lors de l'upload multiple: " + e.getMessage());
        }
    }

    @PostMapping("/upload-complete")
    public ResponseEntity<DocumentDtoResponse> uploadCompleteDocument(@RequestBody DocumentUploadRequest uploadRequest) {
        log.info("Réception d'un document complet en base64");
        DocumentDtoResponse response = documentService.uploadCompleteDocument(uploadRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDtoResponse>> searchDocuments(
            @RequestParam("q") String searchTerm,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Boolean archive) {
        List<DocumentDtoResponse> results = documentService.searchDocuments(searchTerm, type, categorie, classeId, archive);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/archive/{archive}")
    public ResponseEntity<List<DocumentDtoResponse>> getDocumentsByArchive(@PathVariable boolean archive) {
        List<DocumentDtoResponse> documents = documentService.getDocumentsByArchive(archive);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDtoResponse> getDocumentById(@PathVariable Long id) {
        DocumentDtoResponse document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/hash/{hash}")
    public ResponseEntity<DocumentDtoResponse> getDocumentByHash(@PathVariable String hash) {
        DocumentDtoResponse document = documentService.getDocumentByHash(hash);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDtoResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentDtoRequest request) {
        DocumentDtoResponse updated = documentService.updateDocument(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/increment-download")
    public ResponseEntity<Void> incrementDownloadCount(@PathVariable Long id) {
        documentService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            log.info("Tentative de téléchargement du document ID: {}", id);

            DocumentEntity document = documentService.getDocumentEntityById(id);

            if (document == null) {
                log.error("Document non trouvé avec l'ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            if (document.getFilePath() == null || document.getFilePath().isEmpty()) {
                log.error("Aucun fichier associé au document ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            log.info("Fichier trouvé au chemin: {}", document.getFilePath());

            byte[] fileContent = documentFileStorageService.downloadDocumentByFilePath(document.getFilePath());

            documentService.incrementDownloadCount(id);

            String contentType = document.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = documentFileStorageService.getContentType(document.getFileExtension());
            }

            String filename = document.getLibelle();
            if (document.getFileExtension() != null && !filename.endsWith(document.getFileExtension())) {
                filename += document.getFileExtension();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du document {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hash/{hash}/download")
    public ResponseEntity<byte[]> downloadDocumentByHash(@PathVariable String hash) {
        try {
            log.info("Tentative de téléchargement du document avec hash: {}", hash);

            DocumentEntity document = documentService.getDocumentEntityByHash(hash);

            if (document == null) {
                log.error("Document non trouvé avec le hash: {}", hash);
                return ResponseEntity.notFound().build();
            }

            if (document.getFilePath() == null || document.getFilePath().isEmpty()) {
                log.error("Aucun fichier associé au document avec hash: {}", hash);
                return ResponseEntity.badRequest().build();
            }

            log.info("Fichier trouvé au chemin: {}", document.getFilePath());

            byte[] fileContent = documentFileStorageService.downloadDocumentByFilePath(document.getFilePath());

            documentService.incrementDownloadCount(document.getId());

            String contentType = document.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = documentFileStorageService.getContentType(document.getFileExtension());
            }

            String filename = document.getLibelle();
            if (document.getFileExtension() != null && !filename.endsWith(document.getFileExtension())) {
                filename += document.getFileExtension();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du document avec hash {}", hash, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}