package com.example.raceapp.exception;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ApiExceptionTest {

    private static final String TEST_MESSAGE = "Test error";
    private static final HttpStatus TEST_STATUS = HttpStatus.BAD_REQUEST;
    private static final String TEST_CODE = "ERR_001";

    private static class TestApiException extends ApiException {
        protected TestApiException(String message, HttpStatus status, String errorCode) {
            super(message, status, errorCode);
        }
    }

    @Test
    void testConstructor() {
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE);

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_STATUS, exception.getStatus());
        assertTrue(exception.getDetails().isEmpty());
    }

    @Test
    void testWithDetails_NullMap_ThrowsException() {
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE);

        assertThrows(IllegalArgumentException.class, () -> exception.withDetails(null));
    }

    @Test
    void testWithDetails_AddsEntries() {
        Map<String, String> details = Map.of("key1", "value1", "key2", "value2");
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE)
                .withDetails(details);

        assertEquals(2, exception.getDetails().size());
        assertEquals("value1", exception.getDetails().get("key1"));
        assertEquals("value2", exception.getDetails().get("key2"));
    }

    @Test
    void testWithDetail_SingleEntry() {
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE)
                .withDetail("errorKey", "errorValue");

        assertEquals(1, exception.getDetails().size());
        assertEquals("errorValue", exception.getDetails().get("errorKey"));
    }

    @Test
    void testWithDetail_MultipleEntries() {
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE)
                .withDetail("code", 404)
                .withDetail("retryable", true);

        assertEquals(2, exception.getDetails().size());
        assertEquals(404, exception.getDetails().get("code"));
        assertEquals(true, exception.getDetails().get("retryable"));
    }

    @Test
    void testMethodChaining() {
        ApiException exception = new TestApiException(TEST_MESSAGE, TEST_STATUS, TEST_CODE);

        assertSame(exception, exception.withDetail("key", "value"));
        assertSame(exception, exception.withDetails(new HashMap<>()));
    }
}