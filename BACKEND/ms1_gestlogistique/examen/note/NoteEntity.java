package com.groupeisi.ms1_gestschool.examen.note;

import com.groupeisi.ms1_gestschool.examen.bulletin.BulletinEntity;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private Double valeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_id", nullable = false)
    private BulletinEntity bulletin;

    // Version avec relation ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessionmatiere_id", referencedColumnName = "id")
    private EpreuveEntity sessionmatiere;

    // Champ calculé pour l'ID (optionnel)
    @Column(name = "sessionmatiere_id", insertable = false, updatable = false)
    private String sessionmatiereId;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private Double coefficient;

    @Column(nullable = false)
    private String typeEvaluation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}