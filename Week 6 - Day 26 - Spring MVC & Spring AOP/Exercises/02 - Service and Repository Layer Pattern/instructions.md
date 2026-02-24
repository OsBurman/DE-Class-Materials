# Exercise 02 — Service and Repository Layer Pattern

## Learning Objectives
- Separate HTTP, business logic, and data access into three distinct layers
- Understand the role of each layer: Controller → Service → Repository
- Use a DTO (Data Transfer Object) to decouple the API contract from the data model
- Perform manual mapping between a DTO and an entity

---

## Background

In production Spring applications, controllers should **not** directly access data. Instead:

| Layer | Responsibility | Annotation |
|---|---|---|
| **Controller** | Handle HTTP requests/responses | `@RestController` |
| **Service** | Business logic | `@Service` |
| **Repository** | Data access (in-memory or database) | `@Repository` |

A **DTO (Data Transfer Object)** is a simplified object used to carry data between layers or across the API. It often omits internal fields (like `id`) that the client should not supply.

---

## Starter Code

The following files are provided:

| File | Status |
|---|---|
| `pom.xml` | Complete — add `spring-boot-starter-web` dependency |
| `LibraryApplication.java` | Complete the `@SpringBootApplication` setup |
| `Book.java` | Complete — entity with `id`, `title`, `genre` |
| `BookDto.java` | Complete — DTO with `title` and `genre` only |
| `BookRepository.java` | Complete the in-memory repository |
| `BookService.java` | Complete the business logic layer |
| `BookController.java` | Complete the REST controller |

---

## Tasks

### 1. Finish `pom.xml`
- Add `spring-boot-starter-web` as a dependency

### 2. Finish `LibraryApplication.java`
- Add `@SpringBootApplication`
- Call `SpringApplication.run(LibraryApplication.class, args)` in `main`

### 3. Finish `BookRepository.java`
The repository holds the data. Implement the following methods using the in-memory `List<Book>`:

| Method | Signature | Behaviour |
|---|---|---|
| `findAll` | `List<Book> findAll()` | Return all books |
| `findById` | `Optional<Book> findById(int id)` | Return matching book or empty |
| `save` | `Book save(Book book)` | Add to list, return saved book |
| `update` | `Optional<Book> update(int id, Book updated)` | Replace by id, return updated book |
| `delete` | `boolean delete(int id)` | Remove by id, return true if removed |

### 4. Finish `BookService.java`
The service layer calls the repository and converts between `Book` and `BookDto`.

- `getAllBooks()` — return `List<BookDto>` mapped from all books
- `getBookById(int id)` — return `Optional<BookDto>`
- `createBook(BookDto dto)` — generate a new id (e.g., `books size + 1`), map to `Book`, save, return `BookDto`
- `updateBook(int id, BookDto dto)` — map dto to Book, call repository update, return `Optional<BookDto>`
- `deleteBook(int id)` — delegate to repository, return `boolean`

**Manual mapping helper** (add a private method in `BookService`):
```java
private BookDto toDto(Book book) {
    return new BookDto(book.title(), book.genre());
}
```

### 5. Finish `BookController.java`
Inject `BookService` via constructor. Implement the five endpoints:

| Method | Path | Request | Response |
|---|---|---|---|
| `GET` | `/api/books` | — | `200 OK` list of `BookDto` |
| `GET` | `/api/books/{id}` | — | `200 OK` or `404` |
| `POST` | `/api/books` | `BookDto` body | `201 Created` with new `BookDto` |
| `PUT` | `/api/books/{id}` | `BookDto` body | `200 OK` or `404` |
| `DELETE` | `/api/books/{id}` | — | `204 No Content` or `404` |

---

## Expected Behaviour

Test with curl or Postman after running `LibraryApplication`:

```bash
# GET all
curl http://localhost:8080/api/books

# GET by id
curl http://localhost:8080/api/books/1

# POST (note: no id in body)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Refactoring","genre":"Programming"}'

# PUT
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code (Updated)","genre":"Programming"}'

# DELETE
curl -X DELETE http://localhost:8080/api/books/2
```

---

## Reflection Questions

1. Why is it a bad idea for the controller to directly access the repository?
2. What is the difference between `Book` (entity) and `BookDto`?
3. In `BookService.createBook()`, why do we generate the `id` in the service layer rather than accepting it from the client?
4. What are the trade-offs of manual mapping vs a mapping library like MapStruct?
