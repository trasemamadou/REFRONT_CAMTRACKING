package com.groupeisi.ms1_gestschool.scolarite.niveau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper

public interface NiveauMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "libelle", target = "libelle")
    @Mapping(source = "ecole", target = "ecole.nom")
    @Mapping(source = "actif", target = "actif")
    NiveauEntity toNiveauEntity(NiveauDtoRequest niveau);
    NiveauDtoResponse toNiveauDtoResponse(NiveauEntity niveau);
    List<NiveauDtoResponse> toListNiveauDtoResponse(List<NiveauEntity> niveaux);
}
