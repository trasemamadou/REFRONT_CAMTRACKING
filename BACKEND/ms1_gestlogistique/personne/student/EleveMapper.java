package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEntity;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEleveDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EleveMapper {

    // ================================
    // DTO → ENTITY
    // ================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "situationMatrimoniale", ignore = true)
    @Mapping(target = "numeroCNI", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "dossier.id", ignore = true)

    // 🔥 conversion Date → LocalDate (IMPORTANT)
    @Mapping(target = "dateNaissance", source = "dateNaissance", qualifiedByName = "dateToLocalDate")

    PersonneEntity toStudentEntity(EleveDtoRequest request);


    // ================================
    // ENTITY → DTO
    // ================================
    @Mapping(target = "dateNaissance", source = "dateNaissance", qualifiedByName = "localDateToDate")
    @Mapping(target = "role", expression = "java(entity.getRole() != null ? entity.getRole().name() : null)")
    @Mapping(source = "dossier", target = "dossier", qualifiedByName = "mapDossierEntityToDto")

    EleveDtoResponse toStudentDtoResponse(PersonneEntity entity);


    List<EleveDtoResponse> toStudentDtoResponseList(List<PersonneEntity> entities);


    // ================================
    // CONVERSIONS DATE 🔥
    // ================================

    // LocalDate → Date
    @Named("localDateToDate")
    default Date localDateToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // Date → LocalDate
    @Named("dateToLocalDate")
    default LocalDate dateToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    // ================================
    // DOSSIER (SAFE - PAS DE BOUCLE)
    // ================================
    @Named("mapDossierEntityToDto")
    default DossierEleveDtoResponse mapDossierEntityToDto(DossierEntity entity) {
        if (entity == null) return null;

        DossierEleveDtoResponse dto = new DossierEleveDtoResponse();
        dto.setId(entity.getId());
        dto.setPhotoProfil(entity.getPhotoProfil());
        dto.setFileNaissance(entity.getFileNaissance());
        dto.setFileRadiation(entity.getFileRadiation());
        dto.setFileSante(entity.getFileSante());
        dto.setFileCni(entity.getFileCni());
        dto.setNomUrgence(entity.getNomUrgence());
        dto.setTelUrgence(entity.getTelUrgence());
        dto.setLienUrgence(entity.getLienUrgence());
        dto.setAntecedents(entity.getAntecedents());
        dto.setAllergies(entity.getAllergies());
        dto.setAdresse(entity.getAdresse());
        dto.setEcolePrecedente(entity.getEcolePrecedente());
        dto.setIdentifiantScolaire(entity.getIdentifiantScolaire());
        dto.setLangueEtude(entity.getLangueEtude());
        dto.setGroupeSanguin(entity.getGroupeSanguin());

        // ❌ NE PAS mapper entity.getPersonne() → évite StackOverflow

        return dto;
    }


    // ================================
    // OPTION : MAP ID → ENTITY
    // ================================
    @Named("mapDossierIdToEntity")
    default DossierEntity mapDossierIdToEntity(String id) {
        if (id == null) return null;
        DossierEntity dossier = new DossierEntity();
        dossier.setId(id);
        return dossier;
    }
}