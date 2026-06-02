package com.edgecloud.auth.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.edgecloud.auth.dto.LoginRequest;
import com.edgecloud.auth.dto.LoginResponse;
import com.edgecloud.auth.dto.RegisterRequest;
import com.edgecloud.auth.dto.RegisterResponse;
import com.edgecloud.auth.dto.TokenValidationResponse;
import com.edgecloud.auth.entity.User;
import com.edgecloud.auth.exception.AuthenticationException;
import com.edgecloud.auth.exception.DuplicateEmailException;
import com.edgecloud.auth.repository.UserRepository;
import com.edgecloud.auth.security.JwtService;
import com.edgecloud.auth.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User(email, passwordHash, request.getRole());
        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getRole().name()
        );
    }
    
    @Override
    public TokenValidationResponse validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Invalid token");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            throw new AuthenticationException("Invalid token");
        }

        return new TokenValidationResponse(
                true,
                jwtService.extractEmail(token),
                jwtService.extractUserId(token),
                jwtService.extractRole(token)
        );
    }
}