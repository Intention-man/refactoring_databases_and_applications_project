package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ProjectEquipmentDTO;
import com.example.prac.data.model.dataEntity.ProjectEquipment;
import com.example.prac.mappers.impl.ProjectEquipmentMapper;
import com.example.prac.repository.data.ProjectEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ProjectEquipmentService {
    private final ProjectEquipmentRepository projectEquipmentRepository;
    private final ProjectEquipmentMapper projectEquipmentMapper;

    public ProjectEquipmentDTO save(ProjectEquipmentDTO projectEquipmentDTO) {
        ProjectEquipment projectEquipment = projectEquipmentMapper.mapFrom(projectEquipmentDTO);
        return projectEquipmentMapper.mapTo(projectEquipmentRepository.save(projectEquipment));
    }

    public List<ProjectEquipmentDTO> findAll() {
        return StreamSupport.stream(projectEquipmentRepository.findAll().spliterator(), false)
                .map(projectEquipmentMapper::mapTo)
                .toList();
    }

    public Optional<ProjectEquipmentDTO> findById(Long id) {
        return projectEquipmentRepository.findById(id.intValue())
                .map(projectEquipmentMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return projectEquipmentRepository.existsById(id.intValue());
    }

    public ProjectEquipmentDTO partialUpdate(Long id, ProjectEquipmentDTO projectEquipmentDTO) {
        projectEquipmentDTO.setId(id.intValue());
        return projectEquipmentRepository.findById(id.intValue()).map(existing -> {
            ProjectEquipmentDTO existingDTO = projectEquipmentMapper.mapTo(existing);
            // Обновляйте поля, которые необходимо.
            return projectEquipmentMapper.mapTo(projectEquipmentRepository.save(projectEquipmentMapper.mapFrom(existingDTO)));
        }).orElseThrow(() -> new RuntimeException("ProjectEquipment doesn't exist"));
    }

    public void delete(Long id) {
        projectEquipmentRepository.deleteById(id.intValue());
    }
}