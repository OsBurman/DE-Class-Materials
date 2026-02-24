package com.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)          // activates @Mock / @InjectMocks
@DisplayName("BookService Mockito Tests")
class BookServiceTest {

    @Mock
    BookRepository bookRepository;           // Mockito creates a mock at test start

    @InjectMocks
    BookService bookService;                 // real object; mock is injected via constructor

    // ── Task 1 — Basic Stubbing ────────────────────────────────────────────

    @Test
    @DisplayName("getAllBooks() returns stubbed list")
    void testGetAllBooks() {
        Book b1 = new Book(1L, "Clean Code", "Robert C. Martin", "Tech");
        Book b2 = new Book(2L, "Effective Java", "Joshua Bloch", "Tech");

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
    }

    // ── Task 2 — Verify Interactions ──────────────────────────────────────

    @Test
    @DisplayName("getAllBooks() calls repository exactly once")
    void testGetAllBooksCallsRepository() {
        when(bookRepository.findAll()).thenReturn(List.of());

        bookService.getAllBooks();

        verify(bookRepository, times(1)).findAll();
    }

    // ── Task 3 — Argument Matchers ────────────────────────────────────────

    @Test
    @DisplayName("saveBook() returns book with assigned id")
    void testSaveBook() {
        Book toSave  = new Book(null, "The Pragmatic Programmer", "Hunt & Thomas", "Tech");
        Book saved   = new Book(1L,   "The Pragmatic Programmer", "Hunt & Thomas", "Tech");

        // any(Book.class) matches whatever object is passed to save()
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        Book result = bookService.saveBook(toSave);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findById() returns the correct book")
    void testFindById() {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "Tech");

        // eq(1L) matches only the literal value 1L
        when(bookRepository.findById(eq(1L))).thenReturn(Optional.of(book));

        Book result = bookService.findById(1L);

        assertEquals("Clean Code", result.getTitle());
    }

    // ── Task 4 — Exception Stubbing ───────────────────────────────────────

    @Test
    @DisplayName("findById() throws RuntimeException when book not found")
    void testFindByIdNotFound() {
        when(bookRepository.findById(eq(99L))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.findById(99L));
    }
}
