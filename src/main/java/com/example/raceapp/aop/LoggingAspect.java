package com.example.raceapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.example.raceapp.controller.*.*(..)) || " +
            "execution(* com.example.raceapp.service.*.*(..)) || " +
            "execution(* com.example.raceapp.repository.*.*(..))")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("Entering {}.{}() with args: {}", className, methodName, args);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("Exiting {}.{}() | Execution time: {} ms | Result: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), result);
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("Error in {}.{}() | Time: {} ms | Error: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage(), e);
            throw e;
        }
    }
}