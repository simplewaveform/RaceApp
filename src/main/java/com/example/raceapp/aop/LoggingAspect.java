package com.example.raceapp.aop;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect class for logging method entry, exit, and exceptions in the application.
 * This aspect targets methods within the service and controller packages.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Logs method entry and exit along with their arguments and results.
     *
     * @param joinPoint The join point representing the method being executed.
     * @return The result of the method execution.
     * @throws Throwable If an exception occurs during method execution.
     */
    @Around("within(com.example.raceapp.service..*) || within(com.example.raceapp.controller..*)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isInfoEnabled()) {
            logger.info("Entering: {} with arguments = {}",
                    joinPoint.getSignature().toShortString(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        Object result = joinPoint.proceed();
        if (logger.isInfoEnabled()) {
            logger.info("Exiting: {} with result = {}",
                    joinPoint.getSignature().toShortString(),
                    result);
        }
        return result;
    }

    /**
     * Logs exceptions thrown by methods within the service and controller packages.
     *
     * @param joinPoint The join point representing the method that threw the exception.
     * @param ex The exception that was thrown.
     */
    @AfterThrowing(pointcut = "within(com.example.raceapp.service..*) ||"
            + "within(com.example.raceapp.controller..*)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodSignature = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());
        logger.error("Exception in {} with arguments {}: {}", methodSignature,
                args, ex.getMessage(), ex);
    }
}