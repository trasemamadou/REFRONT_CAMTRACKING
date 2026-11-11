package com.groupeisi.ms1_gestschool.bibliotheques;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "fileExtension", source = "fileExtension")
    DocumentDtoResponse toDto(DocumentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "fileExtension", source = "fileExtension")
    DocumentEntity toEntity(DocumentDtoRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "fileExtension", source = "fileExtension")
    void updateEntity(DocumentDtoRequest dto, @MappingTarget DocumentEntity entity);
}