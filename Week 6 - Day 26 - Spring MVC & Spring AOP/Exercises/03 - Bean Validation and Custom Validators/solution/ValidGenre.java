package com.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenreValidator.class)
public @interface ValidGenre {
    String message() default "Genre must be one of: Programming, Science Fiction, Fantasy, History";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
