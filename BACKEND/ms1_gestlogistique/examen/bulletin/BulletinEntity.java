package com.groupeisi.ms1_gestschool.examen.bulletin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulletin")
@Getter
@Setter
public class BulletinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String eleveId;

    @Column(nullable = false)
    private String sessionclasseId;

    @Column(nullable = false)
    private String trimestre;

    @Column(nullable = false)
    private Double moyenneGenerale;

    private String appreciation;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}