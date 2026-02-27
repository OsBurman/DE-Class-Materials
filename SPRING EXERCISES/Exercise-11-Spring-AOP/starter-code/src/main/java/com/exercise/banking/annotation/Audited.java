package com.exercise.banking.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO 1: Create the @Audited custom annotation.
//         It needs:
//         - @Target(ElementType.METHOD)  — only applicable on methods
//         - @Retention(RetentionPolicy.RUNTIME)  — available at runtime for AOP to read
//         - One attribute: String action() default ""
//           This lets us mark methods with: @Audited(action = "DEPOSIT")

// Add the meta-annotations here:
public @interface Audited {
    // TODO 1: Add String action() default "";
}
