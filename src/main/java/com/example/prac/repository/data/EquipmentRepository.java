package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Equipment;
import org.springframework.data.repository.CrudRepository;

public interface EquipmentRepository extends CrudRepository<Equipment, Integer> {
}