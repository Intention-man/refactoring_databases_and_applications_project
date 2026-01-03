package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.ProjectMapper;
import com.example.prac.repository.data.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private ProjectDTO projectDTO;
    private Project project;

    @BeforeEach
    void setUp() {
        projectDTO = new ProjectDTO();
        projectDTO.setName("Test Project");
        projectDTO.setStatus("ACTIVE");
        projectDTO.setStartDate("2024-01-01");
        projectDTO.setEndDate("2024-12-31");
        projectDTO.setBudget(1000000L);
        projectDTO.setSpaceStationId(1);

        project = new Project();
        project.setProjectId(1);
        project.setName("Test Project");
        project.setStatus("ACTIVE");
        project.setStartDate(LocalDate.parse("2024-01-01"));
        project.setEndDate(LocalDate.parse("2024-12-31"));
        project.setBudget(1000000L);
    }

    @Test
    void save_ShouldSetOverdueStatus_WhenProjectIsPastDue() {
        // Arrange
        projectDTO.setEndDate(LocalDate.now().minusDays(1).toString());
        project.setEndDate(LocalDate.now().minusDays(1));
        project.setStatus("ACTIVE");

        when(projectMapper.mapFrom(projectDTO)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return p;
        });
        when(projectMapper.mapTo(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            ProjectDTO dto = new ProjectDTO();
            dto.setStatus(p.getStatus());
            return dto;
        });

        // Act
        ProjectDTO result = projectService.save(projectDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void save_ShouldNotSetOverdueStatus_WhenProjectIsCompleted() {
        // Arrange
        projectDTO.setStatus("COMPLETED");
        projectDTO.setEndDate(LocalDate.now().minusDays(1).toString());
        project.setStatus("COMPLETED");
        project.setEndDate(LocalDate.now().minusDays(1));

        when(projectMapper.mapFrom(projectDTO)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.mapTo(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            ProjectDTO dto = new ProjectDTO();
            dto.setStatus(p.getStatus());
            return dto;
        });

        // Act
        ProjectDTO result = projectService.save(projectDTO);

        // Assert
        assertEquals("COMPLETED", result.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void save_ShouldNotSetOverdueStatus_WhenProjectIsNotPastDue() {
        // Arrange
        projectDTO.setEndDate(LocalDate.now().plusDays(10).toString());
        project.setEndDate(LocalDate.now().plusDays(10));
        project.setStatus("ACTIVE");

        when(projectMapper.mapFrom(projectDTO)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.mapTo(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            ProjectDTO dto = new ProjectDTO();
            dto.setStatus(p.getStatus());
            return dto;
        });

        // Act
        ProjectDTO result = projectService.save(projectDTO);

        // Assert
        assertEquals("ACTIVE", result.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void partialUpdate_ShouldSetOverdueStatus_WhenProjectIsPastDue() {
        // Arrange
        Project existingProject = new Project();
        existingProject.setProjectId(1);
        existingProject.setStatus("ACTIVE");
        existingProject.setEndDate(LocalDate.now().minusDays(1));

        ProjectDTO updateDTO = new ProjectDTO();
        updateDTO.setEndDate(LocalDate.now().minusDays(2).toString());

        when(projectRepository.findById(1)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            return p;
        });
        when(projectMapper.mapTo(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            ProjectDTO dto = new ProjectDTO();
            dto.setStatus(p.getStatus());
            return dto;
        });

        // Act
        ProjectDTO result = projectService.partialUpdate(1, updateDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void partialUpdate_ShouldThrowResourceNotFoundException_WhenProjectDoesNotExist() {
        // Arrange
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.partialUpdate(1, new ProjectDTO())
        );

        assertTrue(exception.getMessage().contains("Project"));
        verify(projectRepository, never()).save(any(Project.class));
    }
}

