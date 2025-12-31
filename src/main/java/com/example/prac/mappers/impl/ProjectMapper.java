package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

//@Component
//public class ProjectMapper implements Mapper<Project, ProjectDTO> {
//    private final ModelMapper modelMapper;
//
//    public ProjectMapper() {
//        this.modelMapper = new ModelMapper();
//    }
//
//    @Override
//    public ProjectDTO mapTo(Project project) {
//        return modelMapper.map(project, ProjectDTO.class);
//    }
//
//    @Override
//    public Project mapFrom(ProjectDTO projectDTO) {
//        return modelMapper.map(projectDTO, Project.class);
//    }
//}

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProjectMapper implements Mapper<Project, ProjectDTO> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Project mapFrom(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }

        Project project = new Project();
        project.setProjectId(dto.getProjectId());
        project.setName(dto.getName());
        project.setStatus(dto.getStatus());
        project.setBudget(dto.getBudget());

        if (dto.getStartDate() != null) {
            project.setStartDate(LocalDate.parse(dto.getStartDate(), DATE_FORMATTER));
        }

        if (dto.getEndDate() != null) {
            project.setEndDate(LocalDate.parse(dto.getEndDate(), DATE_FORMATTER));
        }

        if (dto.getSpaceStationId() != null) {
            SpaceStation spaceStation = new SpaceStation();
            spaceStation.setStationId(dto.getSpaceStationId());
            project.setSpaceStation(spaceStation);
        }

        return project;
    }

    @Override
    public ProjectDTO mapTo(Project entity) {
        if (entity == null) {
            return null;
        }

        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(entity.getProjectId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setBudget(entity.getBudget());

        if (entity.getStartDate() != null) {
            dto.setStartDate(entity.getStartDate().format(DATE_FORMATTER));
        }

        if (entity.getEndDate() != null) {
            dto.setEndDate(entity.getEndDate().format(DATE_FORMATTER));
        }

        if (entity.getSpaceStation() != null) {
            dto.setSpaceStationId(entity.getSpaceStation().getStationId());
        }

        return dto;
    }
}