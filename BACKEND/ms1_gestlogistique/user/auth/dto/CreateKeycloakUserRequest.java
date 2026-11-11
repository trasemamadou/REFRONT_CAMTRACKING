package com.groupeisi.ms1_gestschool.user.auth.dto;

import java.util.List;

public record CreateKeycloakUserRequest(
        String personneId,
        String password,
        List<String> roles
) {}