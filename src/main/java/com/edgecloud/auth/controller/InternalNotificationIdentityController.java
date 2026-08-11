package com.edgecloud.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edgecloud.auth.dto.NotificationIdentityRequest;
import com.edgecloud.auth.dto.NotificationIdentityResponse;
import com.edgecloud.auth.service.NotificationIdentityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/users")
public class InternalNotificationIdentityController {

    private final NotificationIdentityService identityService;

    public InternalNotificationIdentityController(NotificationIdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/notification-identities")
    public ResponseEntity<List<NotificationIdentityResponse>> identities(
            @Valid @RequestBody NotificationIdentityRequest request) {
        return ResponseEntity.ok(identityService.resolve(request));
    }
}
