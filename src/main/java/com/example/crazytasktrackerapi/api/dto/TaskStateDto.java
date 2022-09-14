package com.example.crazytasktrackerapi.api.dto;

import com.example.crazytasktrackerapi.store.entities.TaskStateEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDto {

    @NonNull
    Long id;

    @NonNull
    String name;


    @JsonProperty("left_task_state_id")
    Long leftTaskStateId;

    @JsonProperty("right_task_state_id")
    Long rightTaskStateId;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
    //?
    @NonNull
    List<TaskDto> tasks;
}
