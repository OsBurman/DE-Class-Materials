package com.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class GenreValidator implements ConstraintValidator<ValidGenre, String> {

    private static final Set<String> ALLOWED_GENRES = Set.of(
            "Programming", "Science Fiction", "Fantasy", "History"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && ALLOWED_GENRES.contains(value);
    }
}
