package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametrageclasseRepository extends JpaRepository<ParametrageclasseEntity, String> {

    /* ===================== RECHERCHES PAR CLASSE OU PARAMETRAGE ===================== */

    List<ParametrageclasseEntity> findByClasseId(String classeId);

    List<ParametrageclasseEntity> findByParametrageId(String parametrageId);
   ParametrageclasseEntity findByClasseIdAndParametrage_Object(String classseId, FacturationObject parametrageObject);

    /* ===================== VÉRIFICATION D’EXISTENCE ===================== */

    @Query("SELECT COUNT(p) > 0 FROM ParametrageclasseEntity p " +
            "WHERE p.classe.id = :classeId AND p.parametrage.id = :parametrageId")
    boolean existsByClasseIdAndParametrageId(@Param("classeId") String classeId,
                                             @Param("parametrageId") String parametrageId);

    Optional<ParametrageclasseEntity> findByClasseIdAndParametrageId(String classeId, String parametrageId);

    /* ===================== FILTRAGE PAR ACTIF ===================== */

    List<ParametrageclasseEntity> findByActifTrue();
}
