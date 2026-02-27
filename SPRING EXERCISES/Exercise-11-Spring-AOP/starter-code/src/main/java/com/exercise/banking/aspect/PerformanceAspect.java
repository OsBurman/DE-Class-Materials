package com.exercise.banking.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// TODO 6: Add @Aspect and @Component to this class.
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    // TODO 7: Create an @Around advice targeting all service methods.
    //
    //         @Around("execution(* com.exercise.banking.service.*.*(..))")
    //         public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    //
    //         Steps:
    //         1. long start = System.currentTimeMillis();
    //         2. Object result = joinPoint.proceed();  ← executes the actual service method
    //         3. long end = System.currentTimeMillis();
    //         4. Log: "[PERF] " + joinPoint.getSignature().getName() + " completed in " + (end - start) + " ms"
    //         5. return result;
    //
    //         IMPORTANT: @Around MUST call joinPoint.proceed() and return its result,
    //         otherwise the actual method never executes!
}
