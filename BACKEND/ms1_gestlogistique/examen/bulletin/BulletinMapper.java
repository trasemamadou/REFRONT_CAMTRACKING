package com.groupeisi.ms1_gestschool.examen.bulletin;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BulletinMapper {
    BulletinMapper INSTANCE = Mappers.getMapper(BulletinMapper.class);

    BulletinDtoResponse toDto(BulletinEntity entity);

    BulletinEntity toEntity(BulletinDtoRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BulletinDtoRequest dto, @MappingTarget BulletinEntity entity);
}