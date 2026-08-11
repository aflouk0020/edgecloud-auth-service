package com.edgecloud.auth.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationIdentityRequest(
        @NotNull @Size(min = 1, max = 100) List<@Valid @NotNull UUID> userIds) {
}
