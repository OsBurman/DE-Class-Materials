package com.testing;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false) private String author;
    @Column(nullable = false) private String genre;

    public Book() {}
    public Book(String title, String author, String genre) {
        this.title = title; this.author = author; this.genre = genre;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { title = t; }
    public String getAuthor() { return author; }
    public void setAuthor(String a) { author = a; }
    public String getGenre() { return genre; }
    public void setGenre(String g) { genre = g; }
}
