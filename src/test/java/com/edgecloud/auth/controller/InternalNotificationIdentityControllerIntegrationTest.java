package com.edgecloud.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.edgecloud.auth.entity.User;
import com.edgecloud.auth.entity.UserRole;
import com.edgecloud.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "edgecloud.internal.service-key=test-internal-key")
@AutoConfigureMockMvc
class InternalNotificationIdentityControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void resolvesOneAuthoritativeIdentityWithoutSecrets() throws Exception {
        User user = userRepository.save(new User(
                "one-" + UUID.randomUUID() + "@example.test", "password-hash-must-not-leak", UserRole.OPERATOR));

        String body = perform(List.of(user.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(user.getId().toString()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].platformRole").value("OPERATOR"))
                .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("password", "passwordHash", "password-hash-must-not-leak");
    }

    @Test
    void deduplicatesInputOmitsUnknownIdsAndOrdersDeterministically() throws Exception {
        User first = userRepository.save(new User(
                "first-" + UUID.randomUUID() + "@example.test", "hash", UserRole.ADMIN));
        User second = userRepository.save(new User(
                "second-" + UUID.randomUUID() + "@example.test", "hash", UserRole.VIEWER));
        UUID unknown = UUID.randomUUID();

        String body = perform(List.of(second.getId(), unknown, first.getId(), second.getId()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(body);

        List<String> actualIds = new ArrayList<>();
        response.forEach(node -> actualIds.add(node.get("userId").asText()));
        assertThat(actualIds).containsExactlyInAnyOrder(first.getId().toString(), second.getId().toString());
        assertThat(actualIds).isSortedAccordingTo(Comparator.naturalOrder());
        assertThat(body).doesNotContain(unknown.toString());
    }

    @Test
    void validatesEmptyMalformedAndOversizedInput() throws Exception {
        mockMvc.perform(post("/internal/users/notification-identities")
                        .header("X-Internal-Service-Key", "test-internal-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userIds\":[]}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/internal/users/notification-identities")
                        .header("X-Internal-Service-Key", "test-internal-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userIds\":[\"not-a-uuid\"]}"))
                .andExpect(status().isBadRequest());

        List<UUID> oversized = java.util.stream.Stream.generate(UUID::randomUUID).limit(101).toList();
        perform(oversized).andExpect(status().isBadRequest());
    }

    @Test
    void requiresValidInternalAuthentication() throws Exception {
        String content = "{\"userIds\":[\"" + UUID.randomUUID() + "\"]}";
        mockMvc.perform(post("/internal/users/notification-identities")
                        .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/internal/users/notification-identities")
                        .header("X-Internal-Service-Key", "wrong")
                        .contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.ResultActions perform(List<UUID> userIds) throws Exception {
        return mockMvc.perform(post("/internal/users/notification-identities")
                .header("X-Internal-Service-Key", "test-internal-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("userIds", userIds))));
    }
}
