package com.groupeisi.ms1_gestschool.templates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    Optional<TemplateEntity> findByCode(String code);
    List<TemplateEntity> findByType(TemplateType type);
    List<TemplateEntity> findByActiveTrue();
    @Query("SELECT t FROM TemplateEntity t WHERE t.type = :type AND t.active = true")
    List<TemplateEntity> findActiveByType(@Param("type") TemplateType type);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}