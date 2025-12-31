package com.example.prac.mappers.impl;


import com.example.prac.data.DTO.data.ExperimentDTO;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.mappers.Mapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class ExperimentMapper implements Mapper<Experiment, ExperimentDTO> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Experiment mapFrom(ExperimentDTO dto) {
        if (dto == null) {
            return null;
        }

        Experiment experiment = new Experiment();
        experiment.setExperimentId(dto.getExperimentId());
        experiment.setName(dto.getName());
        experiment.setDescription(dto.getDescription());
        experiment.setStatus(dto.getStatus());

        if (dto.getDeadline() != null) {
            experiment.setDeadline(LocalDate.parse(dto.getDeadline(), DATE_FORMATTER));
        }

        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setProjectId(dto.getProjectId());
            experiment.setProject(project);
        }

        return experiment;
    }

    @Override
    public ExperimentDTO mapTo(Experiment entity) {
        if (entity == null) {
            return null;
        }

        ExperimentDTO dto = new ExperimentDTO();
        dto.setExperimentId(entity.getExperimentId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());

        if (entity.getDeadline() != null) {
            dto.setDeadline(entity.getDeadline().format(DATE_FORMATTER));
        }

        if (entity.getProject() != null) {
            dto.setProjectId(entity.getProject().getProjectId());
        }

        return dto;
    }
}