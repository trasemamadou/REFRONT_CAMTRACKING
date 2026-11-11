package com.groupeisi.ms1_gestschool.templates;

import org.springframework.core.io.Resource;
import java.util.List;
import java.util.Map;

public interface TemplateService {

    // Génération de documents
    Resource generateDocument(DocumentType type, DocumentFormat format, Map<String, Object> data);
    byte[] generateDocumentAsBytes(DocumentType type, DocumentFormat format, Map<String, Object> data);
    String generateDocumentAsBase64(DocumentType type, DocumentFormat format, Map<String, Object> data);
    String parseTemplateContent(String templateCode, Map<String, Object> data);

    // CRUD Templates (si vous utilisez JPA)
    TemplateDtoResponse createTemplate(TemplateDtoRequest request);
    TemplateDtoResponse getTemplateById(Long id);
    TemplateDtoResponse getTemplateByCode(String code);
    List<TemplateDtoResponse> getAllTemplates();
    List<TemplateDtoResponse> getActiveTemplates();
    List<TemplateDtoResponse> getTemplatesByType(TemplateType type);
    TemplateDtoResponse updateTemplate(Long id, TemplateDtoRequest request);
    void deleteTemplate(Long id);
    boolean existsByCode(String code);

    // Données de test
    Map<String, Object> createSampleInvoiceData();
    Map<String, Object> createSampleGradesData();
    Map<String, Object> createSampleSalaryData();
}