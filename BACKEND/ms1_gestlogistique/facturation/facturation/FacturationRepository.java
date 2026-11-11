package com.groupeisi.ms1_gestschool.facturation.facturation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FacturationRepository extends JpaRepository<FacturationEntity, String>, JpaSpecificationExecutor<FacturationEntity> {

    Page<FacturationEntity> findAllByParametrageclasse_Parametrage_Ecole_Nom(String ecoleNom, Pageable pageable);

    // *** NOUVELLES MÉTHODES POUR LA GESTION DU LIBELLÉ ***

    // Recherche par libellé (utile pour avoir une méthode explicite)
    Optional<FacturationEntity> findByLibelle(String libelle);

    // Vérifier si un libellé existe déjà
    boolean existsByLibelle(String libelle);

    // Compter les facturations entre deux dates (pour la génération de libellé)
    long countByDateGenerationBetween(LocalDateTime start, LocalDateTime end);
}