package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ProjectEquipmentDTO;
import com.example.prac.data.model.dataEntity.ProjectEquipment;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectEquipmentMapper implements Mapper<ProjectEquipment, ProjectEquipmentDTO> {
    private final ModelMapper modelMapper;

    public ProjectEquipmentMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ProjectEquipmentDTO mapTo(ProjectEquipment projectEquipment) {
        return modelMapper.map(projectEquipment, ProjectEquipmentDTO.class);
    }

    @Override
    public ProjectEquipment mapFrom(ProjectEquipmentDTO projectEquipmentDTO) {
        return modelMapper.map(projectEquipmentDTO, ProjectEquipment.class);
    }
}