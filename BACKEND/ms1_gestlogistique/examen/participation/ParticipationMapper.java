package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseMapper;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionInstance;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionMapper;
import com.groupeisi.ms1_gestschool.examen.session.SessionEntity;
import com.groupeisi.ms1_gestschool.examen.session.SessionMapper;
import com.groupeisi.ms1_gestschool.scolarite.enseignement.EnseignementMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ClasseMapper.class, SessionMapper.class, EnseignementMapper.class, CompositionMapper.class}
)
public interface ParticipationMapper {

    // ========== Entity → Response DTO ==========
    @Mapping(target = "classe", source = "classe")
    @Mapping(target = "session", source = "session")
    @Mapping(target = "matieres", source = "matieres") // Corrigé: matieres au lieu de enseignements
    @Mapping(target = "compositionInstance", source = "compositionInstance")
    ParticipationDtoResponse toDto(ParticipationEntity entity);

    // ========== Request DTO → Entity (création) ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classe", source = "classeId", qualifiedByName = "idToClasse")
    @Mapping(target = "session", source = "sessionId", qualifiedByName = "idToSession")
    @Mapping(target = "matieres", ignore = true) // Ignoré car géré séparément dans le service
    @Mapping(target = "enseignements", ignore = true) // Ignoré si non utilisé
    @Mapping(target = "compositionInstance", source = "compositionInstanceId", qualifiedByName = "idToCompositionInstance")
    ParticipationEntity toEntity(ParticipationDtoRequest dto);

    // ========== Mise à jour d'une entité existante ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classe", source = "classeId", qualifiedByName = "idToClasse")
    @Mapping(target = "session", source = "sessionId", qualifiedByName = "idToSession")
    @Mapping(target = "matieres", ignore = true) // Ignoré car géré séparément
    @Mapping(target = "enseignements", ignore = true)
    @Mapping(target = "compositionInstance", source = "compositionInstanceId", qualifiedByName = "idToCompositionInstance")
    void updateEntityFromDto(ParticipationDtoRequest dto, @MappingTarget ParticipationEntity entity);

    // ========== Méthodes de conversion ID → Entité (références simples) ==========
    @Named("idToClasse")
    default ClasseEntity idToClasse(String id) {
        if (id == null) return null;
        ClasseEntity classe = new ClasseEntity();
        classe.setId(id);
        return classe;
    }

    @Named("idToSession")
    default SessionEntity idToSession(String id) {
        if (id == null) return null;
        SessionEntity session = new SessionEntity();
        session.setId(id);
        return session;
    }

    @Named("idToCompositionInstance")
    default CompositionInstance idToCompositionInstance(String id) {
        if (id == null) return null;
        CompositionInstance instance = new CompositionInstance();
        instance.setId(id);
        return instance;
    }

    // ========== Méthode helper pour la création avec matieres ==========
    default ParticipationEntity toEntityWithMatieres(ParticipationDtoRequest dto) {
        if (dto == null) return null;

        ParticipationEntity entity = toEntity(dto);

        // Les matieres seront ajoutées via le service SessionmatiereService.createList()
        // après la sauvegarde de la sessionclasse

        return entity;
    }
}