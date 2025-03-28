package com.example.raceapp.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspectTest.class);

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Test
    void logAround_ShouldLogMethodEntryAndExit() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(mock(MethodSignature.class));
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        try (MockedStatic<LoggerFactory> mocked = mockStatic(LoggerFactory.class)) {
            mocked.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

            // Act
            Object result = loggingAspect.logAround(proceedingJoinPoint);

            // Assert
            assertEquals("result", result);
            // Можно добавить проверку вызовов logger.info()
        }
    }
}