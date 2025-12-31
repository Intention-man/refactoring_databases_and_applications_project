package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.SpaceStationDTO;
import com.example.prac.data.model.dataEntity.SpaceStation;
import com.example.prac.mappers.Mapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class SpaceStationMapper implements Mapper<SpaceStation, SpaceStationDTO> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SpaceStation mapFrom(SpaceStationDTO dto) {
        if (dto == null) {
            return null;
        }

        SpaceStation spaceStation = new SpaceStation();
        spaceStation.setStationId(dto.getStationId());
        spaceStation.setName(dto.getName());
        spaceStation.setOrbit(dto.getOrbit());

        if (dto.getLaunchDate() != null) {
            spaceStation.setLaunchDate(LocalDate.parse(dto.getLaunchDate(), DATE_FORMATTER));
        }

        return spaceStation;
    }

    public SpaceStationDTO mapTo(SpaceStation entity) {
        if (entity == null) {
            return null;
        }

        SpaceStationDTO dto = new SpaceStationDTO();
        dto.setStationId(entity.getStationId());
        dto.setName(entity.getName());
        dto.setOrbit(entity.getOrbit());

        if (entity.getLaunchDate() != null) {
            dto.setLaunchDate(entity.getLaunchDate().format(DATE_FORMATTER));
        }

        return dto;
    }
}
