package com.example.prac.integration;

import com.example.prac.data.DTO.data.SpaceStationDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.repository.auth.ActorRepository;
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
class SpaceStationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @BeforeEach
    void setUp() {
        spaceStationRepository.deleteAll();
        actorRepository.deleteAll();

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
    void createSpaceStation_ShouldReturnCreated_WhenValidData() throws Exception {
        // Arrange
        SpaceStationDTO spaceStationDTO = new SpaceStationDTO();
        spaceStationDTO.setName("Test Station");
        spaceStationDTO.setOrbit("Earth Orbit");
        spaceStationDTO.setLaunchDate("2024-01-01");

        // Act & Assert
        mockMvc.perform(post("/api/space-stations")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spaceStationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Station"))
                .andExpect(jsonPath("$.orbit").value("Earth Orbit"));
    }

    @Test
    void createSpaceStation_ShouldReturnConflict_WhenNameAlreadyExists() throws Exception {
        // Arrange
        SpaceStation existing = new SpaceStation();
        existing.setName("Existing Station");
        existing.setOrbit("Earth Orbit");
        spaceStationRepository.save(existing);

        SpaceStationDTO spaceStationDTO = new SpaceStationDTO();
        spaceStationDTO.setName("Existing Station");
        spaceStationDTO.setOrbit("Mars Orbit");

        // Act & Assert
        mockMvc.perform(post("/api/space-stations")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spaceStationDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void getSpaceStationById_ShouldReturnSpaceStation_WhenExists() throws Exception {
        // Arrange
        SpaceStation spaceStation = new SpaceStation();
        spaceStation.setName("Existing Station");
        spaceStation.setOrbit("Earth Orbit");
        spaceStation = spaceStationRepository.save(spaceStation);

        // Act & Assert
        mockMvc.perform(get("/api/space-stations/{id}", spaceStation.getStationId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Station"))
                .andExpect(jsonPath("$.stationId").value(spaceStation.getStationId()));
    }

    @Test
    void getSpaceStationById_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/space-stations/{id}", 999)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSpaceStations_ShouldReturnList_WhenAuthenticated() throws Exception {
        // Arrange
        SpaceStation station1 = new SpaceStation();
        station1.setName("Station 1");
        station1.setOrbit("Earth Orbit");
        spaceStationRepository.save(station1);

        SpaceStation station2 = new SpaceStation();
        station2.setName("Station 2");
        station2.setOrbit("Mars Orbit");
        spaceStationRepository.save(station2);

        // Act & Assert
        mockMvc.perform(get("/api/space-stations")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateSpaceStationPartially_ShouldUpdateFields_WhenValidData() throws Exception {
        // Arrange
        SpaceStation spaceStation = new SpaceStation();
        spaceStation.setName("Original Name");
        spaceStation.setOrbit("Earth Orbit");
        spaceStation = spaceStationRepository.save(spaceStation);

        SpaceStationDTO updateDTO = new SpaceStationDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setOrbit("Mars Orbit");

        // Act & Assert
        mockMvc.perform(patch("/api/space-stations/{id}", spaceStation.getStationId())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.orbit").value("Mars Orbit"));
    }

    @Test
    void deleteSpaceStation_ShouldReturnNoContent_WhenSpaceStationExists() throws Exception {
        // Arrange
        SpaceStation spaceStation = new SpaceStation();
        spaceStation.setName("Station to Delete");
        spaceStation.setOrbit("Earth Orbit");
        spaceStation = spaceStationRepository.save(spaceStation);

        // Act & Assert
        mockMvc.perform(delete("/api/space-stations/{id}", spaceStation.getStationId())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        // Verify space station was deleted
        assertEquals(0, spaceStationRepository.count());
    }
}

