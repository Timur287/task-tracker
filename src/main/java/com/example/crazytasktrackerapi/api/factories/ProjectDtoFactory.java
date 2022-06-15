package com.example.crazytasktrackerapi.api.factories;

import com.example.crazytasktrackerapi.api.dto.ProjectDto;
import com.example.crazytasktrackerapi.store.entities.ProjectEntity;
//*псевдофабрика*
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity projectEntity){
        return ProjectDto.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .createdAt(projectEntity.getCreatedAt())
                .updatedAt(projectEntity.getUpdatedAt())
                .build();
    }
}
