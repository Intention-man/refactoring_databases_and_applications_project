package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ActorTaskDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.dataEntity.ActorTask;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.exception.BusinessLogicException;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.ActorTaskMapper;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ActorTaskRepository;
import com.example.prac.repository.data.TaskRepository;
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
class ActorTaskServiceTest {

    @Mock
    private ActorTaskRepository actorTaskRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ActorTaskMapper actorTaskMapper;

    @InjectMocks
    private ActorTaskService actorTaskService;

    private Actor testActor;
    private Task openTask;
    private Task activeTask;
    private ActorTaskDTO actorTaskDTO;

    @BeforeEach
    void setUp() {
        testActor = Actor.builder()
                .actorId(1)
                .username("testuser")
                .build();

        openTask = new Task();
        openTask.setTaskId(1);
        openTask.setStatus("OPEN");
        openTask.setName("Test Task");

        activeTask = new Task();
        activeTask.setTaskId(2);
        activeTask.setStatus("ACTIVE");
        activeTask.setName("Active Task");

        actorTaskDTO = new ActorTaskDTO();
        actorTaskDTO.setActorId(1);
        actorTaskDTO.setTaskId(1);
    }

    @Test
    void save_ShouldCreateActorTaskAndUpdateTaskStatus_WhenTaskIsOpen() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(taskRepository.findById(1)).thenReturn(Optional.of(openTask));
        when(actorTaskRepository.save(any(ActorTask.class))).thenAnswer(invocation -> {
            ActorTask at = invocation.getArgument(0);
            at.setId(1);
            return at;
        });
        when(taskRepository.save(any(Task.class))).thenReturn(openTask);

        // Act
        ActorTaskDTO result = actorTaskService.save(actorTaskDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getActorId());
        assertEquals(1, result.getTaskId());
        verify(actorRepository).findById(1);
        verify(taskRepository).findById(1);
        verify(actorTaskRepository).save(any(ActorTask.class));
        verify(taskRepository).save(openTask);
        assertEquals("ACTIVE", openTask.getStatus());
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenActorDoesNotExist() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> actorTaskService.save(actorTaskDTO)
        );

        assertTrue(exception.getMessage().contains("Actor"));
        verify(actorRepository).findById(1);
        verify(taskRepository, never()).findById(anyInt());
        verify(actorTaskRepository, never()).save(any(ActorTask.class));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTaskDoesNotExist() {
        // Arrange
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> actorTaskService.save(actorTaskDTO)
        );

        assertTrue(exception.getMessage().contains("Task"));
        verify(actorRepository).findById(1);
        verify(taskRepository).findById(1);
        verify(actorTaskRepository, never()).save(any(ActorTask.class));
    }

    @Test
    void save_ShouldThrowBusinessLogicException_WhenTaskIsNotOpen() {
        // Arrange
        actorTaskDTO.setTaskId(2);
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(taskRepository.findById(2)).thenReturn(Optional.of(activeTask));

        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> actorTaskService.save(actorTaskDTO)
        );

        assertTrue(exception.getMessage().contains("Task is not open"));
        verify(actorRepository).findById(1);
        verify(taskRepository).findById(2);
        verify(actorTaskRepository, never()).save(any(ActorTask.class));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void save_ShouldNotUpdateTaskStatus_WhenTaskIsNotOpen() {
        // Arrange
        Task completedTask = new Task();
        completedTask.setTaskId(3);
        completedTask.setStatus("COMPLETED");

        actorTaskDTO.setTaskId(3);
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(taskRepository.findById(3)).thenReturn(Optional.of(completedTask));

        // Act & Assert
        assertThrows(BusinessLogicException.class, () -> actorTaskService.save(actorTaskDTO));
        verify(taskRepository, never()).save(any(Task.class));
    }
}

