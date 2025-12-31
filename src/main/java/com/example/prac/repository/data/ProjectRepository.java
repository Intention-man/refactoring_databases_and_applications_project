package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Integer> {

    @Query("SELECT p FROM Project p WHERE p.name LIKE %:substring%")
    List<Project> findByNameContaining(@Param("substring") String substring);
}

