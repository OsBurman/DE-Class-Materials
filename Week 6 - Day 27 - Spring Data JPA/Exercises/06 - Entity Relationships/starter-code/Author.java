package com.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // TODO: Add @OneToMany relationship to Book
    //       - mappedBy = "author"  (matches the field name in Book.java)
    //       - cascade = CascadeType.ALL  (saving an Author also saves its Books)
    //       - Use a List<Book> field initialised to new ArrayList<>()

    protected Author() {}

    public Author(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    // TODO: Add getter for books list

    // Helper to keep both sides of the relationship in sync
    public void addBook(Book book) {
        // TODO: Add book to this.books list and set book.setAuthor(this)
    }

    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "'}";
    }
}
