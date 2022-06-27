package com.example.crazytasktrackerapi.store.repositories;

import com.example.crazytasktrackerapi.store.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByName(String name);

    Stream<ProjectEntity> streamAllBy();

    // @Query("select p from ProjectEntity p where p.name like ?1%")
    Stream<ProjectEntity> streamAllByNameStartsWithIgnoreCase(String prefixName);
}
