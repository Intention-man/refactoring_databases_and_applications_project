package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ActorTaskDTO;
import com.example.prac.data.model.dataEntity.ActorTask;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ActorTaskMapper implements Mapper<ActorTask, ActorTaskDTO> {
    private final ModelMapper modelMapper;

    public ActorTaskMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ActorTaskDTO mapTo(ActorTask actorTask) {
        return modelMapper.map(actorTask, ActorTaskDTO.class);
    }

    @Override
    public ActorTask mapFrom(ActorTaskDTO actorTaskDTO) {
        return modelMapper.map(actorTaskDTO, ActorTask.class);
    }
}
