package com.example.prac.service.data;

import com.example.prac.data.DTO.data.EquipmentDTO;
import com.example.prac.data.model.dataEntity.Equipment;
import com.example.prac.mappers.impl.EquipmentMapper;
import com.example.prac.repository.data.EquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentDTO save(EquipmentDTO equipmentDTO) {
        Equipment equipment = equipmentMapper.mapFrom(equipmentDTO);
        return equipmentMapper.mapTo(equipmentRepository.save(equipment));
    }

    public List<EquipmentDTO> findAll() {
        return StreamSupport.stream(equipmentRepository.findAll().spliterator(), false)
                .map(equipmentMapper::mapTo)
                .toList();
    }

    public Optional<EquipmentDTO> findById(Long id) {
        return equipmentRepository.findById(id.intValue())
                .map(equipmentMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return equipmentRepository.existsById(id.intValue());
    }

    public EquipmentDTO partialUpdate(Long id, EquipmentDTO equipmentDTO) {
        equipmentDTO.setEquipmentId(id.intValue());
        return equipmentRepository.findById(id.intValue()).map(existing -> {
            EquipmentDTO existingDTO = equipmentMapper.mapTo(existing);
            Optional.ofNullable(equipmentDTO.getType()).ifPresent(existingDTO::setType);
            Optional.ofNullable(equipmentDTO.getDescription()).ifPresent(existingDTO::setDescription);
            Optional.ofNullable(equipmentDTO.getStatus()).ifPresent(existingDTO::setStatus);
            Optional.ofNullable(equipmentDTO.getBudget()).ifPresent(existingDTO::setBudget);
            return equipmentMapper.mapTo(equipmentRepository.save(equipmentMapper.mapFrom(existingDTO)));
        }).orElseThrow(() -> new RuntimeException("Equipment doesn't exist"));
    }


    public void delete(Long id) {
        equipmentRepository.deleteById(id.intValue());
    }
}
