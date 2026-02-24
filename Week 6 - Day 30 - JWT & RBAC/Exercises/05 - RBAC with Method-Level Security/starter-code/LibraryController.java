package com.jwt.rbac.controller;

import com.jwt.rbac.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exercise 05 – Library REST controller.
 *
 * Delegates to LibraryService for all business logic.
 * No changes needed here – focus on LibraryService and SecurityConfig.
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/books")
    public List<String> listBooks() {
        return libraryService.listAllBooks();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public String addBook(@RequestBody String title) {
        return libraryService.addBook(title);
    }

    @DeleteMapping("/books/{title}")
    public String deleteBook(@PathVariable String title) {
        return libraryService.deleteBook(title);
    }

    @GetMapping("/me")
    public String me() {
        return libraryService.getCurrentUser();
    }
}
