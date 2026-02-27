package com.exercise.socialmedia.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.exercise.socialmedia.service.*.*(..))")
    public void serviceLayer() {}

    // TODO 9: Add @Before advice — log method name + args
    // TODO 9: Add @AfterThrowing advice — log method name + exception message
}
