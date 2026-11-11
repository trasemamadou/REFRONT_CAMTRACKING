package com.groupeisi.ms1_gestschool.examen.note;

import com.groupeisi.ms1_gestschool.examen.epreuve.SessionmatiereId;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    // Mapping Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bulletin", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "sessionmatiere", ignore = true) // On ignore car on utilise l'ID
    NoteEntity toEntity(NoteDtoRequest request);

    // Mapping Entity → Response
    @Mapping(target = "sessionmatiereId", source = "entity.sessionmatiereId")
    @Mapping(target = "valeur", source = "entity.valeur")
    @Mapping(target = "coefficient", source = "entity.coefficient")
    @Mapping(target = "valeurPonderee", expression = "java(entity.getValeur() * entity.getCoefficient())")
    @Mapping(target = "matiereNom", ignore = true) // Sera rempli dans le service
    NoteDtoResponse toDto(NoteEntity entity);

    // Mapping Request → Entity (update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bulletin", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "sessionmatiere", ignore = true)
    void updateEntityFromDto(NoteDtoRequest request, @MappingTarget NoteEntity entity);

    // Méthodes de conversion si nécessaire (mais on ignore pour éviter les erreurs)
    default Long map(SessionmatiereId value) {
        return null; // Non utilisé
    }

    default SessionmatiereId map(Long value) {
        return null; // Non utilisé
    }
}