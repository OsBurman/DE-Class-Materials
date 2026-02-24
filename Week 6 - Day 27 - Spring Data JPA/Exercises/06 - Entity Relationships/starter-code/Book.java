package com.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "published_year")
    private int publishedYear;

    // TODO: Add @ManyToOne relationship to Author
    //       - @JoinColumn(name = "author_id")
    //       - Field: private Author author;

    // TODO: Add @ManyToMany relationship to Tag
    //       - @JoinTable(name = "book_tags",
    //             joinColumns = @JoinColumn(name = "book_id"),
    //             inverseJoinColumns = @JoinColumn(name = "tag_id"))
    //       - Field: private List<Tag> tags = new ArrayList<>();

    protected Book() {}

    public Book(String title, int publishedYear) {
        this.title = title;
        this.publishedYear = publishedYear;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getPublishedYear() { return publishedYear; }

    // TODO: Add getter/setter for author
    // TODO: Add getter for tags

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', publishedYear=" + publishedYear + "}";
    }
}
