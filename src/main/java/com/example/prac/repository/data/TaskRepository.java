package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Integer> {
}