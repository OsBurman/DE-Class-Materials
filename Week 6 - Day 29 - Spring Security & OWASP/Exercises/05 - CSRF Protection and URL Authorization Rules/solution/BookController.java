package com.security.csrfauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @GetMapping("/public/books")
    public List<String> publicBooks() {
        return List.of("Clean Code", "The Pragmatic Programmer");
    }

    @GetMapping("/books")
    public List<String> securedBooks() {
        return List.of("Spring in Action", "Effective Java");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public String addBook(@RequestBody String title) {
        return "Book added: " + title;
    }

    @PutMapping("/books/{id}")
    public String updateBook(@PathVariable Long id, @RequestBody String title) {
        return "Book " + id + " updated to: " + title;
    }

    @DeleteMapping("/books/{id}")
    public String deleteBook(@PathVariable Long id) {
        return "Book " + id + " deleted.";
    }
}
