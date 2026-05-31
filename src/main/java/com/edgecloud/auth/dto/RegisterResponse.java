package com.edgecloud.auth.dto;

import java.util.UUID;

import com.edgecloud.auth.entity.UserRole;

public class RegisterResponse {

    private UUID id;
    private String email;
    private UserRole role;

    public RegisterResponse(UUID id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
