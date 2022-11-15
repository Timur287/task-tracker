package com.example.crazytasktrackerapi.api.controllers;

import com.example.crazytasktrackerapi.api.controllers.helpers.ControllerHelper;
import com.example.crazytasktrackerapi.api.dto.AskDto;
import com.example.crazytasktrackerapi.api.dto.ProjectDto;
import com.example.crazytasktrackerapi.api.factories.ProjectDtoFactory;
import com.example.crazytasktrackerapi.exceptions.BadRequestException;
import com.example.crazytasktrackerapi.exceptions.NotFoundException;
import com.example.crazytasktrackerapi.store.entities.ProjectEntity;
import com.example.crazytasktrackerapi.store.repositories.ProjectRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
@RequestMapping("/api")
public class ProjectController {

    ProjectRepository projectRepository;
    ControllerHelper controllerHelper;

    public static final String CREATE_PROJECT = "/projects";
    public static final String EDIT_PROJECT = "/projects/{project_id}";
    public static final String DELETE_PROJECT = "/projects/{project_id}";
    public static final String FETCH_PROJECTS = "/projects";
    public static final String CREATE_OR_UPDATE_PROJECT = "/projects";



    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName
    ){
        optionalPrefixName = optionalPrefixName
                .filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectEntityStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectEntityStream.map(ProjectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }


    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam("project_name") String projectName){

        if(projectName.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty.");
        }

        projectRepository
                .findByName(projectName)
                .ifPresent(project-> {
                    throw new BadRequestException("Project is already exists");
                });

        ProjectEntity projectEntity = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(projectName)
                        .build()
        );

        return ProjectDtoFactory.makeProjectDto(projectEntity);
    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_id") Long projectId,
            @RequestParam("project_name") String projectName){

        if(projectName.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty.");
        }

        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(projectId);
        // проверка на существование имени
        projectRepository
                .findByName(projectName)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(),projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException("Project with that name is already exists");
                });

        projectEntity.setName(projectName);

        projectEntity = projectRepository.saveAndFlush(projectEntity);

        return ProjectDtoFactory.makeProjectDto(projectEntity);
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false)
                    Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false)
                    Optional<String> optionalProjectName
    ){

        optionalProjectName = optionalProjectName
                .filter(projectName->!projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();

        if(isCreate && !optionalProjectName.isPresent()){
            throw new BadRequestException("Project name can't be empty");
        }

        final ProjectEntity projectEntity = optionalProjectId
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(()->ProjectEntity.builder().build());

        // проверка существует ли уже проект с таким именем
        optionalProjectName.flatMap(projectName ->
            projectRepository
                    .findByName(projectName)
                    .filter(anotherProject ->
                            !Objects.equals(anotherProject.getId(), projectEntity.getId())
                    )
        ).ifPresent(anotherProject -> {
            throw new BadRequestException("Project" + anotherProject.getName() + "exists");
        });

        projectEntity.setName(optionalProjectName.get());

        final ProjectEntity savedProject = projectRepository.saveAndFlush(projectEntity);

        return ProjectDtoFactory.makeProjectDto(savedProject);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDto deleteProject(@PathVariable("project_id") Long projectId){

        controllerHelper.getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);
        return AskDto.makeDefault(
                !projectRepository.findById(projectId).isPresent()
        );
    }
}
