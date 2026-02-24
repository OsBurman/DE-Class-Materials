package com.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// TODO 1: Annotate this class so Spring Data MongoDB maps it to the "books" collection.
//         Use @Document(collection = "books")
public class Book {

    // TODO 2: Annotate this field with @Id so MongoDB uses it as the document identifier.
    private String id;

    private String title;
    private String author;
    private String genre;
    private int    year;
    private double price;

    // TODO 3: Add a constructor that accepts all six fields in this order:
    //         id, title, author, genre, year, price

    // TODO 4: Add a no-argument constructor (required by Spring Data for deserialization).
    //         public Book() {}

    // TODO 5: Add getters for all six fields.
    //         getId(), getTitle(), getAuthor(), getGenre(), getYear(), getPrice()
}
