package com.academy.graphql.model;

// Input record types — these are COMPLETE.

public class InputTypes {

    public record AddBookInput(String title, String genre, int publishedYear, String authorId) {
    }

    public record UpdateBookInput(String title, String genre, Integer publishedYear) {
    }

    public record AddReviewInput(int rating, String comment, String reviewer) {
    }
}
