package com.graphql.bookstore;

public record Book(String id, String title, String genre, int year, String authorId) {}
