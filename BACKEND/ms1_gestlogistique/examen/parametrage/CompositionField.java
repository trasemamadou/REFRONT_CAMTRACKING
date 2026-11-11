package com.groupeisi.ms1_gestschool.examen.parametrage;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "composition_field")
public class CompositionField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "label", nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FieldType type;

    @Column(name = "formule", columnDefinition = "TEXT")
    private String formule;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    private CompositionInstance instance;
}