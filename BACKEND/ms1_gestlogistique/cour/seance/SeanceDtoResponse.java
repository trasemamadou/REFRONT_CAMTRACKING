package com.groupeisi.ms1_gestschool.cour.seance;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereDtoResponse;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SeanceDtoResponse{
 private  Long id;
 private String name;
 private LocalDateTime beginDate;
 private LocalDateTime endDate;
 private String description;
 private boolean archive;
 private MatiereDtoResponse course;
}