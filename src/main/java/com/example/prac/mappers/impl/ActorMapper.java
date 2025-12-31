package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.ActorDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ActorMapper implements Mapper<Actor, ActorDTO> {
    private final ModelMapper modelMapper;

    public ActorMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ActorDTO mapTo(Actor actor) {
        return modelMapper.map(actor, ActorDTO.class);
    }

    @Override
    public Actor mapFrom(ActorDTO actorDTO) {
        return modelMapper.map(actorDTO, Actor.class);
    }
}
