package com.example.prac.controller;

import com.example.prac.data.DTO.data.ProjectEquipmentDTO;
import com.example.prac.service.data.ProjectEquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-equipment")
@AllArgsConstructor
public class ProjectEquipmentController {
    private final ProjectEquipmentService projectEquipmentService;

    @PostMapping
    public ResponseEntity<ProjectEquipmentDTO> createProjectEquipment(@RequestBody ProjectEquipmentDTO projectEquipmentDTO) {
        ProjectEquipmentDTO savedProjectEquipment = projectEquipmentService.save(projectEquipmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProjectEquipment);
    }

    @GetMapping
    public ResponseEntity<List<ProjectEquipmentDTO>> getAllProjectEquipment() {
        List<ProjectEquipmentDTO> projectEquipment = projectEquipmentService.findAll();
        return ResponseEntity.ok(projectEquipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectEquipmentDTO> getProjectEquipmentById(@PathVariable Long id) {
        return projectEquipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectEquipmentDTO> updateProjectEquipmentPartially(@PathVariable Long id, @RequestBody ProjectEquipmentDTO projectEquipmentDTO) {
        try {
            ProjectEquipmentDTO updatedProjectEquipment = projectEquipmentService.partialUpdate(id, projectEquipmentDTO);
            return ResponseEntity.ok(updatedProjectEquipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectEquipment(@PathVariable Long id) {
        projectEquipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
