package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutoratRepository extends JpaRepository<TutoratEntity, Long> {
}
