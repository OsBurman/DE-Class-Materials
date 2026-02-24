# Coding Standards & Best Practices

## Why Coding Standards Matter

Coding standards are agreed-upon rules that govern how source code is written — naming, formatting, structure, and documentation. They are not suggestions — they are professional norms that determine:

- **Readability** — other developers (and future-you) can understand code quickly
- **Maintainability** — consistent patterns make it easy to find and change code
- **Collaboration** — teams can work in the same codebase without friction
- **Code reviews** — reviewers focus on logic, not style
- **Automated tooling** — linters, formatters, and IDE inspections enforce rules automatically

> "Code is read far more often than it is written." — widely attributed to Guido van Rossum

---

## Java Naming Conventions

Java naming conventions are enforced by community standards (Oracle Java Code Conventions) and major style guides (Google Java Style Guide). Deviating from these will be noticed in every code review.

### Classes and Interfaces

| Rule | Convention | Example |
|---|---|---|
| Naming style | **PascalCase** (UpperCamelCase) | `BookService`, `UserRepository` |
| Class names | Noun or noun phrase | `Order`, `PaymentProcessor` |
| Interface names | Adjective or noun | `Serializable`, `BookRepository` |
| Abstract classes | Often prefixed with `Abstract` | `AbstractEntity`, `AbstractController` |
| Exception classes | Suffixed with `Exception` | `BookNotFoundException`, `ValidationException` |
| Test classes | Suffixed with `Test` | `BookServiceTest`, `OrderControllerTest` |

```java
// ✅ CORRECT
public class BookService { }
public interface PaymentGateway { }
public abstract class AbstractEntity { }
public class BookNotFoundException extends RuntimeException { }

// ❌ WRONG
public class bookService { }     // lowercase start
public class Book_Service { }    // underscores
public class BOOKSERVICE { }     // all caps
```

---

### Methods

| Rule | Convention | Example |
|---|---|---|
| Naming style | **camelCase** (lowerCamelCase) | `findById`, `calculateTotal` |
| Method names | Verb or verb phrase | `save()`, `processOrder()`, `isValid()` |
| Boolean methods | Prefix with `is`, `has`, `can`, `should` | `isActive()`, `hasPermission()` |
| Getters | Prefix with `get` | `getName()`, `getPrice()` |
| Setters | Prefix with `set` | `setName()`, `setPrice()` |
| Factory methods | Often `of`, `from`, `create`, `build` | `List.of()`, `Optional.of()` |

```java
// ✅ CORRECT
public List<Book> findAllByAuthor(String authorName) { }
public boolean isAvailable() { }
public void processCheckout(Order order) { }

// ❌ WRONG
public List<Book> FindAllByAuthor(String authorName) { }  // PascalCase
public void Process_Checkout(Order order) { }             // underscores
public void a(Order o) { }                                // non-descriptive
```

---

### Variables and Parameters

| Rule | Convention | Example |
|---|---|---|
| Naming style | **camelCase** | `bookTitle`, `totalPrice` |
| Names | Descriptive nouns | `customer`, `orderCount` |
| Loop counters | `i`, `j`, `k` acceptable | `for (int i = 0; ...)` |
| Avoid | Single letters (except loops) | ~~`String s`~~ → `String name` |
| Avoid | Abbreviations | ~~`custNm`~~ → `customerName` |
| Collections | Plural noun | `books`, `orderItems` |

```java
// ✅ CORRECT
String customerName;
int totalItemCount;
List<Book> availableBooks;
Map<String, Order> ordersByCustomerId;

// ❌ WRONG
String cn;           // cryptic abbreviation
int x;               // meaningless name
List<Book> list;     // non-descriptive type name
Map<String, Order> m;
```

---

### Constants

| Rule | Convention | Example |
|---|---|---|
| Naming style | **UPPER_SNAKE_CASE** | `MAX_RETRY_COUNT` |
| Declaration | `static final` | `public static final int MAX_SIZE = 100;` |
| Location | Class-level, usually `public static final` or within an `enum` | |

```java
// ✅ CORRECT
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_CURRENCY = "USD";
public static final double TAX_RATE = 0.08;

// ❌ WRONG
public static final int maxRetryCount = 3;  // camelCase
public static final String defaultCurrency = "USD";
int MAX_SIZE = 100;   // not static final — not a constant
```

