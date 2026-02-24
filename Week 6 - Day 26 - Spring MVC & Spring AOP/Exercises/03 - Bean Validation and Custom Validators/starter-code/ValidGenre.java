package com.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Add @Target({ElementType.FIELD})
// TODO: Add @Retention(RetentionPolicy.RUNTIME)
// TODO: Add @Constraint(validatedBy = GenreValidator.class)
public @interface ValidGenre {

    // TODO: Add: String message() default "Genre must be one of: Programming, Science Fiction, Fantasy, History";
    // TODO: Add: Class<?>[] groups() default {};
    // TODO: Add: Class<? extends Payload>[] payload() default {};
}
