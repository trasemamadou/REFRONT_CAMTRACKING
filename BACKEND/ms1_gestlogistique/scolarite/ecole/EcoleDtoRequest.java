package com.groupeisi.ms1_gestschool.scolarite.ecole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EcoleDtoRequest {

    @NotBlank(message = "Le nom de l'école est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(min = 9, max = 20, message = "Le téléphone doit contenir entre 9 et 20 caractères")
    private String telephone;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String emailPrescripteur;

    @NotBlank(message = "Le nom du directeur est obligatoire")
    @Size(max = 100, message = "Le nom du directeur ne doit pas dépasser 100 caractères")
    private String directeur;
    @NotBlank(message = "La photo du logo est obligatoire")
    private String photoLogo;
}