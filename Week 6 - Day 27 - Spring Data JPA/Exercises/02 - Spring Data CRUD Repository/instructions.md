# Exercise 02 — Spring Data CRUD Repository

## Objective
Use `JpaRepository` to perform all standard CRUD operations without writing any SQL — only a repository interface and Spring Data's automatic implementation.

## Background
Spring Data JPA auto-generates a repository implementation at runtime from a plain interface. By extending `JpaRepository<T, ID>`, you get `save()`, `findById()`, `findAll()`, `delete()`, and more for free. This replaces the manual DAO pattern with zero boilerplate.

## Requirements
1. Complete `pom.xml` — add `spring-boot-starter-data-jpa` and `h2` (runtime scope).
2. Complete `LibraryApplication.java`.
3. `Book.java` is provided and complete — do not modify it.
4. Create `BookRepository.java` as an interface that extends `JpaRepository<Book, Long>`. No method bodies needed.
5. Complete `DataLoader.java`:
   - **Create** — save three books using `bookRepository.save(...)` and print each result.
   - **Read all** — call `bookRepository.findAll()` and print each book.
   - **Read by id** — call `bookRepository.findById(1L)` and print the result using `ifPresent`.
   - **Update** — load book with id 1, create a new `Book` with the same id but title `"Clean Code (2nd Ed.)"`, save it, and print it.
   - **Delete** — call `bookRepository.deleteById(2L)`, then print the remaining count using `bookRepository.count()`.

## Hints
- `save()` returns the managed entity with the generated id — use the return value, not the original object.
- `findById()` returns `Optional<Book>` — use `.ifPresent(System.out::println)`.
- For the update, pass a `Book` with the **existing id** to `save()` — JPA will issue an `UPDATE` not an `INSERT`.
- `bookRepository.count()` returns a `long`.

## Expected Output
```
--- CREATE ---
Saved: Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Saved: Book{id=2, title='Dune', genre='Science Fiction', publishedYear=1965}
Saved: Book{id=3, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}
--- READ ALL ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Book{id=2, title='Dune', genre='Science Fiction', publishedYear=1965}
Book{id=3, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}
--- READ BY ID ---
Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
--- UPDATE ---
Updated: Book{id=1, title='Clean Code (2nd Ed.)', genre='Programming', publishedYear=2008}
--- DELETE ---
Books remaining: 2
```
