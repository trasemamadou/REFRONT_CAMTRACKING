package com.groupeisi.ms1_gestschool.personne;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<PersonneEntity, String>, JpaSpecificationExecutor<PersonneEntity> {

    // ========== RECHERCHES PAR IDENTIFIANTS ==========

    // Recherche par matricule
    Optional<PersonneEntity> findByMatricule(String matricule);

    // Recherche par matricule et rôle
    Optional<PersonneEntity> findByMatriculeAndRole(String matricule, Role role);

    // Recherche par téléphone
    Optional<PersonneEntity> findByTelephone(String telephone);

    // Recherche par email
    Optional<PersonneEntity> findByEmail(String email);

    // Recherche par ID et rôle
    Optional<PersonneEntity> findByIdAndRole(String id, Role role);
    Optional<PersonneEntity> findByKeycloakId(String keycloakId);

    // ========== RECHERCHES AVEC DOSSIER ==========

    @Query("""
       SELECT p FROM PersonneEntity p
       LEFT JOIN FETCH p.dossier d
       WHERE p.id = :id
       """)
    Optional<PersonneEntity> findPersonneWithDossier(@Param("id") String id);

    @Query("""
       SELECT p FROM PersonneEntity p
       LEFT JOIN FETCH p.dossier d
       WHERE p.id = :id AND p.role = :role
       """)
    Optional<PersonneEntity> findPersonneWithDossierByIdAndRole(@Param("id") String id, @Param("role") Role role);


    // ========== RECHERCHES PAR RÔLE ==========

    // Liste des personnes par rôle
    List<PersonneEntity> findByRole(Role role);

    // Page des personnes par rôle
    Page<PersonneEntity> findByRole(Role role, Pageable pageable);

    // Personne par rôle avec dossier
    @Query("""
       SELECT p FROM PersonneEntity p
       LEFT JOIN FETCH p.dossier d
       WHERE p.role = :role
       """)
    List<PersonneEntity> findByRoleWithDossier(@Param("role") Role role);

    @Query("""
       SELECT p FROM PersonneEntity p
       LEFT JOIN FETCH p.dossier d
       WHERE p.role = :role
       """)
    Page<PersonneEntity> findByRoleWithDossier(@Param("role") Role role, Pageable pageable);


    // ========== VÉRIFICATIONS D'EXISTENCE ==========

    // Par matricule
    boolean existsByMatricule(String matricule);

    // Par email
    boolean existsByEmail(String email);

    // Par téléphone
    boolean existsByTelephone(String telephone);

    // Par numéro CNI
    boolean existsByNumeroCNI(String numeroCNI);


    // ========== RECHERCHES AVEC CRITÈRES ==========

    // Toutes les personnes paginées
    Page<PersonneEntity> findAll(Pageable pageable);

    // Recherche par nom ou prénom (tous rôles confondus)
    @Query("""
       SELECT p FROM PersonneEntity p
       WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%'))
       """)
    Page<PersonneEntity> searchByNomOrPrenom(@Param("search") String search, Pageable pageable);

    // Recherche par nom ou prénom avec rôle spécifique
    @Query("""
       SELECT p FROM PersonneEntity p
       WHERE (LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')))
          AND p.role = :role
       """)
    Page<PersonneEntity> searchByNomOrPrenomAndRole(@Param("search") String search, @Param("role") Role role, Pageable pageable);

    // Recherche par statut
    Page<PersonneEntity> findByStatut(StatutPerson statut, Pageable pageable);

    // Recherche par statut et rôle
    Page<PersonneEntity> findByStatutAndRole(StatutPerson statut, Role role, Pageable pageable);


    // ========== RECHERCHES SPÉCIFIQUES POUR ENSEIGNANTS ==========

    // Enseignants par spécialité (via le dossier)
    @Query("""
       SELECT p FROM PersonneEntity p
       JOIN p.dossier d
       WHERE p.role = :role
          AND LOWER(d.specialite) LIKE LOWER(CONCAT('%', :specialite, '%'))
       """)
    Page<PersonneEntity> findEnseignantsBySpecialite(@Param("specialite") String specialite, @Param("role") Role role, Pageable pageable);

    // Enseignants par type de contrat (via le dossier)
    @Query("""
       SELECT p FROM PersonneEntity p
       JOIN p.dossier d
       WHERE p.role = :role
          AND d.typeContrat = :typeContrat
       """)
    Page<PersonneEntity> findEnseignantsByTypeContrat(@Param("typeContrat") TypeContrat typeContrat, @Param("role") Role role, Pageable pageable);


    // ========== RECHERCHES AVANCÉES ==========

    // Recherche multicritères pour enseignants
    @Query("""
       SELECT p FROM PersonneEntity p
       LEFT JOIN p.dossier d
       WHERE p.role = :role
          AND (:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%')))
          AND (:prenom IS NULL OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')))
          AND (:statut IS NULL OR p.statut = :statut)
          AND (:archive IS NULL OR p.archive = :archive)
          AND (:specialite IS NULL OR LOWER(d.specialite) LIKE LOWER(CONCAT('%', :specialite, '%')))
          AND (:typeContrat IS NULL OR d.typeContrat = :typeContrat)
       """)
    Page<PersonneEntity> searchEnseignants(
            @Param("role") Role role,
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("statut") StatutPerson statut,
            @Param("archive") Boolean archive,
            @Param("specialite") String specialite,
            @Param("typeContrat") TypeContrat typeContrat,
            Pageable pageable
    );


    // ========== COMPTAGES ==========

    // Nombre d'enseignants actifs
    long countByRoleAndStatut(Role role, StatutPerson statut);

    // Nombre d'enseignants par spécialité
    @Query("""
       SELECT COUNT(p) FROM PersonneEntity p
       JOIN p.dossier d
       WHERE p.role = :role
          AND LOWER(d.specialite) = LOWER(:specialite)
       """)
    long countEnseignantsBySpecialite(@Param("role") Role role, @Param("specialite") String specialite);
    // Ajoutez ces méthodes dans votre PersonneRepository existant

// ========== NOUVELLES MÉTHODES POUR KEYCLOAK ==========


    // Vérifier si un username existe déjà
    boolean existsByUsername(String username);

    // Trouver toutes les personnes avec un compte Keycloak
    List<PersonneEntity> findAllByKeycloakIdIsNotNull();


    // Trouver par username
    Optional<PersonneEntity> findByUsername(String username);
}