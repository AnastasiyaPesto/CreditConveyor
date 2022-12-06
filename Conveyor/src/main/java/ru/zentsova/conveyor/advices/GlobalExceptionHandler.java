package ru.zentsova.conveyor.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zentsova.conveyor.util.exceptions.ApplicationError;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;

/**
 * Class GlobalExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationError> handle(ApplicationException ex) {
        return new ResponseEntity<>(new ApplicationError(HttpStatus.CONFLICT.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}