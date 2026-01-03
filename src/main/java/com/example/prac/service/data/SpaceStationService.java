package com.example.prac.service.data;

import com.example.prac.data.DTO.data.SpaceStationDTO;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.exception.ResourceAlreadyExistsException;
import com.example.prac.exception.ResourceNotFoundException;
import com.example.prac.mappers.impl.SpaceStationMapper;
import com.example.prac.repository.data.SpaceStationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.example.prac.mappers.impl.SpaceStationMapper.DATE_FORMATTER;

@Service
@AllArgsConstructor
public class SpaceStationService {
    private final SpaceStationRepository spaceStationRepository;
    private final SpaceStationMapper spaceStationMapper;

    public SpaceStationDTO save(SpaceStationDTO spaceStationDTO) {
        if (spaceStationDTO.getName() != null && isNameAlreadyUsed(spaceStationDTO.getName())) {
            throw new ResourceAlreadyExistsException("SpaceStation", spaceStationDTO.getName());
        }
        SpaceStation spaceStation = spaceStationMapper.mapFrom(spaceStationDTO);
        return spaceStationMapper.mapTo(spaceStationRepository.save(spaceStation));
    }

    public List<SpaceStationDTO> findAll() {
        return StreamSupport.stream(spaceStationRepository.findAll().spliterator(), false)
                .map(spaceStationMapper::mapTo)
                .toList();
    }


    public Optional<SpaceStationDTO> findById(Long id) {
        return spaceStationRepository.findById(id.intValue())
                .map(spaceStationMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return spaceStationRepository.existsById(id.intValue());
    }

    public boolean isNameAlreadyUsed(String name) {
        return spaceStationRepository.existsByName(name);
    }

//    public SpaceStationDTO partialUpdate(Long id, SpaceStationDTO spaceStationDTO) {
//        spaceStationDTO.setStationId(id.intValue());
//        return spaceStationRepository.findById(id.intValue()).map(existing -> {
//            SpaceStationDTO existingDTO = spaceStationMapper.mapTo(existing);
//            Optional.ofNullable(spaceStationDTO.getName()).ifPresent(existingDTO::setName);
//            return spaceStationMapper.mapTo(spaceStationRepository.save(spaceStationMapper.mapFrom(existingDTO)));
//        }).orElseThrow(() -> new RuntimeException("SpaceStation doesn't exist"));
//    }

    public SpaceStationDTO partialUpdate(int id, SpaceStationDTO spaceStationDTO) {
        return spaceStationRepository.findById(id).map(existing -> {
            Optional.ofNullable(spaceStationDTO.getName()).ifPresent(existing::setName);

            Optional.ofNullable(spaceStationDTO.getLaunchDate())
                    .map(launchDate -> LocalDate.parse(launchDate, DATE_FORMATTER))
                    .ifPresent(existing::setLaunchDate);

            Optional.ofNullable(spaceStationDTO.getOrbit()).ifPresent(existing::setOrbit);

            SpaceStation updatedEntity = spaceStationRepository.save(existing);
            return spaceStationMapper.mapTo(updatedEntity);
        }).orElseThrow(() -> new ResourceNotFoundException("SpaceStation", (long) id));
    }

    public void delete(Long id) {
        spaceStationRepository.deleteById(id.intValue());
    }
}