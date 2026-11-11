package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprentissageDtoRequest {
    private String id;
    @NotNull(message = "La classe est obligatoire")
    private String classeId;
    @NotNull(message = "L'élève est obligatoire")
    private String studentId;
}
