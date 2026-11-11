package com.groupeisi.ms1_gestschool.cour.matiere;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MatiereRepository extends JpaRepository<MatiereEntity, String>,  JpaSpecificationExecutor<MatiereEntity> {
    Optional<MatiereEntity> findById(String name);
    Page<MatiereEntity> findAllByEcole_Nom(String nom, Pageable pageable);
}
