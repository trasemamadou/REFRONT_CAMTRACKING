package com.groupeisi.ms1_gestschool.facturation.parametrage;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class ParametrageDtoRequest {
    @NotBlank(message = "La description ne peut pas être vide")
    private String libelle;
    @NotBlank(message = "L'object ne peux pas être vide")
    private String object;
    @NotBlank(message = "La date de debut ne peux pas être vide")
    private Date dateDebut;
    @NotBlank(message = "La date de fin ne peux pas être vide")
    private Date dateFin;
    @NotBlank(message = "Le montant ne peux pas être vide")
    private Double montant;
    private String ecole;
}
