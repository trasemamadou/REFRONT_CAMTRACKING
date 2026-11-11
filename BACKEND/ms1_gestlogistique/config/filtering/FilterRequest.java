package com.groupeisi.ms1_gestschool.config.filtering;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class FilterRequest {
    @NotBlank(message = "Le champ est obligatoire")
    private String champ;

    @Pattern(regexp = "contains|equals|startsWith|endsWith|greaterThan|lessThan|gte|lte",
            message = "Le mode doit être l'un des suivants : contains, equals, startsWith, endsWith, greaterThan, lessThan, gte, lte")
    private String mode = "contains";

    @NotBlank(message = "Le mot-clé est obligatoire")
    private String keyword;
}