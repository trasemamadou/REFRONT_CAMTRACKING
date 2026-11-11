package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprentissageMapper {

    // --------- DTO → Entity ---------
    @Mapping(source = "classeId", target = "classe", qualifiedByName = "mapClasse")
    @Mapping(source = "studentId", target = "student", qualifiedByName = "mapStudent")
    ApprentissageEntity toEntity(ApprentissageDtoRequest dto);

    // --------- Entity → DTO ---------
    @Mapping(source = "student", target = "eleve")
    @Mapping(target = "statut", ignore = true)
    ApprentissageDtoResponse toDtoResponse(ApprentissageEntity entity);

    List<ApprentissageDtoResponse> toDtoResponseList(List<ApprentissageEntity> entities);

    // --------- Méthodes ID → Entity ---------
    @Named("mapClasse")
    default ClasseEntity mapClasse(String id) {
        if (id == null) return null;
        ClasseEntity classe = new ClasseEntity();
        classe.setId(id);
        return classe;
    }

    @Named("mapStudent")
    default PersonneEntity mapStudent(String id) {
        if (id == null) return null;
        PersonneEntity student = new PersonneEntity();
        student.setId(id);
        return student;
    }
    default String map(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.get(0).name();
    }
}
