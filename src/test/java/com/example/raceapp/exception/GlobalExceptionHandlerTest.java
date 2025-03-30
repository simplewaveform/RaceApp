package com.example.raceapp.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setup() {
        MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void handleHttpMessageNotReadable_ReturnsBadRequest() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleAllExceptions_ReturnsInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleAllExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Controller
    private static class TestController {
        @GetMapping("/test-validation")
        public void throwValidation() {
            throw new ValidationException(Map.of("test", "error"));
        }
    }

    @Test
    void handleNotFoundException_ReturnsNotFound() throws Exception {
        NotFoundException ex = new NotFoundException("Test message");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleApiExceptions(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test message", Objects.requireNonNull(response.getBody()).get("error"));
    }

    @Test
    void handleValidationException_ReturnsBadRequestWithErrors() {
        Map<String, String> errors = Map.of("field", "error message");
        ValidationException ex = new ValidationException(errors);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleApiExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).containsKey("errors"));
    }

    @Test
    void handleApiException_WithoutDetails() {
        ApiException ex = new BadRequestException("Test error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleApiExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error", Objects.requireNonNull(response.getBody()).get("error"));
        assertFalse(response.getBody().containsKey("errors"));
    }


    @Test
    void handleMissingParams_ReturnsBadRequest() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMissingParams(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Required parameter 'param' is not present", response.getBody().get("error"));
    }


}