package com.groupeisi.ms1_gestschool.config.filtering;

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GenericFilterServiceImpl<T> implements GenericFilterService<T> {

    private final Class<T> entityClass;

    public GenericFilterServiceImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Specification<T> buildSpecification(FilterRequest filter) {
        return (root, query, cb) -> {
            if (filter == null || !StringUtils.hasText(filter.getChamp()) || !StringUtils.hasText(filter.getKeyword())) {
                return cb.conjunction();
            }

            String champ = filter.getChamp();
            String keyword = filter.getKeyword().trim();
            String mode = filter.getMode() != null ? filter.getMode().toLowerCase() : "contains";

            Path<?> path = getPath(root, champ);
            if (path == null) {
                return cb.conjunction(); // champ non trouvé, on ignore
            }

            Class<?> fieldType = path.getJavaType();

            try {
                // Gestion des différents types
                if (fieldType == String.class) {
                    return handleString((Path<String>) path, cb, keyword, mode);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    return handleNumber(path, cb, keyword, mode, Integer.class);
                } else if (fieldType == Long.class || fieldType == long.class) {
                    return handleNumber(path, cb, keyword, mode, Long.class);
                } else if (fieldType == Double.class || fieldType == double.class) {
                    return handleNumber(path, cb, keyword, mode, Double.class);
                } else if (fieldType == BigDecimal.class) {
                    return handleBigDecimal((Path<BigDecimal>) path, cb, keyword, mode);
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    return handleBoolean((Path<Boolean>) path, cb, keyword);
                } else if (fieldType == LocalDate.class) {
                    return handleLocalDate((Path<LocalDate>) path, cb, keyword, mode);
                } else if (fieldType == LocalDateTime.class) {
                    return handleLocalDateTime((Path<LocalDateTime>) path, cb, keyword, mode);
                } else if (fieldType.isEnum()) {
                    return handleEnum((Path<Enum>) path, cb, keyword, (Class<Enum>) fieldType);
                } else {
                    // Par défaut, on traite comme string
                    return handleString((Path<String>) path.as(String.class), cb, keyword, mode);
                }
            } catch (Exception e) {
                // En cas d'erreur de parsing, on retourne une conjonction (pas de filtre)
                return cb.conjunction();
            }
        };
    }

    private Path<?> getPath(Root<T> root, String champ) {
        String[] parts = champ.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
            if (path == null) return null;
        }
        return path;
    }

    private Predicate handleString(Path<String> path, CriteriaBuilder cb, String keyword, String mode) {
        String lowerKeyword = keyword.toLowerCase();
        return switch (mode) {
            case "startswith" -> cb.like(cb.lower(path), lowerKeyword + "%");
            case "endswith" -> cb.like(cb.lower(path), "%" + lowerKeyword);
            case "contains" -> cb.like(cb.lower(path), "%" + lowerKeyword + "%");
            default -> cb.equal(path, keyword); // equals
        };
    }

    private <N extends Number & Comparable<N>> Predicate handleNumber(Path<?> path, CriteriaBuilder cb, String keyword, String mode, Class<N> numberClass) {
        try {
            N value = parseNumber(keyword, numberClass);
            Path<N> numPath = (Path<N>) path.as(numberClass);
            return switch (mode) {
                case "greaterthan" -> cb.greaterThan(numPath, value);
                case "lessthan" -> cb.lessThan(numPath, value);
                case "gte" -> cb.greaterThanOrEqualTo(numPath, value);
                case "lte" -> cb.lessThanOrEqualTo(numPath, value);
                default -> cb.equal(numPath, value);
            };
        } catch (NumberFormatException e) {
            return cb.conjunction();
        }
    }

    private Predicate handleBigDecimal(Path<BigDecimal> path, CriteriaBuilder cb, String keyword, String mode) {
        try {
            BigDecimal value = new BigDecimal(keyword);
            return switch (mode) {
                case "greaterthan" -> cb.greaterThan(path, value);
                case "lessthan" -> cb.lessThan(path, value);
                case "gte" -> cb.greaterThanOrEqualTo(path, value);
                case "lte" -> cb.lessThanOrEqualTo(path, value);
                default -> cb.equal(path, value);
            };
        } catch (NumberFormatException e) {
            return cb.conjunction();
        }
    }

    private Predicate handleBoolean(Path<Boolean> path, CriteriaBuilder cb, String keyword) {
        Boolean value = Boolean.parseBoolean(keyword);
        return cb.equal(path, value);
    }

    private Predicate handleLocalDate(Path<LocalDate> path, CriteriaBuilder cb, String keyword, String mode) {
        try {
            LocalDate date = LocalDate.parse(keyword, DateTimeFormatter.ISO_LOCAL_DATE);
            return switch (mode) {
                case "greaterthan" -> cb.greaterThan(path, date);
                case "lessthan" -> cb.lessThan(path, date);
                case "gte" -> cb.greaterThanOrEqualTo(path, date);
                case "lte" -> cb.lessThanOrEqualTo(path, date);
                default -> cb.equal(path, date);
            };
        } catch (DateTimeParseException e) {
            return cb.conjunction();
        }
    }

    private Predicate handleLocalDateTime(Path<LocalDateTime> path, CriteriaBuilder cb, String keyword, String mode) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(keyword, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return switch (mode) {
                case "greaterthan" -> cb.greaterThan(path, dateTime);
                case "lessthan" -> cb.lessThan(path, dateTime);
                case "gte" -> cb.greaterThanOrEqualTo(path, dateTime);
                case "lte" -> cb.lessThanOrEqualTo(path, dateTime);
                default -> cb.equal(path, dateTime);
            };
        } catch (DateTimeParseException e) {
            return cb.conjunction();
        }
    }

    private Predicate handleEnum(Path<Enum> path, CriteriaBuilder cb, String keyword, Class<Enum> enumClass) {
        try {
            Enum<?> enumValue = Enum.valueOf(enumClass, keyword.toUpperCase());
            return cb.equal(path, enumValue);
        } catch (IllegalArgumentException e) {
            return cb.conjunction();
        }
    }

    @SuppressWarnings("unchecked")
    private <N extends Number & Comparable<N>> N parseNumber(String keyword, Class<N> numberClass) {
        if (Integer.class.equals(numberClass)) {
            return (N) Integer.valueOf(keyword);
        } else if (Long.class.equals(numberClass)) {
            return (N) Long.valueOf(keyword);
        } else if (Double.class.equals(numberClass)) {
            return (N) Double.valueOf(keyword);
        } else {
            throw new NumberFormatException("Type non supporté: " + numberClass);
        }
    }

    @Override
    public Page<T> searchByFilters(JpaSpecificationExecutor<T> repository, List<FilterRequest> filters, Pageable pageable) {
        Specification<T> spec = buildSpecificationFromFilters(filters);
        return repository.findAll(spec, pageable);
    }

    @Override
    public Page<T> searchAndSortByFilters(JpaSpecificationExecutor<T> repository, List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {
        Specification<T> spec = buildSpecificationFromFilters(filters);
        Pageable sortedPageable = pageable;

        if (sorter != null && StringUtils.hasText(sorter.getChamp())) {
            Sort.Direction direction = sorter.isAsc() ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sorter.getChamp()));
        }

        return repository.findAll(spec, sortedPageable);
    }

    private Specification<T> buildSpecificationFromFilters(List<FilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return Specification.where(null);
        }

        Specification<T> spec = Specification.where(null);
        for (FilterRequest filter : filters) {
            if (filter != null && StringUtils.hasText(filter.getKeyword())) {
                spec = spec.and(buildSpecification(filter));
            }
        }
        return spec;
    }
}