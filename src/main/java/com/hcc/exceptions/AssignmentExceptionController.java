package com.hcc.exceptions;

import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AssignmentExceptionController extends ResponseEntityExceptionHandler {
    private final Logger log = LogManager.getLogger(AssignmentExceptionController.class);

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = UnauthorizedUpdateException.class)
    public ResponseEntity<?> handleUnauthorizedUpdateException(UnauthorizedUpdateException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
}
