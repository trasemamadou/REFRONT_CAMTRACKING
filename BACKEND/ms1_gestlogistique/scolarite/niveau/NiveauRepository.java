package com.groupeisi.ms1_gestschool.scolarite.niveau;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NiveauRepository extends JpaRepository<NiveauEntity, String>, JpaSpecificationExecutor<NiveauEntity> {
}
