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

    public BookController(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> dtos = books.stream()
                .map(bookMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto dto) {
        Book book = bookMapper.toEntity(dto);
        books.add(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toDto(book));
    }
}
