package com.graphql.bookstore;

/**
 * authorId is the foreign key stored in this record.
 * The actual Author object is resolved by @SchemaMapping in BookController.
 * rating is Double (nullable) – not all books have been rated.
 */
public record Book(
        String id,
        String title,
        String genre,
        int year,
        boolean available,
        Double rating,
        String authorId) {

    /** Returns a copy with the available flag changed. */
    public Book withAvailable(boolean newAvailable) {
        return new Book(id, title, genre, year, newAvailable, rating, authorId);
    }
}
