package com.groupeisi.ms1_gestschool.bibliotheques;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentFileStorageService {

    @Value("${document.storage.path:./uploads/documents}")
    private String storagePath;

    private String makeUrlSafeHash(String hash) {
        if (hash == null || hash.isEmpty()) {
            return "unknown";
        }
        return hash.replace('+', '-')
                .replace('/', '_')
                .replace('=', '_')
                .replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    public String getContentType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "application/octet-stream";
        }

        switch (extension.toLowerCase()) {
            case ".pdf":
                return "application/pdf";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    public FileSaveResult saveFile(MultipartFile file, String hash) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Dossier créé: {}", uploadPath.toAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String contentType = file.getContentType();

        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentType(extension);
        }

        String safeHash = makeUrlSafeHash(hash);
        String cleanFilename = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "unknown";

        String filename = safeHash + "_" + System.currentTimeMillis() + "_" + cleanFilename;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        log.info("Fichier sauvegardé: {} - Extension: {} - Type MIME: {}",
                filePath.toAbsolutePath(), extension, contentType);

        return FileSaveResult.builder()
                .filePath(filePath.toString())
                .extension(extension)
                .contentType(contentType)
                .build();
    }

    public FileSaveResult saveFileFromBase64(String base64Content, String hash, String fileName) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Dossier créé: {}", uploadPath.toAbsolutePath());
        }

        String extension = getFileExtension(fileName);
        String contentType = getContentType(extension);

        String safeHash = makeUrlSafeHash(hash);
        String cleanFileName = fileName != null ? fileName.replaceAll("[^a-zA-Z0-9.-]", "_") : "unknown";

        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        String filename = safeHash + "_" + System.currentTimeMillis() + "_" + cleanFileName;
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, decodedBytes);

        log.info("Fichier sauvegardé à partir du base64: {} - Extension: {} - Type MIME: {}",
                filePath.toAbsolutePath(), extension, contentType);

        return FileSaveResult.builder()
                .filePath(filePath.toString())
                .extension(extension)
                .contentType(contentType)
                .build();
    }

    public byte[] downloadDocumentByFilePath(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                log.error("Fichier non trouvé: {}", filePath);
                throw new RuntimeException("Fichier non trouvé: " + filePath);
            }
            byte[] content = Files.readAllBytes(path);
            log.info("Fichier chargé avec succès: {} ({} bytes)", filePath, content.length);
            return content;
        } catch (IOException e) {
            log.error("Erreur lors du téléchargement du fichier {}", filePath, e);
            throw new RuntimeException("Erreur lors du téléchargement: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("Fichier supprimé: {}", filePath);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier {}", filePath, e);
        }
    }
}