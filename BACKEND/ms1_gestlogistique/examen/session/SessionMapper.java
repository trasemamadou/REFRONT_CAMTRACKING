package com.groupeisi.ms1_gestschool.examen.session;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "libelle", target = "libelle")
    @Mapping(source = "dateDebut", target = "dateDebut")
    SessionDtoResponse toDto(SessionEntity entity);

    SessionEntity toEntity(SessionDtoRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SessionDtoRequest dto, @MappingTarget SessionEntity entity);
}
