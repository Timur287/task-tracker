package com.example.crazytasktrackerapi.api.controllers;

import com.example.crazytasktrackerapi.api.dto.AskDto;
import com.example.crazytasktrackerapi.api.dto.TaskDto;
import com.example.crazytasktrackerapi.api.factories.TaskDtoFactory;
import com.example.crazytasktrackerapi.exceptions.BadRequestException;
import com.example.crazytasktrackerapi.exceptions.NotFoundException;
import com.example.crazytasktrackerapi.store.entities.TaskEntity;
import com.example.crazytasktrackerapi.store.entities.TaskStateEntity;
import com.example.crazytasktrackerapi.store.repositories.TaskRepository;
import com.example.crazytasktrackerapi.store.repositories.TaskStateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskController {

    public static final String CREATE_TASK = "/tasks";
    public static final String DELETE_TASK_BY_NAME = "/tasks/";
    public static final String UPDATE_TASK = "/tasks/";
    public static final String GET_TASKS = "/tasks";
    public static final String DELETE_TASKS = "/tasks";


    private final TaskRepository taskRepository;
    private final TaskStateRepository taskStateRepository;

    @PostMapping(CREATE_TASK)
    public TaskDto createTask(@RequestParam String taskName, @RequestParam String taskStateName ){

        if(taskName.trim().isEmpty()||taskStateName.trim().isEmpty()){
            throw new BadRequestException("Task name or task state name can not be empty");
        }

        Optional<TaskEntity> taskEntity = taskRepository.findTaskEntityByNameAndTaskStateEntityName(taskName,taskStateName);

        if(taskEntity.isPresent()){
            throw new BadRequestException("Task with this name is already exists");
        }

        TaskStateEntity taskStateEntity = taskStateRepository.findByName(taskStateName).get();

        TaskEntity newTaskEntity = taskRepository.saveAndFlush(
                TaskEntity.builder()
                .name(taskName)
                .taskStateEntity(taskStateEntity)
                .build()
        );
        return TaskDtoFactory.makeTaskDto(newTaskEntity);
    }

    @GetMapping(GET_TASKS)
    public List<TaskDto> getTasks(@RequestParam String taskStateName){
        Optional<TaskStateEntity> taskStateEntity = taskStateRepository.findByName(taskStateName);
        if(!taskStateEntity.isPresent()){
            throw new BadRequestException("TaskState with this name does not exists");
        }
        return taskStateEntity.get().getTasks().stream().map(TaskDtoFactory::makeTaskDto).collect(Collectors.toList());
    }

    @PatchMapping(UPDATE_TASK)
    public TaskDto updateTask(
            @RequestParam String taskName,
            @RequestParam(required = false) Optional<String> optionalNewTaskName,
            @RequestParam(required = false) Optional<String> optionalDescription
    ){

        if(taskName.trim().isEmpty()){
            throw new BadRequestException("Task name can't be empty");
        }

        TaskEntity taskEntity = taskRepository.findByName(taskName).orElseThrow(()-> new NotFoundException("Task with this name does not exists"));

        if(!(optionalNewTaskName.isPresent()&& optionalDescription.isPresent())){
                return TaskDtoFactory.makeTaskDto(taskEntity);
        }

        String newTaskName;
        String description;

        if(optionalNewTaskName.isPresent()){
            newTaskName = optionalNewTaskName.get();
            taskEntity.setName(newTaskName);
        }

        if(optionalDescription.isPresent()){
            description = optionalDescription.get();
            taskEntity.setDescription(description);
        }

        TaskEntity savedTask = taskRepository.saveAndFlush(taskEntity);

        return TaskDtoFactory.makeTaskDto(savedTask);
    }

    @DeleteMapping(DELETE_TASK_BY_NAME)
    public AskDto deleteTask(@RequestParam String name){

        TaskEntity taskEntity = taskRepository.findByName(name).orElseThrow(()->new NotFoundException("Task with that name doesn't exists"));
        taskRepository.deleteById(taskEntity.getId());

        return AskDto.makeDefault(!taskRepository.findById(taskEntity.getId()).isPresent());
    }

    @DeleteMapping(DELETE_TASKS)
    public void deleteAllTasks(){
        taskRepository.deleteAll();
    }
}
