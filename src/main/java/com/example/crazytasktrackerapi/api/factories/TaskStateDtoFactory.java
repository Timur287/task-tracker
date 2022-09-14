package com.example.crazytasktrackerapi.api.factories;

import com.example.crazytasktrackerapi.api.dto.TaskDto;
import com.example.crazytasktrackerapi.api.dto.TaskStateDto;
import com.example.crazytasktrackerapi.store.entities.TaskStateEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskStateDtoFactory {

    public static TaskStateDto makeTaskStateDto(TaskStateEntity taskStateEntity){
        return TaskStateDto.builder()
                .id(taskStateEntity.getId())
                .name(taskStateEntity.getName())
                .createdAt(taskStateEntity.getCreatedAt())
                .leftTaskStateId(taskStateEntity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(taskStateEntity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .tasks(taskStateEntity
                        .getTasks()
                        .stream()
                        .map(TaskDtoFactory::makeTaskDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
