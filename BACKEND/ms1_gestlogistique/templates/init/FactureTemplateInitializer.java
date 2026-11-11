package com.groupeisi.ms1_gestschool.templates.init;

import com.groupeisi.ms1_gestschool.templates.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FactureTemplateInitializer implements TemplateInitializer {

    private final TemplateService templateService;

    public FactureTemplateInitializer(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public String getCode() {
        return "FACTURE_001";
    }

    @Override
    public void initialize() {
        if (templateService.existsByCode(getCode())) {
            log.info("ℹ️ Template {} existe déjà", getCode());
            return;
        }

        try {
            // Charger le template HTML depuis les resources
            ClassPathResource resource = new ClassPathResource("templates/pdf/facture_template.html");

            byte[] templateContent;
            String fileName;
            String contentType;

            if (resource.exists()) {
                // Si le fichier existe, l'utiliser
                templateContent = resource.getInputStream().readAllBytes();
                fileName = "facture_template.html";
                contentType = "text/html";
                log.info("📄 Template facture chargé depuis le fichier");
            } else {
                // Sinon, utiliser un template par défaut
                templateContent = createDefaultFactureTemplate().getBytes();
                fileName = "facture_default.html";
                contentType = "text/html";
                log.warn("⚠️ Fichier template facture introuvable, utilisation du template par défaut");
            }

            TemplateDtoRequest request = TemplateDtoRequest.builder()
                    .code(getCode())
                    .name("Facture standard")
                    .description("Template de facture au format PDF")
                    .type(TemplateType.FACTURE)
                    .format(DocumentFormat.PDF)
                    .templateFile(templateContent)
                    .fileName(fileName)
                    .contentType(contentType)
                    .fileExtension("pdf")
                    .active(true)
                    .build();

            templateService.createTemplate(request);
            log.info("✅ Template {} créé avec succès", getCode());

        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du template {}", getCode(), e);
        }
    }

    private String createDefaultFactureTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Facture</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #333; border-bottom: 2px solid #000; padding-bottom: 10px; }
                    .client-info { margin: 20px 0; padding: 15px; background: #f9f9f9; }
                    .invoice-details { margin: 30px 0; }
                    table { width: 100%; border-collapse: collapse; }
                    th { background: #4CAF50; color: white; padding: 10px; text-align: left; }
                    td { padding: 10px; border-bottom: 1px solid #ddd; }
                    .total { font-size: 20px; font-weight: bold; text-align: right; margin-top: 20px; }
                    .footer { margin-top: 50px; font-size: 12px; text-align: center; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>FACTURE</h1>
                    <p>N° ${invoiceNumber}</p>
                    <p>Date: ${generationDate}</p>
                </div>
                
                <div class="client-info">
                    <h3>Client:</h3>
                    <p><strong>${client.name}</strong></p>
                    <p>${client.address}</p>
                    <p>Tél: ${client.phone}</p>
                    <p>Email: ${client.email}</p>
                </div>
                
                <div class="invoice-details">
                    <table>
                        <thead>
                            <tr>
                                <th>Description</th>
                                <th>Quantité</th>
                                <th>Prix unitaire</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${items}
                        </tbody>
                    </table>
                </div>
                
                <div class="total">
                    <p>Sous-total: ${subTotal} €</p>
                    <p>TVA (${taxRate}%): ${taxAmount} €</p>
                    <p><strong>TOTAL TTC: ${total} €</strong></p>
                </div>
                
                <div class="footer">
                    <p>${companyName} - ${companySiret}</p>
                    <p>${companyAddress}</p>
                </div>
            </body>
            </html>
        """;
    }
}