package com.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// TODO: Add @Aspect annotation
// TODO: Add @Component annotation
public class TransactionAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionAspect.class);

    // TODO: Add @Before("execution(* com.library.service.BookService.*(..))")
    // Log: "[TX] Starting operation: " + joinPoint.getSignature().getName()
    public void logBefore(JoinPoint joinPoint) {
        // TODO
    }

    // TODO: Add @AfterThrowing(pointcut = "execution(* com.library.service.BookService.*(..))", throwing = "ex")
    // Log: "[TX] Exception caught in {} — potential rollback: {}"
    //      with joinPoint.getSignature().getName() and ex.getMessage()
    public void logOnException(JoinPoint joinPoint, Exception ex) {
        // TODO
    }
}
