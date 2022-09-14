package com.example.crazytasktrackerapi.store.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String name;


    @Builder.Default
    Instant createdAt =  Instant.now();

    @Builder.Default
    Instant updatedAt = Instant.now();

    @OneToMany
    @Builder.Default
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    List<TaskStateEntity> taskStates = new ArrayList<>();
}