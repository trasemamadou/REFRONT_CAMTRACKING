package com.groupeisi.ms1_gestschool.config.filtering;

import org.springframework.stereotype.Component;

@Component
public class GenericFilterServiceFactory {
    public <T> GenericFilterService<T> createGenericFilterService(Class<T> entityClass) {
        return new GenericFilterServiceImpl<>(entityClass);
    }
}