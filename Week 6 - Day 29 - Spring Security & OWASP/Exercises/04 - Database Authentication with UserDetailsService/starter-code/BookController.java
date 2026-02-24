package com.security.dbauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Simple REST controller used to verify Spring Security rules.
 *
 * Endpoint security (to be configured in SecurityConfig):
 *   GET  /public/books  – open to everyone (no auth required)
 *   GET  /books         – any authenticated user
 *   POST /books         – ADMIN role only
 */
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
