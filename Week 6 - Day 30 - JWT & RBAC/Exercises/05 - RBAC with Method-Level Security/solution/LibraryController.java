package com.jwt.rbac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller – delegates all business logic (including security checks)
 * to LibraryService. No security annotations here; they live on the service.
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    /** GET /library/books — requires authentication (enforced by @PreAuthorize on service) */
    @GetMapping("/books")
    public ResponseEntity<List<String>> listBooks() {
        return ResponseEntity.ok(libraryService.listAllBooks());
    }

    /** POST /library/books — requires ADMIN role */
    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestParam String title) {
        return ResponseEntity.status(201).body(libraryService.addBook(title));
    }

    /** DELETE /library/books/{title} — requires ADMIN or LIBRARIAN role */
    @DeleteMapping("/books/{title}")
    public ResponseEntity<String> deleteBook(@PathVariable String title) {
        return ResponseEntity.ok(libraryService.deleteBook(title));
    }

    /** GET /library/me — returns the name of the authenticated user */
    @GetMapping("/me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok(libraryService.getCurrentUser());
    }
}
