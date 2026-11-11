package com.groupeisi.ms1_gestschool.scolarite.enseignement;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnseignementRepository extends JpaRepository<EnseignementEntity, String> {
    List<EnseignementEntity> getAllByClasseId(String classeId);
}
