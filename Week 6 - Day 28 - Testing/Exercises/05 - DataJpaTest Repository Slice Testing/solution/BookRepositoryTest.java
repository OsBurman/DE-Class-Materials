package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest        // loads JPA slice + H2; each test rolls back automatically
@DisplayName("BookRepository @DataJpaTest Tests")
class BookRepositoryTest {

    @Autowired
    TestEntityManager em;           // used to persist test data directly

    @Autowired
    BookRepository bookRepository;

    // ── Task 1 — Save and Find ────────────────────────────────────────────

    @Test
    @DisplayName("Persisted book can be retrieved by id")
    void testSaveAndFindById() {
        Book book = em.persistAndFlush(new Book("Clean Code", "Robert C. Martin", "Tech"));

        Optional<Book> found = bookRepository.findById(book.getId());

        assertTrue(found.isPresent());
        assertEquals("Clean Code", found.get().getTitle());
    }

    // ── Task 2 — findByGenre ──────────────────────────────────────────────

    @Test
    @DisplayName("findByGenre returns only books matching the genre")
    void testFindByGenre() {
        em.persist(new Book("Clean Code", "Robert C. Martin", "Tech"));
        em.persist(new Book("Effective Java", "Joshua Bloch", "Tech"));
        em.persist(new Book("Dune", "Frank Herbert", "Fiction"));
        em.flush();

        List<Book> techBooks = bookRepository.findByGenre("Tech");

        assertEquals(2, techBooks.size());
    }

    // ── Task 3 — findByAuthor ─────────────────────────────────────────────

    @Test
    @DisplayName("findByAuthor returns books for the given author")
    void testFindByAuthor() {
        em.persist(new Book("Clean Code", "Robert C. Martin", "Tech"));
        em.persist(new Book("The Clean Coder", "Robert C. Martin", "Tech"));
        em.persist(new Book("Effective Java", "Joshua Bloch", "Tech"));
        em.flush();

        List<Book> books = bookRepository.findByAuthor("Robert C. Martin");

        assertEquals(2, books.size());
    }

    // ── Task 4 — findByTitle ──────────────────────────────────────────────

    @Test
    @DisplayName("findByTitle returns the correct book")
    void testFindByTitle() {
        em.persistAndFlush(new Book("Refactoring", "Martin Fowler", "Tech"));

        Optional<Book> result = bookRepository.findByTitle("Refactoring");

        assertTrue(result.isPresent());
        assertEquals("Martin Fowler", result.get().getAuthor());
    }

    // ── Task 5 — Delete ───────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById removes the book")
    void testDeleteById() {
        Book book = em.persistAndFlush(new Book("Domain-Driven Design", "Eric Evans", "Tech"));
        Long id = book.getId();

        bookRepository.deleteById(id);
        em.flush();
        em.clear();     // evict first-level cache so findById hits the DB

        assertTrue(bookRepository.findById(id).isEmpty());
    }
}
