package com.example.prac.integration;

import com.example.prac.data.DTO.data.ExperimentDTO;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExperimentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Project project;

    @BeforeEach
    void setUp() {
        experimentRepository.deleteAll();
        projectRepository.deleteAll();
        actorRepository.deleteAll();

        // Create test project
        project = new Project();
        project.setName("Test Project");
        project.setStatus("ACTIVE");
        project = projectRepository.save(project);

        // Create test actor with MANAGER role
        Actor manager = Actor.builder()
                .username("manager")
                .password(passwordEncoder.encode("password"))
                .role(Role.MANAGER)
                .contactInformation("manager@test.com")
                .build();
        manager = actorRepository.save(manager);

        // Generate JWT token
        authToken = "Bearer " + jwtService.generateToken(manager);
    }

    @Test
    void createExperiment_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        ExperimentDTO experimentDTO = new ExperimentDTO();
        experimentDTO.setName("Test Experiment");
        experimentDTO.setDescription("Test Description");
        experimentDTO.setStatus("OPEN");
        experimentDTO.setDeadline(LocalDate.now().plusDays(10).toString()); // Future date to avoid OVERDUE
        experimentDTO.setProjectId(project.getProjectId());

        // Act & Assert
        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experimentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Experiment"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createExperiment_ShouldSetOverdueStatus_WhenDeadlineIsPast() throws Exception {
        // Arrange
        ExperimentDTO experimentDTO = new ExperimentDTO();
        experimentDTO.setName("Overdue Experiment");
        experimentDTO.setStatus("ACTIVE");
        experimentDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        experimentDTO.setProjectId(project.getProjectId());

        // Act & Assert
        mockMvc.perform(post("/api/experiments")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experimentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void getExperimentById_ShouldReturnExperiment_WhenExists() throws Exception {
        // Arrange
        Experiment experiment = new Experiment();
        experiment.setName("Existing Experiment");
        experiment.setStatus("OPEN");
        experiment.setProject(project);
        experiment = experimentRepository.save(experiment);

        // Act & Assert
        mockMvc.perform(get("/api/experiments/{id}", experiment.getExperimentId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Experiment"))
                .andExpect(jsonPath("$.experimentId").value(experiment.getExperimentId()));
    }

    @Test
    void getExperimentById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/experiments/{id}", 999)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllExperiments_ShouldReturnList_WhenAuthenticated() throws Exception {
        // Arrange
        Experiment experiment1 = new Experiment();
        experiment1.setName("Experiment 1");
        experiment1.setStatus("OPEN");
        experiment1.setProject(project);
        experimentRepository.save(experiment1);

        Experiment experiment2 = new Experiment();
        experiment2.setName("Experiment 2");
        experiment2.setStatus("ACTIVE");
        experiment2.setProject(project);
        experimentRepository.save(experiment2);

        // Act & Assert
        mockMvc.perform(get("/api/experiments")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateExperimentPartially_ShouldSetOverdueStatus_WhenDeadlineIsUpdatedToPast() throws Exception {
        // Arrange
        Experiment experiment = new Experiment();
        experiment.setName("Test Experiment");
        experiment.setStatus("ACTIVE");
        experiment.setDeadline(LocalDate.parse("2024-12-31"));
        experiment.setProject(project);
        experiment = experimentRepository.save(experiment);

        ExperimentDTO updateDTO = new ExperimentDTO();
        updateDTO.setDeadline(LocalDate.now().minusDays(1).toString());

        // Act & Assert
        mockMvc.perform(patch("/api/experiments/{id}", experiment.getExperimentId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void updateExperimentPartially_ShouldUpdateFields_WhenValidData() throws Exception {
        // Arrange
        Experiment experiment = new Experiment();
        experiment.setName("Original Name");
        experiment.setStatus("OPEN");
        experiment.setProject(project);
        experiment = experimentRepository.save(experiment);

        ExperimentDTO updateDTO = new ExperimentDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setDescription("Updated Description");

        // Act & Assert
        mockMvc.perform(patch("/api/experiments/{id}", experiment.getExperimentId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void deleteExperiment_ShouldReturnNoContent_WhenExperimentExists() throws Exception {
        // Arrange
        Experiment experiment = new Experiment();
        experiment.setName("Experiment to Delete");
        experiment.setStatus("OPEN");
        experiment.setProject(project);
        experiment = experimentRepository.save(experiment);

        // Act & Assert
        mockMvc.perform(delete("/api/experiments/{id}", experiment.getExperimentId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // Verify experiment was deleted
        assertEquals(0, experimentRepository.count());
    }
}

