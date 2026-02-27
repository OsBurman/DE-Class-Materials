package com.exercise.banking.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// TODO 2: Add @Aspect — tells Spring this class contains AOP advice.
//         Add @Component — makes it a Spring-managed bean.
//         Without both, the aspect will not be applied!
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Define a reusable pointcut expression — targets ALL methods in the service
    // package.
    // We can reference this pointcut by its method name in the advice annotations
    // below.
    @Pointcut("execution(* com.exercise.banking.service.*.*(..))")
    public void serviceLayer() {
    }

    // TODO 3: Create a @Before advice using the serviceLayer() pointcut.
    // Annotate with: @Before("serviceLayer()")
    // Method signature: public void logBefore(JoinPoint joinPoint)
    // Log: "[BEFORE] Calling: " + joinPoint.getSignature().getName()
    // + " with args: " + Arrays.toString(joinPoint.getArgs())

    // TODO 4: Create an @AfterReturning advice.
    // Annotate with: @AfterReturning(pointcut = "serviceLayer()", returning =
    // "result")
    // Method signature: public void logAfterReturning(JoinPoint joinPoint, Object
    // result)
    // Log: "[AFTER RETURNING] " + joinPoint.getSignature().getName() + " returned:
    // " + result

    // TODO 5: Create an @AfterThrowing advice.
    // Annotate with: @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    // Method signature: public void logAfterThrowing(JoinPoint joinPoint, Exception
    // ex)
    // Log: "[AFTER THROWING] " + joinPoint.getSignature().getName()
    // + " threw: " + ex.getMessage()
}
