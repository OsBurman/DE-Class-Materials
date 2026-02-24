package com.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class BookController {

    @GetMapping("/public/books")
    public List<String> publicBooks() { return List.of("The Hobbit", "Dune"); }

    @GetMapping("/books")
    public List<String> getBooks() { return List.of("Clean Code", "Effective Java"); }

    @PostMapping("/books")
    public ResponseEntity<String> createBook(@RequestBody String title) {
        return ResponseEntity.status(HttpStatus.CREATED).body("Created: " + title);
    }
}
