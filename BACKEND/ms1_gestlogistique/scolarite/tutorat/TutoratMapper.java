package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TutoratMapper {

    /* =========================
       Entity -> Response
       ========================= */
    @Mapping(source = "eleve.id", target = "eleveId")
    @Mapping(source = "eleve.nom", target = "eleveNom")
    @Mapping(source = "eleve.prenom", target = "elevePrenom")
    @Mapping(source = "tuteur.id", target = "tuteurId")
    @Mapping(source = "tuteur.nom", target = "tuteurNom")
    @Mapping(source = "tuteur.prenom", target = "tuteurPrenom")
    TutoratDtoResponse toResponse(TutoratEntity entity);

    /* =========================
       Create
       ========================= */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eleve", source = "eleve")
    @Mapping(target = "tuteur", source = "tuteur")
    TutoratEntity toEntity(
            TutoratDtoRequest request,
            PersonneEntity eleve,
            PersonneEntity tuteur
    );

    /* =========================
       Update
       ========================= */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eleve", source = "eleve")
    @Mapping(target = "tuteur", source = "tuteur")
    void updateEntity(
            @MappingTarget TutoratEntity entity,
            TutoratDtoRequest request,
            PersonneEntity eleve,
            PersonneEntity tuteur
    );
}