---

### Packages

| Rule | Convention | Example |
|---|---|---|
| Naming style | All **lowercase**, no underscores | `com.example.bookstore` |
| Structure | Reversed domain name + project + layer | `com.revature.bookstore.service` |
| Avoid | Uppercase letters | ~~`com.Example.BookStore`~~ |
| Avoid | Underscores | ~~`com.example.book_store`~~ |

```
com.revature.bookstore
    ├── controller     ← REST controllers
    ├── service        ← Business logic
    ├── repository     ← Data access layer
    ├── model          ← (or domain / entity)
    │   └── entity
    ├── dto            ← Data Transfer Objects
    ├── exception      ← Custom exceptions
    ├── config         ← Spring configuration classes
    └── util           ← Utility/helper classes
```

---

## File Organization and Package Structure

### Standard Layered Architecture (most common in Spring projects)

```
src/main/java/com/revature/bookstore/
├── BookstoreApplication.java          ← @SpringBootApplication entry point
│
├── controller/
│   ├── BookController.java            ← Handles HTTP requests for /books
│   └── OrderController.java
│
├── service/
│   ├── BookService.java               ← Interface (if needed)
│   ├── BookServiceImpl.java           ← Implementation
│   └── OrderService.java
│
├── repository/
│   ├── BookRepository.java            ← Spring Data JPA interface
│   └── OrderRepository.java
│
├── model/
│   ├── Book.java                      ← JPA entity or domain object
│   ├── Order.java
│   └── Author.java
│
├── dto/
│   ├── BookRequest.java               ← Input DTO
│   ├── BookResponse.java              ← Output DTO
│   └── ErrorResponse.java
│
├── exception/
│   ├── BookNotFoundException.java
│   └── GlobalExceptionHandler.java    ← @ControllerAdvice
│
├── config/
│   ├── AppConfig.java                 ← Spring @Configuration
│   └── SecurityConfig.java
│
└── util/
    └── DateUtils.java
```

### One Class Per File Rule

Java enforces one **public** class per file, and the filename **must** match the class name exactly (case-sensitive).

```java
// File: BookService.java
public class BookService {       // ✅ filename matches class name
    // ...
}
```

### Keep Classes Focused — Single Responsibility Principle

Each class should have **one reason to change**. Signs a class needs to be split:
- The class name contains "and" (`BookAndOrderService`)
- The class is longer than ~200–300 lines
- Methods in the class have nothing in common

---

## Code Comments and Documentation

### When to Comment

Comments should explain **why**, not **what**. Code itself should explain **what** it does.

```java
// ❌ BAD — explains the obvious
int count = 0;  // set count to zero

// ✅ GOOD — explains business reason
// Skip the first item because it's a header row, not real data
for (int i = 1; i < rows.size(); i++) { }
```

### Inline Comments

Use `//` for single-line or end-of-line comments. Keep them brief.

```java
// ✅ Good inline comments
String encryptedPassword = bcrypt.hash(rawPassword);  // never store plain text passwords

// TODO: Replace with Redis cache when scaling beyond single node
Map<String, Book> bookCache = new HashMap<>();

// FIXME: This breaks when author name contains special characters
String query = "SELECT * FROM books WHERE author = '" + name + "'";
```

### Block Comments (Rare)

Use `/* */` for longer inline explanations or temporarily disabling code blocks.

```java
/*
 * Retry logic: we attempt the payment up to MAX_RETRY_COUNT times
 * with exponential backoff before throwing a PaymentFailedException.
 * This avoids hammering the payment gateway on transient failures.
 */
for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
    // ...
}
```

---

## Javadoc

Javadoc is Java's built-in documentation system. It generates HTML documentation from structured comments placed directly above classes, methods, and fields.

Javadoc comments use `/** ... */` (note double asterisk on opening line).

### Class-Level Javadoc

```java
/**
 * Service layer for managing book inventory in the Bookstore application.
 *
 * <p>Provides CRUD operations and business logic for {@link Book} entities.
 * All methods validate input and throw appropriate exceptions on failure.</p>
 *
 * @author Scott Burman
 * @version 1.0
 * @since 2024-01-15
 * @see BookRepository
 */
public class BookService {
```

### Method-Level Javadoc

