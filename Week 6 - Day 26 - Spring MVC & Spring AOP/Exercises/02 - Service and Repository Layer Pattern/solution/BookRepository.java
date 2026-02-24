package com.library.repository;

import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1, "Clean Code", "Programming"),
            new Book(2, "Dune", "Science Fiction"),
            new Book(3, "The Pragmatic Programmer", "Programming")
    ));

    public List<Book> findAll() {
        return books;
    }

    public Optional<Book> findById(int id) {
        return books.stream()
                .filter(b -> b.id() == id)
                .findFirst();
    }

    public Book save(Book book) {
        books.add(book);
        return book;
    }

    public Optional<Book> update(int id, Book updated) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).id() == id) {
                books.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public boolean delete(int id) {
        int sizeBefore = books.size();
        books.removeIf(b -> b.id() == id);
        return books.size() < sizeBefore;
    }
}
