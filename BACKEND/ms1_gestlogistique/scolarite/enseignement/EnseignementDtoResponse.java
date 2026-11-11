package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoResponse;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereDtoResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnseignementDtoResponse {
    private String id;
    private ClasseDtoResponse classe;
    private EnseignantDtoResponse enseignant;
    private MatiereDtoResponse matiere;
}
