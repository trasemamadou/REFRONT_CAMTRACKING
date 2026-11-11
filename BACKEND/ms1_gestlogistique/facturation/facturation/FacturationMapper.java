package com.groupeisi.ms1_gestschool.facturation.facturation;
import com.groupeisi.ms1_gestschool.personne.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface FacturationMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "libelle", target = "libelle")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "montant", target = "montant")
    @Mapping(source = "object", target = "object")
    @Mapping(source = "statut", target = "statut")
    @Mapping(source = "parametrageclasseId", target = "parametrageclasse.id")
    @Mapping(source = "studentId", target = "student.id")
     FacturationEntity toRegistrationEntity(FacturationDtoRequest registration);
     FacturationDtoResponse toRegistrationDtoResponse(FacturationEntity registration);
     List<FacturationDtoResponse> toListRegistrationDtoResponse(List<FacturationEntity> registrations);
    default String map(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.get(0).name();
    }
}
