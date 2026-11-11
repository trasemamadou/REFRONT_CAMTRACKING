package com.groupeisi.ms1_gestschool.templates;

import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateMapper {

    @Mapping(target = "fileExtension", expression = "java(getFileExtension(entity))")
    TemplateDtoResponse toDto(TemplateEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "format", expression = "java(getFormatFromRequest(request))")
    @Mapping(target = "fileExtension", expression = "java(getFileExtensionFromRequest(request))")
    @Mapping(target = "content", expression = "java(getContentFromRequest(request))")
    TemplateEntity toEntity(TemplateDtoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "format", expression = "java(getFormatFromRequest(request))")
    @Mapping(target = "fileExtension", expression = "java(getFileExtensionFromRequest(request))")
    @Mapping(target = "content", expression = "java(getContentFromRequest(request))")
    void updateEntityFromRequest(TemplateDtoRequest request, @MappingTarget TemplateEntity entity);

    default String getFileExtensionFromRequest(TemplateDtoRequest request) {
        if (request.getFileExtension() != null) {
            return request.getFileExtension();
        }
        if (request.getFormat() != null) {
            return switch (request.getFormat()) {
                case PDF -> "pdf";
                case EXCEL -> "xlsx";
                case WORD -> "docx";
                case HTML -> "html";
                case TEXT -> "txt";
                case CSV -> "csv";
            };
        }
        return TemplateType.getDefaultExtension(request.getType());
    }

    default String getContentFromRequest(TemplateDtoRequest request) {
        if (request.getTemplateFile() != null && request.getTemplateFile().length > 0) {
            return "[FICHIER TEMPLATE: " + request.getFileName() + "]";
        }
        return request.getContent();
    }

    default String getFileExtension(TemplateEntity entity) {
        if (entity.getFileExtension() != null) {
            return entity.getFileExtension();
        }
        if (entity.getFormat() == null) {
            return TemplateType.getDefaultExtension(entity.getType());
        }
        return entity.getFormat().name().toLowerCase();
    }

    default DocumentFormat getFormatFromRequest(TemplateDtoRequest request) {
        if (request.getFormat() != null) {
            return request.getFormat();
        }
        return request.getType().getDefaultFormat();
    }

    default List<TemplateDtoResponse> toDtoList(List<TemplateEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}