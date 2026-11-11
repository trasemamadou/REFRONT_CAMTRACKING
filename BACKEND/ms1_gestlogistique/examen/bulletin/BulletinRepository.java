package com.groupeisi.ms1_gestschool.examen.bulletin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulletinRepository extends JpaRepository<BulletinEntity, String> {
    // Méthodes de recherche personnalisées
    List<BulletinEntity> findByEleveId(String eleveId);
    List<BulletinEntity> findBySessionclasseId(String sessionclasseId);
}