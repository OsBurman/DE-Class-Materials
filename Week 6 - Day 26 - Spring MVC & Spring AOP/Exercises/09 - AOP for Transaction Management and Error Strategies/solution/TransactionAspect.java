package com.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionAspect.class);

    @Before("execution(* com.library.service.BookService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("[TX] Starting operation: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(
            pointcut = "execution(* com.library.service.BookService.*(..))",
            throwing = "ex")
    public void logOnException(JoinPoint joinPoint, Exception ex) {
        log.warn("[TX] Exception caught in {} — potential rollback: {}",
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}
