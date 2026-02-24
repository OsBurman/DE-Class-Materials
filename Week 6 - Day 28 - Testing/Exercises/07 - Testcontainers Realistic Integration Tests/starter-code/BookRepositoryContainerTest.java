package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 07 – Testcontainers: Realistic Integration Tests
 *
 * TODO: Complete this class by following the instructions in the comments.
 */
// TODO: Add @DataJpaTest
// TODO: Add @AutoConfigureTestDatabase(replace = Replace.NONE) — do NOT use H2
// TODO: Add @Testcontainers
@DisplayName("BookRepository Testcontainers Tests")
class BookRepositoryContainerTest {

    // TODO: Declare a static PostgreSQLContainer<?> field annotated with @Container
    //       new PostgreSQLContainer<>("postgres:16-alpine")

    // TODO: Add @DynamicPropertySource to wire the container URL, username, password
    //       into spring.datasource.url / username / password

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    // ── Task 3 — Tests ────────────────────────────────────────────────────

    @Test
    @DisplayName("Saved book can be found by id")
    void testSaveAndFind() {
        // TODO: Save a Book using bookRepository.save()
        // TODO: Find it by id
        // TODO: Assert it is present and the title matches
    }

    @Test
    @DisplayName("findByGenre returns correct subset")
    void testFindByGenre() {
        // TODO: Save two "Tech" books and one "Fiction" book
        // TODO: Call findByGenre("Tech") and assert 2 results
    }

    @Test
    @DisplayName("findByTitle returns correct book")
    void testFindByTitle() {
        // TODO: Save a book with a known title
        // TODO: Call findByTitle(title) and assert the Optional is present
    }
}
