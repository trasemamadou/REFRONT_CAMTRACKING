package com.groupeisi.ms1_gestschool.examen.epreuve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpreuveRepository extends JpaRepository<EpreuveEntity, String> {

    boolean existsByMatiere_IdAndParticipation_Id(String matiereId, String participationId);

    Optional<EpreuveEntity> findByMatiere_IdAndParticipation_Id(String matiereId, String participationId);

    List<EpreuveEntity> findByParticipation_Id(String participationId);

    List<EpreuveEntity> findByMatiere_Id(String matiereId);

    void deleteByParticipation_Id(String participationId);
}