```java
/**
 * Retrieves a book by its unique identifier.
 *
 * <p>If no book is found with the given ID, a
 * {@link BookNotFoundException} is thrown.</p>
 *
 * @param bookId  the unique ID of the book to retrieve; must not be null
 * @return        the {@link Book} with the specified ID
 * @throws BookNotFoundException  if no book exists with the given {@code bookId}
 * @throws IllegalArgumentException  if {@code bookId} is null
 */
public Book findById(Long bookId) {
    if (bookId == null) {
        throw new IllegalArgumentException("bookId must not be null");
    }
    return bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));
}
```

### Constructor Javadoc

```java
/**
 * Constructs a new {@code BookService} with the required repository dependency.
 *
 * @param bookRepository  the repository used for book data access; must not be null
 */
public BookService(BookRepository bookRepository) {
    this.bookRepository = Objects.requireNonNull(bookRepository,
            "bookRepository must not be null");
}
```

### Field-Level Javadoc

```java
/**
 * Maximum number of books returnable in a single paginated response.
 * Prevents memory exhaustion on large datasets.
 */
public static final int MAX_PAGE_SIZE = 100;
```

### Javadoc Tags Reference

| Tag | Applies To | Purpose |
|---|---|---|
| `@param name description` | Methods, constructors | Documents a parameter |
| `@return description` | Methods (non-void) | Documents return value |
| `@throws ExceptionType description` | Methods, constructors | Documents a thrown exception |
| `@author name` | Classes | Author of the class |
| `@version version` | Classes | Version string |
| `@since version-or-date` | Classes, methods | When this was introduced |
| `@see TypeOrMethod` | Any | Cross-reference link |
| `{@link ClassName#method}` | Any (inline) | Inline hyperlink |
| `{@code expression}` | Any (inline) | Inline monospaced code |
| `@deprecated message` | Any | Marks element as deprecated |

### Generating Javadoc

```bash
# Maven
mvn javadoc:javadoc
# Output: target/site/apidocs/

# Gradle
./gradlew javadoc
# Output: build/docs/javadoc/
```

---

## Anti-Patterns to Avoid

### Naming Anti-Patterns

```java
// ❌ Meaningless names
int d;
String temp;
Object obj;
List<String> data;

// ❌ Misleading names
boolean isTrue = false;          // variable name contradicts value
int count = calculateTotal();    // "count" doesn't describe what this actually is

// ❌ Hungarian notation (do NOT use in Java)
String strName;
int intCount;
boolean bIsActive;

// ❌ Excessive abbreviations
String custNm;
int empCnt;
```

### Comment Anti-Patterns

```java
// ❌ Commented-out code (use Git, not comments)
// String result = legacyCalculation(x, y);
// if (result.contains("error")) return null;

// ❌ Redundant comments
// Increment i by 1
i++;

// ❌ Outdated comments (worse than no comment)
// Returns a List of all active users
public List<Order> findAllOrders() { }   // method no longer does what comment says
```

### Structure Anti-Patterns

```java
// ❌ God class — doing too many things
public class ApplicationManager {
    public void saveUser() { }
    public void sendEmail() { }
    public void generateReport() { }
    public void connectToDatabase() { }
    public void validatePayment() { }
    // 200 more methods...
}

// ❌ Magic numbers — unexplained numeric literals
if (status == 3) { }             // what does 3 mean?
double discounted = price * 0.85; // where does 0.85 come from?

// ✅ Use named constants instead
private static final int STATUS_SHIPPED = 3;
private static final double MEMBER_DISCOUNT_RATE = 0.85;

if (status == STATUS_SHIPPED) { }
double discounted = price * MEMBER_DISCOUNT_RATE;
```

---

## Summary Checklist

| Area | Convention |
|---|---|
| Classes | PascalCase, noun/noun phrase |
| Interfaces | PascalCase, adjective or noun |
| Methods | camelCase, verb/verb phrase |
| Variables | camelCase, descriptive noun |
| Constants | UPPER_SNAKE_CASE, `static final` |
| Packages | all lowercase, no underscores |
| Booleans | prefix with `is`, `has`, `can` |
| Exceptions | suffix with `Exception` |
| Tests | suffix with `Test` |
| File names | match public class name exactly |
| Javadoc | required for all public API members |
| Comments | explain why, not what |
| Magic numbers | replace with named constants |
| Dead code | delete it — use Git history |
