package com.example.raceapp.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Logger logger;

    @Test
    void logAround_ShouldLogMethodEntryAndExit() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("TestClass.testMethod()");
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        LoggingAspect spyAspect = spy(loggingAspect);
        doReturn(logger).when(spyAspect).getLogger();
        when(logger.isInfoEnabled()).thenReturn(true);

        // Act
        Object result = spyAspect.logAround(proceedingJoinPoint);

        // Assert
        assertEquals("result", result);
        verify(logger).info(eq("Entering: {} with arguments = {}"),
                eq("TestClass.testMethod()"),
                eq("[arg1, 123]"));
        verify(logger).info(eq("Exiting: {} with result = {}"),
                eq("TestClass.testMethod()"),
                eq("result"));
    }

    @Test
    void logAround_ShouldNotLogWhenInfoDisabled() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        LoggingAspect spyAspect = spy(loggingAspect);
        doReturn(logger).when(spyAspect).getLogger();
        when(logger.isInfoEnabled()).thenReturn(false);

        // Act
        Object result = spyAspect.logAround(proceedingJoinPoint);

        // Assert
        assertEquals("result", result);
        verify(logger, never()).info(anyString(), any(), any());
    }

    @Test
    void logAround_ShouldRethrowException() throws Throwable {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("TestClass.testMethod()");
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        LoggingAspect spyAspect = spy(loggingAspect);
        doReturn(logger).when(spyAspect).getLogger();
        when(logger.isInfoEnabled()).thenReturn(true);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> spyAspect.logAround(proceedingJoinPoint));
        assertEquals(expectedException, thrown);
        verify(logger).info(eq("Entering: {} with arguments = {}"),
                eq("TestClass.testMethod()"),
                eq("[]"));
        verify(logger, never()).info(contains("Exiting:"));
    }

    @Test
    void logAfterThrowing_ShouldLogException() {
        // Arrange
        Throwable exception = new RuntimeException("Test exception");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("TestClass.testMethod()");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});

        LoggingAspect spyAspect = spy(loggingAspect);
        doReturn(logger).when(spyAspect).getLogger();

        // Act
        spyAspect.logAfterThrowing(joinPoint, exception);

        // Assert
        verify(logger).error(eq("Exception in {} with arguments {}: {}"),
                eq("TestClass.testMethod()"),
                eq("[arg1, 123]"),
                eq("Test exception"),
                eq(exception));
    }

    @Test
    void logAfterThrowing_ShouldLogExceptionWithEmptyArgs() {
        // Arrange
        Throwable exception = new RuntimeException("Test exception");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("TestClass.testMethod()");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        LoggingAspect spyAspect = spy(loggingAspect);
        doReturn(logger).when(spyAspect).getLogger();

        // Act
        spyAspect.logAfterThrowing(joinPoint, exception);

        // Assert
        verify(logger).error(eq("Exception in {} with arguments {}: {}"),
                eq("TestClass.testMethod()"),
                eq("[]"),
                eq("Test exception"),
                eq(exception));
    }

    @Test
    void getLogger_ShouldReturnLogger() {
        LoggingAspect aspect = new LoggingAspect();
        Logger logger = aspect.getLogger();

        assertNotNull(logger);
    }

}