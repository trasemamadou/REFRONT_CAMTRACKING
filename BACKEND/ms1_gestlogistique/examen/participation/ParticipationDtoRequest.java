// File: SessionclasseDtoRequest.java
package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveDtoRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParticipationDtoRequest {
    @NotNull(message = "L'ID de la classe est obligatoire")
    private String classeId;

    @NotNull(message = "L'ID de la session est obligatoire")
    private String sessionId;

    // Nouveau champ pour lier l'instance de composition
    private String compositionInstanceId;

    private List<EpreuveDtoRequest> matieres;
}