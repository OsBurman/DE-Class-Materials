# Day 28 Application — Testing: TDD Library Service

## Overview

Practice **Test-Driven Development** by writing tests first, then implementing a `LibraryService`. Then write `MockMvc` integration tests for a provided `BookController`.

---

## Learning Goals

- Follow the Red-Green-Refactor TDD cycle
- Write JUnit 5 tests with `@Test`, `@BeforeEach`, `@AfterEach`
- Use all JUnit assertion types
- Write parameterized tests with `@ParameterizedTest`
- Mock dependencies with Mockito (`@Mock`, `@InjectMocks`)
- Use `when().thenReturn()` and `verify()`
- Test Spring MVC with `MockMvc`
- Write integration tests with `@SpringBootTest`

---

## TDD Workflow — IMPORTANT

For Part 1: **write the test first → watch it fail (Red) → implement the minimum to pass (Green) → refactor**. Do NOT look at the implementation first.

---

## Part 1 — TDD: `LibraryService`

The `LibraryService` manages a book inventory. Write these tests in `LibraryServiceTest.java` BEFORE implementing the service.

**Task 1 — Setup**  
```java
@BeforeEach
void setUp() {
    mockBookRepository = mock(BookRepository.class);
    libraryService = new LibraryService(mockBookRepository);
}
```

**Task 2 — Test: `checkOutBook`**  
- When: a book exists and is available → return the book with `available = false`
- When: a book doesn't exist → throw `BookNotFoundException`
- When: a book is already checked out → throw `BookNotAvailableException`

**Task 3 — Test: `returnBook`**  
- When: book is returned → `available = true`
- When: book ID doesn't exist → throw `BookNotFoundException`

**Task 4 — Parameterized test**  
```java
@ParameterizedTest
@CsvSource({"978-0-06-112008-4,true", "978-0-7432-7356-5,false"})
void checkOutBook_withVariousIsbns(String isbn, boolean expected) { ... }
```

**Task 5 — Implement `LibraryService`** (AFTER writing tests)  
Make all tests pass, then refactor.

---

## Part 2 — Mockito: `BookService`

**Task 6**  
`BookService` depends on `BookRepository` (JPA). Write unit tests:
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock BookRepository bookRepository;
    @InjectMocks BookService bookService;

    @Test void findById_shouldReturnBook_whenExists() { ... }
    @Test void findById_shouldThrow_whenNotExists() { ... }
    @Test void save_shouldCallRepositorySave() { ... }
    @Test void delete_shouldVerifyRepositoryDelete() { ... } // use verify()
}
```

---

## Part 3 — MockMvc: `BookController`

The `BookController` is provided (do not modify). Write `BookControllerTest`:

**Task 7**  
```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean BookService bookService;
    @Autowired ObjectMapper objectMapper;

    @Test void getBook_shouldReturn200_withValidId() { ... }
    @Test void getBook_shouldReturn404_whenNotFound() { ... }
    @Test void createBook_shouldReturn201_withValidBody() { ... }
    @Test void createBook_shouldReturn400_withInvalidBody() { ... }
    @Test void deleteBook_shouldReturn204() { ... }
}
```
Use `mockMvc.perform(get("/api/books/1")).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("..."))`.

---

## Submission Checklist

- [ ] Tests written BEFORE implementation (TDD order followed for Part 1)
- [ ] Red-Green-Refactor cycle documented in comments
- [ ] All tests pass with `mvn test`
- [ ] `@BeforeEach` and `@AfterEach` used
- [ ] Parameterized test with `@CsvSource`
- [ ] Mockito: `when/thenReturn` and `verify()` both used
- [ ] MockMvc tests cover: 200, 201, 204, 400, 404 status codes
- [ ] `@WebMvcTest` with `@MockBean` used
