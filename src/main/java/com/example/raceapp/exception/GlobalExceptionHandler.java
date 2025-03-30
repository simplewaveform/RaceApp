package com.example.raceapp.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers. Centralizes exception handling
 * and returns standardized error responses in JSON format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class); // <--- Добавил логгер

    /**
     * Handles all custom exceptions derived from {@link ApiException}.
     * Logs the exception and returns a structured error response.
     *
     * @param ex the caught custom exception
     * @return ResponseEntity containing error details and HTTP status
     */
    @ExceptionHandler({
        BadRequestException.class,
        NotFoundException.class,
        InternalServerException.class,
        ValidationException.class
    })
    public ResponseEntity<Map<String, Object>> handleApiExceptions(ApiException ex) {
        log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(ex);
    }

    /**
     * Handles Spring validation errors from {@link MethodArgumentNotValidException}.
     * Extracts field-level errors and wraps them in a {@link ValidationException}.
     *
     * @param ex the caught validation exception
     * @return ResponseEntity containing validation errors and HTTP status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");

        // Convert errors to Map<String, List<String>> (field → list of messages)
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        body.put("errors", errors); // Provide a Map instead of a List
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for all unanticipated exceptions.
     * Logs the exception and returns a generic 500 Internal Server Error response.
     *
     * @param ex the caught exception
     * @return ResponseEntity with generic error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        ApiException apiEx = new InternalServerException("Internal server error");
        return buildResponse(apiEx);
    }

    /**
     * Handles {@link HttpMessageNotReadableException} which occurs when the request
     * body cannot be converted to a target object.
     * This typically happens due to invalid JSON format or missing required fields.
     *
     * @param ex the {@link HttpMessageNotReadableException} that was thrown
     * @return a {@link ResponseEntity} containing an error response with
     *         details about the exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        ApiException apiEx = new BadRequestException("Invalid JSON format: " + ex.getMessage());
        return buildResponse(apiEx);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = "Required parameter '" + ex.getParameterName() + "' is not present";
        ApiException apiEx = new BadRequestException(message);
        return buildResponse(apiEx);
    }

    /**
     * Constructs a standardized error response body.
     *
     * @param ex the exception to process
     * @return ResponseEntity with structured error response
     */
    private ResponseEntity<Map<String, Object>> buildResponse(ApiException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getMessage());

        if (!ex.getDetails().isEmpty()) {
            body.put("errors", ex.getDetails());
        }

        return new ResponseEntity<>(body, ex.getStatus());
    }
}