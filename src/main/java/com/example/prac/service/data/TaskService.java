package com.example.prac.service.data;

import com.example.prac.data.DTO.data.TaskDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.mappers.impl.TaskMapper;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    public TaskDTO save(TaskDTO taskDTO) {
        Task task = taskMapper.mapFrom(taskDTO);
        return taskMapper.mapTo(taskRepository.save(task));
    }

    public List<TaskDTO> findAll() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .map(taskMapper::mapTo)
                .toList();
    }

    public Optional<TaskDTO> findById(Long id) {
        return taskRepository.findById(id.intValue())
                .map(taskMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return taskRepository.existsById(id.intValue());
    }

    public TaskDTO partialUpdate(Integer id, TaskDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return taskRepository.findById(id).map(existing -> {
            Optional.ofNullable(dto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(dto.getDescription()).ifPresent(existing::setDescription);
            Optional.ofNullable(dto.getStatus()).ifPresent(existing::setStatus);

            Optional.ofNullable(dto.getDeadline())
                    .ifPresent(deadline -> existing.setDeadline(LocalDate.parse(deadline, formatter)));

            if (dto.getProjectId() != null) {
                Project project = projectRepository.findById(dto.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project doesn't exist"));
                existing.setProject(project);
            }

            Task saved = taskRepository.save(existing);
            return taskMapper.mapTo(saved);
        }).orElseThrow(() -> new RuntimeException("Task doesn't exist"));
    }


    public void delete(Long id) {
        taskRepository.deleteById(id.intValue());
    }
}
