package com.example.prac.controller;

import com.example.prac.data.DTO.data.ActorDTO;
import com.example.prac.service.data.ActorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@AllArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @GetMapping
    public ResponseEntity<List<ActorDTO>> getAllActors() {
        List<ActorDTO> actors = actorService.findAll();
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable int id) {
        return actorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActorPartially(@PathVariable int id, @RequestBody ActorDTO actorDTO) {
        try {
            ActorDTO updatedActor = actorService.partialUpdate(id, actorDTO);
            return ResponseEntity.ok(updatedActor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable int id) {
        actorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
