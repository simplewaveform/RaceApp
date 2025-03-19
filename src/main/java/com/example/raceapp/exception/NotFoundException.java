package com.example.raceapp.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents a 404 Not Found API error response.
 * This exception should be thrown when a requested resource cannot be located.
 * Common use cases include:
 * Missing database entities
 * Invalid resource identifiers
 * Access to deleted or archived content
 *
 * @see ApiException Base class for API exceptions
 */
public class NotFoundException extends ApiException {

    /**
     * Constructs a new NotFoundException with a descriptive message.
     *
     * @param message The human-readable error description that will be returned
     *                in the API response. Should clearly identify the missing resource
     *                (e.g., "User with ID 123 not found").
     */
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "not_found");
    }
}