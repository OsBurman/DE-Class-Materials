# Exercise 03 — Service Layer & Dependency Injection

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Understand the **3-layer architecture**: Controller → Service → Repository
- Create a `@Service` component and inject it into a controller
- Program to **interfaces** (`BookService` interface + `BookServiceImpl` implementation)
- Use **constructor injection** (the preferred Spring approach)
- Understand the difference between `@Component`, `@Service`, `@Repository`, `@Controller`
- Apply **single responsibility principle** — business logic lives in the service layer

---

## 📋 What You're Building
A **Library Book Management System** demonstrating clean separation of concerns.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/books` | Get all books |
| `GET` | `/api/books/{id}` | Get a book by ID |
| `GET` | `/api/books/available` | Get only available books |
| `POST` | `/api/books` | Add a new book |
| `PUT` | `/api/books/{id}/checkout` | Check out a book (mark unavailable) |
| `PUT` | `/api/books/{id}/return` | Return a book (mark available) |
| `DELETE` | `/api/books/{id}` | Remove a book |
| `GET` | `/api/books/stats` | Get library statistics |

---

## 🏗️ Project Setup
```bash
cd Exercise-03-Service-Layer-and-DI/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/library/
├── LibraryApplication.java
├── model/
│   ├── Book.java
│   └── LibraryStats.java
├── repository/
│   └── BookRepository.java       ← In-memory store (already done)
├── service/
│   ├── BookService.java          ← ⭐ Interface — define the contract
│   └── BookServiceImpl.java      ← ⭐ Implementation — the business logic
└── controller/
    └── BookController.java       ← ⭐ Thin controller — delegates to service
```

---

## ✅ TODOs

### `service/BookService.java`
- [ ] **TODO 1**: Create the `BookService` interface with these method signatures:
  - `List<Book> getAllBooks()`
  - `Optional<Book> getBookById(Long id)`
  - `List<Book> getAvailableBooks()`
  - `Book addBook(Book book)`
  - `Book checkOutBook(Long id)`  — throws `IllegalStateException` if already checked out
  - `Book returnBook(Long id)`    — throws `IllegalStateException` if not checked out
  - `boolean removeBook(Long id)`
  - `LibraryStats getStats()`

### `service/BookServiceImpl.java`
- [ ] **TODO 2**: Add `@Service` annotation to make this a Spring-managed bean
- [ ] **TODO 3**: Inject `BookRepository` via constructor injection
- [ ] **TODO 4**: Implement `getAllBooks()` — delegate to repository
- [ ] **TODO 5**: Implement `getBookById(Long id)` — delegate to repository
- [ ] **TODO 6**: Implement `getAvailableBooks()` — use repository, filter where `isAvailable == true`
- [ ] **TODO 7**: Implement `addBook(Book book)` — set `availableDate = LocalDate.now()`, then save
- [ ] **TODO 8**: Implement `checkOutBook(Long id)`:
  - Find book or throw `NoSuchElementException("Book not found: " + id)`
  - If `!book.isAvailable()`, throw `IllegalStateException("Book is already checked out")`
  - Set `book.setAvailable(false)`, save, return saved book
- [ ] **TODO 9**: Implement `returnBook(Long id)`:
  - Find book or throw `NoSuchElementException("Book not found: " + id)`
  - If `book.isAvailable()`, throw `IllegalStateException("Book is not checked out")`
  - Set `book.setAvailable(true)`, save, return saved book
- [ ] **TODO 10**: Implement `removeBook(Long id)` — delegate to repository
- [ ] **TODO 11**: Implement `getStats()` — build a `LibraryStats` object:
  - `totalBooks` = all books count
  - `availableBooks` = count of available books
  - `checkedOutBooks` = count of unavailable books

### `controller/BookController.java`
- [ ] **TODO 12**: Add `@RestController` and `@RequestMapping("/api/books")`
- [ ] **TODO 13**: Inject `BookService` (the **interface**, not `BookServiceImpl`) via constructor
- [ ] **TODO 14**: Implement all 8 endpoints by delegating to the service
  - For `checkoutBook` and `returnBook`: catch `IllegalStateException` and return `400 Bad Request`
  - For `getStats`: return `200 OK` with `LibraryStats`

---

## 💡 Key Concepts

### Why Use an Interface?
```java
// Controller depends on the INTERFACE, not the implementation
private final BookService bookService;  // ✅ Good

// vs.
private final BookServiceImpl bookService;  // ❌ Tightly coupled
```
This allows you to swap implementations (e.g., for testing: `MockBookService`).

### Spring Stereotype Annotations
| Annotation | Meaning | Where Used |
|-----------|---------|-----------|
| `@Component` | Generic Spring bean | Utility classes |
| `@Service` | Business logic bean | Service layer |
| `@Repository` | Data access bean | Repository layer |
| `@Controller` / `@RestController` | HTTP handler | Controller layer |

All four work the same way technically — they're all `@Component` specializations.
The difference is semantic clarity and tooling support.

### Constructor Injection (Recommended)
```java
@Service
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;  // final = immutable once injected

    // Spring sees ONE constructor and injects automatically (no @Autowired needed)
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```
