package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.mappers.impl.ProjectMapper;
import com.example.prac.repository.data.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDTO save(ProjectDTO projectDTO) {
        Project project = projectMapper.mapFrom(projectDTO);
        return projectMapper.mapTo(projectRepository.save(project));
    }

    public List<ProjectDTO> findAll() {
        return StreamSupport.stream(projectRepository.findAll().spliterator(), false)
                .map(projectMapper::mapTo)
                .toList();
    }

    public Optional<ProjectDTO> findById(Long id) {
        return projectRepository.findById(id.intValue())
                .map(projectMapper::mapTo);
    }

    public List<ProjectDTO> findByNameContaining(String substring) {
        return projectRepository.findByNameContaining(substring).stream()
                .map(projectMapper::mapTo)
                .toList();
    }


    public boolean isExists(Long id) {
        return projectRepository.existsById(id.intValue());
    }


    public ProjectDTO partialUpdate(Integer id, ProjectDTO projectDTO) {
        return projectRepository.findById(id).map(existing -> {
            Optional.ofNullable(projectDTO.getName()).ifPresent(existing::setName);
            Optional.ofNullable(projectDTO.getStatus()).ifPresent(existing::setStatus);

            Optional.ofNullable(projectDTO.getStartDate())
                    .map(startDate -> LocalDate.parse(startDate, ProjectMapper.DATE_FORMATTER))
                    .ifPresent(existing::setStartDate);

            Optional.ofNullable(projectDTO.getEndDate())
                    .map(endDate -> LocalDate.parse(endDate, ProjectMapper.DATE_FORMATTER))
                    .ifPresent(existing::setEndDate);

            Optional.ofNullable(projectDTO.getBudget()).ifPresent(existing::setBudget);

            if (projectDTO.getSpaceStationId() != null) {
                SpaceStation spaceStation = new SpaceStation();
                spaceStation.setStationId(projectDTO.getSpaceStationId());
                existing.setSpaceStation(spaceStation);
            }

            Project updatedProject = projectRepository.save(existing);
            return projectMapper.mapTo(updatedProject);
        }).orElseThrow(() -> new RuntimeException("Project doesn't exist"));
    }

    public void delete(Long id) {
        projectRepository.deleteById(id.intValue());
    }

}
