package com.example.crazytasktrackerapi.api.factories;

import com.example.crazytasktrackerapi.api.dto.TaskStateDto;
import com.example.crazytasktrackerapi.store.entities.TaskStateEntity;

public class TaskStateDtoFactory {

    public TaskStateDto makeTaskStateDto(TaskStateEntity taskStateEntity){
        return TaskStateDto.builder()
                .id(taskStateEntity.getId())
                .name(taskStateEntity.getName())
                .createdAt(taskStateEntity.getCreatedAt())
                .ordinal(taskStateEntity.getOrdinal())
                .build();
    }
}
