package com.edgecloud.auth.service;

import com.edgecloud.auth.dto.RegisterRequest;
import com.edgecloud.auth.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
}
