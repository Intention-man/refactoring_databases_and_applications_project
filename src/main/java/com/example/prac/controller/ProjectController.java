package com.example.prac.controller;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.service.data.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO savedProject = projectService.save(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.findById(id)
                .orElseThrow(() -> new com.example.prac.exception.ResourceNotFoundException("Project", id));
        return ResponseEntity.ok(project);
    }

    @GetMapping("/find_by_name_containing")
    public ResponseEntity<List<ProjectDTO>> getByNameContaining(@RequestBody String substring) {
        List<ProjectDTO> projects = projectService.findByNameContaining(substring);
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProjectPartially(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.partialUpdate(id.intValue(), projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
