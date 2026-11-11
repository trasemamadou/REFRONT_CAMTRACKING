package com.groupeisi.ms1_gestschool.examen.session;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class SessionDtoRequest {

    private String id;
    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;
    private LocalDate dateDebut;
}
