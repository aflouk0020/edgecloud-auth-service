package com.edgecloud.auth.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.edgecloud.auth.entity.User;
import com.edgecloud.auth.entity.UserRole;
import com.edgecloud.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DevelopmentDemoAccountSeederTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void createsMissingAdminWithEncodedPassword() throws Exception {
        when(userRepository.existsByEmail("dashboard.admin@edgecloud.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        var seeder = seeder(" Dashboard.Admin@EdgeCloud.com ", "Password123!");

        seeder.run(new DefaultApplicationArguments());

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getEmail()).isEqualTo("dashboard.admin@edgecloud.com");
        org.assertj.core.api.Assertions.assertThat(saved.getPasswordHash()).isEqualTo("encoded-password");
        org.assertj.core.api.Assertions.assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void leavesExistingAccountUntouched() throws Exception {
        when(userRepository.existsByEmail("dashboard.admin@edgecloud.com")).thenReturn(true);

        seeder("dashboard.admin@edgecloud.com", "Password123!").run(new DefaultApplicationArguments());

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectsMissingCredentialsWhenEnabled() {
        assertThatThrownBy(() -> seeder("", "").run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class);
    }

    private DevelopmentDemoAccountSeeder seeder(String email, String password) {
        return new DevelopmentDemoAccountSeeder(
                userRepository, passwordEncoder, email, password, UserRole.ADMIN);
    }
}
