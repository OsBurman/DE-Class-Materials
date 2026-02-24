package com.library.model;

// TODO: Add @NotBlank and @Size(min = 2, max = 100) on the title field
// TODO: Add @NotNull and @ValidGenre on the genre field
public record CreateBookRequest(
        String title,
        String genre
) {}
