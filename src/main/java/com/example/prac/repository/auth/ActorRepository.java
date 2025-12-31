package com.example.prac.repository.auth;

import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends CrudRepository<Actor, Integer> {
    boolean existsByUsername(String username);

    Optional<Actor> findByUsername(String username);

    List<Actor> findByRole(Role role);

    boolean existsByRole(Role role);
}
