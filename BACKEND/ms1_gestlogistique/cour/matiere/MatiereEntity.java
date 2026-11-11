package com.groupeisi.ms1_gestschool.cour.matiere;


import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name="matiere")
public class MatiereEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean  archive;
    @ManyToOne
    @JoinColumn(name = "ecole_nom")
    private EcoleEntity ecole;
}
