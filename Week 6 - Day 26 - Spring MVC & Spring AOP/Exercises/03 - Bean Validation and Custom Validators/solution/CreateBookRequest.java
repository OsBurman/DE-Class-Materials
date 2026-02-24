package com.library.model;

import com.library.validation.ValidGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank @Size(min = 2, max = 100) String title,
        @NotNull @ValidGenre String genre
) {}
