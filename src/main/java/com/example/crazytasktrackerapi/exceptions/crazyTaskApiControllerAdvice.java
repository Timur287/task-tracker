package com.example.crazytasktrackerapi.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class crazyTaskApiControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public String badRequestExceptionHandler(BadRequestException ex){
        return "Bad request exception";
    }

}
