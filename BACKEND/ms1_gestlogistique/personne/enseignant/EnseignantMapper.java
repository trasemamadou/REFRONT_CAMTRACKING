package com.groupeisi.ms1_gestschool.personne.enseignant;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EnseignantMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "sexe", source = "sexe")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "dossier.id", source = "dossierId")
    PersonneEntity toEnseignantEntity(EnseignantDtoRequest request);
    @Mapping(target = "role", source = "role")
    @Mapping(target = "sexe", source = "sexe")
    EnseignantDtoResponse toEnseignantDtoResponse(PersonneEntity entity);
    List<EnseignantDtoResponse> toEnseignantDtoResponseList(List<PersonneEntity> entities);
}