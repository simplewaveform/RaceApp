package com.example.raceapp.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application that handles various exceptions
 * and provides appropriate HTTP response statuses and messages.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException thrown during validation failures
     * in request bodies.
     *
     * @param ex the exception object containing validation errors
     * @return a ResponseEntity with status 400 (Bad Request) and a map of validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = (error instanceof FieldError fe) ? fe.getField() : "unknown";
            errors.put(fieldName, error.getDefaultMessage());
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles ConstraintViolationException thrown when constraints on entity fields
     * are violated.
     *
     * @param ex the exception object containing constraint violation errors
     * @return a ResponseEntity with status 400 (Bad Request) and a map of constraint violations
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });

        log.warn("Constraint violation: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles DataIntegrityViolationException thrown when there is a violation
     * in data integrity, typically during database operations.
     *
     * @param ex the exception object containing the root cause of the data integrity violation
     * @return a ResponseEntity with status 409 (Conflict) and a map containing the error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        String message = "Data integrity violation: " + Objects.requireNonNullElse(
                ex.getRootCause(), ex).getMessage();
        log.error(message, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
    }

    /**
     * Handles CustomException which is thrown for specific application errors.
     *
     * @param ex the custom exception object containing the status and error message
     * @return a ResponseEntity with the status and message from the custom exception
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(CustomException ex) {
        log.error("Custom exception: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles EntityNotFoundException thrown when an entity is not found in the database.
     *
     * @param ex the exception object containing the message for the missing entity
     * @return a ResponseEntity with status 404 (Not Found) and the error message
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles IllegalArgumentException thrown for invalid arguments passed to methods.
     *
     * @param ex the exception object containing the invalid argument message
     * @return a ResponseEntity with status 400 (Bad Request) and the error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles all other exceptions that are not explicitly handled by other methods.
     *
     * @param ex the general exception object
     * @return a ResponseEntity with status 500 (Internal Server Error) and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
                "Internal server error"));
    }
}
