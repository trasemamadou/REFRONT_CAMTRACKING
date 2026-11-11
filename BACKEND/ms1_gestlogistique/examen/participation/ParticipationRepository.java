package com.groupeisi.ms1_gestschool.examen.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationEntity, String> {

    // Vérifie si une session-classe existe déjà pour cette classe et session
    boolean existsByClasseIdAndSessionId(String classeId, String sessionId);

    // Recherche par ID de classe
    java.util.List<ParticipationEntity> findByClasseId(String classeId);

    // Recherche par ID de session
    java.util.List<ParticipationEntity> findBySessionId(String sessionId);
}