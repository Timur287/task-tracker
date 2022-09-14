package com.example.crazytasktrackerapi.store.repositories;

import com.example.crazytasktrackerapi.store.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByName(String name);

    Optional<TaskEntity> findTaskEntityByNameAndTaskStateEntityName(String name, String taskStateName);

    Optional<TaskEntity> findTaskEntityByName(String name);
}