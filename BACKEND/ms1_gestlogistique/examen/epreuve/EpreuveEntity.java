package com.groupeisi.ms1_gestschool.examen.epreuve;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "epreuve")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "matiere_id")
    private MatiereEntity matiere;

    @ManyToOne
    @JoinColumn(name = "participation_id")
    private ParticipationEntity participation;

    @Column(nullable = false)
    private Double coef = 1.0;
}