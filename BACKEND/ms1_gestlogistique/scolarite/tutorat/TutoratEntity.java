package com.groupeisi.ms1_gestschool.scolarite.tutorat;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tutorat")
public class TutoratEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    private PersonneEntity eleve;

    @ManyToOne
    @JoinColumn(name = "tuteur_id", nullable = false)
    private PersonneEntity tuteur;
}
