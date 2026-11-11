package com.groupeisi.ms1_gestschool.templates.init;

import com.groupeisi.ms1_gestschool.templates.*;
import com.groupeisi.ms1_gestschool.templates.generator.exel.ExcelGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SalaireTemplateInitializer implements TemplateInitializer {

    private final TemplateService templateService;
    private final ExcelGenerator excelGenerator;

    public SalaireTemplateInitializer(
            TemplateService templateService,
            ExcelGenerator excelGenerator) {
        this.templateService = templateService;
        this.excelGenerator = excelGenerator;
    }

    @Override
    public String getCode() {
        return "SALAIRE_001";
    }

    @Override
    public void initialize() {
        if (templateService.existsByCode(getCode())) {
            log.info("ℹ️ Template {} existe déjà", getCode());
            return;
        }

        try {
            // Générer le template Excel pour fiche de salaire
            byte[] templateContent = generateSalaryTemplate();

            TemplateDtoRequest request = TemplateDtoRequest.builder()
                    .code(getCode())
                    .name("Fiche de salaire standard")
                    .description("Template de fiche de salaire au format Excel")
                    .type(TemplateType.FICHE_SALAIRE)
                    .format(DocumentFormat.EXCEL)
                    .templateFile(templateContent)
                    .fileName("salaire_template.xlsx")
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

    private byte[] generateSalaryTemplate() {
        try {
            String htmlTemplate = createSalaryHtmlTemplate();
            return excelGenerator.generate(htmlTemplate, templateService.createSampleSalaryData());
        } catch (Exception e) {
            log.error("Erreur lors de la génération du template salaire", e);
            throw new RuntimeException("Impossible de générer le template salaire", e);
        }
    }

    private String createSalaryHtmlTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Fiche de salaire</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #000; }
                    .header h1 { color: #1976D2; }
                    .employee-info { background: #E3F2FD; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
                    table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    th { background: #1976D2; color: white; padding: 10px; }
                    td { padding: 8px; border: 1px solid #ddd; }
                    .total { font-size: 18px; font-weight: bold; }
                    .positive { color: green; }
                    .negative { color: red; }
                    .footer { margin-top: 30px; text-align: center; font-size: 11px; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>FICHE DE SALAIRE</h1>
                    <p>Mois: ${month}</p>
                </div>
                
                <div class="employee-info">
                    <h3>${employee.fullName}</h3>
                    <p>Poste: ${employee.position}</p>
                    <p>Matricule: ${employee.employeeId}</p>
                    <p>Date d'embauche: ${employee.hireDate}</p>
                </div>
                
                <h3>Éléments de paie</h3>
                
                <table>
                    <tr><th colspan="2">Gains</th></tr>
                    <tr><td>Salaire de base</td><td class="positive">${earnings.baseSalary} €</td></tr>
                    <tr><td>Prime d'ancienneté</td><td class="positive">${earnings.seniorityBonus} €</td></tr>
                    <tr><td>Heures supplémentaires</td><td class="positive">${earnings.overtimeAmount} €</td></tr>
                    <tr><td>Prime de responsabilité</td><td class="positive">${earnings.responsibilityBonus} €</td></tr>
                    <tr><td><strong>Total gains</strong></td><td><strong>${earnings.totalEarnings} €</strong></td></tr>
                </table>
                
                <table>
                    <tr><th colspan="2">Retenues</th></tr>
                    <tr><td>Cotisations sociales</td><td class="negative">-${deductions.socialContributions} €</td></tr>
                    <tr><td>CSG/CRDS</td><td class="negative">-${deductions.csgCrs} €</td></tr>
                    <tr><td>Retraite</td><td class="negative">-${deductions.pension} €</td></tr>
                    <tr><td>Mutuelle</td><td class="negative">-${deductions.healthInsurance} €</td></tr>
                    <tr><td><strong>Total retenues</strong></td><td><strong>${deductions.totalDeductions} €</strong></td></tr>
                </table>
                
                <div class="total">
                    <p>NET À PAYER: ${netSalary} €</p>
                    <p>Net imposable: ${netTaxable} €</p>
                </div>
                
                <div class="footer">
                    <p>${company.name} - SIRET: ${company.siret}</p>
                    <p>${company.address}</p>
                    <p>Document généré le ${generationDate}</p>
                </div>
            </body>
            </html>
        """;
    }
}