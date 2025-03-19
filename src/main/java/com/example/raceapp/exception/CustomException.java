package com.example.raceapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling specific application exceptions with HTTP status codes.
 */
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
}