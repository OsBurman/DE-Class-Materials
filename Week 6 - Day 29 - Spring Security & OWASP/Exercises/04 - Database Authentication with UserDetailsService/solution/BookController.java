package com.security.dbauth.controller;

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
}
