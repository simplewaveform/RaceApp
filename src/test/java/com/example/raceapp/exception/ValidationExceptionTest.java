package com.example.raceapp.exception;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidationExceptionTest {

    @Test
    void testConstructor_NullErrors_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new ValidationException(null);
        });
    }

    @Test
    void testConstructor_ValidErrors() {
        Map<String, String> errors = Map.of("field1", "error1", "field2", "error2");

        ValidationException exception = new ValidationException(errors);

        assertEquals("Validation failed", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(errors, exception.getErrors());

        assertEquals(errors, exception.getDetails());
    }

    @Test
    void testGetErrors() {
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid format");
        errors.put("age", "Must be over 18");

        ValidationException exception = new ValidationException(errors);
        Map<String, String> retrievedErrors = exception.getErrors();

        assertEquals(2, retrievedErrors.size());
        assertEquals("Invalid format", retrievedErrors.get("email"));
        assertEquals("Must be over 18", retrievedErrors.get("age"));
    }

}