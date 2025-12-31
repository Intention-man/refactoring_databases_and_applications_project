package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.SystemDTO;
import com.example.prac.data.model.dataEntity.System;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SystemMapper implements Mapper<System, SystemDTO> {
    private final ModelMapper modelMapper;

    public SystemMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public SystemDTO mapTo(System system) {
        return modelMapper.map(system, SystemDTO.class);
    }

    @Override
    public System mapFrom(SystemDTO systemDTO) {
        return modelMapper.map(systemDTO, System.class);
    }
}