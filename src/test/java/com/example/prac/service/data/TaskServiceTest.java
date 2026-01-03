package com.example.prac.service.data;

import com.example.prac.data.DTO.data.TaskDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.TaskMapper;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.TaskRepository;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private TaskDTO taskDTO;
    private Task task;

    @BeforeEach
    void setUp() {
        taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setStatus("OPEN");
        taskDTO.setDeadline("2024-12-31");
        taskDTO.setProjectId(1);

        task = new Task();
        task.setTaskId(1);
        task.setName("Test Task");
        task.setStatus("OPEN");
        task.setDeadline(LocalDate.parse("2024-12-31"));
    }

    @Test
    void save_ShouldSetOverdueStatus_WhenTaskIsPastDue() {
        // Arrange
        taskDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        task.setDeadline(LocalDate.now().minusDays(1));
        task.setStatus("ACTIVE");

        when(taskMapper.mapFrom(taskDTO)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            return t;
        });
        when(taskMapper.mapTo(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            TaskDTO dto = new TaskDTO();
            dto.setStatus(t.getStatus());
            return dto;
        });

        // Act
        TaskDTO result = taskService.save(taskDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void save_ShouldNotSetOverdueStatus_WhenTaskIsCompleted() {
        // Arrange
        taskDTO.setStatus("COMPLETED");
        taskDTO.setDeadline(LocalDate.now().minusDays(1).toString());
        task.setStatus("COMPLETED");
        task.setDeadline(LocalDate.now().minusDays(1));

        when(taskMapper.mapFrom(taskDTO)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.mapTo(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            TaskDTO dto = new TaskDTO();
            dto.setStatus(t.getStatus());
            return dto;
        });

        // Act
        TaskDTO result = taskService.save(taskDTO);

        // Assert
        assertEquals("COMPLETED", result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void partialUpdate_ShouldSetOverdueStatus_WhenTaskIsPastDue() {
        // Arrange
        Task existingTask = new Task();
        existingTask.setTaskId(1);
        existingTask.setStatus("ACTIVE");
        existingTask.setDeadline(LocalDate.now().minusDays(1));

        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setDeadline(LocalDate.now().minusDays(2).toString());

        when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            return t;
        });
        when(taskMapper.mapTo(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            TaskDTO dto = new TaskDTO();
            dto.setStatus(t.getStatus());
            return dto;
        });

        // Act
        TaskDTO result = taskService.partialUpdate(1, updateDTO);

        // Assert
        assertEquals("OVERDUE", result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void partialUpdate_ShouldThrowResourceNotFoundException_WhenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.partialUpdate(1, new TaskDTO())
        );

        assertTrue(exception.getMessage().contains("Task"));
        verify(taskRepository, never()).save(any(Task.class));
    }
}

