package com.groupeisi.ms1_gestschool.bibliotheques;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentFileStorageService fileStorageService;

    @Transactional
    public DocumentDtoResponse createDocument(DocumentDtoRequest request) {
        if (documentRepository.existsByHash(request.getHash())) {
            log.warn("Tentative de création d'un document avec un hash existant: {}", request.getHash());
            throw new RuntimeException("Un document avec ce hash existe déjà. Le document est probablement un doublon.");
        }

        DocumentEntity entity = documentMapper.toEntity(request);
        DocumentEntity saved = documentRepository.save(entity);
        log.info("Document créé avec succès avec le hash: {}", saved.getHash());
        return documentMapper.toDto(saved);
    }

    @Transactional
    public DocumentDtoResponse uploadDocument(MultipartFile file, DocumentDtoRequest metadata) {
        try {
            String hash = calculateFileHash(file);

            if (documentRepository.existsByHash(hash)) {
                throw new RuntimeException("Un document avec ce contenu existe déjà");
            }

            FileSaveResult saveResult = fileStorageService.saveFile(file, hash);

            metadata.setHash(hash);
            metadata.setDatePublication(LocalDate.now());
            metadata.setFileType(saveResult.getContentType());
            metadata.setFileExtension(saveResult.getExtension());

            DocumentEntity entity = documentMapper.toEntity(metadata);
            entity.setFilePath(saveResult.getFilePath());

            DocumentEntity saved = documentRepository.save(entity);
            log.info("Document uploadé avec succès: {} - Fichier sauvegardé à: {} - Type: {}",
                    saved.getId(), saveResult.getFilePath(), saveResult.getContentType());

            return documentMapper.toDto(saved);
        } catch (IOException e) {
            log.error("Erreur lors de l'upload du document", e);
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }

    @Transactional
    public List<DocumentDtoResponse> uploadMultipleDocuments(List<MultipartFile> files, DocumentDtoRequest baseMetadata) {
        return files.stream()
                .map(file -> uploadDocument(file, baseMetadata))
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentDtoResponse uploadCompleteDocument(DocumentUploadRequest uploadRequest) {
        try {
            if (documentRepository.existsByHash(uploadRequest.getHash())) {
                throw new RuntimeException("Un document avec ce contenu existe déjà");
            }

            FileSaveResult saveResult = fileStorageService.saveFileFromBase64(
                    uploadRequest.getFileContent(),
                    uploadRequest.getHash(),
                    uploadRequest.getFileName()
            );

            DocumentDtoRequest metadata = DocumentDtoRequest.builder()
                    .libelle(uploadRequest.getLibelle())
                    .description(uploadRequest.getDescription())
                    .datePublication(uploadRequest.getDatePublication())
                    .hash(uploadRequest.getHash())
                    .archive(uploadRequest.isArchive())
                    .fileType(saveResult.getContentType())
                    .fileExtension(saveResult.getExtension())
                    .build();

            DocumentEntity entity = documentMapper.toEntity(metadata);
            entity.setFilePath(saveResult.getFilePath());

            DocumentEntity saved = documentRepository.save(entity);
            log.info("Document uploadé avec succès depuis base64: {} - Fichier sauvegardé à: {} - Type: {}",
                    saved.getId(), saveResult.getFilePath(), saveResult.getContentType());

            return documentMapper.toDto(saved);
        } catch (IOException e) {
            log.error("Erreur lors de l'upload du document", e);
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentDtoResponse> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentDtoResponse getDocumentById(Long id) {
        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'id: " + id));
        return documentMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public DocumentEntity getDocumentEntityById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'id: " + id));
    }

    @Transactional(readOnly = true)
    public DocumentDtoResponse getDocumentByHash(String hash) {
        DocumentEntity entity = documentRepository.findByHash(hash)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec le hash: " + hash));
        return documentMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public DocumentEntity getDocumentEntityByHash(String hash) {
        return documentRepository.findByHash(hash)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec le hash: " + hash));
    }

    @Transactional
    public DocumentDtoResponse updateDocument(Long id, DocumentDtoRequest request) {
        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'id: " + id));

        if (!entity.getHash().equals(request.getHash()) && documentRepository.existsByHash(request.getHash())) {
            throw new RuntimeException("Un autre document avec ce hash existe déjà.");
        }

        documentMapper.updateEntity(request, entity);
        DocumentEntity updated = documentRepository.save(entity);
        log.info("Document mis à jour avec succès: {}", updated.getId());
        return documentMapper.toDto(updated);
    }

    @Transactional
    public void deleteDocument(Long id) {
        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'id: " + id));

        if (entity.getFilePath() != null && !entity.getFilePath().isEmpty()) {
            fileStorageService.deleteFile(entity.getFilePath());
        }

        documentRepository.deleteById(id);
        log.info("Document supprimé avec succès: {}", id);
    }

    @Transactional(readOnly = true)
    public List<DocumentDtoResponse> getDocumentsByArchive(boolean archive) {
        return documentRepository.findByArchive(archive)
                .stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean checkDocumentExistsByHash(String hash) {
        return documentRepository.existsByHash(hash);
    }

    @Transactional
    public void incrementDownloadCount(Long id) {
        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'id: " + id));
        documentRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDocumentsStats() {
        long totalDocuments = documentRepository.count();
        long archivedDocuments = documentRepository.findByArchive(true).size();
        long activeDocuments = totalDocuments - archivedDocuments;

        return Map.of(
                "total", totalDocuments,
                "archived", archivedDocuments,
                "active", activeDocuments
        );
    }

    @Transactional(readOnly = true)
    public List<DocumentDtoResponse> searchDocuments(String searchTerm, String type, String categorie, Long classeId, Boolean archive) {
        return documentRepository.findByLibelleContainingIgnoreCase(searchTerm)
                .stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    private String calculateFileHash(MultipartFile file) throws IOException {
        try {
            byte[] fileBytes = file.getBytes();
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);
            return java.util.Base64.getEncoder().encodeToString(hashBytes);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du calcul du hash SHA-256", e);
        }
    }
}