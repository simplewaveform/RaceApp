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

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    protected Logger getLogger() {
        return logger;
    }

    @Around("within(com.example.raceapp.service..*) || within(com.example.raceapp.controller..*)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = getLogger();
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

    @AfterThrowing(pointcut = "within(com.example.raceapp.service..*) ||"
            + "within(com.example.raceapp.controller..*)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        Logger logger = getLogger();
        String methodSignature = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());
        logger.error("Exception in {} with arguments {}: {}", methodSignature,
                args, ex.getMessage(), ex);
    }
}