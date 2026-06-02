package com.edgecloud.auth.dto;

public record LoginResponse(
        String token,
        String role
) {}