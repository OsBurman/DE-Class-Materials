package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String genre;

    @Column(name = "published_year")
    private int publishedYear;

    protected Book() {}

    public Book(String title, String genre, int publishedYear) {
        this.title = title;
        this.genre = genre;
        this.publishedYear = publishedYear;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getPublishedYear() { return publishedYear; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', genre='" + genre + "', publishedYear=" + publishedYear + "}";
    }
}
