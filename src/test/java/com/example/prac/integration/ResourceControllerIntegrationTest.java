package com.example.prac.integration;

import com.example.prac.data.DTO.data.ResourceDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Resource;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.ResourceRepository;
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
class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

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
        resourceRepository.deleteAll();
        projectRepository.deleteAll();
        actorRepository.deleteAll();

        // Create test project
        project = new Project();
        project.setName("Test Project");
        project.setStatus("ACTIVE");
        project = projectRepository.save(project);

        // Create test actor with LOGISTICIAN role
        Actor logistician = Actor.builder()
                .username("logistician")
                .password(passwordEncoder.encode("password"))
                .role(Role.LOGISTICIAN)
                .contactInformation("logistician@test.com")
                .build();
        logistician = actorRepository.save(logistician);

        // Generate JWT token
        authToken = "Bearer " + jwtService.generateToken(logistician);
    }

    @Test
    void createResource_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setType("Water");
        resourceDTO.setQuantity(1000L);
        resourceDTO.setUnit("liters");
        resourceDTO.setProjectId(project.getProjectId());

        // Act & Assert
        mockMvc.perform(post("/api/resources")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Water"))
                .andExpect(jsonPath("$.quantity").value(1000))
                .andExpect(jsonPath("$.unit").value("liters"));
    }

    @Test
    void getResourceById_ShouldReturnResource_WhenExists() throws Exception {
        // Arrange
        Resource resource = new Resource();
        resource.setType("Food");
        resource.setQuantity(500L);
        resource.setUnit("kg");
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        // Act & Assert
        mockMvc.perform(get("/api/resources/{id}", resource.getResourceId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Food"))
                .andExpect(jsonPath("$.resourceId").value(resource.getResourceId()));
    }

    @Test
    void getResourceById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/resources/{id}", 999)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllResources_ShouldReturnList_WhenAuthenticated() throws Exception {
        // Arrange
        Resource resource1 = new Resource();
        resource1.setType("Water");
        resource1.setQuantity(1000L);
        resource1.setProject(project);
        resourceRepository.save(resource1);

        Resource resource2 = new Resource();
        resource2.setType("Food");
        resource2.setQuantity(500L);
        resource2.setProject(project);
        resourceRepository.save(resource2);

        // Act & Assert
        mockMvc.perform(get("/api/resources")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateResourcePartially_ShouldUpdateFields_WhenValidData() throws Exception {
        // Arrange
        Resource resource = new Resource();
        resource.setType("Original Type");
        resource.setQuantity(100L);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        ResourceDTO updateDTO = new ResourceDTO();
        updateDTO.setType("Updated Type");
        updateDTO.setQuantity(200L);
        updateDTO.setUnit("kg");

        // Act & Assert
        mockMvc.perform(patch("/api/resources/{id}", resource.getResourceId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Updated Type"))
                .andExpect(jsonPath("$.quantity").value(200))
                .andExpect(jsonPath("$.unit").value("kg"));
    }

    @Test
    void deleteResource_ShouldReturnNoContent_WhenResourceExists() throws Exception {
        // Arrange
        Resource resource = new Resource();
        resource.setType("Resource to Delete");
        resource.setQuantity(100L);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        // Act & Assert
        mockMvc.perform(delete("/api/resources/{id}", resource.getResourceId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // Verify resource was deleted
        assertEquals(0, resourceRepository.count());
    }
}

