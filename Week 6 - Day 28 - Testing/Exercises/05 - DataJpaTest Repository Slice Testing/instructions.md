# Exercise 05 - @DataJpaTest: Repository Slice Testing

## Learning Objectives

By the end of this exercise you will be able to:

- Use `@DataJpaTest` to load only the JPA slice of the application context
- Test Spring Data JPA repository methods against an in-memory H2 database
- Write tests for derived query methods (`findByGenre`, `findByAuthor`)
- Use `@BeforeEach` to seed test data with `TestEntityManager`
- Understand why slice tests are faster than full `@SpringBootTest`

---

## Background

`@DataJpaTest` configures:
- An embedded H2 database (replaces your real DB automatically)
- Spring Data JPA repositories
- `TestEntityManager` for direct entity manipulation in tests
- **Nothing else** — no controllers, no services, no web layer

```java
@DataJpaTest
class BookRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired BookRepository    bookRepository;
}
```

Each test method runs inside a transaction that is **rolled back** after the
test, keeping the database clean between tests.

---

## Domain Setup

`Book` is a JPA entity (`@Entity`, `@Id`, `@GeneratedValue`).  
`BookRepository` extends `JpaRepository<Book, Long>` and adds:
- `List<Book> findByGenre(String genre)`
- `List<Book> findByAuthor(String author)`
- `Optional<Book> findByTitle(String title)`

---

## Task 1 — Save and Find

1. In `testSaveAndFindById()`:
   - Persist a `Book` with `TestEntityManager.persistAndFlush(book)`.
   - Call `bookRepository.findById(book.getId())`.
   - Assert the result is present and the title matches.

## Task 2 — Derived Query: findByGenre

2. In `testFindByGenre()`:
   - Persist two "Tech" books and one "Fiction" book.
   - Call `bookRepository.findByGenre("Tech")`.
   - Assert exactly 2 results are returned.

## Task 3 — Derived Query: findByAuthor

3. In `testFindByAuthor()`:
   - Persist two books by `"Robert C. Martin"` and one by another author.
   - Call `bookRepository.findByAuthor("Robert C. Martin")`.
   - Assert exactly 2 results are returned.

## Task 4 — findByTitle

4. In `testFindByTitle()`:
   - Persist a book with title `"Refactoring"`.
   - Call `bookRepository.findByTitle("Refactoring")`.
   - Assert the result is present.

## Task 5 — Delete

5. In `testDeleteById()`:
   - Persist a book, capture its id.
   - Call `bookRepository.deleteById(id)`.
   - Assert `bookRepository.findById(id)` is empty.

---

## Running the Tests

```bash
cd starter-code
mvn test
```

H2 is on the test classpath via `spring-boot-starter-test` — no extra setup needed.
