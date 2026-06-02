package com.edgecloud.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.edgecloud.auth.dto.LoginRequest;
import com.edgecloud.auth.dto.LoginResponse;
import com.edgecloud.auth.dto.RegisterRequest;
import com.edgecloud.auth.dto.RegisterResponse;
import com.edgecloud.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}