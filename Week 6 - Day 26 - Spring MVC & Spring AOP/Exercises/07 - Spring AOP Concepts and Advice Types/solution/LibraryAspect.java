package com.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LibraryAspect {

    @Pointcut("execution(* com.library.service.BookService.*(..))")
    public void bookServiceMethods() {}

    @Before("bookServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[Before] About to execute: " + joinPoint.getSignature().getName());
    }

    @After("bookServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("[After] Finished executing: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "bookServiceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[AfterReturning] Method returned: " + result);
    }

    @AfterThrowing(pointcut = "bookServiceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        System.out.println("[AfterThrowing] Exception thrown: " + ex.getMessage());
    }

    @Around("bookServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("[Around] Before: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        System.out.println("[Around] After: " + joinPoint.getSignature().getName());
        return result;
    }
}
