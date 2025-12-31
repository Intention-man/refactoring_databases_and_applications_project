package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ModuleDTO;
import com.example.prac.data.model.dataEntity.Module;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper implements Mapper<Module, ModuleDTO> {
    private final ModelMapper modelMapper;

    public ModuleMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ModuleDTO mapTo(Module module) {
        return modelMapper.map(module, ModuleDTO.class);
    }

    @Override
    public Module mapFrom(ModuleDTO moduleDTO) {
        return modelMapper.map(moduleDTO, Module.class);
    }
}