package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 06 – @SpringBootTest Integration Testing
 *
 * Coverage discussion (Task 4):
 * ─────────────────────────────
 * TODO: Add a comment block here answering:
 *  - What percentage of the application's branches/lines are exercised by this test?
 *  - Why is >85% a common industry target?
 *  - What types of bugs does ONLY an integration test catch?
 *
 * Example starters:
 *  "These tests exercise the full stack: controller → service → repository → H2 DB.
 *   Every line in BookController and BookService is touched, giving ~100% line coverage
 *   for those classes. The >85% target balances thoroughness with build speed.
 *   Integration tests catch wiring bugs (e.g. wrong @Autowired), SQL mapping errors,
 *   and HTTP serialisation issues that unit tests with mocks cannot detect."
 */
// TODO: Add @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// TODO: Add @ActiveProfiles("test")
@DisplayName("Book API Integration Tests")
class BookIntegrationTest {

    // TODO: @Autowired TestRestTemplate restTemplate;

    // ── Task 1 — GET all books (empty DB) ─────────────────────────────────

    @Test
    @DisplayName("GET /books returns 200 and empty array when no books exist")
    void testGetAllBooksEmpty() {
        // TODO: ResponseEntity<Book[]> response = restTemplate.getForEntity("/books", Book[].class);
        // TODO: assertEquals(HttpStatus.OK, response.getStatusCode());
        // TODO: assertEquals(0, response.getBody().length);
    }

    // ── Task 2 — POST then GET ────────────────────────────────────────────

    @Test
    @DisplayName("POST /books creates book, GET /books/{id} retrieves it")
    void testCreateAndRetrieveBook() {
        // TODO: Create a Book payload (no id)

        // TODO: Post it: ResponseEntity<Book> created = restTemplate.postForEntity("/books", payload, Book.class);
        // TODO: assertEquals(HttpStatus.CREATED, created.getStatusCode());
        // TODO: assertNotNull(created.getBody().getId());

        // TODO: Use the returned id to GET /books/{id} and assert the title matches
    }

    // ── Task 3 — DELETE ───────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /books/{id} removes the book; subsequent GET returns 404")
    void testDeleteBook() {
        // TODO: POST a book to get an id

        // TODO: exchange(DELETE) and assert 204 No Content

        // TODO: GET the same id and assert 404
    }
}
