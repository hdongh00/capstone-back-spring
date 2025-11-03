package com.website.common;

import com.website.common.exception.InvalidEnumValueException;
import com.website.common.exception.NotAllowException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<String> handleInvalidEnum(InvalidEnumValueException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(NotAllowException.class)
    public ResponseEntity<String> handleNotAllow(NotAllowException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
