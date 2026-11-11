package com.groupeisi.ms1_gestschool.user.auth.dto;
import java.util.List;

public record UserProfileDto(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        List<String> realmRoles,
        List<String> clientRoles,
        boolean enabled
) {}