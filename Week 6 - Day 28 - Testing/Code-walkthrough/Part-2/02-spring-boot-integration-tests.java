package com.bookstore.controller;

// =============================================================================
// SPRING BOOT TESTING — @SpringBootTest, @WebMvcTest, MockMvc, @DataJpaTest
// =============================================================================
// Spring Boot provides specialized test annotations that load only the parts
// of the application context you actually need for each test type.
//
// Test Slice Annotations (load PARTIAL context — faster):
//   @WebMvcTest      — loads only the web layer (controllers, filters, serialization)
//   @DataJpaTest     — loads only JPA layer (repositories, entity manager, H2)
//   @DataMongoTest   — loads only MongoDB components
//   @JsonTest        — loads only JSON serialization components
//
// @SpringBootTest   — loads the FULL application context (all beans)
//                     Use for true end-to-end integration tests
//                     Slowest but most realistic
// =============================================================================

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


// =============================================================================
// SECTION 1: @WebMvcTest — Test the Web Layer in Isolation
// =============================================================================
// @WebMvcTest loads ONLY:
//   - Controllers (@RestController, @Controller)
//   - Filters, interceptors, argument resolvers
//   - Jackson serialization/deserialization
//
// It does NOT load: services, repositories, databases.
// You replace service dependencies with @MockBean.
//
// MockMvc is automatically configured — it simulates HTTP requests without
// actually starting a real HTTP server.
// =============================================================================

@WebMvcTest(BookController.class)   // only loads BookController's slice
@DisplayName("BookController Web Layer Tests")
class BookControllerTest {

    // MockMvc — the HTTP request simulator
    // Autowired automatically when @WebMvcTest is active
    @Autowired
    private MockMvc mockMvc;

