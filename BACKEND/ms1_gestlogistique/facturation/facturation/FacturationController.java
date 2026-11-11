package com.groupeisi.ms1_gestschool.facturation.facturation;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.config.global.APIResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturations")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FacturationController {

    private final FacturationService facturationService;

    // ===================== CRUD ENDPOINTS =====================

    @GetMapping
    public ResponseEntity<List<FacturationDtoResponse>> getAllFacturations() {
        return facturationService.getAllFacturations()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/filter")
    public ResponseEntity<APIResponse<?>> filterFacturations(
            @RequestBody @Valid List<FilterRequest> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "true") boolean sortAsc) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            SorterRequest sorter = null;
            if (sortField != null && !sortField.isEmpty()) {
                sorter = new SorterRequest();
                sorter.setChamp(sortField);
                sorter.setAsc(sortAsc);
            }
            System.out.println("Filters :"+ filters);

            Page<FacturationDtoResponse> result = facturationService.filterFacturations(filters, sorter, pageable);

            if (result.isEmpty()) {
                APIResponse<?> response = new APIResponse<>(
                        "error",
                        404,
                        "Aucune facturation ne correspond aux critères de recherche",
                        null
                );
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("content", result.getContent());
            data.put("totalElements", result.getTotalElements());
            data.put("totalPages", result.getTotalPages());
            data.put("currentPage", result.getNumber());
            data.put("pageSize", result.getSize());

            APIResponse<?> response = new APIResponse<>(
                    "success",
                    200,
                    "Facturations trouvées avec succès",
                    data
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            APIResponse<?> response = new APIResponse<>(
                    "error",
                    400,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<FacturationDtoResponse> addFacturation(
            @Valid @RequestBody FacturationDtoRequest dto) {
        return facturationService.addFacturation(dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{libelle}")
    public ResponseEntity<FacturationDtoResponse> getFacturation(@PathVariable String libelle) {
        return facturationService.getFacturationByLibelle(libelle)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{libelle}")
    public ResponseEntity<FacturationDtoResponse> updateFacturation(
            @PathVariable String libelle,
            @Valid @RequestBody FacturationDtoRequest dto) {
        return facturationService.updateFacturation(libelle, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{libelle}")
    public ResponseEntity<Map<String, String>> deleteFacturation(@PathVariable String libelle) {
        Optional<?> result = facturationService.deleteFacturation(libelle);
        if (result.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Facturation supprimée avec succès",
                    "libelle", libelle
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Facturation non trouvée", "libelle", libelle));
        }
    }

    // ===================== STATUS MANAGEMENT ENDPOINTS =====================

    @PutMapping("/status/{libelle}/{status}")
    public ResponseEntity<FacturationDtoResponse> changeStatusFacturation(
            @PathVariable String libelle,
            @PathVariable FacturationStatus status) {
        return facturationService.changeStatusFacturation(libelle, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{libelle}/approve")
    public ResponseEntity<FacturationDtoResponse> approveFacturation(@PathVariable String libelle) {
        return facturationService.approveFacturation(libelle)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== DOCUMENT GENERATION ENDPOINTS =====================

    @GetMapping("/{libelle}/invoice/pdf")
    public ResponseEntity<Resource> generateInvoicePdf(@PathVariable String libelle) {
        try {
            Resource invoicePdf = facturationService.generateInvoicePdf(libelle);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"facture_" + libelle + ".pdf\"")
                    .body(invoicePdf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{libelle}/invoice/excel")
    public ResponseEntity<Resource> generateInvoiceExcel(@PathVariable String libelle) {
        try {
            Resource invoiceExcel = facturationService.generateInvoiceExcel(libelle);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"facture_" + libelle + ".xlsx\"")
                    .body(invoiceExcel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{libelle}/invoice/html")
    public ResponseEntity<String> generateInvoiceHtml(@PathVariable String libelle) {
        try {
            String invoiceHtml = facturationService.generateInvoiceHtml(libelle);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(invoiceHtml);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération de la facture: " + e.getMessage());
        }
    }

    @GetMapping("/{libelle}/invoice")
    public ResponseEntity<Resource> generateInvoice(@PathVariable String libelle) {
        try {
            Resource invoice = facturationService.generateInvoice(libelle);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"facture_" + libelle + ".pdf\"")
                    .body(invoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ===================== STATISTICS ENDPOINTS =====================

    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable FacturationStatus status) {
        return ResponseEntity.ok(0L);
    }

    @GetMapping("/stats/total")
    public ResponseEntity<Double> getTotalMontant() {
        return ResponseEntity.ok(0.0);
    }
}