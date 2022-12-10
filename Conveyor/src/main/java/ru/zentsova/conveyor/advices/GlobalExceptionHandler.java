package ru.zentsova.conveyor.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zentsova.conveyor.model.ApplicationError;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;
import ru.zentsova.conveyor.util.exceptions.LoanDecisionException;

/**
 * Class GlobalExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationError> handle(ApplicationException ex) {
        return new ResponseEntity<>(new ApplicationError().statusCode(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoanDecisionException.class)
    public ResponseEntity<ApplicationError> handle(LoanDecisionException ex) {
        return new ResponseEntity<>(new ApplicationError().statusCode(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}