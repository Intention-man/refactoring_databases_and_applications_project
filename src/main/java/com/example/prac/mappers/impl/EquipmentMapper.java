package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.EquipmentDTO;
import com.example.prac.data.model.dataEntity.Equipment;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper implements Mapper<Equipment, EquipmentDTO> {
    private final ModelMapper modelMapper;

    public EquipmentMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public EquipmentDTO mapTo(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentDTO.class);
    }

    @Override
    public Equipment mapFrom(EquipmentDTO equipmentDTO) {
        return modelMapper.map(equipmentDTO, Equipment.class);
    }
}