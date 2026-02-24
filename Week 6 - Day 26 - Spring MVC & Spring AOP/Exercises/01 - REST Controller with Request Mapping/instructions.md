# Exercise 01: REST Controller with Request Mapping

## Objective
Build a fully mapped REST controller using all five HTTP method annotations and practice extracting path variables, query parameters, and request bodies.

## Background
Spring MVC's `DispatcherServlet` acts as a front controller — it receives every incoming HTTP request and routes it to the correct `@RestController` method based on the URL and HTTP method. Understanding how to map different request types and extract data from them is the foundation of every Spring REST API.

## Requirements

1. Complete `pom.xml`: add `spring-boot-starter-web` to the dependencies.
2. Complete `LibraryApplication.java` with `@SpringBootApplication` and a `main` method.
3. Complete `BookController.java` with the following endpoints (all under `/api/books`):
   - `GET /api/books` — returns the hardcoded list of `Book` objects; accepts an **optional** `@RequestParam String genre` — if provided, filter the list to books matching that genre (case-insensitive); respond with `200 OK`.
   - `GET /api/books/{id}` — returns a single `Book` by `@PathVariable int id`; if not found (id not in list), return `404 Not Found` with body `"Book not found"` using `ResponseEntity`.
   - `POST /api/books` — accepts a `@RequestBody Book` and adds it to the list; respond with `201 Created` and the created book.
   - `PUT /api/books/{id}` — accepts a `@RequestBody Book` and replaces the existing book at that id; if not found, return `404`; respond with `200 OK` and the updated book.
   - `DELETE /api/books/{id}` — removes the book with that id; if not found return `404`; respond with `204 No Content`.
4. The `Book` record/class must have fields: `int id`, `String title`, `String genre`.
5. Pre-populate the in-memory list with three books: `{1, "Clean Code", "Programming"}`, `{2, "Dune", "Science Fiction"}`, `{3, "The Pragmatic Programmer", "Programming"}`.

## Hints
- `ResponseEntity.ok(body)` is shorthand for a `200` response; `ResponseEntity.status(HttpStatus.CREATED).body(book)` sets `201`.
- `ResponseEntity.notFound().build()` returns a `404` with no body; `ResponseEntity.noContent().build()` returns `204`.
- Use `java.util.ArrayList` (not `List.of`) so the list is mutable.
- `@RequestParam(required = false)` makes the parameter optional — check for `null` before filtering.

## Expected Output
`GET /api/books` → `200` with full list  
`GET /api/books?genre=Programming` → `200` with only books where genre = "Programming"  
`GET /api/books/2` → `200` with Dune  
`GET /api/books/99` → `404` with body `"Book not found"`  
`POST /api/books` with body `{"id":4,"title":"Refactoring","genre":"Programming"}` → `201` with the new book  
`DELETE /api/books/2` → `204`
