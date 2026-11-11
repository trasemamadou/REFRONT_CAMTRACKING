package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EnseignantDtoRequest {

    private String id;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, message = "Le prénom doit avoir au moins 2 caractères")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, message = "Le nom doit avoir au moins 2 caractères")
    private String nom;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le lieu de naissance est obligatoire")
    private String lieuNaissance;

    @NotBlank(message = "La photo de profil est obligatoire")
    private String photoProfil;

    @NotBlank(message = "Le sexe est obligatoire")
    @Pattern(regexp = "M|F", message = "Le sexe doit être 'M' ou 'F'")
    private String sexe;

    @NotBlank(message = "La nationalité est obligatoire")
    private String nationalite;

    private String situationMatrimoniale;

    private String matricule;

    private String role;  // sera converti automatiquement en Role (enum)

    @NotBlank(message = "Le numéro CNI est obligatoire")
    @Size(min = 8, max = 30, message = "Le numéro CNI doit avoir entre 8 et 30 caractères")
    private String numeroCNI;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String password;   // optionnel

    private boolean archive;

    private StatutPerson statut;

    private LocalDateTime dateCreation;  // optionnel, géré par @PrePersist
    private Long dossierId;
}