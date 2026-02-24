package com.library.controller;

import com.library.mapper.BookMapper;
import com.library.model.Book;
import com.library.model.BookDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookMapper bookMapper;

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1, "Clean Code", "Programming"),
            new Book(2, "Dune", "Science Fiction"),
            new Book(3, "The Pragmatic Programmer", "Programming")
    ));

    // TODO: Add constructor injection — accept BookMapper and assign it
    public BookController() {
        this.bookMapper = null;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        // TODO: Use bookMapper.toDto(book) to map each Book in the list to a BookDto
        //       Collect and return as ResponseEntity.ok(...)
        return null;
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto dto) {
        // TODO: Convert dto to a Book using bookMapper.toEntity(dto)
        //       Add the Book to the list
        //       Return 201 Created with bookMapper.toDto(savedBook)
        return null;
    }
}
