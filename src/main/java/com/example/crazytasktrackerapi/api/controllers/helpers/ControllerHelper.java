package com.example.crazytasktrackerapi.api.controllers.helpers;

import com.example.crazytasktrackerapi.exceptions.NotFoundException;
import com.example.crazytasktrackerapi.store.entities.ProjectEntity;
import com.example.crazytasktrackerapi.store.repositories.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ControllerHelper {

    ProjectRepository projectRepository;

     public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(()->new NotFoundException("Project not found"));
    }
}
