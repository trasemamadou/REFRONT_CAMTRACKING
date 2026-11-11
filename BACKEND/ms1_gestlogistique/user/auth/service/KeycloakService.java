package com.groupeisi.ms1_gestschool.user.auth.service;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakService {
    String createUser(UserRepresentation user, String password);
    List<UserRepresentation> getUsers();
    UserRepresentation getUserById(String userId);
    void updateUser(String userId, UserRepresentation user);
    void deleteUser(String userId);

    // Nouvelles fonctionnalités
    void resetPassword(String userId, String newPassword);
    void assignRealmRoles(String userId, List<String> roles);
    List<RoleRepresentation> getUserRealmRoles(String userId);

}