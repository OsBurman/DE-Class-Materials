package com.exercise.banking.aspect;

import com.exercise.banking.annotation.Audited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// TODO 8: Add @Aspect and @Component to this class.
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    // TODO 9: Create an @Around advice that triggers on methods annotated with
    // @Audited.
    //
    // @Around("@annotation(audited)")
    // public Object audit(ProceedingJoinPoint joinPoint, Audited audited) throws
    // Throwable {
    //
    // Note: "@annotation(audited)" binds the annotation instance to the parameter
    // "audited"
    // so you can call audited.action() to get the action string.
    //
    // Steps:
    // 1. String methodName = joinPoint.getSignature().getName();
    // 2. Log: "[AUDIT] Action=" + audited.action() + " Method=" + methodName
    // + " started at " + LocalDateTime.now()
    // 3. Object result = joinPoint.proceed();
    // 4. Log: "[AUDIT] Action=" + audited.action() + " Method=" + methodName + "
    // completed successfully"
    // 5. return result;
    //
    // Bonus: Wrap proceed() in try/catch and log if it throws.
}
