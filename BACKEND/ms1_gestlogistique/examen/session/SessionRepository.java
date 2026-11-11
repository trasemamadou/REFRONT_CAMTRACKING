package com.groupeisi.ms1_gestschool.examen.session;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, String>, JpaSpecificationExecutor<SessionEntity> {
    Page<SessionEntity> findAll(Pageable pageable);
}
