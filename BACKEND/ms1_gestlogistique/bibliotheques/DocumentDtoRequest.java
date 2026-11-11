package com.groupeisi.ms1_gestschool.bibliotheques;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDtoRequest {

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private String description;

    private LocalDate datePublication;

    @NotBlank(message = "Le hash du document est obligatoire")
    private String hash;

    private boolean archive;

    private String fileType;

    private String fileExtension;
}