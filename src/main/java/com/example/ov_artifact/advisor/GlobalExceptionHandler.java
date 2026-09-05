package com.example.ov_artifact.advisor;

import com.example.ov_artifact.util.StandardResponse;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

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

    // 6. Data Integrity Violation (e.g., Foreign Key Constraint failure)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = "Cannot delete or update this record because it is referenced by other records.";
        
        // Make it slightly more specific if it's a foreign key constraint
        if (e.getMostSpecificCause() != null && e.getMostSpecificCause().getMessage() != null && 
            e.getMostSpecificCause().getMessage().contains("foreign key constraint fails")) {
             message = "This record cannot be deleted as it is associated with other data (e.g. reservations). Please remove associated data first.";
        }
        
        return new ResponseEntity<>(
                new StandardResponse(false, 400, message, null),
                HttpStatus.BAD_REQUEST);
    }
}