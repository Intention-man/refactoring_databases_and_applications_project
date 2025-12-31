package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Experiment;
import org.springframework.data.repository.CrudRepository;

public interface ExperimentRepository extends CrudRepository<Experiment, Integer> {
}