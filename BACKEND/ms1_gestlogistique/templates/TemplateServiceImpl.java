package com.groupeisi.ms1_gestschool.templates;

import com.groupeisi.ms1_gestschool.templates.builder.HtmlDocumentBuilderFactory;
import com.groupeisi.ms1_gestschool.templates.generator.DocumentGeneratorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final HtmlDocumentBuilderFactory builderFactory;
    private final DocumentGeneratorFactory generatorFactory;
    private final TemplateRepository templateRepository; // Si vous utilisez JPA

    @Override
    public Resource generateDocument(DocumentType type, DocumentFormat format, Map<String, Object> data) {
        try {
            // Validation des entrées
            if (type == null) {
                throw new IllegalArgumentException("Le type de document ne peut pas être null");
            }
            if (format == null) {
                throw new IllegalArgumentException("Le format de document ne peut pas être null");
            }
            if (data == null) {
                data = new HashMap<>();
            }

            // Enrichir les données avec des informations par défaut
            Map<String, Object> enrichedData = enrichData(data);

            // Générer le contenu HTML à partir du builder approprié
            String html = builderFactory.getBuilder(type).build(enrichedData);

            if (html == null || html.isEmpty()) {
                throw new IllegalStateException("Le contenu HTML généré est vide");
            }

            // Générer le document final (PDF, Excel, etc.)
            byte[] document = generatorFactory.getGenerator(format).generate(html, enrichedData);

            if (document == null || document.length == 0) {
                throw new IllegalStateException("Le document généré est vide");
            }

            // Générer un nom de fichier
            String filename = generateFilename(type, enrichedData, format);

            log.info("Document généré avec succès - Type: {}, Format: {}, Fichier: {}",
                    type, format, filename);

            return new ByteArrayResource(document) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Erreur lors de la génération du document: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la génération du document", e);
            throw new RuntimeException("Erreur lors de la génération du document: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateDocumentAsBytes(DocumentType type, DocumentFormat format, Map<String, Object> data) {
        Resource resource = generateDocument(type, format, data);
        try {
            return resource.getContentAsByteArray();
        } catch (Exception e) {
            log.error("Erreur lors de la conversion du document en bytes", e);
            throw new RuntimeException("Erreur lors de la conversion du document", e);
        }
    }

    @Override
    public String generateDocumentAsBase64(DocumentType type, DocumentFormat format, Map<String, Object> data) {
        byte[] bytes = generateDocumentAsBytes(type, format, data);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public TemplateDtoResponse createTemplate(TemplateDtoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de création de template ne peut pas être null");
        }

        if (templateRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Un template avec le code " + request.getCode() + " existe déjà");
        }

        TemplateEntity template = TemplateEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .format(request.getFormat() != null ? request.getFormat() : request.getType().getDefaultFormat())
                .templateFile(request.getTemplateFile())
                .fileName(request.getFileName())
                .contentType(request.getContentType())
                .content(request.getContent())
                .fileExtension(request.getFileExtension() != null ?
                        request.getFileExtension() :
                        getFileExtensionFromFormat(request.getFormat(), request.getType()))
                .active(request.isActive())
                .build();

        TemplateEntity saved = templateRepository.save(template);
        log.info("Template créé avec succès - ID: {}, Code: {}", saved.getId(), saved.getCode());

        return mapToDto(saved);
    }

    @Override
    public TemplateDtoResponse getTemplateById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du template ne peut pas être null");
        }

        TemplateEntity template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouvé avec l'ID: " + id));

        return mapToDto(template);
    }

    @Override
    public TemplateDtoResponse getTemplateByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du template ne peut pas être vide");
        }

        TemplateEntity template = templateRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouvé avec le code: " + code));

        return mapToDto(template);
    }

    @Override
    public List<TemplateDtoResponse> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TemplateDtoResponse> getActiveTemplates() {
        return templateRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TemplateDtoResponse> getTemplatesByType(TemplateType type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de template ne peut pas être null");
        }

        return templateRepository.findByType(type).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public TemplateDtoResponse updateTemplate(Long id, TemplateDtoRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du template ne peut pas être null");
        }
        if (request == null) {
            throw new IllegalArgumentException("La requête de mise à jour ne peut pas être null");
        }

        TemplateEntity existing = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouvé avec l'ID: " + id));

        // Vérifier l'unicité du code si modifié
        if (!existing.getCode().equals(request.getCode()) &&
                templateRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Un template avec le code " + request.getCode() + " existe déjà");
        }

        // Mise à jour des champs
        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setType(request.getType());
        existing.setFormat(request.getFormat() != null ? request.getFormat() : request.getType().getDefaultFormat());

        if (request.getTemplateFile() != null) {
            existing.setTemplateFile(request.getTemplateFile());
        }
        if (request.getFileName() != null) {
            existing.setFileName(request.getFileName());
        }
        if (request.getContentType() != null) {
            existing.setContentType(request.getContentType());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }

        existing.setFileExtension(request.getFileExtension() != null ?
                request.getFileExtension() :
                getFileExtensionFromFormat(request.getFormat(), request.getType()));
        existing.setActive(request.isActive());

        TemplateEntity updated = templateRepository.save(existing);
        log.info("Template mis à jour avec succès - ID: {}", updated.getId());

        return mapToDto(updated);
    }

    @Override
    public void deleteTemplate(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du template ne peut pas être null");
        }

        if (!templateRepository.existsById(id)) {
            throw new IllegalArgumentException("Template non trouvé avec l'ID: " + id);
        }

        templateRepository.deleteById(id);
        log.info("Template supprimé avec succès - ID: {}", id);
    }

    @Override
    public boolean existsByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return templateRepository.existsByCode(code);
    }

    @Override
    public String parseTemplateContent(String templateCode, Map<String, Object> data) {
        TemplateEntity template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouvé: " + templateCode));

        if (!template.isActive()) {
            throw new IllegalStateException("Le template " + templateCode + " n'est pas actif");
        }

        Map<String, Object> enrichedData = enrichData(data);

        if (template.getContent() != null && !template.getContent().isEmpty()) {
            return parseTemplateString(template.getContent(), enrichedData);
        } else if (template.getTemplateFile() != null) {
            // Si le template est un fichier, on retourne son nom
            return "[Template fichier: " + template.getFileName() + "]";
        }

        return "";
    }

    // =============== MÉTHODES PRIVÉES ===============

    private Map<String, Object> enrichData(Map<String, Object> data) {
        Map<String, Object> enriched = new HashMap<>();

        if (data != null) {
            enriched.putAll(data);
        }

        // Ajouter des informations par défaut
        LocalDateTime now = LocalDateTime.now();
        enriched.putIfAbsent("generationDate", now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        enriched.putIfAbsent("generationDateTime", now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        enriched.putIfAbsent("generationTimestamp", System.currentTimeMillis());
        enriched.putIfAbsent("currentYear", String.valueOf(now.getYear()));

        return enriched;
    }

    private String generateFilename(DocumentType type, Map<String, Object> data, DocumentFormat format) {
        String extension = switch (format) {
            case PDF -> "pdf";
            case EXCEL -> "xlsx";
            case WORD -> "docx";
            case HTML -> "html";
            case TEXT -> "txt";
            case CSV -> "csv";
        };

        String prefix = type.name().toLowerCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String identifier = "";

        // Essayer d'extraire un identifiant des données
        if (data != null) {
            if (data.containsKey("numero") && data.get("numero") != null) {
                identifier = "_" + data.get("numero").toString().replaceAll("[^a-zA-Z0-9]", "");
            } else if (data.containsKey("id") && data.get("id") != null) {
                identifier = "_" + data.get("id").toString();
            } else if (data.containsKey("code") && data.get("code") != null) {
                identifier = "_" + data.get("code").toString().replaceAll("[^a-zA-Z0-9]", "");
            }
        }

        return String.format("%s%s_%s.%s", prefix, identifier, timestamp, extension);
    }

    private String parseTemplateString(String template, Map<String, Object> data) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        String result = template;

        // Remplacer les variables ${variable}
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace("${" + key + "}", value);
        }

        return result;
    }

    private String getFileExtensionFromFormat(DocumentFormat format, TemplateType type) {
        if (format != null) {
            return switch (format) {
                case PDF -> "pdf";
                case EXCEL -> "xlsx";
                case WORD -> "docx";
                case HTML -> "html";
                case TEXT -> "txt";
                case CSV -> "csv";
            };
        }
        return TemplateType.getDefaultExtension(type);
    }

    private TemplateDtoResponse mapToDto(TemplateEntity entity) {
        if (entity == null) return null;

        return TemplateDtoResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .content(entity.getContent())
                .format(entity.getFormat())
                .fileExtension(entity.getFileExtension())
                .fileName(entity.getFileName())
                .contentType(entity.getContentType())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // =============== DONNÉES DE TEST ===============

    @Override
    public Map<String, Object> createSampleInvoiceData() {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> client = new HashMap<>();
        client.put("name", "École Primaire Les Lilas");
        client.put("address", "123 Avenue des Écoles, 75000 Paris");
        client.put("phone", "01 23 45 67 89");
        client.put("email", "contact@ecole-lilas.fr");
        client.put("siret", "123 456 789 00012");

        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("description", "Fournitures scolaires");
        item1.put("quantity", 50);
        item1.put("unitPrice", 15.50);
        item1.put("total", 775.00);
        items.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("description", "Manuels scolaires");
        item2.put("quantity", 30);
        item2.put("unitPrice", 25.00);
        item2.put("total", 750.00);
        items.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("description", "Matériel informatique");
        item3.put("quantity", 5);
        item3.put("unitPrice", 350.00);
        item3.put("total", 1750.00);
        items.add(item3);

        double subTotal = items.stream()
                .mapToDouble(i -> (Double) i.get("total"))
                .sum();
        double taxRate = 20.0;
        double taxAmount = subTotal * taxRate / 100;
        double totalWithTax = subTotal + taxAmount;

        data.put("invoiceNumber", "FACT-2024-001");
        data.put("client", client);
        data.put("items", items);
        data.put("subTotal", subTotal);
        data.put("taxRate", taxRate);
        data.put("taxAmount", taxAmount);
        data.put("total", totalWithTax);
        data.put("amountInWords", "Deux mille deux cent soixante-quinze euros");

        return data;
    }

    @Override
    public Map<String, Object> createSampleGradesData() {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> student = new HashMap<>();
        student.put("firstName", "Jean");
        student.put("lastName", "Dupont");
        student.put("fullName", "Jean Dupont");
        student.put("birthDate", "15/03/2013");
        student.put("class", "CM2 A");
        student.put("year", "2023-2024");
        student.put("studentId", "20230045");
        data.put("student", student);

        Map<String, Object> school = new HashMap<>();
        school.put("name", "École Primaire Les Lilas");
        school.put("address", "123 Avenue des Écoles, 75000 Paris");
        school.put("phone", "01 23 45 67 89");
        school.put("academy", "Paris");
        data.put("school", school);

        List<Map<String, Object>> grades = new ArrayList<>();

        Map<String, Object> grade1 = new HashMap<>();
        grade1.put("subject", "Mathématiques");
        grade1.put("score", 16.5);
        grade1.put("coefficient", 4);
        grade1.put("maxScore", 20);
        grade1.put("appreciation", "Très bonne maîtrise des concepts");
        grades.add(grade1);

        Map<String, Object> grade2 = new HashMap<>();
        grade2.put("subject", "Français");
        grade2.put("score", 14.0);
        grade2.put("coefficient", 4);
        grade2.put("maxScore", 20);
        grade2.put("appreciation", "Bon travail, peut mieux faire en rédaction");
        grades.add(grade2);

        Map<String, Object> grade3 = new HashMap<>();
        grade3.put("subject", "Histoire-Géographie");
        grade3.put("score", 18.0);
        grade3.put("coefficient", 2);
        grade3.put("maxScore", 20);
        grade3.put("appreciation", "Excellent, très bonne participation");
        grades.add(grade3);

        Map<String, Object> grade4 = new HashMap<>();
        grade4.put("subject", "Sciences");
        grade4.put("score", 15.5);
        grade4.put("coefficient", 3);
        grade4.put("maxScore", 20);
        grade4.put("appreciation", "Très bien");
        grades.add(grade4);

        Map<String, Object> grade5 = new HashMap<>();
        grade5.put("subject", "Anglais");
        grade5.put("score", 17.0);
        grade5.put("coefficient", 2);
        grade5.put("maxScore", 20);
        grade5.put("appreciation", "Excellent en expression orale");
        grades.add(grade5);

        // Calcul des moyennes
        double totalPoints = 0;
        int totalCoeff = 0;
        for (Map<String, Object> g : grades) {
            double score = (Double) g.get("score");
            int coeff = (Integer) g.get("coefficient");
            totalPoints += score * coeff;
            totalCoeff += coeff;
        }
        double average = totalCoeff > 0 ? totalPoints / totalCoeff : 0;

        data.put("grades", grades);
        data.put("average", String.format("%.2f", average));
        data.put("classAverage", "14.5");
        data.put("minAverage", "12.0");
        data.put("maxAverage", "18.5");
        data.put("rank", "5ème sur 25");
        data.put("totalStudents", 25);
        data.put("semester", "1er semestre");
        data.put("mention", average >= 16 ? "Très bien" : (average >= 14 ? "Bien" : "Assez bien"));

        return data;
    }

    @Override
    public Map<String, Object> createSampleSalaryData() {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> employee = new HashMap<>();
        employee.put("firstName", "Marie");
        employee.put("lastName", "Martin");
        employee.put("fullName", "Marie Martin");
        employee.put("position", "Professeur des écoles");
        employee.put("employeeId", "EMP-2024-042");
        employee.put("department", "Enseignement primaire");
        employee.put("hireDate", "01/09/2018");
        data.put("employee", employee);

        Map<String, Object> company = new HashMap<>();
        company.put("name", "Groupe Scolaire Les Lilas");
        company.put("siret", "123 456 789 00012");
        company.put("address", "123 Avenue des Écoles, 75000 Paris");
        company.put("naf", "8520Z");
        company.put("urssaf", "URSSAF Paris");
        data.put("company", company);

        Map<String, Object> earnings = new HashMap<>();
        earnings.put("baseSalary", 2500.00);
        earnings.put("seniorityBonus", 150.00);
        earnings.put("overtimeHours", 10);
        earnings.put("overtimeRate", 25.00);
        earnings.put("overtimeAmount", 250.00);
        earnings.put("responsibilityBonus", 200.00);
        earnings.put("totalEarnings", 3100.00);
        data.put("earnings", earnings);

        Map<String, Object> deductions = new HashMap<>();
        deductions.put("socialContributions", 558.00);
        deductions.put("csgCrs", 279.00);
        deductions.put("pension", 155.00);
        deductions.put("healthInsurance", 93.00);
        deductions.put("totalDeductions", 1085.00);
        data.put("deductions", deductions);

        data.put("netSalary", 2015.00);
        data.put("netTaxable", 2500.00);
        data.put("paymentDate", "31/01/2024");
        data.put("paymentMethod", "Virement bancaire");
        data.put("month", "Janvier 2024");

        return data;
    }
}