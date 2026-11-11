package com.groupeisi.ms1_gestschool.examen.parametrage;
import org.mapstruct.*;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompositionMapper {

    // CompositionInstance mappings - VERSION SIMPLIFIÉE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fields", ignore = true) // On ignore fields ici pour les gérer manuellement
    CompositionInstance toEntity(CompositionInstanceRequestDTO dto);

    @Mapping(target = "fields", source = "fields")
    CompositionInstanceResponseDTO toDto(CompositionInstance entity);

    List<CompositionInstanceResponseDTO> toInstanceDtoList(List<CompositionInstance> entities);

    // Méthode de mise à jour
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fields", ignore = true) // On ignore fields ici
    void updateEntityFromDto(CompositionInstanceRequestDTO dto, @MappingTarget CompositionInstance entity);

    // CompositionField mappings (existants)
    @Mapping(target = "instance", ignore = true)
    CompositionField toEntity(CompositionFieldRequestDTO dto);

    @Mapping(target = "instanceId", source = "instance.id")
    CompositionFieldResponseDTO toDto(CompositionField entity);

    List<CompositionFieldResponseDTO> toFieldDtoList(List<CompositionField> entities);

    // Méthode helper pour créer les fields (pour la création)
    default CompositionInstance createEntityWithFields(CompositionInstanceRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        CompositionInstance instance = toEntity(dto);

        if (dto.getFields() != null) {
            List<CompositionField> fields = dto.getFields().stream()
                    .map(this::toEntity)
                    .peek(field -> field.setInstance(instance))
                    .collect(Collectors.toList());
            instance.setFields(fields);
        }

        return instance;
    }

    // Méthode helper pour mettre à jour les fields (pour la mise à jour)
    default void updateFields(CompositionInstance entity, CompositionInstanceRequestDTO dto) {
        if (dto == null || dto.getFields() == null) {
            return;
        }

        // Clear existing fields
        if (entity.getFields() != null) {
            entity.getFields().clear();
        }

        // Create new fields
        List<CompositionField> fields = dto.getFields().stream()
                .map(this::toEntity)
                .peek(field -> field.setInstance(entity))
                .collect(Collectors.toList());

        entity.setFields(fields);
    }

    // Helper method to handle bidirectional relationship
    @AfterMapping
    default void linkFieldsToInstance(@MappingTarget CompositionInstance instance) {
        if (instance.getFields() != null) {
            instance.getFields().forEach(field -> field.setInstance(instance));
        }
    }
}