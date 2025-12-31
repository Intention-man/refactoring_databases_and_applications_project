package com.example.prac.controller;

import com.example.prac.data.DTO.data.ActorTaskDTO;
import com.example.prac.service.data.ActorTaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actor-tasks")
@AllArgsConstructor
public class ActorTaskController {
    private final ActorTaskService actorTaskService;

    @PostMapping
    public ResponseEntity<ActorTaskDTO> createActorTask(@RequestBody ActorTaskDTO actorTaskDTO) {
        ActorTaskDTO savedActorTask = actorTaskService.save(actorTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActorTask);
    }

    @GetMapping
    public ResponseEntity<List<ActorTaskDTO>> getAllActorTasks() {
        List<ActorTaskDTO> actorTasks = actorTaskService.findAll();
        return ResponseEntity.ok(actorTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorTaskDTO> getActorTaskById(@PathVariable Long id) {
        return actorTaskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorTaskDTO> updateActorTaskPartially(@PathVariable Long id, @RequestBody ActorTaskDTO actorTaskDTO) {
        try {
            ActorTaskDTO updatedActorTask = actorTaskService.partialUpdate(id.intValue(), actorTaskDTO);
            return ResponseEntity.ok(updatedActorTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorTask(@PathVariable Long id) {
        actorTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
