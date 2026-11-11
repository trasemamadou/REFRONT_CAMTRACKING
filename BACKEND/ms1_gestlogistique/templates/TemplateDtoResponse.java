package com.groupeisi.ms1_gestschool.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDtoResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private TemplateType type;
    private String content;
    private DocumentFormat format;
    private String fileExtension;
    private String fileName;
    private String contentType;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}