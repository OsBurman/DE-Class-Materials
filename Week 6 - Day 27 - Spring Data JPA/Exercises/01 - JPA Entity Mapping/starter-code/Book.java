package com.library.model;

import jakarta.persistence.*;

// TODO: Add @Entity annotation
// TODO: Add @Table(name = "books") annotation
public class Book {

    // TODO: Add @Id annotation
    // TODO: Add @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Add @Column(nullable = false, length = 200)
    private String title;

    // TODO: Add @Column(nullable = false, length = 100)
    private String genre;

    // TODO: Add @Column(name = "published_year") to map camelCase → snake_case
    private int publishedYear;

    // TODO: Add a no-arg constructor (required by JPA — can be protected)

    // TODO: Add an all-args constructor: Book(String title, String genre, int publishedYear)

    // TODO: Add getters for all fields

    // TODO: Override toString() to return:
    //       "Book{id=" + id + ", title='" + title + "', genre='" + genre + "', publishedYear=" + publishedYear + "}"
}
