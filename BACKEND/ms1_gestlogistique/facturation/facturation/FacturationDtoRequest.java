package com.groupeisi.ms1_gestschool.facturation.facturation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacturationDtoRequest
{
    private String id;
    private String libelle;
    @NotBlank(message = "La description ne peut pas être vide")
    private String description;
    @NotBlank(message = "L'object ne peux pas être vide")
    private String object;
    private Double montant;
    private String statut;
    private String parametrageclasseId;
    private String studentId;
    private String responsableFinancier;
    private String modePaiement;

}
