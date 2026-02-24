package com.library.repository;

import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Add @Repository annotation
public class BookRepository {

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1, "Clean Code", "Programming"),
            new Book(2, "Dune", "Science Fiction"),
            new Book(3, "The Pragmatic Programmer", "Programming")
    ));

    // TODO: Implement findAll() — return the full list of books
    public List<Book> findAll() {
        return null;
    }

    // TODO: Implement findById(int id) — return Optional.of(book) or Optional.empty()
    public Optional<Book> findById(int id) {
        return null;
    }

    // TODO: Implement save(Book book) — add book to the list and return it
    public Book save(Book book) {
        return null;
    }

    // TODO: Implement update(int id, Book updated)
    //       Iterate over the list; when books.get(i).id() == id, call books.set(i, updated)
    //       Return Optional.of(updated) if found, Optional.empty() otherwise
    public Optional<Book> update(int id, Book updated) {
        return null;
    }

    // TODO: Implement delete(int id)
    //       Record size before removal, call books.removeIf(b -> b.id() == id)
    //       Return true if the size changed, false otherwise
    public boolean delete(int id) {
        return false;
    }
}
