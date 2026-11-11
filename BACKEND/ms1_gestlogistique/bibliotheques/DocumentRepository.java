package com.groupeisi.ms1_gestschool.bibliotheques;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByArchive(boolean archive);
    List<DocumentEntity> findByLibelleContainingIgnoreCase(String libelle);
    Optional<DocumentEntity> findByHash(String hash);
    boolean existsByHash(String hash);
}