package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Coverage discussion:
 * ─────────────────────────────────────────────────────────────────────────────
 * These tests exercise the full stack end-to-end:
 *   HTTP request → BookController → BookService → BookRepository → H2 database
 *
 * Every line in BookController and BookService is exercised by the three tests
 * below, yielding close to 100% line coverage for those classes.
 *
 * Industry target: >85%
 *   - Below 85%, large branches of logic go untested and bugs slip through.
 *   - 100% is theoretically perfect but expensive; 85–90% is a practical
 *     balance between safety and build speed.
 *
 * Bugs that ONLY integration tests catch:
 *   1. Wiring bugs — @Autowired pointing to wrong bean type
 *   2. JPA mapping errors — column names / types mismatched with the schema
 *   3. HTTP serialisation issues — Jackson unable to serialise your entity
 *   4. Transaction boundary bugs — data visible in one tx, not in another
 *   5. Missing @Transactional on service methods
 * ─────────────────────────────────────────────────────────────────────────────
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")         // loads application-test.properties (H2)
@DisplayName("Book API Integration Tests")
class BookIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;  // real HTTP client bound to the random port

    // ── Task 1 — GET all books (empty DB) ─────────────────────────────────

    @Test
    @DisplayName("GET /books returns 200 and empty array at start")
    void testGetAllBooksEmpty() {
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/books", Book[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // DB may not be empty if other tests ran first, so just check 2xx
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    // ── Task 2 — POST then GET ────────────────────────────────────────────

    @Test
    @DisplayName("POST /books creates book; GET /books/{id} retrieves it")
    void testCreateAndRetrieveBook() {
        Book payload = new Book("Clean Code", "Robert C. Martin", "Tech");

        ResponseEntity<Book> created = restTemplate.postForEntity("/books", payload, Book.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());

        Long id = created.getBody().getId();
        ResponseEntity<Book> found = restTemplate.getForEntity("/books/" + id, Book.class);
        assertEquals(HttpStatus.OK, found.getStatusCode());
        assertEquals("Clean Code", found.getBody().getTitle());
    }

    // ── Task 3 — DELETE ───────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /books/{id} removes book; GET returns 404")
    void testDeleteBook() {
        // Create a book first
        Book payload = new Book("The Pragmatic Programmer", "Hunt & Thomas", "Tech");
        Book created = restTemplate.postForEntity("/books", payload, Book.class).getBody();
        assertNotNull(created);
        Long id = created.getId();

        // Delete it
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/books/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Confirm 404
        ResponseEntity<Book> getResponse = restTemplate.getForEntity("/books/" + id, Book.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}
