package com.example.crazytasktrackerapi.api.controllers;

import com.example.crazytasktrackerapi.api.controllers.helpers.ControllerHelper;
import com.example.crazytasktrackerapi.api.dto.TaskStateDto;
import com.example.crazytasktrackerapi.api.factories.TaskStateDtoFactory;
import com.example.crazytasktrackerapi.exceptions.BadRequestException;
import com.example.crazytasktrackerapi.exceptions.NotFoundException;
import com.example.crazytasktrackerapi.store.entities.ProjectEntity;
import com.example.crazytasktrackerapi.store.entities.TaskStateEntity;
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
public class TaskStateController {

    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/projects/{project_id}/task-states";
    public static final String CREATE_TASK_STATE = "/projects/{project_id}/task-states";
    public static final String UPDATE_TASK_STATE = "/task-states/{task_state_id}";
    public static final String CHANGE_TASK_POSITION = "/task-states/{task_state_id}/position/change";



    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable("project_id") Long project_id){

        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(project_id);

        return projectEntity
                .getTaskStates()
                .stream()
                .map(TaskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto saveTaskState(
                    @PathVariable("project_id") Long projectId,
                    @RequestParam("task_state_name") String taskStateName
    ){

        if(taskStateName.trim().isEmpty()){
            throw new BadRequestException("Task state name can't be empty.");
        }
        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(projectId);

        Optional<TaskStateEntity> optionalAnotherTaskState = Optional.empty();

        //check if there is no taskstate with the specified name in the project and find the last taskstate on the right.
        for(TaskStateEntity taskState: projectEntity.getTaskStates()){
           
            if(taskState.getName().equalsIgnoreCase(taskStateName)){
                throw new BadRequestException(
                        String.format("Task with state \"%s\" already exists", taskStateName)
                );
            }
            // the element on the far right
            if(!taskState.getRightTaskState().isPresent()){
                optionalAnotherTaskState = Optional.of(taskState); 
                break;
            }
        }

        //now we create a taskstateEntity and save it to the database
        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
               TaskStateEntity.builder()
                       .name(taskStateName)
                       .project(projectEntity)
                       .build()
       );

       //setting up connections so that we can move the taskstates
       optionalAnotherTaskState
               .ifPresent(anotherTaskState-> {
                   taskState.setLeftTaskState(anotherTaskState);
                   anotherTaskState.setRightTaskState(taskState);
                   taskStateRepository.saveAndFlush(anotherTaskState);
               });

       // updating saved taskstate
       TaskStateEntity savedTaskStateEntity = taskStateRepository.saveAndFlush(taskState);

       return TaskStateDtoFactory.makeTaskStateDto(savedTaskStateEntity);

    }

    @PatchMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(@PathVariable("task_state_id") Long taskStateId,
            @RequestParam String taskStateName){

        if(taskStateName.trim().isEmpty()){
            throw new BadRequestException("Task state can't be empty");
        }

        TaskStateEntity taskStateEntity = getTaskStateOrThrowException(taskStateId);

        // check if there is already a taskstate with the specified name in this project
        taskStateRepository.findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(
                taskStateEntity.getProject().getId(), taskStateName
                ).filter(anotherTaskState -> !anotherTaskState.getId().equals(taskStateId))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException(
                            String.format("Task state with name \"%s\" already exists",taskStateName));
                });

        taskStateEntity.setName(taskStateName);

        return TaskStateDtoFactory.makeTaskStateDto(taskStateRepository.saveAndFlush(taskStateEntity));
    }
    @PatchMapping(CHANGE_TASK_POSITION)
    public TaskStateDto changeTaskPosition(
            @PathVariable("task_state_id") Long taskStateId,
            @RequestParam(name = "left_task_state_id", required = false) Optional<Long> optionalLeftTaskStateId){


        //get the id of the taskstate
        TaskStateEntity changeTaskState = getTaskStateOrThrowException(taskStateId);

        ProjectEntity project = changeTaskState.getProject();
        // if there is only one taskstate in the project
        if(project.getTaskStates().size()==1){
            return TaskStateDtoFactory.makeTaskStateDto(changeTaskState);
        }
        //id of the left taskstate before the position change
        Optional<Long> oldLeftTaskStateId = changeTaskState
                .getLeftTaskState()
                .map(TaskStateEntity::getId);
        //if the id of the taskstate on the left, which was equal to taskstateid in the parameter, then we output original taskstate
        if(oldLeftTaskStateId.equals(optionalLeftTaskStateId)){
            return TaskStateDtoFactory.makeTaskStateDto(changeTaskState);
        }
        //find taskstate that will be on the left by the specified id
        Optional<TaskStateEntity> optionalNewLeftTaskStateEntity = optionalLeftTaskStateId.map(leftTaskStateId -> {

            // if leftTaskState == taskstateId throw exception
            if(leftTaskStateId.equals(taskStateId)){
                throw new BadRequestException("Left task state is can't be equal task state id");
            }


            TaskStateEntity leftTaskStateEntity = getTaskStateOrThrowException(leftTaskStateId);
            // checking whether the taskstates is in the same project
            if(!leftTaskStateEntity.getProject().equals(project)){
                throw new BadRequestException("Task states aren't in the same project");
            }

            return leftTaskStateEntity;
        });

        Optional<TaskStateEntity> optionalNewRightTaskStateEntity;

        if(!optionalNewLeftTaskStateEntity.isPresent()){

            optionalNewRightTaskStateEntity = project
                    .getTaskStates()
                    .stream()
                    .filter(anotherTaskState -> !anotherTaskState.getLeftTaskState().isPresent())
                    .findAny();

        } else {
            optionalNewRightTaskStateEntity = optionalNewLeftTaskStateEntity
                    .get()
                    .getRightTaskState();
        }

        Optional<TaskStateEntity> oldLeftTaskState = changeTaskState.getLeftTaskState();
        Optional<TaskStateEntity> oldRightTaskState = changeTaskState.getRightTaskState();

        oldLeftTaskState.ifPresent(it-> {
                    it.setRightTaskState(oldRightTaskState.orElse(null));
                    taskStateRepository.saveAndFlush(it);
                });

        oldRightTaskState.ifPresent(it-> {
                    it.setLeftTaskState(oldLeftTaskState.orElse(null));
                    taskStateRepository.saveAndFlush(it);
                });

        if(optionalNewLeftTaskStateEntity.isPresent()) {

            TaskStateEntity newLeftTaskState = optionalNewLeftTaskStateEntity.get();

            newLeftTaskState.setRightTaskState(changeTaskState);

            changeTaskState.setLeftTaskState(newLeftTaskState);

        } else {
            changeTaskState.setLeftTaskState(null);
        }

        if(optionalNewRightTaskStateEntity.isPresent()){

            TaskStateEntity newRightTaskStateEntity = optionalNewRightTaskStateEntity.get();

            newRightTaskStateEntity.setLeftTaskState(changeTaskState);

            changeTaskState.setRightTaskState(newRightTaskStateEntity);
        } else {
            changeTaskState.setRightTaskState(null);
        }

        optionalNewLeftTaskStateEntity.ifPresent(taskStateRepository::saveAndFlush);
        optionalNewRightTaskStateEntity.ifPresent(taskStateRepository::saveAndFlush);

        return TaskStateDtoFactory.makeTaskStateDto(changeTaskState);
    }

    private TaskStateEntity getTaskStateOrThrowException(Long taskStateId){
        return taskStateRepository.findById(taskStateId)
                .orElseThrow(()-> new NotFoundException(
                        String.format("Task state with id \"%s\" not found", taskStateId))
                );
    }

}
