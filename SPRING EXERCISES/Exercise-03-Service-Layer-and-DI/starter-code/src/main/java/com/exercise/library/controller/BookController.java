package com.exercise.library.controller;

import com.exercise.library.model.Book;
import com.exercise.library.model.LibraryStats;
import com.exercise.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// TODO 12: Add @RestController and @RequestMapping("/api/books")
public class BookController {

    // TODO 13: Declare a `private final BookService bookService` field.
    // Inject it via constructor. Notice we use the INTERFACE type BookService,
    // not the concrete BookServiceImpl — this is the dependency inversion
    // principle!

    // your constructor here

    // TODO 14a: GET /api/books — return all books with 200 OK
    public ResponseEntity<List<Book>> getAllBooks() {
        return null;
    }

    // TODO 14b: GET /api/books/{id} — return book or 404
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return null;
    }

    // TODO 14c: GET /api/books/available — return available books
    public ResponseEntity<List<Book>> getAvailableBooks() {
        return null;
    }

    // TODO 14d: POST /api/books — add a new book, return 201 Created
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return null;
    }

    // TODO 14e: PUT /api/books/{id}/checkout
    // Call bookService.checkOutBook(id)
    // If IllegalStateException: return 400 Bad Request with the error message
    // Hint: Map.of("error", e.getMessage()) as the body
    public ResponseEntity<?> checkOutBook(@PathVariable Long id) {
        // your code here
        return null;
    }

    // TODO 14f: PUT /api/books/{id}/return
    // Same pattern as checkOutBook but calls returnBook
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        // your code here
        return null;
    }

    // TODO 14g: DELETE /api/books/{id} — return 204 or 404
    public ResponseEntity<Void> removeBook(@PathVariable Long id) {
        return null;
    }

    // TODO 14h: GET /api/books/stats — return LibraryStats with 200 OK
    public ResponseEntity<LibraryStats> getStats() {
        return null;
    }
}
