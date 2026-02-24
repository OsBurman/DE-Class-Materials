package com.testing;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService svc;
    public BookController(BookService svc) { this.svc = svc; }

    @GetMapping
    public List<Book> all() { return svc.getAllBooks(); }

    @GetMapping("/{id}")
    public ResponseEntity<Book> one(@PathVariable Long id) {
        try { return ResponseEntity.ok(svc.findById(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book b) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.saveBook(b));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
