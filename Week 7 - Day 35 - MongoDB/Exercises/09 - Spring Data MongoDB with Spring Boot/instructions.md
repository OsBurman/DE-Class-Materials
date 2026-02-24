# Exercise 09: Spring Data MongoDB with Spring Boot

## Objective
Integrate MongoDB into a Spring Boot application using Spring Data MongoDB — define a document entity, create a `MongoRepository`, and write integration tests using an embedded MongoDB instance.

## Background
Spring Data MongoDB provides a familiar repository abstraction over MongoDB, similar to Spring Data JPA for relational databases. You annotate a POJO with `@Document`, extend `MongoRepository<T, ID>`, and Spring generates the implementation automatically. For testing without a live MongoDB server, the `de.flapdoodle.embed.mongo` library spins up an embedded MongoDB instance.

## Requirements

### Step 1 — Project setup
The `pom.xml` is provided. Review the dependencies: `spring-boot-starter-data-mongodb`, `de.flapdoodle.embed.mongo` (test scope), and `spring-boot-starter-test`.

### Step 2 — Define the document entity
Open `starter-code/Book.java`.

**TODO 1:** Annotate the class with `@Document(collection = "books")`.

**TODO 2:** Annotate the `id` field with `@Id`.

**TODO 3:** Add a constructor that accepts all fields: `id`, `title`, `author`, `genre`, `year`, `price`.

**TODO 4:** Add a no-argument constructor (required by Spring Data for deserialization).

**TODO 5:** Add getters for all fields.

### Step 3 — Create the repository
Open `starter-code/BookRepository.java`.

**TODO 6:** Make `BookRepository` extend `MongoRepository<Book, String>`.

**TODO 7:** Declare a derived query method `List<Book> findByGenre(String genre)` — Spring Data generates the implementation.

**TODO 8:** Declare a derived query method `List<Book> findByYearGreaterThan(int year)` — returns books published after the given year.

### Step 4 — Write integration tests
Open `starter-code/BookRepositoryTest.java`.

**TODO 9:** In `setUp()`, call `bookRepository.deleteAll()` then save three books using `bookRepository.saveAll(...)`.

**TODO 10:** Write test `findAll_returnsAllBooks()` — assert 3 books are returned.

**TODO 11:** Write test `findByGenre_returnsCorrectBooks()` — call `findByGenre("Technology")` and assert the result contains the correct title(s).

**TODO 12:** Write test `findByYearGreaterThan_filtersCorrectly()` — call `findByYearGreaterThan(2000)` and assert only books published after 2000 are returned.

**TODO 13:** Write test `save_andFindById_works()` — save a new book, capture its `id`, then call `findById(id)` and assert the title matches.

## Hints
- `@DataMongoTest` is the test slice annotation for MongoDB tests — it starts an embedded Mongo and only loads `@Document` + repository beans.
- `bookRepository.saveAll(List.of(...))` inserts multiple documents in one call.
- Derived query method names follow the pattern `findBy<FieldName><Condition>` — no implementation needed.
- `Optional<Book> result = bookRepository.findById(id)` — use `result.isPresent()` or `result.get()` in assertions.

## Expected Output

```
// All 4 tests pass:
BookRepositoryTest > findAll_returnsAllBooks() PASSED
BookRepositoryTest > findByGenre_returnsCorrectBooks() PASSED
BookRepositoryTest > findByYearGreaterThan_filtersCorrectly() PASSED
BookRepositoryTest > save_andFindById_works() PASSED
```
