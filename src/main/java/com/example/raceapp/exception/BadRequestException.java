package com.example.raceapp.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents a 400 Bad Request API error response.
 * This exception should be thrown when the client sends invalid or malformed data
 * that cannot be processed by the server. Common use cases include:
 * Validation failures for request parameters or body
 * Missing required fields in payload
 * Semantic errors in request structure
 *
 * @see ApiException Base class for API exceptions
 */
public class BadRequestException extends ApiException {

    /**
     * Constructs a new BadRequestException with a detailed message.
     *
     * @param message The human-readable error description that will be returned
     *                in the API response. Should clearly explain the nature of
     *                the client error (e.g., "Invalid date format in 'startDate' field").
     */
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "bad_request");
    }
}