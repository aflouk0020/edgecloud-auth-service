package com.edgecloud.auth.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.edgecloud.auth.entity.User;
import com.edgecloud.auth.entity.UserRole;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    private User testUser() {
        User user = new User(
                "taha@example.com",
                "$2a$10$dummyhash",
                UserRole.OPERATOR
        );
        user.onCreate();
        return user;
    }

    @Test
    void generateTokenShouldCreateValidToken() {
        User user = testUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void generatedTokenShouldContainUserEmail() {
        User user = testUser();

        String token = jwtService.generateToken(user);

        assertEquals("taha@example.com", jwtService.extractEmail(token));
    }

    @Test
    void generatedTokenShouldContainUserRole() {
        User user = testUser();

        String token = jwtService.generateToken(user);

        assertEquals("OPERATOR", jwtService.extractRole(token));
    }

    @Test
    void generatedTokenShouldContainUserId() {
        User user = testUser();

        String token = jwtService.generateToken(user);

        assertEquals(user.getId().toString(), jwtService.extractUserId(token));
    }

    @Test
    void invalidTokenShouldReturnFalse() {
        assertFalse(jwtService.isTokenValid("invalid-token"));
    }
}
