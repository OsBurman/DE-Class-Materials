package com.exercise.library.service;

import com.exercise.library.model.Book;
import com.exercise.library.model.LibraryStats;

import java.util.List;
import java.util.Optional;

// TODO 1: Define the BookService interface.
//         This interface is the CONTRACT that the controller depends on.
//         It describes WHAT the service can do, not HOW.
//
//         Add the following method signatures:
//           List<Book> getAllBooks();
//           Optional<Book> getBookById(Long id);
//           List<Book> getAvailableBooks();
//           Book addBook(Book book);
//           Book checkOutBook(Long id);
//           Book returnBook(Long id);
//           boolean removeBook(Long id);
//           LibraryStats getStats();
//
// Hint: An interface only has method signatures — no method bodies, no annotations.
public interface BookService {

    // your method signatures here

}
