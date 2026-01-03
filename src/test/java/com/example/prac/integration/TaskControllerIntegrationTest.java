package com.example.prac.integration;

import com.example.prac.data.DTO.data.TaskDTO;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

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
        taskRepository.deleteAll();
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
    void createTask_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus("OPEN");
        taskDTO.setDeadline(LocalDate.now().plusDays(10).toString()); // Future date to avoid OVERDUE
        taskDTO.setProjectId(project.getProjectId());

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createTask_ShouldSetOverdueStatus_WhenDeadlineIsPast() throws Exception {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Overdue Task");
        taskDTO.setStatus("ACTIVE");
        taskDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        taskDTO.setProjectId(project.getProjectId());

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() throws Exception {
        // Arrange
        Task task = new Task();
        task.setName("Existing Task");
        task.setStatus("OPEN");
        task.setProject(project);
        task = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", task.getTaskId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Task"))
                .andExpect(jsonPath("$.taskId").value(task.getTaskId()));
    }

    @Test
    void getTaskById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", 999)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_ShouldReturnList_WhenAuthenticated() throws Exception {
        // Arrange
        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setStatus("OPEN");
        task1.setProject(project);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setStatus("ACTIVE");
        task2.setProject(project);
        taskRepository.save(task2);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateTaskPartially_ShouldSetOverdueStatus_WhenDeadlineIsUpdatedToPast() throws Exception {
        // Arrange
        Task task = new Task();
        task.setName("Test Task");
        task.setStatus("ACTIVE");
        task.setDeadline(LocalDate.parse("2024-12-31"));
        task.setProject(project);
        task = taskRepository.save(task);

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setDeadline(LocalDate.now().minusDays(1).toString());

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}", task.getTaskId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OVERDUE"));
    }

    @Test
    void updateTaskPartially_ShouldUpdateFields_WhenValidData() throws Exception {
        // Arrange
        Task task = new Task();
        task.setName("Original Name");
        task.setStatus("OPEN");
        task.setProject(project);
        task = taskRepository.save(task);

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setDescription("Updated Description");

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}", task.getTaskId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void deleteTask_ShouldReturnNoContent_WhenTaskExists() throws Exception {
        // Arrange
        Task task = new Task();
        task.setName("Task to Delete");
        task.setStatus("OPEN");
        task.setProject(project);
        task = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", task.getTaskId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // Verify task was deleted
        assertEquals(0, taskRepository.count());
    }
}

