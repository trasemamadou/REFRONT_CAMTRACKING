package com.groupeisi.ms1_gestschool.personne.dossier;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DossierMapper {

    // =========================
    // ÉLÈVE - Request to Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)

    // Champs enseignant ignorés
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "diplome", ignore = true)
    @Mapping(target = "dateEmbauche", ignore = true)
    @Mapping(target = "typeContrat", ignore = true)
    @Mapping(target = "salaire", ignore = true)
    @Mapping(target = "banque", ignore = true)
    @Mapping(target = "numeroCompte", ignore = true)
    @Mapping(target = "specialite", ignore = true)

    @Mapping(source = "type", target = "type", qualifiedByName = "stringToTypeDossier")
    DossierEntity toEntity(DossierEleveDtoRequest request);


    // =========================
    // ÉLÈVE - Entity to Response (SAFE)
    // =========================
    @Mapping(source = "id", target = "id")
    @Mapping(source = "photoProfil", target = "photoProfil")
    @Mapping(source = "fileNaissance", target = "fileNaissance")
    @Mapping(source = "fileRadiation", target = "fileRadiation")
    @Mapping(source = "fileSante", target = "fileSante")
    @Mapping(source = "fileCni", target = "fileCni")

    @Mapping(source = "nomUrgence", target = "nomUrgence")
    @Mapping(source = "telUrgence", target = "telUrgence")
    @Mapping(source = "lienUrgence", target = "lienUrgence")

    @Mapping(source = "antecedents", target = "antecedents")
    @Mapping(source = "allergies", target = "allergies")

    @Mapping(source = "adresse", target = "adresse")
    @Mapping(source = "ecolePrecedente", target = "ecolePrecedente")
    @Mapping(source = "identifiantScolaire", target = "identifiantScolaire")
    @Mapping(source = "langueEtude", target = "langueEtude")
    @Mapping(source = "groupeSanguin", target = "groupeSanguin")

    @Mapping(source = "type", target = "type", qualifiedByName = "typeDossierToString")

    // ❌ ON CASSE LA BOUCLE ICI
    @Mapping(target = "eleve", ignore = true)

    DossierEleveDtoResponse toEleveResponse(DossierEntity entity);


    // =========================
    // UPDATE ÉLÈVE
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)

    // Champs enseignant ignorés
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "diplome", ignore = true)
    @Mapping(target = "dateEmbauche", ignore = true)
    @Mapping(target = "typeContrat", ignore = true)
    @Mapping(target = "salaire", ignore = true)
    @Mapping(target = "banque", ignore = true)
    @Mapping(target = "numeroCompte", ignore = true)
    @Mapping(target = "specialite", ignore = true)

    @Mapping(source = "type", target = "type", qualifiedByName = "stringToTypeDossier")
    void updateEntity(@MappingTarget DossierEntity entity, DossierEleveDtoRequest request);


    // =========================
    // ENSEIGNANT - Request to Entity
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)

    // Champs élève ignorés
    @Mapping(target = "fileNaissance", ignore = true)
    @Mapping(target = "fileRadiation", ignore = true)
    @Mapping(target = "fileSante", ignore = true)
    @Mapping(target = "ecolePrecedente", ignore = true)
    @Mapping(target = "identifiantScolaire", ignore = true)
    @Mapping(target = "langueEtude", ignore = true)
    @Mapping(target = "groupeSanguin", ignore = true)

    @Mapping(target = "type", constant = "ENSEIGNANT")
    DossierEntity toEntity(DossierEnseignantDtoRequest request);


    // =========================
    // ENSEIGNANT - Entity to Response (SAFE)
    // =========================
    @Mapping(source = "id", target = "id")
    @Mapping(source = "photoProfil", target = "photoProfil")
    @Mapping(source = "fileCni", target = "fileCni")
    @Mapping(source = "fileCv", target = "fileCv")
    @Mapping(source = "fileContrat", target = "fileContrat")
    @Mapping(source = "fileDiplome", target = "fileDiplome")

    @Mapping(source = "nomUrgence", target = "nomUrgence")
    @Mapping(source = "telUrgence", target = "telUrgence")
    @Mapping(source = "lienUrgence", target = "lienUrgence")

    @Mapping(source = "antecedents", target = "antecedents")
    @Mapping(source = "allergies", target = "allergies")

    @Mapping(source = "experience", target = "experience")
    @Mapping(source = "adresse", target = "adresse")
    @Mapping(source = "diplome", target = "diplome")
    @Mapping(source = "dateEmbauche", target = "dateEmbauche")
    @Mapping(source = "typeContrat", target = "typeContrat")
    @Mapping(source = "salaire", target = "salaire")
    @Mapping(source = "banque", target = "banque")
    @Mapping(source = "numeroCompte", target = "numeroCompte")
    @Mapping(source = "specialite", target = "specialite")

    // ❌ PAS DE BOUCLE
    @Mapping(target = "enseignant", ignore = true)

    DossierEnseignantDtoResponse toEnseignantResponse(DossierEntity entity);


    // =========================
    // UPDATE ENSEIGNANT
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)

    // Champs élève ignorés
    @Mapping(target = "fileNaissance", ignore = true)
    @Mapping(target = "fileRadiation", ignore = true)
    @Mapping(target = "fileSante", ignore = true)
    @Mapping(target = "ecolePrecedente", ignore = true)
    @Mapping(target = "identifiantScolaire", ignore = true)
    @Mapping(target = "langueEtude", ignore = true)
    @Mapping(target = "groupeSanguin", ignore = true)
    @Mapping(target = "type", ignore = true)

    void updateEntity(@MappingTarget DossierEntity entity, DossierEnseignantDtoRequest request);


    // =========================
    // CONVERTERS
    // =========================
    @Named("stringToTypeDossier")
    default TypeDossier stringToTypeDossier(String type) {
        if (type == null) return null;
        try {
            return TypeDossier.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TypeDossier.ELEVE;
        }
    }

    @Named("typeDossierToString")
    default String typeDossierToString(TypeDossier type) {
        return type != null ? type.name() : null;
    }
}