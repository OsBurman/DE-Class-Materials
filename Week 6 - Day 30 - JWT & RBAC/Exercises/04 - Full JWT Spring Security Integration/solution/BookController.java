package com.jwt.integration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<String> books = new ArrayList<>(List.of("Clean Code", "Effective Java"));

    @GetMapping
    public List<String> getBooks() { return books; }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String addBook(@RequestBody String title) {
        books.add(title);
        return "Added: " + title;
    }
}
