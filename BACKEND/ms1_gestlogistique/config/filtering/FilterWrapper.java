package com.groupeisi.ms1_gestschool.config.filtering;

import lombok.Data;

@Data
public class FilterWrapper<T> {
    private FilterRequest filter;
    private SorterRequest sorter;
    private Class<T> entityType;
}