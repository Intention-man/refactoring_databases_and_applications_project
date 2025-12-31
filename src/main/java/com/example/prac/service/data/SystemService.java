package com.example.prac.service.data;

import com.example.prac.data.DTO.data.SystemDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.System;
import com.example.prac.mappers.impl.SystemMapper;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.SystemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class SystemService {
    private final SystemRepository systemRepository;
    private final ProjectRepository projectRepository;
    private final SystemMapper systemMapper;

    public SystemDTO save(SystemDTO systemDTO) {
        System system = systemMapper.mapFrom(systemDTO);
        return systemMapper.mapTo(systemRepository.save(system));
    }

    public List<SystemDTO> findAll() {
        return StreamSupport.stream(systemRepository.findAll().spliterator(), false)
                .map(systemMapper::mapTo)
                .toList();
    }

    public Optional<SystemDTO> findById(Long id) {
        return systemRepository.findById(id.intValue())
                .map(systemMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return systemRepository.existsById(id.intValue());
    }

    public SystemDTO partialUpdate(Integer id, SystemDTO systemDTO) {
        return systemRepository.findById(id).map(existingSystem -> {
            Optional.ofNullable(systemDTO.getType()).ifPresent(existingSystem::setType);
            Optional.ofNullable(systemDTO.getDescription()).ifPresent(existingSystem::setDescription);
            Optional.ofNullable(systemDTO.getStatus()).ifPresent(existingSystem::setStatus);

            if (systemDTO.getProjectId() != null) {
                Project project = projectRepository.findById(systemDTO.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project doesn't exist"));
                existingSystem.setProject(project);
            }

            System updatedSystem = systemRepository.save(existingSystem);
            return systemMapper.mapTo(updatedSystem);
        }).orElseThrow(() -> new RuntimeException("System doesn't exist"));
    }

    public void delete(Long id) {
        systemRepository.deleteById(id.intValue());
    }
}
