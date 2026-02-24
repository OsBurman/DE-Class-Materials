package com.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

// TODO: Add @Aspect annotation
// TODO: Add @Component annotation
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    // TODO: Add @Around("execution(* com.library.service.BookService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO: Create a new StopWatch and call start()
        // TODO: Object result = joinPoint.proceed()
        // TODO: Call stop() on the StopWatch
        // TODO: log.info("[PERF] {} completed in {} ms",
        //                joinPoint.getSignature().getName(),
        //                sw.getTotalTimeMillis())
        // TODO: return result
        return null;
    }
}
