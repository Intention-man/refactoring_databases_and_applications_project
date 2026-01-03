package com.example.prac.integration;

import com.example.prac.data.DTO.data.ActorExperimentDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ExperimentRepository;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.service.auth.JwtService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ActorExperimentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Actor testActor;
    private Experiment openExperiment;
    private Project project;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAll();
        experimentRepository.deleteAll();
        projectRepository.deleteAll();

        // Create test project
        project = new Project();
        project.setName("Test Project");
        project.setStatus("ACTIVE");
        project = projectRepository.save(project);

        // Create test actor
        testActor = Actor.builder()
                .username("manager")
                .password(passwordEncoder.encode("password"))
                .role(Role.MANAGER)
                .contactInformation("manager@test.com")
                .build();
        testActor = actorRepository.save(testActor);

        // Create open experiment
        openExperiment = new Experiment();
        openExperiment.setName("Test Experiment");
        openExperiment.setStatus("OPEN");
        openExperiment.setProject(project);
        openExperiment = experimentRepository.save(openExperiment);

        // Generate JWT token
        authToken = "Bearer " + jwtService.generateToken(testActor);
    }

    @Test
    void createActorExperiment_ShouldCreateAndUpdateExperimentStatus_WhenExperimentIsOpen() throws Exception {
        // Arrange
        ActorExperimentDTO actorExperimentDTO = new ActorExperimentDTO();
        actorExperimentDTO.setActorId(testActor.getActorId());
        actorExperimentDTO.setExperimentId(openExperiment.getExperimentId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorExperimentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actorId").value(testActor.getActorId()))
                .andExpect(jsonPath("$.experimentId").value(openExperiment.getExperimentId()));

        // Verify experiment status was updated to ACTIVE
        Experiment updatedExperiment = experimentRepository.findById(openExperiment.getExperimentId()).orElseThrow();
        assertEquals("ACTIVE", updatedExperiment.getStatus());
    }

    @Test
    void createActorExperiment_ShouldReturnBadRequest_WhenExperimentIsNotOpen() throws Exception {
        // Arrange
        Experiment activeExperiment = new Experiment();
        activeExperiment.setName("Active Experiment");
        activeExperiment.setStatus("ACTIVE");
        activeExperiment.setProject(project);
        activeExperiment = experimentRepository.save(activeExperiment);

        ActorExperimentDTO actorExperimentDTO = new ActorExperimentDTO();
        actorExperimentDTO.setActorId(testActor.getActorId());
        actorExperimentDTO.setExperimentId(activeExperiment.getExperimentId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorExperimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Experiment is not open")));
    }

    @Test
    void createActorExperiment_ShouldReturnNotFound_WhenActorDoesNotExist() throws Exception {
        // Arrange
        ActorExperimentDTO actorExperimentDTO = new ActorExperimentDTO();
        actorExperimentDTO.setActorId(999);
        actorExperimentDTO.setExperimentId(openExperiment.getExperimentId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorExperimentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Actor")));
    }

    @Test
    void createActorExperiment_ShouldReturnNotFound_WhenExperimentDoesNotExist() throws Exception {
        // Arrange
        ActorExperimentDTO actorExperimentDTO = new ActorExperimentDTO();
        actorExperimentDTO.setActorId(testActor.getActorId());
        actorExperimentDTO.setExperimentId(999);

        // Act & Assert
        mockMvc.perform(post("/api/actor-experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorExperimentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Experiment")));
    }
}