    // @MockBean — like Mockito @Mock, but registers the mock in the Spring context
    // Use @MockBean (not @Mock) when testing with Spring annotations like @WebMvcTest
    // @Mock only works with @ExtendWith(MockitoExtension.class), not with Spring slices
    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;   // for JSON serialization in test requests

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Clean Code", "Robert Martin", new BigDecimal("35.00"));
        testBook.setId(1L);
        testBook.setIsbn("978-0-132-35088-4");
    }

    // =========================================================================
    // MockMvc GET Tests
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/books should return 200 with list of books")
    void shouldReturnAllBooks() throws Exception {
        // ARRANGE
        when(bookService.getAllBooks()).thenReturn(List.of(testBook));

        // ACT + ASSERT — perform() runs the request; andExpect() chain asserts the response
        mockMvc.perform(
                get("/api/v1/books")                     // HTTP GET
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())                              // print request/response to console (useful for debugging)
            .andExpect(status().isOk())                  // HTTP 200
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))         // JSON array has 1 element
            .andExpect(jsonPath("$[0].title", is("Clean Code")))      // first element's title
            .andExpect(jsonPath("$[0].author", is("Robert Martin")))  // first element's author
            .andExpect(jsonPath("$[0].price", is(35.00)));             // first element's price
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} should return 200 when book exists")
    void shouldReturnBookById() throws Exception {
        // ARRANGE
        when(bookService.getBookById(1L)).thenReturn(testBook);

        // ACT + ASSERT
        mockMvc.perform(get("/api/v1/books/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Clean Code")))
            .andExpect(jsonPath("$.isbn", is("978-0-132-35088-4")));
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} should return 404 when book not found")
    void shouldReturn404WhenBookNotFound() throws Exception {
        // ARRANGE — service throws when book not found
        when(bookService.getBookById(99L))
            .thenThrow(new BookNotFoundException("Book not found with id: 99"));

        // ACT + ASSERT
        mockMvc.perform(get("/api/v1/books/99"))
            .andExpect(status().isNotFound())                            // HTTP 404
            .andExpect(jsonPath("$.message", containsString("99")));     // error message contains the ID
    }

    // =========================================================================
    // MockMvc POST Tests
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/books should return 201 with created book")
    void shouldCreateBook() throws Exception {
        // ARRANGE
        BookDTO newBookDto = new BookDTO("Refactoring", "Martin Fowler",
            new BigDecimal("40.00"), "978-0-201-48567-7");

        when(bookService.createBook(any(Book.class))).thenReturn(testBook);

        // ACT + ASSERT
        mockMvc.perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newBookDto))  // serialize DTO to JSON body
            )
            .andExpect(status().isCreated())              // HTTP 201
            .andExpect(jsonPath("$.title").exists());     // response has a title field
    }

    @Test
    @DisplayName("POST /api/v1/books should return 400 when title is missing")
    void shouldReturn400WhenTitleIsMissing() throws Exception {
        // ARRANGE — invalid request: missing required title field
        String invalidJson = """
            {
                "author": "Test Author",
                "price": 10.00
            }
            """;

        // ACT + ASSERT — Spring's @Valid should reject this request before it reaches the service
        mockMvc.perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson)
            )
            .andExpect(status().isBadRequest());   // HTTP 400 from @Valid validation failure
    }

    // =========================================================================
    // MockMvc PUT and DELETE Tests
    // =========================================================================

    @Test
    @DisplayName("PUT /api/v1/books/{id} should return 200 with updated book")
    void shouldUpdateBook() throws Exception {
        // ARRANGE
        Book updatedBook = new Book("Clean Code 2nd Edition", "Robert Martin", new BigDecimal("45.00"));
        updatedBook.setId(1L);
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        String updateJson = """
            {
                "title": "Clean Code 2nd Edition",
                "author": "Robert Martin",
                "price": 45.00
            }
            """;

        // ACT + ASSERT
        mockMvc.perform(
                put("/api/v1/books/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateJson)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Clean Code 2nd Edition")));
    }

    @Test
    @DisplayName("DELETE /api/v1/books/{id} should return 204 No Content")
    void shouldDeleteBook() throws Exception {
        // ARRANGE
        doNothing().when(bookService).deleteBook(1L);   // void method stubbing

        // ACT + ASSERT
        mockMvc.perform(delete("/api/v1/books/1"))
            .andExpect(status().isNoContent());   // HTTP 204

        verify(bookService).deleteBook(1L);
    }

    @Test
    @DisplayName("GET /api/v1/books?genre=Fiction should filter by genre")
    void shouldFilterByGenre() throws Exception {
        // ARRANGE
        when(bookService.getBooksByGenreUnderPrice(eq("Fiction"), any()))
            .thenReturn(List.of(testBook));

        // ACT + ASSERT — @RequestParam binding
        mockMvc.perform(get("/api/v1/books")
                .param("genre", "Fiction")
                .param("maxPrice", "100.00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    // =========================================================================
    // MvcResult — capture response for deeper inspection
    // =========================================================================

    @Test
    @DisplayName("Can capture full response with MvcResult for complex assertions")
    void shouldCaptureFullResponse() throws Exception {
        // ARRANGE
        when(bookService.getAllBooks()).thenReturn(List.of(testBook));

        // ACT
        MvcResult result = mockMvc.perform(get("/api/v1/books"))
            .andReturn();   // returns MvcResult without asserting

        // ASSERT — inspect response directly
        int statusCode = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();

        assertEquals(200, statusCode);
        assertTrue(responseBody.contains("Clean Code"));
    }
}


// =============================================================================
// SECTION 2: @DataJpaTest — Test the Repository Layer in Isolation
// =============================================================================
// @DataJpaTest:
//   - Loads ONLY JPA components (repositories, EntityManager, H2 in-memory DB)
//   - Does NOT load controllers, services, or @Component beans
//   - Automatically uses H2 for an isolated, transactional test database
//   - Each test runs in a transaction that is ROLLED BACK after the test
//     (so tests don't affect each other's data)
//   - Use for: testing custom query methods, JPQL correctness, entity validation
// =============================================================================

@DataJpaTest
@DisplayName("BookRepository Data Layer Tests")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    // TestEntityManager — helper for inserting test data without going through the repository
    // Useful for setting up state that the repository being tested will then query
    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    @BeforeEach
    void insertTestData() {
        // Use TestEntityManager to persist test data and flush to H2
        Book book1 = new Book("The Pragmatic Programmer", "Andrew Hunt", new BigDecimal("42.00"));
        book1.setIsbn("978-0-201-61622-4");
        book1.setGenre("Technology");

        Book book2 = new Book("Clean Architecture", "Robert Martin", new BigDecimal("38.00"));
        book2.setIsbn("978-0-134-49416-6");
        book2.setGenre("Technology");

        Book book3 = new Book("The Hobbit", "J.R.R. Tolkien", new BigDecimal("12.99"));
        book3.setIsbn("978-0-547-92822-7");
        book3.setGenre("Fiction");

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();   // ensure data is written to H2 before the test reads it
    }

    @Test
    @DisplayName("findByAuthor should return books by that author")
    void shouldFindBooksByAuthor() {
        List<Book> books = bookRepository.findByAuthor("Robert Martin");

        assertEquals(1, books.size());
        assertEquals("Clean Architecture", books.get(0).getTitle());
    }

    @Test
    @DisplayName("findByGenre should return all books in that genre")
    void shouldFindBooksByGenre() {
        List<Book> techBooks = bookRepository.findByGenre("Technology",
            org.springframework.data.domain.Sort.by("title"));

        assertEquals(2, techBooks.size());
        // With Sort.by("title") ASC, "Clean Architecture" comes before "The Pragmatic Programmer"
        assertEquals("Clean Architecture", techBooks.get(0).getTitle());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase should find partial matches")
    void shouldFindByPartialTitle() {
        List<Book> results = bookRepository.findByTitleContainingIgnoreCase("clean");

        assertEquals(1, results.size());
        assertEquals("Clean Architecture", results.get(0).getTitle());
    }

    @Test
    @DisplayName("existsByIsbn should return true for existing ISBN")
    void shouldReturnTrueForExistingIsbn() {
        assertTrue(bookRepository.existsByIsbn("978-0-201-61622-4"));
        assertFalse(bookRepository.existsByIsbn("999-9-999-99999-9"));
    }

    @Test
    @DisplayName("countByGenre should return correct count")
    void shouldCountByGenre() {
        assertEquals(2, bookRepository.countByGenre("Technology"));
        assertEquals(1, bookRepository.countByGenre("Fiction"));
        assertEquals(0, bookRepository.countByGenre("Mystery"));
    }

    @Test
    @DisplayName("findByPriceBetween should return books in price range")
    void shouldFindBooksInPriceRange() {
        List<Book> books = bookRepository.findByPriceBetween(
            new BigDecimal("10.00"), new BigDecimal("40.00")
        );

        assertEquals(2, books.size());  // The Hobbit (12.99) + Clean Architecture (38.00)
    }

    @Test
    @DisplayName("save should persist a new book and assign an ID")
    void shouldSaveNewBook() {
        // ARRANGE
        Book newBook = new Book("Domain Driven Design", "Eric Evans", new BigDecimal("55.00"));
        newBook.setIsbn("978-0-321-12521-7");
        newBook.setGenre("Technology");

        // ACT
        Book saved = bookRepository.save(newBook);

        // ASSERT
        assertNotNull(saved.getId());   // ID was auto-generated
        assertEquals("Domain Driven Design", saved.getTitle());

        // Verify it's in the database
        Optional<Book> found = bookRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }
}


// =============================================================================
// SECTION 3: @SpringBootTest — Full Integration Tests
// =============================================================================
// @SpringBootTest loads the FULL application context — all beans, configurations,
// and the real application.properties.
//
// Use this for:
//   - Testing that components wire together correctly
//   - Testing request-to-database round trips
//   - Smoke tests (does the app start at all?)
//
// ⚠️ Much slower than slices — spin up time can be 5–30 seconds.
// Use sparingly — have one or two full integration tests, not hundreds.
//
// @ActiveProfiles("test") — activates application-test.properties
// =============================================================================

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// RANDOM_PORT: starts a real embedded server on a random port
// MOCK: uses MockMvc (no real server) — default, faster
// DEFINED_PORT: uses server.port from properties
// NONE: no web server — just loads the context
@ActiveProfiles("test")    // uses src/test/resources/application-test.properties
@DisplayName("Full Integration Tests")
class BookIntegrationTest {

    // TestRestTemplate — makes real HTTP calls to the running test server
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();   // clean slate before each integration test
    }

    @Test
    @DisplayName("POST then GET round trip should persist and retrieve a book")
    void shouldPersistAndRetrieveBook() {
        // ARRANGE — create a book via REST API
        BookDTO newBook = new BookDTO("Integration Test Book", "Test Author",
            new BigDecimal("29.99"), "978-0-000-11111-1");

        // ACT — POST to create
        ResponseEntity<Book> createResponse = restTemplate.postForEntity(
            "/api/v1/books", newBook, Book.class
        );

        // ASSERT — created successfully
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long createdId = createResponse.getBody().getId();

        // ACT — GET to retrieve
        ResponseEntity<Book> getResponse = restTemplate.getForEntity(
            "/api/v1/books/" + createdId, Book.class
        );

        // ASSERT — retrieved correctly
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Book", getResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("GET /api/v1/books should return empty list when no books exist")
    void shouldReturnEmptyListWhenNoBooksExist() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/books", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}


// =============================================================================
// SECTION 4: application-test.properties — test profile configuration
// =============================================================================
/*
Place this file at: src/test/resources/application-test.properties

# Use H2 in-memory database for tests (overrides main application.properties)
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Suppress most logging during tests
logging.level.root=WARN
logging.level.com.bookstore=DEBUG

# Disable security for web layer tests (if applicable)
# spring.security.enabled=false
*/
