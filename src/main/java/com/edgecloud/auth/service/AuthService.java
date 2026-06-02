package com.edgecloud.auth.service;

import com.edgecloud.auth.dto.LoginRequest;
import com.edgecloud.auth.dto.LoginResponse;
import com.edgecloud.auth.dto.RegisterRequest;
import com.edgecloud.auth.dto.RegisterResponse;
import com.edgecloud.auth.dto.TokenValidationResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    TokenValidationResponse validateToken(String authorizationHeader);
}
