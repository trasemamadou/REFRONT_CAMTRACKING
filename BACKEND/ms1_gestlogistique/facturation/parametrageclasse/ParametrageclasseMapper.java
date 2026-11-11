package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParametrageclasseMapper {

    /* ================= ENTITY -> DTO ================= */
    @Mapping(source = "classe", target = "classe")
    @Mapping(source = "parametrage", target = "parametrage")
    ParametrageclasseDtoResponse toDtoResponse(ParametrageclasseEntity entity);

    /* ================= DTO -> ENTITY ================= */
    @Mapping(target = "id", ignore = true) // ID auto généré
    @Mapping(target = "classe", ignore = true) // on mappe après
    @Mapping(target = "parametrage", ignore = true) // on mappe après
    ParametrageclasseEntity toEntity(ParametrageclasseDtoRequest request);

    /* ================= LOGIQUE POST-MAPPING ================= */
    @AfterMapping
    default void mapIdsToEntities(ParametrageclasseDtoRequest request, @MappingTarget ParametrageclasseEntity entity) {
        if (request.getClasseId() != null) {
            ClasseEntity classe = new ClasseEntity();
            classe.setId(request.getClasseId());
            entity.setClasse(classe);
        }

        if (request.getParametrageId() != null) {
            ParametrageEntity param = new ParametrageEntity();
            param.setId(request.getParametrageId());
            entity.setParametrage(param);
        }
    }
}
