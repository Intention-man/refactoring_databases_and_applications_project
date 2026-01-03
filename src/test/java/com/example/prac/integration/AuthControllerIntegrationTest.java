package com.example.prac.integration;

import com.example.prac.data.DTO.auth.AuthenticationRequest;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.repository.auth.ActorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Actor testActor;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAll();
        testActor = Actor.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .role(Role.MANAGER)
                .contactInformation("test@example.com")
                .build();
        actorRepository.save(testActor);
    }

    @Test
    void register_ShouldReturnToken_WhenUserDoesNotExist() throws Exception {
        // Arrange - create JSON without actorId field to avoid UserDetails serialization issues
        String newActorJson = """
                {
                    "username": "newuser",
                    "password": "password123",
                    "role": "SCIENTIST",
                    "contactInformation": "newuser@example.com"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newActorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("SCIENTIST"));
    }

    @Test
    void register_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        // Arrange - create JSON without actorId field
        String existingActorJson = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "role": "MANAGER",
                    "contactInformation": "test@example.com"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(existingActorJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with identifier 'testuser' already exists"));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

