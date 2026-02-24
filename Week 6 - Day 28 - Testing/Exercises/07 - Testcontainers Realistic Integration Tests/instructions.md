# Exercise 07 - Testcontainers: Realistic Integration Tests

## Learning Objectives

By the end of this exercise you will be able to:

- Explain why H2 can give false positives that a real database would catch
- Add the Testcontainers BOM and PostgreSQL module to a Maven project
- Annotate a test class with `@Testcontainers` and `@Container`
- Wire a `PostgreSQLContainer` into Spring's datasource via `@DynamicPropertySource`
- Run JPA repository tests against a real, live PostgreSQL instance

---

## Background

H2 is convenient but it differs from PostgreSQL in edge cases (native queries,
constraints, sequences, JSON columns). Testcontainers spins up a **real Docker
container** for your test run and tears it down afterwards.

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)   // don't replace with H2
@Testcontainers
class BookRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

**Pre-requisite:** Docker must be running on your machine.

---

## Task 1 — Add Testcontainers to pom.xml

1. Add the Testcontainers BOM in `<dependencyManagement>`.
2. Add `testcontainers` core and `postgresql` module (both `scope: test`).
3. Add the PostgreSQL JDBC driver (`scope: runtime`).

## Task 2 — Configure the Container

4. Declare a `static PostgreSQLContainer<?>` field annotated with `@Container`.
5. Add `@DynamicPropertySource` to wire the container URL, username, and password
   into Spring's datasource properties.
6. Add `@AutoConfigureTestDatabase(replace = Replace.NONE)` so Spring does **not**
   swap in H2.

## Task 3 — Write Repository Tests

7. In `testSaveAndFind()`: persist a book, find it by id, assert title matches.
8. In `testFindByGenre()`: persist two "Tech" books and one "Fiction" book,
   call `findByGenre("Tech")`, assert 2 results.
9. In `testFindByTitle()`: persist a book, call `findByTitle(title)`, assert present.

## Task 4 — Observe Container Startup

10. Run `mvn test -pl .` and watch the Docker container spin up in the logs.  
    Note the startup time vs the H2 tests in Ex 05.

---

## Running the Tests

```bash
cd starter-code
mvn test     # Docker must be running
```
