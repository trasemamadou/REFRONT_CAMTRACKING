package com.groupeisi.ms1_gestschool.facturation.facturation;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FacturationService {
    Optional<List<FacturationDtoResponse>> getAllFacturations();
    Optional<FacturationDtoResponse> addFacturation(FacturationDtoRequest registration);
    Optional<FacturationDtoResponse> updateFacturation(String libelle, FacturationDtoRequest registration);
    Optional<FacturationDtoResponse> getFacturationByLibelle(String libelle);
    Page<FacturationDtoResponse> filterFacturations(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
    Optional<?> deleteFacturation(String libelle);
    Optional<FacturationDtoResponse> changeStatusFacturation(String libelle, FacturationStatus status);
    Optional<FacturationDtoResponse> approveFacturation(String libelle);
    Resource generateInvoice(String libelle);
    Resource generateInvoicePdf(String libelle);
    Resource generateInvoiceExcel(String libelle);
    String generateInvoiceHtml(String libelle);
}