package com.bookstore.service;

// =============================================================================
// MOCKITO UNIT TESTS — Full demonstration of mocking in Java
// =============================================================================
// Mockito is the most popular Java mocking framework.
// It lets you create fake (mock) implementations of dependencies so you can
// test a class in complete isolation — no real database, no real HTTP calls.
//
// This file tests BookService by mocking BookRepository and EmailService.
// The service is the "class under test" (CUT); repository and email are mocked.
//
// Dependencies for pom.xml:
//   spring-boot-starter-test already includes mockito-core and mockito-junit-jupiter.
//   For standalone: org.mockito:mockito-junit-jupiter:5.x
// =============================================================================

import com.bookstore.entity.Book;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.EmailService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


// =============================================================================
// SECTION 1: MOCKING CONCEPTS
// =============================================================================
/*
WHY MOCK?

Imagine testing BookService which depends on BookRepository and EmailService.

  Without mocking:              With mocking:
  - needs a real database       - no database needed
  - needs email SMTP server     - no SMTP server needed
  - test breaks if DB is down   - test only breaks if BookService logic is wrong
  - test is slow (DB I/O)       - test runs in milliseconds
  - not isolated — DB state     - 100% isolated — only our code is tested
    affects test result

Mock = a fake object that pretends to be a real dependency.
  You control exactly what it returns.
  You can verify that your code called it the right way.

MOCK vs STUB vs SPY:
  Mock   — a completely fake object; all methods return null/0 by default
           You stub specific methods and verify interactions
  Stub   — a simple fake that returns hardcoded values (no verification needed)
           In Mockito, mocks ARE stubs — same object, used for both purposes
  Spy    — wraps a REAL object; most methods call the real implementation
           but you can stub specific ones to override behavior
*/


// =============================================================================
// SECTION 2: @Mock, @InjectMocks, @ExtendWith(MockitoExtension.class)
// =============================================================================

// @ExtendWith(MockitoExtension.class) — activates Mockito's JUnit 5 integration.
// This processes @Mock, @InjectMocks, @Spy, @Captor annotations automatically.
// Without it, those annotations do nothing — a common beginner mistake.
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests — with Mockito")
class BookServiceTest {

    // @Mock — creates a fake (mock) BookRepository.
    // Mockito generates a proxy class that implements BookRepository.
    // All methods return default values (null for objects, 0 for numbers, empty list, etc.)
    // unless you stub them with when().thenReturn().
    @Mock
    private BookRepository bookRepository;

    @Mock
    private EmailService emailService;  // fake email sender — no SMTP needed

    // @InjectMocks — creates a REAL instance of BookService AND injects all
    // @Mock fields into it (via constructor injection, then setter, then field).
    // Mockito looks at BookService's constructor: BookService(BookRepository, EmailService)
    // and passes the mocks in. The result: a real BookService with fake dependencies.
    @InjectMocks
    private BookService bookService;

