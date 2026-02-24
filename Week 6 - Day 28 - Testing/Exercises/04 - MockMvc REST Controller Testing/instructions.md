# Exercise 04 - MockMvc: REST Controller Testing

## Learning Objectives

By the end of this exercise you will be able to:

- Use `@WebMvcTest` to load only the web layer (no full Spring context)
- Inject `MockMvc` and use it to perform HTTP requests programmatically
- Assert HTTP status codes, response headers, and JSON body content
- Use `@MockBean` to provide a controlled service layer to the controller
- Validate request body deserialization with `POST` tests

---

## Background

`@WebMvcTest` spins up only the controllers, filters, and related web beans —
**not** the service or repository layers.  This makes the tests fast and
focused on HTTP behaviour.

```
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;      // performs fake HTTP calls

    @MockBean  BookService bookService;  // replaces the real service in the context
}
```

`MockMvc` chains:

```
mockMvc.perform(get("/books"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.length()").value(3));
```

---

## Domain

```
GET  /books          → 200 + JSON array of all books
GET  /books/{id}     → 200 + single Book JSON  |  404 if not found
POST /books          → 201 + created Book JSON
DELETE /books/{id}   → 204 No Content
```

---

## Task 1 — GET all books

1. Annotate the test class with `@WebMvcTest(BookController.class)`.
2. Inject `MockMvc` via `@Autowired`.
3. Declare `@MockBean BookService bookService`.
4. In `testGetAllBooks()`:
   - Stub `bookService.getAllBooks()` to return a list of two books.
   - `perform(get("/books"))` and expect `status().isOk()`.
   - Use `jsonPath("$.length()").value(2)` to verify the array size.

## Task 2 — GET by id (found)

5. In `testGetBookById()`:
   - Stub `bookService.findById(1L)` to return a book with title `"Clean Code"`.
   - Expect `status().isOk()` and `jsonPath("$.title").value("Clean Code")`.

## Task 3 — GET by id (not found)

6. In `testGetBookByIdNotFound()`:
   - Stub `bookService.findById(99L)` to throw `RuntimeException`.
   - Expect `status().isNotFound()` (404).

## Task 4 — POST create book

7. In `testCreateBook()`:
   - Build a JSON string for a new book.
   - Stub `bookService.saveBook(any(Book.class))` to return the saved book.
   - `perform(post("/books").contentType(APPLICATION_JSON).content(json))`.
   - Expect `status().isCreated()` and `jsonPath("$.id").value(1)`.

## Task 5 — DELETE book

8. In `testDeleteBook()`:
   - `perform(delete("/books/1"))`.
   - Expect `status().isNoContent()` (204).
   - Verify `bookService.deleteBook(1L)` was called once.

---

## Running the Tests

```bash
cd starter-code
mvn test
```
