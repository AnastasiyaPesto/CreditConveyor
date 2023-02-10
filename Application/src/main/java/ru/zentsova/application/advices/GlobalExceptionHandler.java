package ru.zentsova.application.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zentsova.application.exceptions.ApplicationException;
import ru.zentsova.application.model.ApplicationError;

/**
 * Handling exceptions in controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationError> handle(ApplicationException ex) {
        return new ResponseEntity<>(new ApplicationError().statusCode(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}