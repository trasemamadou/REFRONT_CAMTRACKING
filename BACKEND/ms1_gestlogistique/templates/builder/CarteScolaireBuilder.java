// CarteScolaireBuilder.java
package com.groupeisi.ms1_gestschool.templates.builder;

import com.groupeisi.ms1_gestschool.templates.DocumentType;
import com.groupeisi.ms1_gestschool.templates.FileNormalizer;
import com.groupeisi.ms1_gestschool.templates.ThymeleafTemplateService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarteScolaireBuilder implements HtmlDocumentBuilder {

    private final ThymeleafTemplateService templateService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int QR_CODE_SIZE = 150;

    @Override
    public DocumentType getType() {
        return DocumentType.CARTE_SCOLAIRE;
    }

    @Override
    public String build(Map<String, Object> data) {
        validateData(data);

        // Vérifier si c'est une génération multiple
        if (data.containsKey("eleves") && data.get("eleves") instanceof List) {
            return buildMultipleCartes(data);
        }

        // Génération simple
        return buildSingleCarte(data);
    }

    /**
     * Génère une carte scolaire individuelle
     */
    private String buildSingleCarte(Map<String, Object> data) {
        Map<String, Object> eleve = (Map<String, Object>) data.getOrDefault("eleve", new HashMap<>());

        // Générer le QR code
        String qrCodeBase64 = generateQrCode(eleve);

        // Traiter la photo
        String photoBase64 = processPhoto(eleve);

        // Préparer les données pour le template
        Map<String, Object> templateData = new HashMap<>(data);
        templateData.put("qrCodeBase64", qrCodeBase64);
        templateData.put("photoBase64", photoBase64);
        templateData.put("eleve", eleve);

        // Générer le HTML via Thymeleaf
        return templateService.generateCarteScolaireHtml(templateData);
    }

    /**
     * Génère plusieurs cartes scolaires
     */
    private String buildMultipleCartes(Map<String, Object> data) {
        List<Map<String, Object>> eleves = (List<Map<String, Object>>) data.get("eleves");
        List<Map<String, Object>> cartes = new ArrayList<>();

        for (Map<String, Object> eleve : eleves) {
            Map<String, Object> carteData = new HashMap<>();

            // Générer le QR code pour chaque élève
            String qrCodeBase64 = generateQrCode(eleve);

            // Traiter la photo
            String photoBase64 = processPhoto(eleve);

            carteData.put("eleve", eleve);
            carteData.put("qrCodeBase64", qrCodeBase64);
            carteData.put("photoBase64", photoBase64);

            cartes.add(carteData);
        }

        // Préparer les données pour le template multiple
        Map<String, Object> templateData = new HashMap<>(data);
        templateData.remove("eleves");

        // Générer le HTML via Thymeleaf
        return templateService.generateMultipleCartesHtml(cartes, templateData);
    }

    /**
     * Valide les données d'entrée
     */
    private void validateData(Map<String, Object> data) {
        // Vérifier si c'est une génération multiple
        if (data.containsKey("eleves")) {
            List<Map<String, Object>> eleves = (List<Map<String, Object>>) data.get("eleves");
            if (eleves == null || eleves.isEmpty()) {
                throw new IllegalArgumentException("La liste des élèves ne peut pas être vide");
            }
            return;
        }

        // Vérifier pour une génération simple
        Map<String, Object> eleve = (Map<String, Object>) data.get("eleve");
        if (eleve == null) {
            throw new IllegalArgumentException("Les données de l'élève sont obligatoires");
        }

        String nom = (String) eleve.get("nom");
        String prenom = (String) eleve.get("prenom");

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'élève est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de l'élève est obligatoire");
        }
    }

    /**
     * Traite la photo de l'élève
     */
    private String processPhoto(Map<String, Object> eleve) {
        // Vérifier différents champs possibles pour la photo
        String photo = (String) eleve.get("photo");
        String photoUrl = (String) eleve.get("photoUrl");
        String photoBase64 = (String) eleve.get("photoBase64");

        if (photoBase64 != null && !photoBase64.isEmpty()) {
            return normalizePhoto(photoBase64);
        }

        if (photo != null && !photo.isEmpty()) {
            return normalizePhoto(photo);
        }

        if (photoUrl != null && !photoUrl.isEmpty()) {
            return normalizePhoto(photoUrl);
        }

        return "";
    }

    /**
     * Normalise la photo au format base64 avec préfixe data:
     */
    private String normalizePhoto(String photo) {
        if (photo == null || photo.isEmpty()) {
            return "";
        }

        // Si c'est déjà une URL ou data:image, on la normalise via FileNormalizer
        return FileNormalizer.normalize(photo);
    }

    /**
     * Génère le QR code pour un élève
     */
    private String generateQrCode(Map<String, Object> eleve) {
        try {
            Object idObj = eleve.get("id");
            if (idObj == null) {
                log.warn("ID de l'élève non trouvé pour génération QR code");
                return "";
            }

            String verificationUrl = buildVerificationUrl(idObj);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(verificationUrl, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String qrBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + qrBase64;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Construit l'URL de vérification
     */
    private String buildVerificationUrl(Object id) {
        return baseUrl + "/api/v1/verify/carte/" + id.toString();
    }
}