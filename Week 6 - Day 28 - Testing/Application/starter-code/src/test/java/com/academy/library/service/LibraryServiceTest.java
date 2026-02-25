package com.academy.library.service;

import com.academy.library.exception.BookNotAvailableException;
import com.academy.library.exception.BookNotFoundException;
import com.academy.library.model.Book;
import com.academy.library.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Test Class for LibraryService.
 *
 * ⚠️ IMPORTANT: Write your tests BEFORE implementing LibraryService.java
 * Follow Red → Green → Refactor:
 * 1. Write a test (Red — it fails because method throws
 * UnsupportedOperationException)
 * 2. Write the minimum code in LibraryService to make it pass (Green)
 * 3. Refactor if needed, re-run tests
 */
class LibraryServiceTest {

    private BookRepository mockBookRepository;
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        // TODO Task 2: Initialize mockBookRepository using mock()
        // mockBookRepository = mock(BookRepository.class);
        // libraryService = new LibraryService(mockBookRepository);

        // Remove this line once you implement the setup:
        mockBookRepository = mock(BookRepository.class);
        libraryService = new LibraryService(mockBookRepository);
    }

    @AfterEach
    void tearDown() {
        // TODO: Add any cleanup if needed (usually not required with Mockito)
        verifyNoMoreInteractions(mockBookRepository); // ensures we didn't call unexpected methods
    }

    // ================================================================
    // TODO Task 2: checkOutBook — success case
    // ================================================================
    @Test
    @DisplayName("checkOutBook: should mark book unavailable and return it")
    void checkOutBook_shouldMarkUnavailable_whenBookIsAvailable() {
        // Arrange — TODO: create an available book, stub
        // mockBookRepository.findByIsbn() to return it
        // Act — TODO: call libraryService.checkOutBook(isbn)
        // Assert — TODO: verify book.isAvailable() == false
        // verify mockBookRepository.save() was called once
    }

    // ================================================================
    // TODO Task 2: checkOutBook — book not found
    // ================================================================
    @Test
    @DisplayName("checkOutBook: should throw BookNotFoundException when book doesn't exist")
    void checkOutBook_shouldThrowNotFound_whenBookDoesNotExist() {
        // Arrange — stub findByIsbn() to return Optional.empty()
        // Act & Assert — assertThrows(BookNotFoundException.class, () -> ...)
    }

    // ================================================================
    // TODO Task 2: checkOutBook — already checked out
    // ================================================================
    @Test
    @DisplayName("checkOutBook: should throw BookNotAvailableException when book is checked out")
    void checkOutBook_shouldThrowNotAvailable_whenAlreadyCheckedOut() {
        // Arrange — create a book with available = false
        // Act & Assert — assertThrows(BookNotAvailableException.class, () -> ...)
    }

    // ================================================================
    // TODO Task 3: returnBook — success
    // ================================================================
    @Test
    @DisplayName("returnBook: should mark book available again")
    void returnBook_shouldMarkAvailable_whenBookExists() {
        // TODO
    }

    // ================================================================
    // TODO Task 3: returnBook — not found
    // ================================================================
    @Test
    @DisplayName("returnBook: should throw BookNotFoundException when book doesn't exist")
    void returnBook_shouldThrowNotFound_whenBookDoesNotExist() {
        // TODO
    }

    // ================================================================
    // TODO Task 4: Parameterized test
    // ================================================================
    @ParameterizedTest
    @CsvSource({
            "978-0-06-112008-4, true",
            "978-0-7432-7356-5, false"
    })
    @DisplayName("checkOutBook: should succeed for available books, fail for unavailable")
    void checkOutBook_parameterized(String isbn, boolean isAvailable) {
        // TODO: Arrange: create a book with the given isbn and isAvailable flag
        // stub findByIsbn() to return it
        // Act & Assert:
        // if isAvailable == true → should return book without throwing
        // if isAvailable == false → should throw BookNotAvailableException
    }
}
