package com.example.crazytasktrackerapi.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CrazyTaskApiControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public ErrorDTO badRequestExceptionHandler(BadRequestException ex){
        return ErrorDTO.builder()
                .errorMessage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorDTO notFoundExceptionHandler(NotFoundException ex){
        return ErrorDTO.builder()
                .errorMessage(ex.getMessage())
                .build();
    }

}
