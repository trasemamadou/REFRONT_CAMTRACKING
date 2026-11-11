package com.groupeisi.ms1_gestschool.templates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDtoRequest {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le type est obligatoire")
    private TemplateType type;

    private String content; // optionnel

    private DocumentFormat format;
    private String fileExtension;

    private byte[] templateFile;
    private String fileName;
    private String contentType;

    @Builder.Default
    private boolean active = true;
}