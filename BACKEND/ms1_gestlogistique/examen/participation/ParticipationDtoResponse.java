// File: SessionclasseDtoResponse.java
package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionInstanceResponseDTO; // À créer si besoin
import com.groupeisi.ms1_gestschool.examen.session.SessionDtoResponse;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveDtoResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParticipationDtoResponse {
    private String id;
    private ClasseDtoResponse classe;
    private SessionDtoResponse session;
    private CompositionInstanceResponseDTO compositionInstance; // ou simplement l'ID
    private List<EpreuveDtoResponse> matieres;
}