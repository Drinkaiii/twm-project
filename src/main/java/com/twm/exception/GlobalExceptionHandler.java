package com.twm.exception;

import com.twm.dto.error.ErrorResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Order(99)
    @ExceptionHandler(value = NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn(e);
        return new ResponseEntity<>(ErrorResponseDto.error("No static resource favicon.ico."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Order(100)
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        log.warn(e);
        e.printStackTrace();
        return new ResponseEntity<>(ErrorResponseDto.error("something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Order(100)
    @ExceptionHandler(value = IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleIOException(IOException e) {
        log.warn(e);
        e.printStackTrace();
        return new ResponseEntity<>(ErrorResponseDto.error("An I/O error occurred while processing the request."), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
