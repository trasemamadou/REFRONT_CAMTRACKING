package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageDtoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametrageclasseDtoResponse {
    private String id;
    private ClasseDtoResponse classe;
    private ParametrageDtoResponse parametrage;
    private Double montant;
    private boolean actif;
}