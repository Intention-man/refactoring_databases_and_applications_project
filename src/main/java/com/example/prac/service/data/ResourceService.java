package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ResourceDTO;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.data.model.dataEntity.Resource;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.ResourceMapper;
import com.example.prac.repository.data.ProjectRepository;
import com.example.prac.repository.data.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;

    public ResourceDTO save(ResourceDTO resourceDTO) {
        Resource resource = resourceMapper.mapFrom(resourceDTO);
        return resourceMapper.mapTo(resourceRepository.save(resource));
    }

    public List<ResourceDTO> findAll() {
        return StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .map(resourceMapper::mapTo)
                .toList();
    }

    public Optional<ResourceDTO> findById(Long id) {
        return resourceRepository.findById(id.intValue())
                .map(resourceMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return resourceRepository.existsById(id.intValue());
    }

    public ResourceDTO partialUpdate(Integer id, ResourceDTO dto) {
        return resourceRepository.findById(id).map(existing -> {
            Optional.ofNullable(dto.getType()).ifPresent(existing::setType);
            Optional.ofNullable(dto.getQuantity()).ifPresent(existing::setQuantity);
            Optional.ofNullable(dto.getUnit()).ifPresent(existing::setUnit);

            if (dto.getProjectId() != null) {
                Project project = projectRepository.findById(dto.getProjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project", dto.getProjectId().longValue()));
                existing.setProject(project);
            }

            Resource saved = resourceRepository.save(existing);
            return resourceMapper.mapTo(saved);
        }).orElseThrow(() -> new ResourceNotFoundException("Resource", (long) id));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id.intValue());
    }
}
