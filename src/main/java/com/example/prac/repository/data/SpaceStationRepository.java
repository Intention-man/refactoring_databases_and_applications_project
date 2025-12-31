package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.SpaceStation;
import org.springframework.data.repository.CrudRepository;

public interface SpaceStationRepository extends CrudRepository<SpaceStation, Integer> {
    boolean existsByName(String name);
}