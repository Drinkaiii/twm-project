package com.twm.exception;

import com.twm.dto.error.ErrorResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Order(100)
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        log.warn(e);
        e.printStackTrace();
        return new ResponseEntity<>(ErrorResponseDto.error("something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
