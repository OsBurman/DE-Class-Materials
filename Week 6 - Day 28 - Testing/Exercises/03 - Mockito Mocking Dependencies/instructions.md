# Exercise 03 - Mockito: Mocking Dependencies

## Learning Objectives

By the end of this exercise you will be able to:

- Use `@Mock` and `@InjectMocks` to isolate units under test
- Stub collaborator behaviour with `when(...).thenReturn(...)`
- Assert interaction counts with `verify(mock, times(n))`
- Use argument matchers (`any()`, `eq()`) for flexible stubbing
- Understand the difference between a **mock**, a **stub**, and a **spy**

---

## Background

Real services depend on repositories, HTTP clients, or other slow/stateful
collaborators. Mockito lets you replace those collaborators with controlled
*test doubles* so your unit tests run fast and in total isolation.

Key annotations (with `@ExtendWith(MockitoExtension.class)`):

| Annotation | Purpose |
|---|---|
| `@Mock` | Creates a mock of the annotated type |
| `@InjectMocks` | Creates the real class and injects mocks into it |
| `@Spy` | Wraps a real object, overriding only stubbed calls |
| `@Captor` | Captures arguments passed to a mock |

---

## Scenario

You have a `BookService` that delegates to a `BookRepository`.  
Your job is to test **`BookService` in isolation** by mocking `BookRepository`.

---

## Task 1 — Basic Stubbing

1. Annotate the test class with `@ExtendWith(MockitoExtension.class)`.
2. Declare `@Mock BookRepository bookRepository`.
3. Declare `@InjectMocks BookService bookService`.
4. In `testGetAllBooks()`:
   - Stub `bookRepository.findAll()` to return a list of two `Book` objects.
   - Call `bookService.getAllBooks()`.
   - Assert the returned list has 2 elements.

## Task 2 — Verify Interactions

5. In `testGetAllBooksCallsRepository()`:
   - Call `bookService.getAllBooks()`.
   - Use `verify(bookRepository, times(1)).findAll()` to confirm the repository
     was called exactly once.

## Task 3 — Argument Matchers

6. In `testSaveBook()`:
   - Stub `bookRepository.save(any(Book.class))` to return a `Book` with id `1L`.
   - Call `bookService.saveBook(new Book(...))`.
   - Assert the returned book has id `1L`.

7. In `testFindById()`:
   - Stub `bookRepository.findById(eq(1L))` to return `Optional.of(book)`.
   - Call `bookService.findById(1L)`.
   - Assert the result is present and matches the expected title.

## Task 4 — Exception Stubbing

8. In `testFindByIdNotFound()`:
   - Stub `bookRepository.findById(eq(99L))` to return `Optional.empty()`.
   - Assert that calling `bookService.findById(99L)` throws
     `RuntimeException` (or your custom `BookNotFoundException`).

---

## Running the Tests

```bash
cd starter-code
mvn test
```

A green build means all your Mockito interactions are wired correctly.

---

## Key Concepts

- **Mock**: an object whose methods return default values (null/0/empty) unless stubbed.
- **Stub**: a mock with pre-configured return values.
- **Verify**: checks *how many times* (and with *which arguments*) a method was called.
- **`any()`**: matches any non-null argument of the specified type.
- **`eq(value)`**: matches only when the argument equals `value`.
