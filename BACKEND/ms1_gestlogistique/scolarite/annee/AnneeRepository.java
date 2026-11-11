package com.groupeisi.ms1_gestschool.scolarite.annee;

import com.groupeisi.ms1_gestschool.scolarite.niveau.NiveauEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnneeRepository extends JpaRepository<AnneeEntity, String>, JpaSpecificationExecutor<AnneeEntity> {
}
