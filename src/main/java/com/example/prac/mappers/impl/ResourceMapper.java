package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ResourceDTO;
import com.example.prac.data.model.dataEntity.Resource;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper implements Mapper<Resource, ResourceDTO> {
    private final ModelMapper modelMapper;

    public ResourceMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ResourceDTO mapTo(Resource resource) {
        return modelMapper.map(resource, ResourceDTO.class);
    }

    @Override
    public Resource mapFrom(ResourceDTO resourceDTO) {
        return modelMapper.map(resourceDTO, Resource.class);
    }
}