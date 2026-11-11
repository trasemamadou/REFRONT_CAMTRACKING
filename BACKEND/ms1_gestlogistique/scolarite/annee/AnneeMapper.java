package com.groupeisi.ms1_gestschool.scolarite.annee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AnneeMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "libelle", target = "libelle")
    @Mapping(source = "ecole", target = "ecole.nom")
    @Mapping(source = "actif", target = "actif")
    AnneeEntity toAnneeEntity(AnneeDtoRequest annee);

    AnneeDtoResponse toAnneeDtoResponse(AnneeEntity annee);

    List<AnneeDtoResponse> toListAnneeDtoResponse(List<AnneeEntity> annees);
}