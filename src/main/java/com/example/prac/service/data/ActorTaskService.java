package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ActorTaskDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.dataEntity.ActorTask;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.mappers.impl.ActorTaskMapper;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ActorTaskRepository;
import com.example.prac.repository.data.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ActorTaskService {
    private final ActorTaskRepository actorTaskRepository;
    private final ActorRepository actorRepository;
    private final TaskRepository taskRepository;
    private final ActorTaskMapper actorTaskMapper;
    
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public ActorTaskDTO save(ActorTaskDTO actorTaskDTO) {
        if (!actorRepository.existsById(actorTaskDTO.getActorId())) {
            throw new RuntimeException("Actor doesn't exist");
        }

        Task task = taskRepository.findById(actorTaskDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task doesn't exist"));

        if (!Objects.equals(task.getStatus(), "OPEN")) {
            throw new RuntimeException("Task is not open");
        }

        entityManager.createNativeQuery("CALL add_actor_to_task(:actorId, :taskId)")
                .setParameter("actorId", actorTaskDTO.getActorId())
                .setParameter("taskId", actorTaskDTO.getTaskId())
                .executeUpdate();

        return actorTaskDTO;
    }


    public List<ActorTaskDTO> findAll() {
        return StreamSupport.stream(actorTaskRepository.findAll().spliterator(), false)
                .map(actorTaskMapper::mapTo)
                .toList();
    }

    public Optional<ActorTaskDTO> findById(Long id) {
        return actorTaskRepository.findById(id.intValue())
                .map(actorTaskMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return actorTaskRepository.existsById(id.intValue());
    }

    public ActorTaskDTO partialUpdate(Integer id, ActorTaskDTO dto) {
        return actorTaskRepository.findById(id).map(existing -> {
            if (dto.getActorId() != null) {
                Actor actor = actorRepository.findById(dto.getActorId())
                        .orElseThrow(() -> new RuntimeException("Actor doesn't exist"));
                existing.setActor(actor);
            }

            if (dto.getTaskId() != null) {
                Task task = taskRepository.findById(dto.getTaskId())
                        .orElseThrow(() -> new RuntimeException("Task doesn't exist"));
                existing.setTask(task);
            }

            ActorTask saved = actorTaskRepository.save(existing);
            return actorTaskMapper.mapTo(saved);
        }).orElseThrow(() -> new RuntimeException("ActorTask doesn't exist"));
    }

    public void delete(Long id) {
        actorTaskRepository.deleteById(id.intValue());
    }
}
