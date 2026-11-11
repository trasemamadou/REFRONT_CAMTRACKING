package com.groupeisi.ms1_gestschool.bibliotheques;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDtoResponse {
    private Long id;
    private String libelle;
    private String description;
    private LocalDate datePublication;
    private String hash;
    private boolean archive;
    private String fileType;
    private String fileExtension;
}