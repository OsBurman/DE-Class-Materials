package com.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Exercise 03 – Mockito: Mocking Dependencies
 *
 * TODO: Complete each test method by following the instructions in the comments.
 *       Do NOT modify BookService or BookRepository.
 */
// TODO: Add @ExtendWith(MockitoExtension.class) to enable Mockito annotations
@DisplayName("BookService Mockito Tests")
class BookServiceTest {

    // TODO: Declare a @Mock for BookRepository

    // TODO: Declare @InjectMocks for BookService (Mockito will inject the mock above)

    // ── Task 1 — Basic Stubbing ────────────────────────────────────────────

    @Test
    @DisplayName("getAllBooks() returns stubbed list")
    void testGetAllBooks() {
        // TODO: Create two Book objects

        // TODO: Stub bookRepository.findAll() to return a list containing those books
        //       when(bookRepository.findAll()).thenReturn(...)

        // TODO: Call bookService.getAllBooks() and store the result

        // TODO: Assert the returned list has exactly 2 elements
    }

    // ── Task 2 — Verify Interactions ──────────────────────────────────────

    @Test
    @DisplayName("getAllBooks() calls repository exactly once")
    void testGetAllBooksCallsRepository() {
        // TODO: Stub bookRepository.findAll() to return an empty list

        // TODO: Call bookService.getAllBooks()

        // TODO: Verify that bookRepository.findAll() was called exactly 1 time
        //       verify(bookRepository, times(1)).findAll();
    }

    // ── Task 3 — Argument Matchers ────────────────────────────────────────

    @Test
    @DisplayName("saveBook() returns book with assigned id")
    void testSaveBook() {
        // TODO: Create a Book to save (no id yet)

        // TODO: Create a savedBook with id = 1L (what the repository returns after save)

        // TODO: Stub bookRepository.save(any(Book.class)) to return savedBook

        // TODO: Call bookService.saveBook() and capture the result

        // TODO: Assert the result's id equals 1L
    }

    @Test
    @DisplayName("findById() returns the correct book")
    void testFindById() {
        // TODO: Create a Book with id = 1L and title "Clean Code"

        // TODO: Stub bookRepository.findById(eq(1L)) to return Optional.of(that book)

        // TODO: Call bookService.findById(1L) and store the result

        // TODO: Assert the result's title equals "Clean Code"
    }

    // ── Task 4 — Exception Stubbing ───────────────────────────────────────

    @Test
    @DisplayName("findById() throws RuntimeException when book not found")
    void testFindByIdNotFound() {
        // TODO: Stub bookRepository.findById(eq(99L)) to return Optional.empty()

        // TODO: Use assertThrows to verify that bookService.findById(99L)
        //       throws RuntimeException
    }
}
