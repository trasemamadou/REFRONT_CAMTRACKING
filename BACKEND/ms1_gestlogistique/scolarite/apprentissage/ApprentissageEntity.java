package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "apprentissage")
@Getter
@Setter
public class ApprentissageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private ClasseEntity classe;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private PersonneEntity student;
    private Date dateDebut;
    private Date dateFin;
    @Column(nullable = false)
    private boolean active = true;
}
