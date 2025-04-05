package com.example.raceapp.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for API-related errors.
 * Provides structured error handling with HTTP status codes,
 * error codes, and additional error details.
 * All custom exceptions in the application should extend this class
 * to ensure consistent error responses.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, Object> details = new HashMap<>();

    /**
     * Constructs a new API exception.
     *
     * @param message    Human-readable error message.
     * @param status     HTTP status code.
     * @param errorCode  System-specific error code.
     */
    protected ApiException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
    }

    /**
     * Adds a single detail entry to the error.
     *
     * @param key   Detail entry key.
     * @param value Detail entry value.
     * @return Current exception instance for method chaining.
     */
    public ApiException withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    /**
     * Adds multiple detail entries to the error.
     *
     * @param details Map of detail entries.
     * @return Current exception instance for method chaining.
     * @throws IllegalArgumentException if details map is null.
     */
    public ApiException withDetails(Map<String, String> details) {
        if (details == null) {
            throw new IllegalArgumentException("Details map cannot be null");
        }
        this.details.putAll(details);
        return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}