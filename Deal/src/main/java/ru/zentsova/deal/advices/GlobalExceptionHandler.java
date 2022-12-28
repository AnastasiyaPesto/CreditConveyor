package ru.zentsova.deal.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zentsova.deal.feign.exceptions.ConveyorException;
import ru.zentsova.deal.feign.exceptions.EntityNotExistException;
import ru.zentsova.deal.model.ApplicationError;

/**
 * Handling exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConveyorException.class)
    public ResponseEntity<ApplicationError> handle(ConveyorException ex) {
        return new ResponseEntity<>(new ApplicationError().statusCode(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotExistException.class)
    public ResponseEntity<ApplicationError> handle(EntityNotExistException ex) {
        return new ResponseEntity<>(new ApplicationError().statusCode(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}