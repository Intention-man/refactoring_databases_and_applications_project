package com.example.prac.controller;

import com.example.prac.data.DTO.data.ResourceDTO;
import com.example.prac.service.data.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceDTO> createResource(@RequestBody ResourceDTO resourceDTO) {
        ResourceDTO savedResource = resourceService.save(resourceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResource);
    }

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        List<ResourceDTO> resources = resourceService.findAll();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getResourceById(@PathVariable Long id) {
        ResourceDTO resource = resourceService.findById(id)
                .orElseThrow(() -> new com.example.prac.exception.ResourceNotFoundException("Resource", id));
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResourceDTO> updateResourcePartially(@PathVariable Long id, @RequestBody ResourceDTO resourceDTO) {
        ResourceDTO updatedResource = resourceService.partialUpdate(id.intValue(), resourceDTO);
        return ResponseEntity.ok(updatedResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
