package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 05 – @DataJpaTest Repository Slice Testing
 *
 * TODO: Complete each test method.
 *       Do NOT modify Book, BookRepository, or the pom.xml.
 */
// TODO: Add @DataJpaTest to configure H2 + JPA slice automatically
@DisplayName("BookRepository @DataJpaTest Tests")
class BookRepositoryTest {

    // TODO: @Autowired TestEntityManager em;

    // TODO: @Autowired BookRepository bookRepository;

    // ── Task 1 — Save and Find ────────────────────────────────────────────

    @Test
    @DisplayName("Persisted book can be retrieved by id")
    void testSaveAndFindById() {
        // TODO: Create a new Book and persist it with em.persistAndFlush(book)

        // TODO: Call bookRepository.findById(book.getId())

        // TODO: Assert the result is present and its title matches
    }

    // ── Task 2 — findByGenre ──────────────────────────────────────────────

    @Test
    @DisplayName("findByGenre returns only books with matching genre")
    void testFindByGenre() {
        // TODO: Persist two "Tech" books and one "Fiction" book using em.persist()
        //       then em.flush()

        // TODO: Call bookRepository.findByGenre("Tech")

        // TODO: Assert the result list has exactly 2 elements
    }

    // ── Task 3 — findByAuthor ─────────────────────────────────────────────

    @Test
    @DisplayName("findByAuthor returns books for matching author")
    void testFindByAuthor() {
        // TODO: Persist two books by "Robert C. Martin" and one by "Joshua Bloch"

        // TODO: Call bookRepository.findByAuthor("Robert C. Martin")

        // TODO: Assert exactly 2 results
    }

    // ── Task 4 — findByTitle ──────────────────────────────────────────────

    @Test
    @DisplayName("findByTitle returns the correct book")
    void testFindByTitle() {
        // TODO: Persist a book with title "Refactoring"

        // TODO: Call bookRepository.findByTitle("Refactoring")

        // TODO: Assert the Optional is present
    }

    // ── Task 5 — Delete ───────────────────────────────────────────────────

    @Test
    @DisplayName("deleteById removes the book from the database")
    void testDeleteById() {
        // TODO: Persist a book and capture its id

        // TODO: Call bookRepository.deleteById(id)
        //       Then em.flush() + em.clear() to evict any first-level cache

        // TODO: Assert bookRepository.findById(id) is empty
    }
}
