package com.groupeisi.ms1_gestschool.scolarite.ecole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoleRepository extends JpaRepository<EcoleEntity, String> {
    boolean existsByNom(String nom);
    boolean existsByEmailPrescripteur(String emailPrescripteur);
}