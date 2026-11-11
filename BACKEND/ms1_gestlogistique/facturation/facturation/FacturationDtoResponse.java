package com.groupeisi.ms1_gestschool.facturation.facturation;

import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class FacturationDtoResponse {
    private  String id;
    private String libelle;
    private String object;
    private String description;
  //  private ClasseDtoResponse classe;
    private ParametrageclasseDtoResponse parametrageclasse;
    private EleveDtoResponse student;
    private Double montant;
    private String statut;

    private String responsableFinancier;
    private String modePaiement;

}
