package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ParametrageMapper {

    ParametrageDtoResponse toDto(ParametrageEntity entity);

    List<ParametrageDtoResponse> toDtoList(List<ParametrageEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "ecole", target = "ecole")
    ParametrageEntity toEntity(ParametrageDtoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ParametrageDtoRequest request,
                                 @MappingTarget ParametrageEntity entity);

    // 🔥 Mapping manuel String -> EcoleEntity
    default EcoleEntity map(String value) {
        if (value == null) {
            return null;
        }
        EcoleEntity ecole = new EcoleEntity();
        ecole.setNom(value);
        return ecole;
    }
}