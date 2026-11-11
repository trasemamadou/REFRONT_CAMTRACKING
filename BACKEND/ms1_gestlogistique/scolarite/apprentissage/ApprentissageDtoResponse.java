package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApprentissageDtoResponse {
    private String id;
   // private ClasseDtoResponse classe;
    private String classeNom;
    private EleveDtoResponse eleve;
    private StatutApprentissage statut;
    private Date dateDebut;
    private Date dateFin;
}
