package com.academy.products.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * AOP Logging Aspect — intercepts all service method calls.
 *
 * TODO Task 9: Add the following annotations to this class:
 *   @Aspect      — marks this as an aspect
 *   @Component   — registers it as a Spring bean
 *
 * Then implement each advice method.
 */
@Slf4j
// TODO: add @Aspect and @Component
public class LoggingAspect {

    // Reusable pointcut expression — targets all methods in the service package
    @Pointcut("execution(* com.academy.products.service.*.*(..))")
    public void serviceLayer() {}

    // TODO Task 9a: @Before — log method name and arguments
    // @Before("serviceLayer()")
    // public void logBefore(JoinPoint joinPoint) {
    //     log.info("→ Calling: {}.{}() with args: {}",
    //             joinPoint.getTarget().getClass().getSimpleName(),
    //             joinPoint.getSignature().getName(),
    //             Arrays.toString(joinPoint.getArgs()));
    // }

    // TODO Task 9b: @AfterReturning — log the return value
    // @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    // public void logAfterReturning(JoinPoint joinPoint, Object result) {
    //     log.info("← Returned from: {}() → {}",
    //             joinPoint.getSignature().getName(), result);
    // }

    // TODO Task 10: @Around — measure and log execution time
    // Apply ONLY to methods annotated with @Timed (custom annotation you create)
    // @Around("@annotation(com.academy.products.annotation.Timed)")
    // public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    //     long start = System.currentTimeMillis();
    //     Object result = joinPoint.proceed();
    //     long elapsed = System.currentTimeMillis() - start;
    //     log.info("⏱ {}() executed in {}ms", joinPoint.getSignature().getName(), elapsed);
    //     return result;
    // }
}
