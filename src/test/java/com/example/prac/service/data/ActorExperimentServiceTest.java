package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ActorExperimentDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.dataEntity.ActorExperiment;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.exception.BusinessLogicException;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.ActorExperimentMapper;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ActorExperimentRepository;
import com.example.prac.repository.data.ExperimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorExperimentServiceTest {

    @Mock
    private ActorExperimentRepository actorExperimentRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ActorExperimentMapper actorExperimentMapper;

    @InjectMocks
    private ActorExperimentService actorExperimentService;

    private Actor testActor;
    private Experiment openExperiment;
    private Experiment activeExperiment;
    private ActorExperimentDTO actorExperimentDTO;

    @BeforeEach
    void setUp() {
        testActor = Actor.builder()
                .actorId(1)
                .username("testuser")
                .build();

        openExperiment = new Experiment();
        openExperiment.setExperimentId(1);
        openExperiment.setStatus("OPEN");
        openExperiment.setName("Test Experiment");

        activeExperiment = new Experiment();
        activeExperiment.setExperimentId(2);
        activeExperiment.setStatus("ACTIVE");
        activeExperiment.setName("Active Experiment");

        actorExperimentDTO = new ActorExperimentDTO();
        actorExperimentDTO.setActorId(1);
        actorExperimentDTO.setExperimentId(1);
    }

    @Test
    void save_ShouldCreateActorExperimentAndUpdateExperimentStatus_WhenExperimentIsOpen() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(experimentRepository.findById(1)).thenReturn(Optional.of(openExperiment));
        when(actorExperimentRepository.save(any(ActorExperiment.class))).thenAnswer(invocation -> {
            ActorExperiment ae = invocation.getArgument(0);
            ae.setId(1);
            return ae;
        });
        when(experimentRepository.save(any(Experiment.class))).thenReturn(openExperiment);

        // Act
        ActorExperimentDTO result = actorExperimentService.save(actorExperimentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getActorId());
        assertEquals(1, result.getExperimentId());
        verify(actorRepository).findById(1);
        verify(experimentRepository).findById(1);
        verify(actorExperimentRepository).save(any(ActorExperiment.class));
        verify(experimentRepository).save(openExperiment);
        assertEquals("ACTIVE", openExperiment.getStatus());
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenActorDoesNotExist() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> actorExperimentService.save(actorExperimentDTO)
        );

        assertTrue(exception.getMessage().contains("Actor"));
        verify(actorRepository).findById(1);
        verify(experimentRepository, never()).findById(anyInt());
        verify(actorExperimentRepository, never()).save(any(ActorExperiment.class));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenExperimentDoesNotExist() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(experimentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> actorExperimentService.save(actorExperimentDTO)
        );

        assertTrue(exception.getMessage().contains("Experiment"));
        verify(actorRepository).findById(1);
        verify(experimentRepository).findById(1);
        verify(actorExperimentRepository, never()).save(any(ActorExperiment.class));
    }

    @Test
    void save_ShouldThrowBusinessLogicException_WhenExperimentIsNotOpen() {
        // Arrange
        actorExperimentDTO.setExperimentId(2);
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(experimentRepository.findById(2)).thenReturn(Optional.of(activeExperiment));

        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> actorExperimentService.save(actorExperimentDTO)
        );

        assertTrue(exception.getMessage().contains("Experiment is not open"));
        verify(actorRepository).findById(1);
        verify(experimentRepository).findById(2);
        verify(actorExperimentRepository, never()).save(any(ActorExperiment.class));
        verify(experimentRepository, never()).save(any(Experiment.class));
    }
}

