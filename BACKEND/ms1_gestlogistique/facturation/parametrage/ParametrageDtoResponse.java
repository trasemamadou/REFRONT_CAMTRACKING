package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.scolarite.ecole.EcoleDtoResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class ParametrageDtoResponse {
private String id;
private String object;
private String libelle;
private Date dateDebut;
private Date dateFin;
private EcoleDtoResponse ecole;
}
