package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveEntity;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionInstance;
import com.groupeisi.ms1_gestschool.examen.session.SessionEntity;
import com.groupeisi.ms1_gestschool.scolarite.enseignement.EnseignementEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
// File: SessionclasseEntity.java (extrait modifié)
@Entity
@Getter
@Setter
@Table(name = "sessionclasse")
public class ParticipationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private ClasseEntity classe;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private SessionEntity session;

    // Nouvelle relation vers CompositionInstance
    @ManyToOne
    @JoinColumn(name = "composition_instance_id")
    private CompositionInstance compositionInstance;

    @OneToMany(mappedBy = "sessionclasse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnseignementEntity> enseignements;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sessionclasse_id")
    private List<EpreuveEntity> matieres;
}