package com.library.controller;

import com.library.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// TODO: Add @RestController annotation
// TODO: Add @RequestMapping("/api/books") annotation
public class BookController {

    // TODO: Declare a private List<Book> named 'books' initialized as a new ArrayList
    //       Pre-populate it with these three books using new Book(...):
    //         Book(1, "Clean Code", "Programming")
    //         Book(2, "Dune", "Science Fiction")
    //         Book(3, "The Pragmatic Programmer", "Programming")

    // TODO: Add @GetMapping
    // Returns all books, or filters by genre if the 'genre' query param is provided
    public ResponseEntity<List<Book>> getAllBooks(
            // TODO: Add @RequestParam(required = false) String genre parameter
    ) {
        // TODO: If genre is null, return ResponseEntity.ok(books)
        //       Otherwise filter books where book.genre() equalsIgnoreCase(genre) and return the result
        return null;
    }

    // TODO: Add @GetMapping("/{id}")
    // Returns the book with matching id, or 404 if not found
    public ResponseEntity<?> getBookById(
            // TODO: Add @PathVariable int id parameter
    ) {
        // TODO: Search books for the matching id
        //       If found, return ResponseEntity.ok(book)
        //       If not found, return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found")
        return null;
    }

    // TODO: Add @PostMapping
    // Adds the given book to the list and returns 201 Created
    public ResponseEntity<Book> createBook(
            // TODO: Add @RequestBody Book book parameter
    ) {
        // TODO: add book to books list
        //       return ResponseEntity.status(HttpStatus.CREATED).body(book)
        return null;
    }

    // TODO: Add @PutMapping("/{id}")
    // Replaces the book with matching id; returns 404 if not found
    public ResponseEntity<?> updateBook(
            // TODO: Add @PathVariable int id parameter
            // TODO: Add @RequestBody Book updatedBook parameter
    ) {
        // TODO: Find the book by id using a loop and list index
        //       If found, replace it (books.set(index, updatedBook)) and return ResponseEntity.ok(updatedBook)
        //       If not found, return ResponseEntity.notFound().build()
        return null;
    }

    // TODO: Add @DeleteMapping("/{id}")
    // Removes the book with matching id; returns 204 No Content or 404
    public ResponseEntity<Void> deleteBook(
            // TODO: Add @PathVariable int id parameter
    ) {
        // TODO: Use books.removeIf(b -> b.id() == id)
        //       Track whether removal happened (size changed) and return accordingly:
        //         Success: ResponseEntity.noContent().build()
        //         Not found: ResponseEntity.notFound().build()
        return null;
    }
}
