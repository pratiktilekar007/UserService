package com.lcwd.user.service.exceptions;

import com.lcwd.user.service.payload.APiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobeExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APiResponse> handleResourceNotFoundException(ResourceNotFoundException ex){

        String message = ex.getMessage();

      APiResponse aPiResponse=  APiResponse.builder().message(message).success(true).status(HttpStatus.NOT_FOUND).build();

        return  new ResponseEntity<>(aPiResponse,HttpStatus.NOT_FOUND );

    }
}
