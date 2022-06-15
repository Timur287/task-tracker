package com.example.crazytasktrackerapi.store.entities;

import lombok.*;

import javax.persistence.*;
import java.rmi.server.UID;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(unique = true)
    private String name;


    @Builder.Default
    private Instant createdAt =  Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany
    @Builder.Default
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private List<TaskStateEntity> taskStates = new ArrayList<>();
}