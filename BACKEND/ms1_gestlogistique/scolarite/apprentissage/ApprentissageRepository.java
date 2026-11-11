package com.groupeisi.ms1_gestschool.scolarite.apprentissage;


import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprentissageRepository extends JpaRepository<ApprentissageEntity, String> {
    List<ApprentissageEntity> getAllByClasseId(String classeId);
    Page<ApprentissageEntity> findAllByClasseId(String classeId, Pageable pageable);
    List<ApprentissageEntity> findAllByClasseId(String classeId);
    Page<ApprentissageEntity> findAll(Pageable pageable);
    long countByClasseIdAndActiveTrue(String classeId);
}
