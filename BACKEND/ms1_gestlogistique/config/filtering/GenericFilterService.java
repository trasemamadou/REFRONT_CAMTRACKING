package com.groupeisi.ms1_gestschool.config.filtering;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GenericFilterService<T> {
    Specification<T> buildSpecification(FilterRequest filter);
    Page<T> searchByFilters(JpaSpecificationExecutor<T> repository, List<FilterRequest> filters, Pageable pageable);
    Page<T> searchAndSortByFilters(JpaSpecificationExecutor<T> repository, List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
}