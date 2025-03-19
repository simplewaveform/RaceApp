package com.example.raceapp.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Represents a 400 Bad Request error specifically for validation failures.
 * This exception should be thrown when request data validation fails, typically containing
 * multiple field-specific error messages. Common use cases include:
 * Invalid field formats (e.g., malformed email address)
 * Missing required fields
 * Business rule violations (e.g., invalid date ranges)
 *
 * @see ApiException Base class for API exceptions
 */
public class ValidationException extends ApiException {

    /**
     * Constructs a new validation exception with field-specific errors.
     *
     * @param errors Map containing validation errors where:
     *        Key: Field name with validation failure
     *        Value: Human-readable error description
     *           
     * @throws IllegalArgumentException if errors map is null
     */
    public ValidationException(Map<String, String> errors) {
        super("Validation failed", HttpStatus.BAD_REQUEST, "validation_error");
        if (errors == null) {
            throw new IllegalArgumentException("Errors map cannot be null");
        }
        this.getDetails().putAll(errors);
    }
}