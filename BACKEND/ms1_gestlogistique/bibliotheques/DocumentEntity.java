package com.groupeisi.ms1_gestschool.bibliotheques;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private LocalDate datePublication;

    @Column(nullable = false, unique = true)
    private String hash;

    @Column(nullable = false)
    @Builder.Default
    private boolean archive = false;

    @Column(nullable = true)
    private String filePath;

    // AJOUTER CE CHAMP POUR STOCKER LE TYPE MIME
    @Column(nullable = true)
    private String fileType;

    // AJOUTER CE CHAMP POUR STOCKER L'EXTENSION
    @Column(nullable = true)
    private String fileExtension;
}