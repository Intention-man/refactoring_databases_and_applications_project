package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.TaskDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Task;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TaskMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskDTO mapTo(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(task.getTaskId());
        taskDTO.setName(task.getName());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());

        if (task.getDeadline() != null) {
            taskDTO.setDeadline(task.getDeadline().format(DATE_FORMATTER));
        }

        if (task.getProject() != null) {
            taskDTO.setProjectId(task.getProject().getProjectId());
        }

        return taskDTO;
    }

    public Task mapFrom(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task task = new Task();
        task.setTaskId(taskDTO.getTaskId());
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());

        if (taskDTO.getDeadline() != null) {
            task.setDeadline(LocalDate.parse(taskDTO.getDeadline(), DATE_FORMATTER));
        }

        if (taskDTO.getProjectId() != null) {
            Project project = new Project();
            project.setProjectId(taskDTO.getProjectId());
            task.setProject(project);
        }

        return task;
    }
}