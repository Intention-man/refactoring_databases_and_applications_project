package com.example.prac.integration;

import com.example.prac.data.DTO.data.ActorTaskDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.TaskRepository;
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
class ActorTaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private TaskRepository taskRepository;

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
    private Task openTask;
    private Project project;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAll();
        taskRepository.deleteAll();
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

        // Create open task
        openTask = new Task();
        openTask.setName("Test Task");
        openTask.setStatus("OPEN");
        openTask.setProject(project);
        openTask = taskRepository.save(openTask);

        // Generate JWT token
        authToken = "Bearer " + jwtService.generateToken(testActor);
    }

    @Test
    void createActorTask_ShouldCreateAndUpdateTaskStatus_WhenTaskIsOpen() throws Exception {
        // Arrange
        ActorTaskDTO actorTaskDTO = new ActorTaskDTO();
        actorTaskDTO.setActorId(testActor.getActorId());
        actorTaskDTO.setTaskId(openTask.getTaskId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorTaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actorId").value(testActor.getActorId()))
                .andExpect(jsonPath("$.taskId").value(openTask.getTaskId()));

        // Verify task status was updated to ACTIVE
        Task updatedTask = taskRepository.findById(openTask.getTaskId()).orElseThrow();
        assertEquals("ACTIVE", updatedTask.getStatus());
    }

    @Test
    void createActorTask_ShouldReturnBadRequest_WhenTaskIsNotOpen() throws Exception {
        // Arrange
        Task activeTask = new Task();
        activeTask.setName("Active Task");
        activeTask.setStatus("ACTIVE");
        activeTask.setProject(project);
        activeTask = taskRepository.save(activeTask);

        ActorTaskDTO actorTaskDTO = new ActorTaskDTO();
        actorTaskDTO.setActorId(testActor.getActorId());
        actorTaskDTO.setTaskId(activeTask.getTaskId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Task is not open")));
    }

    @Test
    void createActorTask_ShouldReturnNotFound_WhenActorDoesNotExist() throws Exception {
        // Arrange
        ActorTaskDTO actorTaskDTO = new ActorTaskDTO();
        actorTaskDTO.setActorId(999);
        actorTaskDTO.setTaskId(openTask.getTaskId());

        // Act & Assert
        mockMvc.perform(post("/api/actor-tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorTaskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Actor")));
    }

    @Test
    void createActorTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        // Arrange
        ActorTaskDTO actorTaskDTO = new ActorTaskDTO();
        actorTaskDTO.setActorId(testActor.getActorId());
        actorTaskDTO.setTaskId(999);

        // Act & Assert
        mockMvc.perform(post("/api/actor-tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorTaskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Task")));
    }
}

