package com.example.prac.controller;

import com.example.prac.data.DTO.data.ExperimentDTO;
import com.example.prac.service.data.ExperimentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiments")
@AllArgsConstructor
public class ExperimentController {
    private final ExperimentService experimentService;

    @PostMapping
    public ResponseEntity<ExperimentDTO> createExperiment(@RequestBody ExperimentDTO experimentDTO) {
        ExperimentDTO savedExperiment = experimentService.save(experimentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExperiment);
    }

    @GetMapping
    public ResponseEntity<List<ExperimentDTO>> getAllExperiments() {
        List<ExperimentDTO> experiments = experimentService.findAll();
        return ResponseEntity.ok(experiments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperimentDTO> getExperimentById(@PathVariable Long id) {
        return experimentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExperimentDTO> updateExperimentPartially(@PathVariable Long id, @RequestBody ExperimentDTO experimentDTO) {
        try {
            ExperimentDTO updatedExperiment = experimentService.partialUpdate(id.intValue(), experimentDTO);
            return ResponseEntity.ok(updatedExperiment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperiment(@PathVariable Long id) {
        experimentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
