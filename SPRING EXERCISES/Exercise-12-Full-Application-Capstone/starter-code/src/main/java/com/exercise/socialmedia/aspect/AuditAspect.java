package com.exercise.socialmedia.aspect;

import com.exercise.socialmedia.annotation.Audited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("[AUDIT] Action={} Method={} started at {}", audited.action(), methodName, LocalDateTime.now());
        try {
            Object result = joinPoint.proceed();
            log.info("[AUDIT] Action={} Method={} completed", audited.action(), methodName);
            return result;
        } catch (Exception ex) {
            log.warn("[AUDIT] Action={} Method={} failed: {}", audited.action(), methodName, ex.getMessage());
            throw ex;
        }
    }
}
