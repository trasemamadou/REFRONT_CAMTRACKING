package com.groupeisi.ms1_gestschool.cour.seance;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Table(name = "seance")
@Entity
@Data
@ToString
public class SeanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private String description;
    private boolean archive;
    @ManyToOne(optional = false)
   @JoinColumn(name = "matiere_name", nullable = false)
    private MatiereEntity matiere;
}
