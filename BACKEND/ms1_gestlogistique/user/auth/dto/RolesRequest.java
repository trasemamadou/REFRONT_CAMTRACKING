package com.groupeisi.ms1_gestschool.user.auth.dto;

import java.util.List;

public class RolesRequest {
    private List<String> roles;

    // Constructeurs, getters et setters
    public RolesRequest() {}

    public RolesRequest(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
