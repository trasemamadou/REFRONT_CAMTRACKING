package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<ClasseEntity, String>, JpaSpecificationExecutor<ClasseEntity> {
    Optional<ClasseEntity> findFirstByNom(String nom);
    Page<ClasseEntity> findAllByEcole_Nom(String nom, Pageable pageable);
}
