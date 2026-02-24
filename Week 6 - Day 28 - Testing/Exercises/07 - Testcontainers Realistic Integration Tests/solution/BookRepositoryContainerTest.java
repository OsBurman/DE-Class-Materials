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

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)  // keep real datasource; don't swap in H2
@Testcontainers                                      // manages container lifecycle via @Container
@DisplayName("BookRepository Testcontainers Tests")
class BookRepositoryContainerTest {

    // A single container is shared across all tests in this class (static field)
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    // Injects the running container's connection details into Spring's context
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();     // start each test with an empty table
    }

    // ── Task 3 — Tests ────────────────────────────────────────────────────

    @Test
    @DisplayName("Saved book can be found by id")
    void testSaveAndFind() {
        Book saved = bookRepository.save(new Book("Clean Code", "Robert C. Martin", "Tech"));

        Optional<Book> found = bookRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Clean Code", found.get().getTitle());
    }

    @Test
    @DisplayName("findByGenre returns correct subset")
    void testFindByGenre() {
        bookRepository.save(new Book("Clean Code",     "Robert C. Martin", "Tech"));
        bookRepository.save(new Book("Effective Java", "Joshua Bloch",     "Tech"));
        bookRepository.save(new Book("Dune",           "Frank Herbert",    "Fiction"));

        List<Book> tech = bookRepository.findByGenre("Tech");

        assertEquals(2, tech.size());
    }

    @Test
    @DisplayName("findByTitle returns correct book")
    void testFindByTitle() {
        bookRepository.save(new Book("Refactoring", "Martin Fowler", "Tech"));

        Optional<Book> result = bookRepository.findByTitle("Refactoring");

        assertTrue(result.isPresent());
        assertEquals("Martin Fowler", result.get().getAuthor());
    }
}
