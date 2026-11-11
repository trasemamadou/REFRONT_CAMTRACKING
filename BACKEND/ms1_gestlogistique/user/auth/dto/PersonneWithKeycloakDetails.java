package com.groupeisi.ms1_gestschool.user.auth.dto;

import com.groupeisi.ms1_gestschool.personne.Role;
import com.groupeisi.ms1_gestschool.personne.Sexe;
import java.time.LocalDate;
import java.util.List;

public record PersonneWithKeycloakDetails(
        String id,
        String nom,
        String prenom,
        String email,
        String telephone,
        LocalDate dateNaissance,
        Sexe sexe,
        Role role,
        String keycloakId,
        Boolean keycloakEnabled,
        List<String> roles
) {}