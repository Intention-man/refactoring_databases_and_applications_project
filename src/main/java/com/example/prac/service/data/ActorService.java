package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ActorDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.mappers.impl.ActorMapper;
import com.example.prac.repository.auth.ActorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ActorService {
    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    public ActorDTO save(ActorDTO actorDTO) {
        Actor actor = actorMapper.mapFrom(actorDTO);
        return actorMapper.mapTo(actorRepository.save(actor));
    }

    public List<ActorDTO> findAll() {
        return StreamSupport.stream(actorRepository.findAll().spliterator(), false)
                .map(actorMapper::mapTo)
                .toList();
    }

    public Optional<ActorDTO> findById(int id) {
        return actorRepository.findById(id)
                .map(actorMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return actorRepository.existsById(id.intValue());
    }

    public ActorDTO partialUpdate(int id, ActorDTO actorDTO) {
        actorDTO.setActorId(id);
        return actorRepository.findById(id).map(existing -> {
            Optional.ofNullable(actorDTO.getRole())
                    .map(this::convertToRole)
                    .ifPresent(existing::setRole);
            Optional.ofNullable(actorDTO.getContactInformation()).ifPresent(existing::setContactInformation);
            Optional.ofNullable(actorDTO.getUsername()).ifPresent(existing::setUsername);

            return actorMapper.mapTo(actorRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Actor doesn't exist"));
    }

    private Role convertToRole(String roleString) {
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleString);
        }
    }

    public void delete(int id) {
        actorRepository.deleteById(id);
    }
}
