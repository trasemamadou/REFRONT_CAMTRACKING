package com.groupeisi.ms1_gestschool.user.auth.controller;
import com.groupeisi.ms1_gestschool.user.auth.service.KeycloakService;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users")
@CrossOrigin(origins = "http://localhost:4200")
public class KeycloakUserController {

    private final KeycloakService keycloakService;

    public KeycloakUserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    // Création d'un utilisateur (accessible à tous ou selon votre politique)
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRepresentation user, @RequestParam String password) {
        String userId = keycloakService.createUser(user, password);
        return ResponseEntity.ok(userId);
    }

    // Endpoint d'inscription publique (alias de createUser)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRepresentation user, @RequestParam String password) {
        String userId = keycloakService.createUser(user, password);
        return ResponseEntity.ok(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserRepresentation> getUsers() {
        return keycloakService.getUsers();
    }

    @GetMapping("/{id}")
    public UserRepresentation getUserById(@PathVariable String id) {
        return keycloakService.getUserById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable String id, @RequestBody UserRepresentation user) {
        keycloakService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        keycloakService.deleteUser(id);
    }

    // ---------- NOUVELLES FONCTIONNALITÉS ----------

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public void resetPassword(@PathVariable String id, @RequestParam String newPassword) {
        keycloakService.resetPassword(id, newPassword);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRealmRoles(@PathVariable String id, @RequestBody List<String> roles) {
        keycloakService.assignRealmRoles(id, roles);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleRepresentation> getUserRealmRoles(@PathVariable String id) {
        return keycloakService.getUserRealmRoles(id);
    }
}