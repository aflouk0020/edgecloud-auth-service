package com.edgecloud.auth.dto;

import java.util.UUID;

import com.edgecloud.auth.entity.UserRole;

public record NotificationIdentityResponse(UUID userId, String email, UserRole platformRole) {
}
