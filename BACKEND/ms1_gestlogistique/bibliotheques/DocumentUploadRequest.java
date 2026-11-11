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
public class DocumentUploadRequest {
    private String libelle;
    private String description;
    private LocalDate datePublication;
    private String hash;
    private boolean archive;
    private String fileContent;
    private String fileName;
    private String fileType;
    private long fileSize;
    private Long documentId;
}