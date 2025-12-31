package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Integer> {
}