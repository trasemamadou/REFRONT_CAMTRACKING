package com.groupeisi.ms1_gestschool.templates.init;

import com.groupeisi.ms1_gestschool.templates.*;
import com.groupeisi.ms1_gestschool.templates.generator.exel.ExcelGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BulletinTemplateInitializer implements TemplateInitializer {

    private final TemplateService templateService;
    private final ExcelGenerator excelGenerator;

    public BulletinTemplateInitializer(
            TemplateService templateService,
            ExcelGenerator excelGenerator) {
        this.templateService = templateService;
        this.excelGenerator = excelGenerator;
    }

    @Override
    public String getCode() {
        return "BULLETIN_001";
    }

    @Override
    public void initialize() {
        if (templateService.existsByCode(getCode())) {
            log.info("ℹ️ Template {} existe déjà", getCode());
            return;
        }

        try {
            // Générer le template Excel
            byte[] templateContent = generateBulletinTemplate();

            TemplateDtoRequest request = TemplateDtoRequest.builder()
                    .code(getCode())
                    .name("Bulletin scolaire standard")
                    .description("Template de bulletin scolaire au format Excel")
                    .type(TemplateType.BULLETIN)
                    .format(DocumentFormat.EXCEL)
                    .templateFile(templateContent)
                    .fileName("bulletin_template.xlsx")
                    .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .fileExtension("xlsx")
                    .active(true)
                    .build();

            templateService.createTemplate(request);
            log.info("✅ Template {} créé avec succès", getCode());

        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du template {}", getCode(), e);
        }
    }

    private byte[] generateBulletinTemplate() {
        try {
            // Créer un contenu HTML pour le bulletin
            String htmlTemplate = createBulletinHtmlTemplate();

            // Utiliser ExcelGenerator pour créer le template Excel
            return excelGenerator.generate(htmlTemplate, templateService.createSampleGradesData());

        } catch (Exception e) {
            log.error("Erreur lors de la génération du template bulletin", e);
            throw new RuntimeException("Impossible de générer le template bulletin", e);
        }
    }

    private String createBulletinHtmlTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Bulletin scolaire</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; margin-bottom: 20px; }
                    .header h1 { color: #2E7D32; }
                    .student-info { background: #f5f5f5; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
                    table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    th { background: #4CAF50; color: white; padding: 12px; }
                    td { padding: 10px; border: 1px solid #ddd; }
                    .average { font-size: 18px; font-weight: bold; text-align: right; margin: 20px 0; }
                    .footer { margin-top: 30px; font-size: 12px; text-align: center; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>BULLETIN SCOLAIRE</h1>
                    <p>Année scolaire ${student.year}</p>
                </div>
                
                <div class="student-info">
                    <h3>Élève: ${student.fullName}</h3>
                    <p>Classe: ${student.class}</p>
                    <p>Né(e) le: ${student.birthDate}</p>
                    <p>N° étudiant: ${student.studentId}</p>
                </div>
                
                <div class="school-info">
                    <p><strong>${school.name}</strong></p>
                    <p>${school.address} - Tél: ${school.phone}</p>
                </div>
                
                <table>
                    <thead>
                        <tr>
                            <th>Matière</th>
                            <th>Note (/20)</th>
                            <th>Coefficient</th>
                            <th>Appréciation</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${grades}
                    </tbody>
                </table>
                
                <div class="average">
                    <p>Moyenne générale: ${average}/20</p>
                    <p>Moyenne de la classe: ${classAverage}/20</p>
                    <p>Rang: ${rank}</p>
                    <p>Mention: ${mention}</p>
                </div>
                
                <div class="footer">
                    <p>Document généré le ${generationDate}</p>
                    <p>${school.academy} - ${school.name}</p>
                </div>
            </body>
            </html>
        """;
    }
}