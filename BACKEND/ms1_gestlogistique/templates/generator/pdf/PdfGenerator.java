// PdfGenerator.java - Optimiser pour les cartes scolaires
package com.groupeisi.ms1_gestschool.templates.generator;

import com.groupeisi.ms1_gestschool.templates.DocumentFormat;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Slf4j
@Component
public class PdfGenerator implements DocumentGenerator {

    private static final float CARD_WIDTH_MM = 85.6f;
    private static final float CARD_HEIGHT_MM = 54.0f;
    private static final float MM_TO_POINTS = 2.834645669291339f;

    @Override
    public byte[] generate(String htmlContent, Map<String, Object> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Vérifier si c'est une carte scolaire pour ajuster la taille
            boolean isCarteScolaire = isCarteScolaire(data);

            if (isCarteScolaire) {
                // Générer avec une taille de page personnalisée
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDoc = new PdfDocument(writer);

                // Définir la taille de la page en points
                float widthPoints = CARD_WIDTH_MM * MM_TO_POINTS;
                float heightPoints = CARD_HEIGHT_MM * MM_TO_POINTS;
                PageSize customSize = new PageSize(widthPoints, heightPoints);
                pdfDoc.setDefaultPageSize(customSize);

                // Convertir HTML en PDF
                HtmlConverter.convertToPdf(htmlContent, pdfDoc.getWriter());
                pdfDoc.close();
            } else {
                // Génération normale
                HtmlConverter.convertToPdf(htmlContent, baos);
            }

            byte[] result = baos.toByteArray();
            if (result.length == 0) {
                throw new RuntimeException("Le PDF généré est vide");
            }

            return result;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

    private boolean isCarteScolaire(Map<String, Object> data) {
        if (data == null) return false;
        // Vérifier si les données contiennent les caractéristiques d'une carte scolaire
        return data.containsKey("eleve") ||
                (data.containsKey("carte") && data.get("carte") != null);
    }

    @Override
    public DocumentFormat getFormat() {
        return DocumentFormat.PDF;
    }
}