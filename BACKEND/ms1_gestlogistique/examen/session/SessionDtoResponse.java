package com.groupeisi.ms1_gestschool.examen.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDtoResponse {

    private String id;
    private String libelle;
    private LocalDate dateDebut;
}
