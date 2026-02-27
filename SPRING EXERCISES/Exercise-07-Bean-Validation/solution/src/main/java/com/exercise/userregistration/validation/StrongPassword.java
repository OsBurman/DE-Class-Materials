package com.exercise.userregistration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be 8+ characters with uppercase, lowercase, digit, and special character (@$!%*?&)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
