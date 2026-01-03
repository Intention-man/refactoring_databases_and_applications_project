package com.example.prac.integration;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.SpaceStationRepository;
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
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SpaceStationRepository spaceStationRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private SpaceStation spaceStation;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        spaceStationRepository.deleteAll();
        actorRepository.deleteAll();

        // Create test space station
        spaceStation = new SpaceStation();
        spaceStation.setName("Test Station");
        spaceStation.setOrbit("Earth Orbit");
        spaceStation = spaceStationRepository.save(spaceStation);

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
    void createProject_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Project");
        projectDTO.setStatus("ACTIVE");
        projectDTO.setStartDate(LocalDate.now().toString());
        projectDTO.setEndDate(LocalDate.now().plusDays(30).toString()); // Future date to avoid OVERDUE
        projectDTO.setBudget(1000000L);
        projectDTO.setSpaceStationId(spaceStation.getStationId());

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createProject_ShouldSetOverdueStatus_WhenEndDateIsPast() throws Exception {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Overdue Project");
        projectDTO.setStatus("ACTIVE");
        projectDTO.setStartDate("2024-01-01");
        projectDTO.setEndDate(LocalDate.now().minusDays(1).toString());
        projectDTO.setBudget(1000000L);
        projectDTO.setSpaceStationId(spaceStation.getStationId());

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenExists() throws Exception {
        // Arrange
        Project project = new Project();
        project.setName("Existing Project");
        project.setStatus("ACTIVE");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(30)); // Future date to avoid OVERDUE
        project.setSpaceStation(spaceStation);
        project = projectRepository.save(project);

        // Act & Assert
        mockMvc.perform(get("/api/projects/{id}", project.getProjectId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Project"))
                .andExpect(jsonPath("$.projectId").value(project.getProjectId()));
    }

    @Test
    void getProjectById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/projects/{id}", 999)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProjectPartially_ShouldSetOverdueStatus_WhenEndDateIsUpdatedToPast() throws Exception {
        // Arrange
        Project project = new Project();
        project.setName("Test Project");
        project.setStatus("ACTIVE");
        project.setStartDate(LocalDate.parse("2024-01-01"));
        project.setEndDate(LocalDate.parse("2024-12-31"));
        project.setSpaceStation(spaceStation);
        project = projectRepository.save(project);

        ProjectDTO updateDTO = new ProjectDTO();
        updateDTO.setEndDate(LocalDate.now().minusDays(1).toString());

        // Act & Assert
        mockMvc.perform(patch("/api/projects/{id}", project.getProjectId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void getAllProjects_ShouldReturnList_WhenAuthenticated() throws Exception {
        // Arrange
        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setStatus("ACTIVE");
        project1.setSpaceStation(spaceStation);
        projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setStatus("ACTIVE");
        project2.setSpaceStation(spaceStation);
        projectRepository.save(project2);

        // Act & Assert
        mockMvc.perform(get("/api/projects")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateProjectPartially_ShouldUpdateFields_WhenValidData() throws Exception {
        // Arrange
        Project project = new Project();
        project.setName("Original Name");
        project.setStatus("ACTIVE");
        project.setBudget(1000000L);
        project.setSpaceStation(spaceStation);
        project = projectRepository.save(project);

        ProjectDTO updateDTO = new ProjectDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setBudget(2000000L);

        // Act & Assert
        mockMvc.perform(patch("/api/projects/{id}", project.getProjectId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.budget").value(2000000));
    }

    @Test
    void deleteProject_ShouldReturnNoContent_WhenProjectExists() throws Exception {
        // Arrange
        Project project = new Project();
        project.setName("Project to Delete");
        project.setStatus("ACTIVE");
        project.setSpaceStation(spaceStation);
        project = projectRepository.save(project);

        // Act & Assert
        mockMvc.perform(delete("/api/projects/{id}", project.getProjectId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // Verify project was deleted
        assertEquals(0, projectRepository.count());
    }

    @Test
    void getProjectByNameContaining_ShouldReturnMatchingProjects() throws Exception {
        // Arrange
        Project project1 = new Project();
        project1.setName("Mars Mission");
        project1.setStatus("ACTIVE");
        project1.setEndDate(LocalDate.now().plusDays(30)); // Future date to avoid OVERDUE
        project1.setSpaceStation(spaceStation);
        projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Moon Base");
        project2.setStatus("ACTIVE");
        project2.setEndDate(LocalDate.now().plusDays(30)); // Future date to avoid OVERDUE
        project2.setSpaceStation(spaceStation);
        projectRepository.save(project2);

        Project project3 = new Project();
        project3.setName("Mars Colony");
        project3.setStatus("ACTIVE");
        project3.setEndDate(LocalDate.now().plusDays(30)); // Future date to avoid OVERDUE
        project3.setSpaceStation(spaceStation);
        projectRepository.save(project3);

        // Act & Assert
        // Note: GET with @RequestBody - Spring doesn't support @RequestBody with GET by default
        // We need to use POST or change the endpoint. For now, let's skip this test
        // or use a workaround with HttpEntity
        mockMvc.perform(get("/api/projects/find_by_name_containing")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Mars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createProject_ShouldNotSetOverdueStatus_WhenProjectIsCompleted() throws Exception {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Completed Project");
        projectDTO.setStatus("COMPLETED");
        projectDTO.setStartDate("2024-01-01");
        projectDTO.setEndDate(LocalDate.now().minusDays(1).toString());
        projectDTO.setBudget(1000000L);
        projectDTO.setSpaceStationId(spaceStation.getStationId());

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}

