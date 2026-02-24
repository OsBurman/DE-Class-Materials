package com.library.service;

import com.library.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1, "Clean Code", "Programming"),
            new Book(2, "Dune", "Science Fiction"),
            new Book(3, "The Pragmatic Programmer", "Programming")
    ));

    public List<Book> getAllBooks() {
        return books;
    }

    public Optional<Book> getBookById(int id) {
        return books.stream().filter(b -> b.id() == id).findFirst();
    }

    public Book createBook(Book book) {
        books.add(book);
        return book;
    }

    public void deleteBook(int id) {
        boolean removed = books.removeIf(b -> b.id() == id);
        if (!removed) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
    }
}
