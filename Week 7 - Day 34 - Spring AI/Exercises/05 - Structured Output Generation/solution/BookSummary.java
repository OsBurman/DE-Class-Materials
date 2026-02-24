package com.springai;

public record BookSummary(
        String title,
        String author,
        String genre,
        int yearPublished
) {}
