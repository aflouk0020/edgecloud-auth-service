package com.edgecloud.auth.dto;

public record TokenValidationResponse(
        boolean valid,
        String email,
        String userId,
        String role
) {
}