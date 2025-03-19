package com.example.raceapp.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents a 500 Internal Server Error API response.
 * This exception should be thrown when an unexpected server-side error occurs
 * that cannot be attributed to client input. Typical scenarios include:
 * Database connection failures
 * Third-party service outages
 * Unhandled runtime exceptions
 *
 * @see ApiException Base class for API exceptions
 */
public class InternalServerException extends ApiException {

    /**
     * Constructs a new InternalServerException with an error message.
     *
     * @param message Generic error description for technical logging purposes.
     *                Should avoid exposing sensitive system details
     *                (e.g., "Failed to process payment transaction").
     */
    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "internal_error");
    }
}