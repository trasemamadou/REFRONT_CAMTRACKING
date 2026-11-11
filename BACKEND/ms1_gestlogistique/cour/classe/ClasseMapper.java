package com.groupeisi.ms1_gestschool.cour.classe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ClasseMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "nom", target = "nom")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "archive", target = "archive")
    @Mapping(source = "niveau", target = "niveau")
    @Mapping(source = "anneeScolaire", target = "anneeScolaire")

// Effectif & capacité
    @Mapping(source = "effectifActuel", target = "effectifActuel")
    @Mapping(source = "capaciteMax", target = "capaciteMax")
    @Mapping(source = "tauxOccupation", target = "tauxOccupation")

// Organisation
    @Mapping(source = "regime", target = "regime")
    @Mapping(source = "langueEnseignement", target = "langueEnseignement")
    @Mapping(source = "regimeDescription", target = "regimeDescription")
    @Mapping(source = "ecole", target = "ecole.nom")

     ClasseEntity toClasseEntity(ClasseDtoRequest classe);
     ClasseDtoResponse toClasseDtoResponse(ClasseEntity classe);
    List<ClasseDtoResponse> toListClasseDtoResponse(List<ClasseEntity> classes);
 //   Page<ClasseDtoResponse> toListClasseDtoResponsePaged(Page<ClasseEntity> classes);
}
