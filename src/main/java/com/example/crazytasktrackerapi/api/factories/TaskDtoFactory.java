package com.example.crazytasktrackerapi.api.factories;

import com.example.crazytasktrackerapi.api.dto.TaskDto;
import com.example.crazytasktrackerapi.store.entities.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity taskEntity){
        return TaskDto.builder()
                .id(taskEntity.getId())
                .name(taskEntity.getName())
                .createdAt(taskEntity.getCreatedAt())
                .description(taskEntity.getDescription())
                .build();
    }
}
