package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enseignement")
@Getter
@Setter
public class EnseignementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private ClasseEntity classe;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personne_id", nullable = false)
    private PersonneEntity enseignant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_name", nullable = false)
    private MatiereEntity matiere;
    @ManyToOne
    @JoinColumn(name = "sessionclasse_id", nullable = false)
    private ParticipationEntity sessionclasse;
}
