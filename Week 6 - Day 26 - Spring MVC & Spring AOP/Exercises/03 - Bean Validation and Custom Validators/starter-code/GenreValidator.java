package com.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

// TODO: Implement ConstraintValidator<ValidGenre, String>
public class GenreValidator {

    // The set of allowed genres
    private static final Set<String> ALLOWED_GENRES = Set.of(
            "Programming", "Science Fiction", "Fantasy", "History"
    );

    // TODO: Override isValid(String value, ConstraintValidatorContext context)
    //       Return true if value is not null AND ALLOWED_GENRES.contains(value)
    //       Return false otherwise
}
