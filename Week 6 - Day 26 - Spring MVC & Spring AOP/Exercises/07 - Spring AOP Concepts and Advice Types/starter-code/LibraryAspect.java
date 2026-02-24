package com.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

// TODO: Add @Aspect annotation
// TODO: Add @Component annotation
public class LibraryAspect {

    // TODO: Define a named pointcut targeting all methods in BookService
    //       Pointcut expression: "execution(* com.library.service.BookService.*(..))"
    //       Method name: bookServiceMethods()
    @Pointcut("execution(* com.library.service.BookService.*(..))")
    public void bookServiceMethods() {}

    // TODO: Add @Before("bookServiceMethods()")
    // Log: "[Before] About to execute: " + joinPoint.getSignature().getName()
    public void logBefore(JoinPoint joinPoint) {
        // TODO: System.out.println(...)
    }

    // TODO: Add @After("bookServiceMethods()")
    // Log: "[After] Finished executing: " + joinPoint.getSignature().getName()
    public void logAfter(JoinPoint joinPoint) {
        // TODO: System.out.println(...)
    }

    // TODO: Add @AfterReturning(pointcut = "bookServiceMethods()", returning = "result")
    // Log: "[AfterReturning] Method returned: " + result
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        // TODO: System.out.println(...)
    }

    // TODO: Add @AfterThrowing(pointcut = "bookServiceMethods()", throwing = "ex")
    // Log: "[AfterThrowing] Exception thrown: " + ex.getMessage()
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        // TODO: System.out.println(...)
    }

    // TODO: Add @Around("bookServiceMethods()")
    // Log before proceed, call joinPoint.proceed(), log after, return result
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO: System.out.println("[Around] Before: " + joinPoint.getSignature().getName())
        // TODO: Object result = joinPoint.proceed()
        // TODO: System.out.println("[Around] After: " + joinPoint.getSignature().getName())
        // TODO: return result
        return null;
    }
}
