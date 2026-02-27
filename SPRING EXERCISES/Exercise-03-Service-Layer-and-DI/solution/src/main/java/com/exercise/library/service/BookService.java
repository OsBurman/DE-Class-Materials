package com.exercise.library.service;

import com.exercise.library.model.Book;
import com.exercise.library.model.LibraryStats;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();
    Optional<Book> getBookById(Long id);
    List<Book> getAvailableBooks();
    Book addBook(Book book);
    Book checkOutBook(Long id);
    Book returnBook(Long id);
    boolean removeBook(Long id);
    LibraryStats getStats();
}
