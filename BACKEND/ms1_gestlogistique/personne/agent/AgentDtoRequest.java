package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDtoRequest {

    /* =========================
       ÉTAPE 1 : Infos personnelles
       ========================= */
    private Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, message = "Le prénom doit avoir au moins 2 caractères")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, message = "Le nom doit avoir au moins 2 caractères")
    private String nom;
    private String photoProfil;

    @NotBlank(message = "Le sexe est obligatoire")
    @Pattern(regexp = "M|F", message = "Le sexe doit être 'M' ou 'F'")
    private String sexe;

    private String matricule;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9+ ]{8,15}$", message = "Format de téléphone invalide")
    private String telephone;

    @Email(message = "Format d'email invalide")
    private String email;
    private String role;
    private StatutPerson statut;
    private LocalDateTime dateCreation;
}