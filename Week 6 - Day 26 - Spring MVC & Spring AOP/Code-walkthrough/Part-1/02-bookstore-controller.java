package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.CreateBookRequest;
import com.bookstore.dto.UpdateBookRequest;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// =============================================================================
// @RestController vs @Controller
// =============================================================================
// @Controller  → marks a Spring MVC controller; return values are resolved as
//                VIEW NAMES (Thymeleaf, JSP, etc.) unless you also add
//                @ResponseBody to a method.
//
// @RestController → shorthand for @Controller + @ResponseBody on EVERY method.
//                   Every return value is serialized directly to JSON (via
//                   Jackson). Use this for REST APIs — which is what we build.
// =============================================================================

@RestController
// =============================================================================
// @RequestMapping at the CLASS level
// =============================================================================
// Sets a BASE PATH for every endpoint in this controller.
// All mappings inside combine with this prefix.
// e.g.: @GetMapping("/") becomes GET /api/v1/books
//       @GetMapping("/{id}") becomes GET /api/v1/books/{id}
// =============================================================================
@RequestMapping("/api/v1/books")
public class BookController {

    // Inject the service — controllers call services, never repositories
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // =========================================================================
    // @GetMapping — HTTP GET
    // =========================================================================
    // Returns a list of all books.
    // Produces: HTTP 200 OK + JSON array
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks(
            // =================================================================
            // @RequestParam — query string parameters
            // =================================================================
            // Optional request params: GET /api/v1/books?genre=fiction&page=0
            // 'required = false' → parameter is optional (won't 400 if absent)
            // 'defaultValue' → used when the param is absent
            // =================================================================
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        List<BookDTO> books = bookService.findAll(genre, page, size);
        return ResponseEntity.ok(books);  // shorthand for ResponseEntity with 200 OK
    }

    // =========================================================================
    // @GetMapping with @PathVariable — HTTP GET /{id}
    // =========================================================================
    // Path variable: GET /api/v1/books/42
    // {id} in the path template maps to the 'id' method parameter.
    //
    // @PathVariable String id  → name matches template variable by default
    // @PathVariable("bookId") Long id → explicit name mapping
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    // =========================================================================
    // @PostMapping — HTTP POST (create a new resource)
    // =========================================================================
    // @RequestBody → Spring deserializes the JSON request body into the
    //                CreateBookRequest object using Jackson.
    //                Without this annotation, the parameter would be null.
    //
    // @Valid → triggers Bean Validation on the @RequestBody object.
    //          If validation fails, Spring throws MethodArgumentNotValidException
    //          and returns 400 Bad Request before your method is even called.
    //
    // Best practice: return 201 Created with a Location header pointing to
    //                the newly created resource.
    // =========================================================================

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookDTO created = bookService.createBook(request);

        // Build a Location header: http://localhost:8080/api/v1/books/43
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity
                .created(location)    // Sets status to 201 Created
                .body(created);       // Includes the created resource in the body
    }

    // =========================================================================
    // @PutMapping — HTTP PUT (full update / replace)
    // =========================================================================
    // PUT replaces the entire resource. The client sends all fields.
    // Combines a path variable (which resource) and a request body (new state).
    // =========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {

        BookDTO updated = bookService.updateBook(id, request);
        return ResponseEntity.ok(updated);
    }

    // =========================================================================
    // @PatchMapping — HTTP PATCH (partial update)
    // =========================================================================
    // PATCH updates only the fields provided by the client.
    // Often used when you only want to change one field (e.g., price) without
    // sending the entire object.
    // =========================================================================

    @PatchMapping("/{id}/price")
    public ResponseEntity<BookDTO> updateBookPrice(
            @PathVariable Long id,
            @RequestParam double newPrice) {

        BookDTO updated = bookService.updatePrice(id, newPrice);
        return ResponseEntity.ok(updated);
    }

    // =========================================================================
    // @DeleteMapping — HTTP DELETE
    // =========================================================================
    // Returns 204 No Content — successful deletion, no response body.
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    // =========================================================================
    // ResponseEntity — full control over the HTTP response
    // =========================================================================
    // ResponseEntity<T> lets you control:
    //   - Status code (200, 201, 204, 400, 404, 500, ...)
    //   - Response headers
    //   - Response body
    //
    // Common factory methods:
    //   ResponseEntity.ok(body)            → 200 OK with body
    //   ResponseEntity.created(uri)        → 201 Created with Location header
    //   ResponseEntity.noContent()         → 204 No Content
    //   ResponseEntity.notFound()          → 404 Not Found (no body)
    //   ResponseEntity.badRequest()        → 400 Bad Request
    //   ResponseEntity.status(418).body(x) → any status code
    // =========================================================================

    // =========================================================================
    // Custom response with manual headers
    // =========================================================================
    // Shows how to add custom HTTP headers to a response.
    // GET /api/v1/books/42/download → returns book as a "file download"
    // =========================================================================

    @GetMapping("/{id}/export")
    public ResponseEntity<String> exportBookData(@PathVariable Long id) {
        BookDTO book = bookService.findById(id);
        String csvLine = book.getId() + "," + book.getTitle() + "," + book.getAuthor();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"book-" + id + ".csv\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add("X-Book-Export-Version", "1.0");  // custom header

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvLine);
    }

    // =========================================================================
    // @RequestMapping (generic) — matches ANY HTTP method
    // =========================================================================
    // Use when you need fine-grained method + path control,
    // or when matching multiple HTTP verbs to the same handler.
    // Most of the time, prefer the specific @GetMapping / @PostMapping etc.
    // =========================================================================

    // Example: HEAD request (same as GET but no body — used by API clients to
    // check if a resource exists without downloading it)
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkBookExists(@PathVariable Long id) {
        bookService.findById(id);  // throws 404 if not found
        return ResponseEntity.ok().build();  // 200 OK, no body
    }

    // =========================================================================
    // HTTP Status Codes — Common Reference
    // =========================================================================
    //
    // 2xx SUCCESS
    //   200 OK            — General success
    //   201 Created       — Resource created (POST)
    //   204 No Content    — Success, no body (DELETE, PUT with no return)
    //
    // 3xx REDIRECTION
    //   301 Moved Permanently — Resource moved to new URL
    //   302 Found             — Temporary redirect
    //
    // 4xx CLIENT ERRORS
    //   400 Bad Request      — Invalid input / validation failure
    //   401 Unauthorized     — Authentication required
    //   403 Forbidden        — Authenticated but not authorized
    //   404 Not Found        — Resource doesn't exist
    //   409 Conflict         — Duplicate / state conflict
    //   422 Unprocessable    — Valid JSON but semantic errors
    //
    // 5xx SERVER ERRORS
    //   500 Internal Server Error — Unexpected exception
    //   503 Service Unavailable  — Server overloaded / down for maintenance
    // =========================================================================
}
