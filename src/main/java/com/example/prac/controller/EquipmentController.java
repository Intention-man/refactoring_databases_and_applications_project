package com.example.prac.controller;

import com.example.prac.data.DTO.data.EquipmentDTO;
import com.example.prac.service.data.EquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@AllArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentDTO> createEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        EquipmentDTO savedEquipment = equipmentService.save(equipmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        List<EquipmentDTO> equipment = equipmentService.findAll();
        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipmentById(@PathVariable Long id) {
        return equipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EquipmentDTO> updateEquipmentPartially(@PathVariable Long id, @RequestBody EquipmentDTO equipmentDTO) {
        try {
            EquipmentDTO updatedEquipment = equipmentService.partialUpdate(id, equipmentDTO);
            return ResponseEntity.ok(updatedEquipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
