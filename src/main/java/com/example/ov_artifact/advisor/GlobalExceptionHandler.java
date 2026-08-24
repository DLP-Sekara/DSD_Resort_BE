package com.example.ov_artifact.advisor;

import com.example.ov_artifact.util.StandardResponse;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1.(404 Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardResponse> handleNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(
                new StandardResponse(false, 404, e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    // 2.(400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse> handleBadRequest(IllegalArgumentException e) {
        return new ResponseEntity<>(
                new StandardResponse(false, 400, e.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    // 3.(401 Unauthorized)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse> handleUnauthorized(AccessDeniedException e) {
        return new ResponseEntity<>(
                new StandardResponse(false, 401, "You don't have permission: " + e.getMessage(), null),
                HttpStatus.UNAUTHORIZED);
    }

    // 4.(For logic errors)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardResponse> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(
                new StandardResponse(false, 400, e.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    // 5.(500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleAllExceptions(Exception e) {
        return new ResponseEntity<>(
                new StandardResponse(false, 500, "Internal Server Error: " + e.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
}