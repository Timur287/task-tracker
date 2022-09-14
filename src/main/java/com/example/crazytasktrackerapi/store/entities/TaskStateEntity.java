package com.example.crazytasktrackerapi.store.entities;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="task_state")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    String name;

    @Builder.Default
    Instant createdAt = Instant.now();

    @OneToOne
    TaskStateEntity leftTaskState;

    @OneToOne
    TaskStateEntity rightTaskState;

    @ManyToOne
    ProjectEntity project;

    @OneToMany(mappedBy = "taskStateEntity", cascade = CascadeType.ALL)
    @Builder.Default
    List<TaskEntity> tasks = new ArrayList<>();


    public Optional<TaskStateEntity> getLeftTaskState(){
        return Optional.ofNullable(leftTaskState);
    }

    public Optional<TaskStateEntity> getRightTaskState(){
        return Optional.ofNullable(rightTaskState);
    }

}
