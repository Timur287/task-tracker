package com.example.crazytasktrackerapi.store.entities;


import lombok.*;

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
public class TaskStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String name;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToOne
    TaskStateEntity leftTaskState;

    @OneToOne
    TaskStateEntity rightTaskState;

    @ManyToOne
    ProjectEntity project;

    @OneToMany
    @Builder.Default
    @JoinColumn(name="task_state_id", referencedColumnName = "id")
    private List<TaskEntity> tasks = new ArrayList<>();


    public Optional<TaskStateEntity> getLeftTaskState(){
        return Optional.ofNullable(leftTaskState);
    }

    public Optional<TaskStateEntity> getRightTaskState(){
        return Optional.ofNullable(rightTaskState);
    }

}
