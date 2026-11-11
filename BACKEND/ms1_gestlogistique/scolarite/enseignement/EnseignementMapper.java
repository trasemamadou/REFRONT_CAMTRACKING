package com.groupeisi.ms1_gestschool.scolarite.enseignement;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnseignementMapper {

    // --------- DTO → Entity ---------
    @Mapping(source = "classeId", target = "classe", qualifiedByName = "mapClasse")
    @Mapping(source = "enseignantId", target = "enseignant", qualifiedByName = "mapEnseignant")
    @Mapping(source = "matiereName", target = "matiere", qualifiedByName = "mapMatiere")
    EnseignementEntity  toEntity(EnseignementDtoRequest dto);

    // --------- Entity → DTO ---------
    // --------- Entity → DTO ---------
    @Mapping(source = "classe", target = "classe")
    @Mapping(source = "enseignant", target = "enseignant")
    @Mapping(source = "matiere", target = "matiere")
    EnseignementDtoResponse toDtoResponse(EnseignementEntity entity);

    List<EnseignementDtoResponse> toDtoResponseList(List<EnseignementEntity> entities);

    // --------- Méthodes pour transformer ID → entité ---------
    @Named("mapClasse")
    default ClasseEntity mapClasse(String id) {
        if (id == null) return null;
        ClasseEntity classe = new ClasseEntity();
        classe.setId(id);
        return classe;
    }

    @Named("mapEnseignant")
    default PersonneEntity mapEnseignant(String id) {
        if (id == null) return null;
        PersonneEntity enseignant = new PersonneEntity();
        enseignant.setId(id);
        return enseignant;
    }

    @Named("mapMatiere")
    default MatiereEntity mapMatiere(String name) {
        if (name == null) return null;
        MatiereEntity matiere = new MatiereEntity();
        matiere.setName(name);
        return matiere;
    }
}
