package com.groupeisi.ms1_gestschool.personne.dossier;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DossierRepository extends JpaRepository<DossierEntity, String>, JpaSpecificationExecutor<DossierEntity> {
    List<DossierEntity> findByType(TypeDossier type);
    Optional<DossierEntity> findByIdAndType(String id, TypeDossier type);
    boolean existsByIdAndType(String id, TypeDossier type);
    Page<PersonneEntity> findBySpecialiteContainingIgnoreCase(String specialite, Pageable pageable);
}