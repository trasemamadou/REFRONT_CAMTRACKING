package com.groupeisi.ms1_gestschool.examen.parametrage;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompositionInstance {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String referenceFieldName;
    @Column(columnDefinition = "TEXT")
    private String referenceFormula;
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompositionField> fields;
}