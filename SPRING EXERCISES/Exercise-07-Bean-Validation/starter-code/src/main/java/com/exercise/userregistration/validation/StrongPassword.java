package com.exercise.userregistration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// TODO 8: Define the @StrongPassword custom constraint annotation.
//
//         A constraint annotation MUST have:
//           1. @Constraint(validatedBy = StrongPasswordValidator.class)
//              — tells Bean Validation which validator class to use
//           2. @Target({ElementType.FIELD})
//              — this annotation can only be placed on fields
//           3. @Retention(RetentionPolicy.RUNTIME)
//              — annotation info is kept at runtime (so the framework can read it)
//           4. Three required methods (per Bean Validation spec):
//              - String message() default "Password must be 8+ chars with upper, lower, digit, special"
//              - Class<?>[] groups() default {}
//              - Class<? extends Payload>[] payload() default {}
//
// Hint: Look at how @NotBlank or @Email are defined — they follow the same pattern.

// Add the required meta-annotations here:
public @interface StrongPassword {

    // TODO 8: Add the three required methods:
    // String message() default "...";
    // Class<?>[] groups() default {};
    // Class<? extends Payload>[] payload() default {};
}
