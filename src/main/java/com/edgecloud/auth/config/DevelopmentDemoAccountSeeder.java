package com.edgecloud.auth.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.edgecloud.auth.entity.User;
import com.edgecloud.auth.entity.UserRole;
import com.edgecloud.auth.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "edgecloud.demo-account.enabled", havingValue = "true")
public class DevelopmentDemoAccountSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final UserRole role;

    public DevelopmentDemoAccountSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${edgecloud.demo-account.email:}") String email,
            @Value("${edgecloud.demo-account.password:}") String password,
            @Value("${edgecloud.demo-account.role:ADMIN}") UserRole role) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email.trim().toLowerCase();
        this.password = password;
        this.role = role;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Demo account email and password are required when seeding is enabled");
        }

        if (!userRepository.existsByEmail(email)) {
            userRepository.save(new User(email, passwordEncoder.encode(password), role));
        }
    }
}
