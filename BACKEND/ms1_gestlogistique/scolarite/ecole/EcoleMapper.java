package com.groupeisi.ms1_gestschool.scolarite.ecole;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EcoleMapper {

    EcoleMapper INSTANCE = Mappers.getMapper(EcoleMapper.class);

    @Mapping(target = "dateCreation", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "active", constant = "true")
    @Mapping(source = "photoLogo", target = "photoLogo")
    @Mapping(source = "emailPrescripteur", target = "emailPrescripteur")
    EcoleEntity toEntity(EcoleDtoRequest dtoRequest);

    EcoleDtoResponse toDtoResponse(EcoleEntity entity);

    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDto(EcoleDtoRequest dtoRequest, @org.mapstruct.MappingTarget EcoleEntity entity);
}