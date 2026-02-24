package com.library.controller;

import com.library.model.BookDto;
import com.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Add @RestController annotation
// TODO: Add @RequestMapping("/api/books") annotation
public class BookController {

    private final BookService bookService;

    // TODO: Add constructor injection — accept BookService and assign it
    public BookController() {
        this.bookService = null;
    }

    // TODO: Add @GetMapping
    // Returns all books as a list of BookDto
    public ResponseEntity<List<BookDto>> getAllBooks() {
        // TODO: return ResponseEntity.ok(bookService.getAllBooks())
        return null;
    }

    // TODO: Add @GetMapping("/{id}")
    // Returns 200 with BookDto or 404 if not found
    public ResponseEntity<BookDto> getBookById(
            // TODO: Add @PathVariable int id
    ) {
        // TODO: Use bookService.getBookById(id)
        //       .map(ResponseEntity::ok)
        //       .orElse(ResponseEntity.notFound().build())
        return null;
    }

    // TODO: Add @PostMapping
    // Returns 201 Created with the new BookDto
    public ResponseEntity<BookDto> createBook(
            // TODO: Add @RequestBody BookDto dto
    ) {
        // TODO: BookDto created = bookService.createBook(dto);
        //       return ResponseEntity.status(HttpStatus.CREATED).body(created)
        return null;
    }

    // TODO: Add @PutMapping("/{id}")
    // Returns 200 with updated BookDto or 404
    public ResponseEntity<BookDto> updateBook(
            // TODO: Add @PathVariable int id
            // TODO: Add @RequestBody BookDto dto
    ) {
        // TODO: Use bookService.updateBook(id, dto)
        //       .map(ResponseEntity::ok)
        //       .orElse(ResponseEntity.notFound().build())
        return null;
    }

    // TODO: Add @DeleteMapping("/{id}")
    // Returns 204 No Content or 404
    public ResponseEntity<Void> deleteBook(
            // TODO: Add @PathVariable int id
    ) {
        // TODO: if bookService.deleteBook(id) is true, return ResponseEntity.noContent().build()
        //       otherwise return ResponseEntity.notFound().build()
        return null;
    }
}
