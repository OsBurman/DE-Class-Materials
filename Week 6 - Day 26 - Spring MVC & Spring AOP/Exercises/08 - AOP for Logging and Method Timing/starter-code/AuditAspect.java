package com.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// TODO: Add @Aspect annotation
// TODO: Add @Component annotation
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    // TODO: Add @Before("execution(* com.library.service.BookService.*(..))")
    // Log: "[AUDIT] Calling: {} with args: {}"
    //      using joinPoint.getSignature().getName() and Arrays.toString(joinPoint.getArgs())
    public void logBefore(JoinPoint joinPoint) {
        // TODO
    }

    // TODO: Add @AfterReturning(pointcut = "execution(* com.library.service.BookService.*(..))", returning = "result")
    // Log: "[AUDIT] Completed: {} returned: {}"
    //      using joinPoint.getSignature().getName() and result
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        // TODO
    }
}
