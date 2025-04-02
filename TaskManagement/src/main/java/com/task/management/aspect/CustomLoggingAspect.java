package com.task.management.aspect;

import com.task.management.annotation.LogAfterThrowing;
import com.task.management.annotation.LogBefore;
import com.task.management.annotation.LogExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class CustomLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoggingAspect.class);

    @Before("@annotation(logBefore)")
    public void handleBefore(LogBefore logBefore) {
        logger.info("BEFORE: {}", logBefore.value());
    }

    @AfterThrowing(value = "@annotation(logAfterThrowing)", throwing = "ex")
    public void handleAfterThrowing(LogAfterThrowing logAfterThrowing, Exception ex) {
        logger.error("AFTER THROWING: {}. Exception: {}", logAfterThrowing.value(), ex.getMessage());
    }

    @AfterReturning(value = "@annotation(logAfterReturning)", returning = "result")
    public void handleAfterReturning(LogAfterThrowing logAfterReturning, Exception ex) {
        logger.info("AFTER RETURNING: {}. Result: {}", logAfterReturning.value(), ex.getMessage());
    }

    @Around("@annotation(logExecution)")
    public Object handleAround(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long time = System.currentTimeMillis() - startTime;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("AROUND: {} ({}). Выполнено за : {} мс", method.getName(), logExecution.value(), time);
        return result;
    }

}
