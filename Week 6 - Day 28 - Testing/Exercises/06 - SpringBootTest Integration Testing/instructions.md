# Exercise 06 - @SpringBootTest: Integration Testing

## Learning Objectives

By the end of this exercise you will be able to:

- Use `@SpringBootTest` to load the **complete** application context
- Choose the right `webEnvironment` mode for your test scenario
- Send real HTTP requests with `TestRestTemplate`
- Configure a separate test profile (`application-test.properties`)
- Understand why integration tests complement (not replace) unit tests
- Discuss code coverage goals and what `>85%` means in practice

---

## Background

`@SpringBootTest` brings up the full Spring context — controllers, services,
repositories, security, and all beans. It is slower but catches integration
bugs that slice tests miss.

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookIntegrationTest {

    @Autowired TestRestTemplate restTemplate;

    @LocalServerPort int port;
}
```

`TestRestTemplate` performs **real HTTP calls** against the running server,
making these true end-to-end integration tests.

---

## Test Profile Setup

Create `src/test/resources/application-test.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

This overrides production datasource settings for tests only.

---

## Domain

Reuses the same `Book` entity, `BookRepository`, `BookService`, and
`BookController` from the previous exercises.

---

## Task 1 — GET all books (empty database)

1. In `testGetAllBooksEmpty()`:
   - Call `GET /books`.
   - Assert the HTTP status is `200 OK`.
   - Assert the returned array is empty.

## Task 2 — POST then GET

2. In `testCreateAndRetrieveBook()`:
   - `POST /books` with a `Book` payload.
   - Assert the response status is `201 Created`.
   - Assert the returned book has a non-null id.
   - `GET /books/{id}` with the returned id.
   - Assert the title matches.

## Task 3 — DELETE

3. In `testDeleteBook()`:
   - Create a book (POST).
   - Delete it (DELETE `/books/{id}`).
   - Assert `DELETE` returns `204 No Content`.
   - Assert a subsequent `GET /books/{id}` returns `404`.

## Task 4 — Coverage Discussion

4. In a comment block at the top of your test class, explain:
   - What percentage of branches/lines are exercised by this integration test?
   - Why is `>85%` a common industry target?
   - What types of bugs does only an integration test catch?

---

## Running the Tests

```bash
cd starter-code
mvn test
```
