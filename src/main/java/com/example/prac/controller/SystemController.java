package com.example.prac.controller;

import com.example.prac.data.DTO.data.SystemDTO;
import com.example.prac.service.data.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/systems")
@AllArgsConstructor
public class SystemController {
    private final SystemService systemService;

    @PostMapping
    public ResponseEntity<SystemDTO> createSystem(@RequestBody SystemDTO systemDTO) {
        SystemDTO savedSystem = systemService.save(systemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSystem);
    }

    @GetMapping
    public ResponseEntity<List<SystemDTO>> getAllSystems() {
        List<SystemDTO> systems = systemService.findAll();
        return ResponseEntity.ok(systems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemDTO> getSystemById(@PathVariable Long id) {
        return systemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SystemDTO> updateSystemPartially(@PathVariable Long id, @RequestBody SystemDTO systemDTO) {
        try {
            SystemDTO updatedSystem = systemService.partialUpdate(id.intValue(), systemDTO);
            return ResponseEntity.ok(updatedSystem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystem(@PathVariable Long id) {
        systemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
