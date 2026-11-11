package com.groupeisi.ms1_gestschool.cour.seance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<SeanceEntity, Long> {
  List<SeanceEntity> getSessionsByMatiereName(String matiereName);
}
