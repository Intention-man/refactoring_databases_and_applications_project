package com.example.prac.controller;

import com.example.prac.data.DTO.data.SpaceStationDTO;
import com.example.prac.service.data.SpaceStationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/space-stations")
@AllArgsConstructor
public class SpaceStationController {
    private final SpaceStationService spaceStationService;

    @PostMapping
    public ResponseEntity<SpaceStationDTO> createSpaceStation(@RequestBody SpaceStationDTO spaceStationDTO) {
        SpaceStationDTO savedSpaceStation = spaceStationService.save(spaceStationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSpaceStation);
    }

    @GetMapping
    public ResponseEntity<List<SpaceStationDTO>> getAllSpaceStations() {
        List<SpaceStationDTO> spaceStations = spaceStationService.findAll();
        return ResponseEntity.ok(spaceStations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceStationDTO> getSpaceStationById(@PathVariable Long id) {
        SpaceStationDTO spaceStation = spaceStationService.findById(id)
                .orElseThrow(() -> new com.example.prac.exception.ResourceNotFoundException("SpaceStation", id));
        return ResponseEntity.ok(spaceStation);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SpaceStationDTO> updateSpaceStationPartially(@PathVariable Long id, @RequestBody SpaceStationDTO spaceStationDTO) {
        SpaceStationDTO updatedSpaceStation = spaceStationService.partialUpdate(id.intValue(), spaceStationDTO);
        return ResponseEntity.ok(updatedSpaceStation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpaceStation(@PathVariable Long id) {
        spaceStationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}