    // Test data — reused across tests
    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Clean Code", "Robert Martin", new BigDecimal("35.00"));
        testBook.setId(1L);
    }


    // =========================================================================
    // SECTION 3: STUBBING — when().thenReturn()
    // =========================================================================
    // Stubbing: tell the mock "when this method is called with these args, return this"

    @Test
    @DisplayName("getBookById should return book when found")
    void shouldReturnBookWhenFound() {
        // ARRANGE — stub the mock to return our test book when asked for ID 1
        // Pattern: when(mockObject.method(args)).thenReturn(value)
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // ACT
        Book result = bookService.getBookById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert Martin", result.getAuthor());
    }

    @Test
    @DisplayName("getBookById should throw BookNotFoundException when not found")
    void shouldThrowWhenBookNotFound() {
        // ARRANGE — stub to return empty Optional (simulates "book not found" scenario)
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT + ACT — test that the service throws the right exception
        assertThrows(BookNotFoundException.class,
            () -> bookService.getBookById(99L));
    }

    @Test
    @DisplayName("getAllBooks should return all books from repository")
    void shouldReturnAllBooks() {
        // ARRANGE
        List<Book> books = List.of(
            testBook,
            new Book("Effective Java", "Joshua Bloch", new BigDecimal("45.00")),
            new Book("Design Patterns", "GoF", new BigDecimal("55.00"))
        );
        when(bookRepository.findAll()).thenReturn(books);

        // ACT
        List<Book> result = bookService.getAllBooks();

        // ASSERT
        assertEquals(3, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    @DisplayName("createBook should save and return new book")
    void shouldCreateBookSuccessfully() {
        // ARRANGE — stub existsByIsbn to return false (no duplicate) + stub save to return the book
        Book newBook = new Book("Refactoring", "Martin Fowler", new BigDecimal("40.00"));
        newBook.setIsbn("978-0-201-48567-7");

        when(bookRepository.existsByIsbn("978-0-201-48567-7")).thenReturn(false);
        when(bookRepository.save(newBook)).thenReturn(newBook);  // save returns what it saved

        // ACT
        Book result = bookService.createBook(newBook);

        // ASSERT
        assertNotNull(result);
        assertEquals("Refactoring", result.getTitle());
    }

    @Test
    @DisplayName("createBook should throw DuplicateIsbnException for duplicate ISBN")
    void shouldThrowForDuplicateIsbn() {
        // ARRANGE — stub existsByIsbn to return true (duplicate exists)
        Book duplicate = new Book("Duplicate", "Author", new BigDecimal("10.00"));
        duplicate.setIsbn("978-0-201-48567-7");

        when(bookRepository.existsByIsbn("978-0-201-48567-7")).thenReturn(true);

        // ASSERT
        assertThrows(DuplicateIsbnException.class,
            () -> bookService.createBook(duplicate));
    }

    // --- thenThrow — stub a method to throw an exception ---
    @Test
    @DisplayName("getAllBooks should propagate repository exceptions")
    void shouldPropagateRepositoryExceptions() {
        // ARRANGE — simulate a database failure
        when(bookRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // ASSERT — service should propagate (or wrap) the exception
        assertThrows(RuntimeException.class, () -> bookService.getAllBooks());
    }

    // --- thenAnswer — for complex stubbing with dynamic return values ---
    @Test
    @DisplayName("save should return the book with its input ISBN")
    void shouldReturnSavedBookWithMatchingIsbn() {
        // ARRANGE — return whatever book was passed to save()
        when(bookRepository.save(any(Book.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));  // return first arg

        Book book = new Book("Test", "Author", new BigDecimal("10.00"));
        book.setIsbn("978-1-234-56789-0");

        // ACT
        Book result = bookService.createBook(book);

        // ASSERT
        assertEquals("978-1-234-56789-0", result.getIsbn());
    }


    // =========================================================================
    // SECTION 4: VERIFYING INTERACTIONS — verify()
    // =========================================================================
    // verify() checks that the mock's methods were (or were NOT) called
    // with the expected arguments — a specific number of times.

    @Test
    @DisplayName("createBook should call save exactly once")
    void shouldCallSaveExactlyOnce() {
        // ARRANGE
        Book book = new Book("New Book", "Author", new BigDecimal("20.00"));
        book.setIsbn("978-0-000-00000-0");
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // ACT
        bookService.createBook(book);

        // ASSERT INTERACTIONS
        // verify(mock).method(args) — default = exactly once
        verify(bookRepository).save(book);   // was save() called with this exact book?

        // verify(mock, times(n)).method() — exactly N times
        verify(bookRepository, times(1)).existsByIsbn("978-0-000-00000-0");

        // verify(mock, never()).method() — should NEVER have been called
        verify(bookRepository, never()).deleteById(anyLong());

        // verify(mock, atLeastOnce()).method() — called at least once
        verify(bookRepository, atLeastOnce()).save(any());

        // verify(mock, atMost(2)).method() — called no more than 2 times
        verify(bookRepository, atMost(2)).save(any());
    }

    @Test
    @DisplayName("deleteBook should call deleteById with the correct ID")
    void shouldCallDeleteByIdWithCorrectId() {
        // ARRANGE
        when(bookRepository.existsById(1L)).thenReturn(true);

        // ACT
        bookService.deleteBook(1L);

        // ASSERT — verify deleteById was called with ID 1 (not 2, not 0, not anything else)
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("getBookById should NOT call deleteById")
    void shouldNotCallDeleteOnRead() {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // ACT
        bookService.getBookById(1L);

        // ASSERT — ensure no accidental side effects
        verify(bookRepository, never()).deleteById(anyLong());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBook should send welcome email after saving")
    void shouldSendWelcomeEmailAfterCreatingBook() {
        // ARRANGE
        Book book = new Book("Email Test", "Author", new BigDecimal("15.00"));
        book.setIsbn("978-1-111-11111-1");
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any())).thenReturn(book);

        // ACT
        bookService.createBook(book);

        // ASSERT — verify that email was sent with the book's title
        verify(emailService).sendNewBookNotification(book.getTitle());
    }


    // =========================================================================
    // SECTION 5: ARGUMENT MATCHERS
    // =========================================================================
    // Matchers let you match "any value of this type" instead of exact values.
    // Import: org.mockito.ArgumentMatchers.*

    @Test
    @DisplayName("any() matcher — match any argument of a type")
    void shouldUseAnyMatcher() {
        // ARRANGE — any(Book.class) matches ANY Book passed to save()
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

        Book book = new Book("Title", "Author", new BigDecimal("10.00"));
        book.setIsbn("978-0-000-00001-0");

        // ACT
        bookService.createBook(book);

        // ASSERT — verify that save was called with any Book
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("eq() matcher — match exact value alongside other matchers")
    void shouldUseEqMatcher() {
        // ARRANGE — mix matchers: must use matchers for ALL args if using any matcher
        // eq() wraps an exact value as a matcher (for consistency)
        when(bookRepository.findByGenreAndMaxPrice(eq("Fiction"), any(BigDecimal.class)))
            .thenReturn(List.of(testBook));

        // ACT
        List<Book> result = bookService.getBooksByGenreUnderPrice("Fiction", new BigDecimal("50.00"));

        // ASSERT
        assertEquals(1, result.size());
        verify(bookRepository).findByGenreAndMaxPrice(eq("Fiction"), any(BigDecimal.class));
    }

    @Test
    @DisplayName("ArgumentCaptor — capture the actual argument passed to a mock")
    void shouldCaptureArgumentPassedToSave() {
        // ARRANGE
        // ArgumentCaptor lets you capture the exact object that was passed to a mock method
        // Then you can assert on its properties
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book inputBook = new Book("Captured Book", "Test Author", new BigDecimal("25.00"));
        inputBook.setIsbn("978-1-222-22222-2");

        // ACT
        bookService.createBook(inputBook);

        // ASSERT — capture the argument passed to save() and inspect it
        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();

        assertEquals("Captured Book", capturedBook.getTitle());
        assertEquals("Test Author", capturedBook.getAuthor());
        assertEquals(0, new BigDecimal("25.00").compareTo(capturedBook.getPrice()));
    }

    @Test
    @DisplayName("anyString(), anyLong(), isNull() matchers")
    void shouldDemonstrateOtherMatchers() {
        // Other common matchers:
        //   anyString()  — any non-null String
        //   anyLong()    — any long/Long
        //   anyInt()     — any int/Integer
        //   isNull()     — argument must be null
        //   isNotNull()  — argument must not be null
        //   startsWith("prefix")  — string starts with
        //   contains("substring") — string contains

        when(bookRepository.findByTitleContainingIgnoreCase(anyString()))
            .thenReturn(List.of(testBook));

        List<Book> result = bookService.searchBooks("clean");

        assertEquals(1, result.size());
        verify(bookRepository).findByTitleContainingIgnoreCase(contains("clean"));
    }


    // =========================================================================
    // SECTION 6: SPY — partial mocking of a real object
    // =========================================================================

    @Test
    @DisplayName("@Spy — wrap real object, override only specific methods")
    void shouldUseSpy() {
        // Create a real PriceCalculator, then wrap it in a spy
        PriceCalculator realCalculator = new PriceCalculator();
        PriceCalculator spyCalculator = spy(realCalculator);

        // Most methods use the REAL implementation
        BigDecimal result = spyCalculator.calculatePrice(new BigDecimal("100.00"), MemberType.PREMIUM);
        assertEquals(0, new BigDecimal("90.00").compareTo(result));  // real calculation

        // But we can override specific methods
        doReturn(new BigDecimal("99.00"))
            .when(spyCalculator)
            .calculatePrice(eq(new BigDecimal("100.00")), eq(MemberType.VIP));

        BigDecimal stubbedResult = spyCalculator.calculatePrice(new BigDecimal("100.00"), MemberType.VIP);
        assertEquals(0, new BigDecimal("99.00").compareTo(stubbedResult));  // our stubbed value

        // STANDARD still uses real implementation
        BigDecimal standardResult = spyCalculator.calculatePrice(new BigDecimal("100.00"), MemberType.STANDARD);
        assertEquals(0, new BigDecimal("100.00").compareTo(standardResult));
    }


    // =========================================================================
    // SECTION 7: VERIFYING NO MORE INTERACTIONS
    // =========================================================================

    @Test
    @DisplayName("verifyNoMoreInteractions — ensure no unexpected calls were made")
    void shouldHaveNoUnexpectedRepositoryCalls() {
        // ARRANGE
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // ACT — just a read operation
        bookService.getBookById(1L);

        // ASSERT — verify that ALL interactions were accounted for
        verify(bookRepository).findById(1L);
        verifyNoMoreInteractions(bookRepository);     // fail if any OTHER method was called
        verifyNoInteractions(emailService);            // fail if emailService was touched at all
    }
}
