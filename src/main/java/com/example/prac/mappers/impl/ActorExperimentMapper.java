package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ActorExperimentDTO;
import com.example.prac.data.model.dataEntity.ActorExperiment;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ActorExperimentMapper implements Mapper<ActorExperiment, ActorExperimentDTO> {
    private final ModelMapper modelMapper;

    public ActorExperimentMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ActorExperimentDTO mapTo(ActorExperiment actorExperiment) {
        return modelMapper.map(actorExperiment, ActorExperimentDTO.class);
    }

    @Override
    public ActorExperiment mapFrom(ActorExperimentDTO actorExperimentDTO) {
        return modelMapper.map(actorExperimentDTO, ActorExperiment.class);
    }
}
