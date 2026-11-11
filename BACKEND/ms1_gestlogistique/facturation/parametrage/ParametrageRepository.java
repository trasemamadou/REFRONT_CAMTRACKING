package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametrageRepository
        extends JpaRepository<ParametrageEntity, String>,  JpaSpecificationExecutor<ParametrageEntity> {


    Optional<ParametrageEntity> findByObject( FacturationObject object);
    Page<ParametrageEntity> findAllByEcole_Nom(String nom, Pageable pageable);

}

