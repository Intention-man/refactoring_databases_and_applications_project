package com.example.prac.controller;

import com.example.prac.data.DTO.data.ActorExperimentDTO;
import com.example.prac.service.data.ActorExperimentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actor-experiments")
@AllArgsConstructor
public class ActorExperimentController {
    private final ActorExperimentService actorExperimentService;

    @PostMapping
    public ResponseEntity<ActorExperimentDTO> createActorExperiment(@RequestBody ActorExperimentDTO actorExperimentDTO) {
        ActorExperimentDTO savedActorExperiment = actorExperimentService.save(actorExperimentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActorExperiment);
    }

    @GetMapping
    public ResponseEntity<List<ActorExperimentDTO>> getAllActorExperiments() {
        List<ActorExperimentDTO> actorExperiments = actorExperimentService.findAll();
        return ResponseEntity.ok(actorExperiments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorExperimentDTO> getActorExperimentById(@PathVariable Long id) {
        return actorExperimentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorExperimentDTO> updateActorExperimentPartially(@PathVariable Long id, @RequestBody ActorExperimentDTO actorExperimentDTO) {
        try {
            ActorExperimentDTO updatedActorExperiment = actorExperimentService.partialUpdate(id.intValue(), actorExperimentDTO);
            return ResponseEntity.ok(updatedActorExperiment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorExperiment(@PathVariable Long id) {
        actorExperimentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
