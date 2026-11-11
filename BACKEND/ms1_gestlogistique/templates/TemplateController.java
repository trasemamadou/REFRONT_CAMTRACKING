// TemplateController.java
package com.groupeisi.ms1_gestschool.templates;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/facture/pdf")
    public ResponseEntity<Resource> generateFacturePdf(@RequestBody Map<String, Object> data) {
        Resource document = templateService.generateDocument(
                DocumentType.FACTURE,
                DocumentFormat.PDF,
                data
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(document);
    }

    @PostMapping("/bulletin/excel")
    public ResponseEntity<Resource> generateBulletinExcel(@RequestBody Map<String, Object> data) {
        Resource document = templateService.generateDocument(
                DocumentType.BULLETIN,
                DocumentFormat.EXCEL,
                data
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(document);
    }

    @GetMapping("/test/facture")
    public ResponseEntity<Resource> testFacture() {
        return generateFacturePdf(templateService.createSampleInvoiceData());
    }

    @GetMapping("/test/bulletin")
    public ResponseEntity<Resource> testBulletin() {
        return generateBulletinExcel(templateService.createSampleGradesData());
    }
}