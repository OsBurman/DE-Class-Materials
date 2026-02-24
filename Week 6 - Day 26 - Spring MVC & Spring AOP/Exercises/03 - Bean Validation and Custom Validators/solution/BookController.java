package com.library.controller;

import com.library.model.CreateBookRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<CreateBookRequest> books = new ArrayList<>(List.of(
            new CreateBookRequest("Clean Code", "Programming"),
            new CreateBookRequest("Dune", "Science Fiction"),
            new CreateBookRequest("The Pragmatic Programmer", "Programming")
    ));

    @GetMapping
    public ResponseEntity<List<CreateBookRequest>> getAllBooks() {
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<CreateBookRequest> createBook(@Valid @RequestBody CreateBookRequest request) {
        books.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }
}
