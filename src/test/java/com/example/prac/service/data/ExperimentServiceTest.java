package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ExperimentDTO;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.ExperimentMapper;
import com.example.prac.repository.data.ExperimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentServiceTest {

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentMapper experimentMapper;

    @InjectMocks
    private ExperimentService experimentService;

    private ExperimentDTO experimentDTO;
    private Experiment experiment;

    @BeforeEach
    void setUp() {
        experimentDTO = new ExperimentDTO();
        experimentDTO.setName("Test Experiment");
        experimentDTO.setStatus("OPEN");
        experimentDTO.setDeadline("2024-12-31");
        experimentDTO.setProjectId(1);

        experiment = new Experiment();
        experiment.setExperimentId(1);
        experiment.setName("Test Experiment");
        experiment.setStatus("OPEN");
        experiment.setDeadline(LocalDate.parse("2024-12-31"));
    }

    @Test
    void save_ShouldSetOverdueStatus_WhenExperimentIsPastDue() {
        // Arrange
        experimentDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        experiment.setDeadline(LocalDate.now().minusDays(1));
        experiment.setStatus("ACTIVE");

        when(experimentMapper.mapFrom(experimentDTO)).thenReturn(experiment);
        when(experimentRepository.save(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment e = invocation.getArgument(0);
            return e;
        });
        when(experimentMapper.mapTo(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment e = invocation.getArgument(0);
            ExperimentDTO dto = new ExperimentDTO();
            dto.setStatus(e.getStatus());
            return dto;
        });

        // Act
        ExperimentDTO result = experimentService.save(experimentDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(experimentRepository).save(any(Experiment.class));
    }

    @Test
    void save_ShouldNotSetOverdueStatus_WhenExperimentIsCompleted() {
        // Arrange
        experimentDTO.setStatus("COMPLETED");
        experimentDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        experiment.setStatus("COMPLETED");
        experiment.setDeadline(LocalDate.now().minusDays(1));

        when(experimentMapper.mapFrom(experimentDTO)).thenReturn(experiment);
        when(experimentRepository.save(any(Experiment.class))).thenReturn(experiment);
        when(experimentMapper.mapTo(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment e = invocation.getArgument(0);
            ExperimentDTO dto = new ExperimentDTO();
            dto.setStatus(e.getStatus());
            return dto;
        });

        // Act
        ExperimentDTO result = experimentService.save(experimentDTO);

        // Assert
        assertEquals("COMPLETED", result.getStatus());
        verify(experimentRepository).save(any(Experiment.class));
    }

    @Test
    void partialUpdate_ShouldSetOverdueStatus_WhenExperimentIsPastDue() {
        // Arrange
        Experiment existingExperiment = new Experiment();
        existingExperiment.setExperimentId(1);
        existingExperiment.setStatus("ACTIVE");
        existingExperiment.setDeadline(LocalDate.now().minusDays(1));

        ExperimentDTO updateDTO = new ExperimentDTO();
        updateDTO.setDeadline(LocalDate.now().minusDays(2).toString());

        when(experimentRepository.findById(1)).thenReturn(Optional.of(existingExperiment));
        when(experimentRepository.save(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment e = invocation.getArgument(0);
            return e;
        });
        when(experimentMapper.mapTo(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment e = invocation.getArgument(0);
            ExperimentDTO dto = new ExperimentDTO();
            dto.setStatus(e.getStatus());
            return dto;
        });

        // Act
        ExperimentDTO result = experimentService.partialUpdate(1, updateDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(experimentRepository).save(any(Experiment.class));
    }

    @Test
    void partialUpdate_ShouldThrowResourceNotFoundException_WhenExperimentDoesNotExist() {
        // Arrange
        when(experimentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> experimentService.partialUpdate(1, new ExperimentDTO())
        );

        assertTrue(exception.getMessage().contains("Experiment"));
        verify(experimentRepository, never()).save(any(Experiment.class));
    }
}

