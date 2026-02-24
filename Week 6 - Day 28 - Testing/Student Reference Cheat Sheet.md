# Day 28 — Testing: Complete Review Guide

---

## 1. Test Pyramid

Three levels of testing, ordered by quantity and speed:

```
        ╱╲
       ╱  ╲           E2E (few)       — full stack, slow, brittle
      ╱────╲
     ╱      ╲         Integration     — Spring context + DB, medium speed
    ╱────────╲
   ╱  Unit    ╲       Unit (many)     — one class, all deps mocked, milliseconds
  ╱────────────╲
```

- **Unit tests** — test one class in isolation; all dependencies are mocked; hundreds of them; run on every save
- **Integration tests** — test components together; real Spring slice or full context; run on every commit
- **E2E tests** — test the full deployed system; kept to a minimum; too slow and brittle for large suites

**Anti-pattern:** inverted pyramid — mostly E2E tests, few unit tests. Every failure requires debugging the entire stack.

---

## 2. TDD Principles

TDD is a **development technique**, not just a testing strategy.

**Write the test before writing the code.** The test is a specification. Writing it first forces you to think about the API from the consumer's perspective before thinking about the implementation.

| Benefit | Explanation |
|---------|-------------|
| Guaranteed coverage | You cannot write untested production code |
| Cleaner APIs | You design the interface before the implementation |
| Smaller methods | Large, complex methods are hard to test — TDD pushes toward simple code |
| Regression protection | Every feature has a test; old tests catch new breakage |

**Common misconceptions:**
- ❌ "TDD means 100% coverage" — it means the code you write is covered
- ❌ "TDD is slower" — slower day 1, faster by day 5 (fewer debugging sessions)
- ❌ "Write all tests upfront" — one test at a time, alternating with code

---

## 3. Red-Green-Refactor Cycle

**🔴 RED** — Write a failing test for behavior that doesn't exist yet. Run it. It must fail — if it passes, the feature already exists or the test is wrong.

**🟢 GREEN** — Write the minimum production code to make the test pass. No more. Do not optimize. Do not add unspecified features.

**🔵 REFACTOR** — Improve the code. Extract constants, rename variables, remove duplication. The tests are the safety net — if they stay green, the refactoring is safe.

**Cycle time:** aim for 2–5 minutes per cycle. Short cycles = tight feedback loop.

```java
// 🔴 RED
@Test
void shouldApplyDiscountForPremiumMember() {
    PricingService service = new PricingService();
    assertEquals(new BigDecimal("80.00"),
        service.calculatePrice(new BigDecimal("100.00"), MemberType.PREMIUM));
}
// Compilation failure = RED ✅

// 🟢 GREEN — just enough to pass
public BigDecimal calculatePrice(BigDecimal price, MemberType type) {
    if (type == MemberType.PREMIUM)
        return price.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
    return price;
}

// 🔵 REFACTOR — extract constant, use switch expression
private static final BigDecimal PREMIUM_RATE = new BigDecimal("0.80");
public BigDecimal calculatePrice(BigDecimal price, MemberType type) {
    return switch (type) {
        case PREMIUM -> price.multiply(PREMIUM_RATE).setScale(2, RoundingMode.HALF_UP);
        default      -> price;
    };
}
```

---

## 4. Arrange-Act-Assert Pattern

Every test has three distinct sections separated by blank lines.

```java
@Test
void findById_returnsBookDto_whenBookExists() {
    // Arrange — set up state and inputs
    Book book = new Book(1L, "Clean Code", new BigDecimal("35.00"));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // Act — one method call; store the result
    BookDto result = bookService.findById(1L);

    // Assert — verify the outcome
    assertNotNull(result);
    assertEquals("Clean Code", result.getTitle());
}
```

**Rules:**
- **One behavior per test** — if the test name contains "and," split it into two tests
- **Act is one line** — multiple method calls in Act = testing multiple things
- Use `assertAll()` to group related assertions so all failures are reported
- Blank lines between sections are a visual convention for future readers

---

## 5. JUnit 5 Architecture

JUnit 5 is three modules:

| Module | Role |
|--------|------|
| **JUnit Platform** | Foundation — discovers and launches test frameworks; what Maven/Gradle/IDE talks to |
| **JUnit Jupiter** | Programming model — all `@Test`, `@BeforeEach`, `@DisplayName` annotations |
| **JUnit Vintage** | Backward compatibility — runs JUnit 3 and 4 tests on the Platform |

In practice: you write with **Jupiter** (all imports from `org.junit.jupiter.api`); the **Platform** runs your tests; **Vintage** is for legacy codebases.

---

## 6. spring-boot-starter-test

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Includes everything needed for most Spring Boot tests:

| Library | Purpose |
|---------|---------|
| JUnit Jupiter | Test framework |
| Mockito | Mocking / stubbing |
| AssertJ | Fluent assertions |
| JSONPath (Jayway) | JSON path assertions in MockMvc |
| Spring Test | `MockMvc`, `TestRestTemplate`, Spring `TestContext` |
| Hamcrest | Matcher library |

No additional dependencies required for standard testing scenarios.

---

## 7. Test Class Conventions

```
src/main/java/com/bookstore/service/BookService.java
           ↓  same package, test source root
src/test/java/com/bookstore/service/BookServiceTest.java
```

- Class name: `{ClassUnderTest}Test`
- Not `public` — JUnit 5 does not require it
- Test methods: not `public`, return `void`

```java
class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);   // fresh mock before each test
        bookService    = new BookService(bookRepository);
    }
}
```

---

## 8. @Test Annotation

```java
@Test
void methodName_expectedBehavior_whenCondition() {
    // test body
}
```

- Marks a method as a test method
- JUnit discovers and runs all `@Test` methods in the class
- A test passes if it completes without throwing an exception
- A test fails if an assertion throws `AssertionError` or any other exception is thrown

---

## 9. Test Method Naming Convention

Format: `methodName_expectedBehavior_whenCondition`

| Example method name | Reads as |
|--------------------|----------|
| `findById_returnsBook_whenBookExists` | findById returns book when book exists |
| `createBook_throwsException_whenIsbnDuplicate` | createBook throws exception when ISBN is duplicate |
| `calculatePrice_appliesDiscount_forPremiumMembers` | calculatePrice applies discount for premium members |
| `deleteBook_doesNotCallRepo_whenBookNotFound` | deleteBook does not call repo when book not found |

---

## 10. JUnit 5 Core Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

// Equality
assertEquals(expected, actual);
assertEquals(expected, actual, "message on failure");
assertNotEquals(unexpected, actual);

// Nullity
assertNull(value);
assertNotNull(value);

// Boolean
assertTrue(condition);
assertFalse(condition);

// Exception — returns the exception for further assertions
ResourceNotFoundException ex = assertThrows(
    ResourceNotFoundException.class,
    () -> bookService.findById(99L));
assertEquals("Book not found: 99", ex.getMessage());

// No exception thrown
assertDoesNotThrow(() -> bookService.findById(1L));

// All run together — all failures reported at once
assertAll("BookDto properties",
    () -> assertNotNull(result.getId()),
    () -> assertEquals("Clean Code", result.getTitle()),
    () -> assertEquals(new BigDecimal("35.00"), result.getPrice()));

// Arrays
assertArrayEquals(expectedArray, actualArray);
```

---

## 11. AssertJ Reference

```java
import static org.assertj.core.api.Assertions.*;

// Equality
assertThat(result.getTitle()).isEqualTo("Clean Code");
assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("35.00"));

// Null / not null
assertThat(result).isNotNull();
assertThat(result).isNull();

// Boolean
assertThat(condition).isTrue();
assertThat(condition).isFalse();

// Strings
assertThat(message).contains("error");
assertThat(message).startsWith("Book");
assertThat(message).endsWith("not found");

// Collections
assertThat(books).hasSize(3);
assertThat(books).isEmpty();
assertThat(books).isNotEmpty();
assertThat(books).contains(book1, book2);
assertThat(books).extracting(BookDto::getTitle)
                 .containsExactlyInAnyOrder("Clean Code", "Dune", "Spring Boot");

// Exceptions
assertThatThrownBy(() -> bookService.findById(99L))
    .isInstanceOf(ResourceNotFoundException.class)
    .hasMessageContaining("99");

// Chained on same object
assertThat(result).isNotNull()
                  .extracting(BookDto::getTitle)
                  .isEqualTo("Clean Code");
```

---

## 12. assertThrows and Exception Testing

```java
// Basic — just verify exception type is thrown
assertThrows(ResourceNotFoundException.class,
    () -> bookService.findById(99L));

// Capture the exception for further assertion
ResourceNotFoundException ex = assertThrows(
    ResourceNotFoundException.class,
    () -> bookService.findById(99L));
assertEquals("Book not found: 99", ex.getMessage());
assertTrue(ex.getMessage().contains("99"));

// AssertJ equivalent — more fluent
assertThatThrownBy(() -> bookService.findById(99L))
    .isInstanceOf(ResourceNotFoundException.class)
    .hasMessage("Book not found: 99");
```

---

## 13. @BeforeEach / @AfterEach

```java
@BeforeEach
void setUp() {
    // Runs before EACH test method
    // Create fresh mocks here — prevents inter-test contamination
    bookRepository = mock(BookRepository.class);
    bookService    = new BookService(bookRepository);
}

@AfterEach
void tearDown() {
    // Runs after EACH test method
    // Release resources opened per test (streams, connections)
    // Rarely needed when using Mockito mocks
}
```

**Most test setup belongs in `@BeforeEach`.** Tests must be independent — the result of test A must never affect test B.

---

## 14. @BeforeAll / @AfterAll

```java
@BeforeAll
static void globalSetUp() {
    // Runs ONCE before any test method in the class
    // MUST be static (default PER_METHOD lifecycle)
    // Use for expensive shared resources (e.g., database connection)
    sharedResource = ExpensiveResource.open();
}

@AfterAll
static void globalTearDown() {
    // Runs ONCE after all test methods complete
    // MUST be static (default lifecycle)
    sharedResource.close();
}
```

**Use sparingly** — shared state between tests is a source of subtle bugs. Prefer `@BeforeEach` for most setup.

---

## 15. @TestInstance

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceTest {

    // With PER_CLASS: one instance for all tests — @BeforeAll and @AfterAll
    // do NOT need to be static
    @BeforeAll
    void globalSetUp() { ... }    // no static keyword needed

    @AfterAll
    void globalTearDown() { ... } // no static keyword needed
}
```

| Lifecycle | Behavior | Default |
|-----------|----------|---------|
| `PER_METHOD` | New instance per test method | ✅ Yes |
| `PER_CLASS` | One instance for all test methods — removes static requirement | No |

---

## 16. @DisplayName

```java
@DisplayName("BookService — findById")
class BookServiceTest {

    @Test
    @DisplayName("returns BookDto with correct fields when book exists")
    void findById_returnsBook_whenExists() { ... }

    @Test
    @DisplayName("throws ResourceNotFoundException when book ID does not exist")
    void findById_throwsException_whenNotFound() { ... }
}
```

Test report output:
```
BookService — findById
  ✅ returns BookDto with correct fields when book exists
  ✅ throws ResourceNotFoundException when book ID does not exist
```

---

## 17. @Nested

Groups related tests into nested inner classes. Each nested class can have its own `@BeforeEach` (runs after the outer `@BeforeEach`).

```java
@DisplayName("BookService")
class BookServiceTest {

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test @DisplayName("returns book when exists")    void whenExists()    { ... }
        @Test @DisplayName("throws when not found")       void whenNotFound()  { ... }
    }

    @Nested
    @DisplayName("createBook")
    class CreateBook {
        @BeforeEach void createBookSetUp() { /* extra setup for create scenarios */ }
        @Test @DisplayName("saves and returns dto")         void savesBook()     { ... }
        @Test @DisplayName("throws when ISBN duplicate")    void duplicateIsbn() { ... }
    }
}
```

Report output:
```
BookService
  findById
    ✅ returns book when exists
    ✅ throws when not found
  createBook
    ✅ saves and returns dto
    ✅ throws when ISBN duplicate
```

---

## 18. @ParameterizedTest with @ValueSource

Single primitive/String values, one per test invocation:

```java
@ParameterizedTest(name = "rejects price {0}")
@ValueSource(ints = {0, -1, -100, 999999})
void shouldRejectInvalidPrices(int invalidPrice) {
    assertThrows(InvalidPriceException.class, () -> service.setPrice(invalidPrice));
}
// Runs 4 times, one per value
```

---

## 19. @ParameterizedTest with @CsvSource

Multiple values per test case, comma-separated rows:

```java
@ParameterizedTest(name = "{0} costs {1} in category {2}")
@CsvSource({
    "Clean Code,   35.00, TECHNICAL",
    "Dune,         25.00, SCIFI",
    "Foundation,   22.50, SCIFI"
})
void shouldStoreCorrectCategory(String title, String price, String expectedCategory) {
    BookDto result = service.createBook(new CreateBookRequest(title, new BigDecimal(price), expectedCategory));
    assertEquals(expectedCategory, result.getCategory());
}
// Runs 3 times, one row per test invocation
```

---

## 20. @ParameterizedTest with @MethodSource

Complex objects — point at a static factory method returning `Stream<Arguments>`:

```java
@ParameterizedTest
@MethodSource("invalidRequestProvider")
void shouldRejectInvalidCreateRequest(CreateBookRequest request, String expectedFieldInError) {
    Exception ex = assertThrows(ValidationException.class, () -> service.createBook(request));
    assertTrue(ex.getMessage().contains(expectedFieldInError));
}

static Stream<Arguments> invalidRequestProvider() {
    return Stream.of(
        Arguments.of(new CreateBookRequest(null, new BigDecimal("10"), "FICTION"), "title"),
        Arguments.of(new CreateBookRequest("Book", null, "FICTION"),               "price"),
        Arguments.of(new CreateBookRequest("Book", new BigDecimal("10"), null),    "category")
    );
}
```

---

## 21. @NullAndEmptySource

Supplies `null` and `""` as test arguments:

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "\t", "\n"})
void shouldRejectBlankTitle(String blankTitle) {
    assertThrows(ValidationException.class,
        () -> service.createBook(new CreateBookRequest(blankTitle, new BigDecimal("10"), "FICTION")));
}
// Runs for: null, "", "  ", "\t", "\n"
```

---

## 22. Test Organization and Package Structure

```
src/
├── main/java/com/bookstore/
│   ├── controller/BookController.java
│   ├── service/BookService.java
│   └── repository/BookRepository.java
└── test/java/com/bookstore/
    ├── controller/BookControllerTest.java   ← @WebMvcTest (slice)
    ├── service/BookServiceTest.java         ← unit test (Mockito)
    └── repository/BookRepositoryTest.java  ← @DataJpaTest (slice)
```

**Naming convention:**

| Suffix | Type | Speed |
|--------|------|-------|
| `Test` | Unit or slice test | Fast (<1s) |
| `IT` | Integration test (`@SpringBootTest`) | Slower (Spring context + real DB) |

---

## 23. Test Tags and Selective Execution

```java
@Tag("unit")
class BookServiceTest { ... }

@Tag("integration")
class BookApiIntegrationTest { ... }
```

Maven Surefire — run only unit-tagged tests (fast build):
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <groups>unit</groups>
        <excludedGroups>integration</excludedGroups>
    </configuration>
</plugin>
```

Maven Failsafe — run integration tests separately:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <groups>integration</groups>
    </configuration>
</plugin>
```

---

## 24. JaCoCo Maven Setup

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <!-- instrument bytecode before tests run -->
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <!-- generate HTML report after tests -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <!-- fail build if coverage drops below threshold -->
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.85</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Report location after `mvn test`: `target/site/jacoco/index.html`

---

## 25. Code Coverage Best Practices

**Coverage types:**

| Metric | What it measures |
|--------|-----------------|
| **Line coverage** | % of executable lines executed by tests |
| **Branch coverage** | % of conditional branches (if/else, switch) exercised |
| **Method coverage** | % of methods called at least once |

**Branch coverage matters more than line coverage** — an `if` with no else can show 100% line coverage even if the false branch was never executed.

**What to test vs skip:**

| Target | Test? |
|--------|-------|
| Service business logic | ✅ Highest ROI — every business rule, every exception path |
| Controller request/response mapping | ✅ @WebMvcTest |
| Custom repository `@Query` methods | ✅ @DataJpaTest |
| Validation logic | ✅ Unit tests |
| Lombok-generated getters/setters | ❌ Skip |
| Spring `@Configuration` classes | ❌ Skip |
| Simple DTOs / POJOs with no logic | ❌ Skip |
| `main()` method | ❌ Skip |

**Coverage ≠ quality.** A test that calls every method but makes zero assertions gives high coverage with zero value.

---

## 26. Test Double Types

| Type | Definition | Mockito equivalent |
|------|-----------|-------------------|
| **Mock** | Records calls; returns configured values | `@Mock` / `mock(Class)` |
| **Stub** | Returns preconfigured responses (no call recording) | `when().thenReturn()` on a mock |
| **Spy** | Wraps a real object; intercepts specific calls | `@Spy` / `spy(object)` |
| **Fake** | Simplified but working implementation | `new InMemoryBookRepository()` |
| **Dummy** | Passed as argument but never used | `null` argument |

In practice, "mock" covers both mock and stub — a Mockito mock both records calls and can be configured to return values.

---

## 27. Mockito Setup: @ExtendWith + @Mock + @InjectMocks

**Option A — annotation-based (most concise):**
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    BookService bookService;   // Mockito creates BookService and injects the mocks
}
```

**Option B — manual in @BeforeEach (most explicit, preferred for constructor injection):**
```java
class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService    = new BookService(bookRepository);  // clearly shows constructor
    }
}
```

`@InjectMocks` injection order: constructor first, then setter, then field. **Use constructor injection** in production code — it makes the dependency graph explicit and tests easier to write.

---

## 28. Stubbing: when().thenReturn()

```java
// Basic return value
when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

// Return null
when(bookRepository.findById(99L)).thenReturn(Optional.empty());

// Successive calls return different values
when(bookRepository.count())
    .thenReturn(0L)    // first call
    .thenReturn(1L)    // second call
    .thenReturn(2L);   // subsequent calls

// Return a new object on each call
when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
```

**Default return values for unstubbed calls:**
| Return type | Default value |
|-------------|---------------|
| Object | `null` |
| `int`, `long`, etc. | `0` |
| `boolean` | `false` |
| `Collection` | empty collection |
| `Optional` | `Optional.empty()` |

---

## 29. Stubbing: thenThrow, thenAnswer, doNothing

```java
// Throw an exception
when(bookRepository.findById(99L))
    .thenThrow(new ResourceNotFoundException("Book not found: 99"));

// Compute return value from the actual arguments (simulate DB-assigned ID)
when(bookRepository.save(any(Book.class)))
    .thenAnswer(invocation -> {
        Book book = invocation.getArgument(0);
        book.setId(1L);
        return book;
    });

// Void method — throw exception
doThrow(new DataIntegrityViolationException("constraint"))
    .when(bookRepository).deleteById(1L);

// Void method — do nothing (explicit, but also the default)
doNothing().when(emailService).sendWelcomeEmail(anyString());
```

---

## 30. Argument Matchers Reference

```java
import static org.mockito.ArgumentMatchers.*;

any(Book.class)          // any non-null Book
anyLong()                // any long value
anyString()              // any non-null String
anyInt()                 // any int
anyList()                // any List
eq("exact value")        // exact match (use within matchers context)
isNull()                 // null argument
isNotNull()              // any non-null argument
argThat(b -> b.getTitle().startsWith("Clean"))   // custom predicate
contains("bookstore")    // String containing substring
startsWith("Welcome")    // String with prefix
endsWith("@gmail.com")   // String with suffix
```

**Critical all-or-nothing rule:** if you use a matcher for any argument, ALL arguments must use matchers:

```java
// ❌ WRONG — mixing exact value and matcher
when(service.search(anyString(), 10)).thenReturn(results);

// ✅ CORRECT — wrap the exact value in eq()
when(service.search(anyString(), eq(10))).thenReturn(results);
```

---

## 31. verify() Reference

```java
// Called exactly once (default)
verify(bookRepository).save(any(Book.class));
verify(bookRepository, times(1)).save(any(Book.class));

// Never called
verify(bookRepository, never()).save(any());

// Specific count
verify(emailService, times(3)).sendNotification(anyString());

// At least / at most
verify(emailService, atLeast(1)).sendEmail(anyString());
verify(emailService, atMost(2)).sendEmail(anyString());

// Verify nothing else was called on this mock
verifyNoMoreInteractions(bookRepository);
```

---

## 32. ArgumentCaptor

Captures the actual argument passed to a mock method for inspection:

```java
ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

// Capture during verify
verify(bookRepository).save(captor.capture());

// Inspect what was actually passed to save()
Book savedBook = captor.getValue();
assertEquals("Clean Code", savedBook.getTitle());
assertEquals("TECHNICAL", savedBook.getCategory());
assertNotNull(savedBook.getCreatedAt());

// Multiple captures — captor.getAllValues() returns a List<Book>
```

**Use case:** when you can't inspect the saved object from the service's return value — e.g., the service maps to a DTO before returning, but you want to assert on the entity that was persisted.

---

## 33. InOrder Verification

Verify that methods were called in a specific sequence:

```java
InOrder inOrder = inOrder(bookRepository, emailService);

// These must be called in this exact order
inOrder.verify(bookRepository).save(any(Book.class));
inOrder.verify(emailService).sendWelcomeEmail(anyString());
```

Use when the order of side effects matters — e.g., audit log must be written before the response is sent.

---

## 34. @Spy: Partial Mocking

A spy wraps a real object. All methods call the real implementation unless explicitly stubbed.

```java
@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Spy
    PricingService pricingService = new PricingService();   // real object

    @Test
    void calculateTotal_usesRealLogic_exceptForDiscount() {
        // Override one method while keeping the rest real
        doReturn(new BigDecimal("0.80")).when(pricingService).getDiscountRate(MemberType.PREMIUM);

        BigDecimal total = pricingService.calculateTotal(new BigDecimal("100.00"), MemberType.PREMIUM);
        assertEquals(new BigDecimal("80.00"), total);
    }
}
```

**Always use `doReturn().when()` on spies — never `when().thenReturn()`:**
```java
// ❌ WRONG — calls the real method before stubbing it
when(spy.getRealMethod()).thenReturn(value);

// ✅ CORRECT
doReturn(value).when(spy).getRealMethod();
```

**When to use a spy:** legacy code where extracting dependencies isn't feasible; same-class method call delegation. Prefer full mocks for new code.

---

## 35. Spring Boot Test Slices Overview

| Annotation | Loads | Mock with | Use for |
|-----------|-------|-----------|---------|
| `@WebMvcTest` | Controllers, WebMvc config, Jackson, Validation | `@MockBean` for services | HTTP request/response, validation, status codes |
| `@DataJpaTest` | Entities, Repositories, JPA/Hibernate, H2 | Nothing (real repo + H2) | Custom queries, relationships |
| `@JsonTest` | Jackson ObjectMapper | Nothing | DTO serialization/deserialization |
| `@RestClientTest` | HTTP client config | Nothing | RestTemplate/WebClient setup |
| `@SpringBootTest` | Full application context | `@MockBean` for specific beans | Cross-layer integration, startup behavior |

**Rule:** use the narrowest slice that still tests what you need. Narrower = faster.

---

## 36. @WebMvcTest + MockMvc Reference

```java
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  BookService bookService;

    @Test
    void getBook_returns200_whenExists() throws Exception {
        when(bookService.findById(1L)).thenReturn(new BookDto(1L, "Clean Code", new BigDecimal("35.00")));

        mockMvc.perform(get("/api/books/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void createBook_returns201_whenValid() throws Exception {
        CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL");
        BookDto created = new BookDto(1L, "Clean Code", new BigDecimal("35.00"));
        when(bookService.createBook(any())).thenReturn(created);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/books/1")));
    }

    @Test
    void createBook_returns400_whenTitleBlank() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateBookRequest("", new BigDecimal("35.00"), "TECHNICAL"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    @Test
    void getBook_returns404_whenNotFound() throws Exception {
        when(bookService.findById(99L)).thenThrow(new ResourceNotFoundException("Book not found: 99"));
        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Book not found: 99"));
    }
}
```

---

## 37. MockMvc HTTP Methods and Assertions

```java
// HTTP methods
mockMvc.perform(get("/path/{id}", id))
mockMvc.perform(post("/path").contentType(APPLICATION_JSON).content(json))
mockMvc.perform(put("/path/{id}", id).contentType(APPLICATION_JSON).content(json))
mockMvc.perform(delete("/path/{id}", id))
mockMvc.perform(patch("/path/{id}", id).contentType(APPLICATION_JSON).content(json))

// Adding headers / params
.header("Authorization", "Bearer token")
.param("page", "0")
.param("size", "10")

// Common assertions
.andExpect(status().isOk())               // 200
.andExpect(status().isCreated())          // 201
.andExpect(status().isNoContent())        // 204
.andExpect(status().isBadRequest())       // 400
.andExpect(status().isNotFound())         // 404
.andExpect(status().isUnauthorized())     // 401
.andExpect(header().string("Location", containsString("/api/books/1")))
.andExpect(content().contentType(APPLICATION_JSON))
.andExpect(jsonPath("$.id").value(1))
.andExpect(jsonPath("$.title").value("Clean Code"))
.andExpect(jsonPath("$.books").isArray())
.andExpect(jsonPath("$.books.length()").value(3))
.andExpect(jsonPath("$.books[0].title").value("Clean Code"))

// Print request/response to console (debug)
.andDo(print())
```

---

## 38. @DataJpaTest Reference

```java
@DataJpaTest
class BookRepositoryTest {

    @Autowired BookRepository bookRepository;
    @Autowired TestEntityManager entityManager;   // helper for direct DB inserts

    @Test
    void findByCategory_returnsOnlyMatchingBooks() {
        // Arrange — insert directly into persistence context
        entityManager.persist(new Book("Clean Code",  new BigDecimal("35.00"), "TECHNICAL"));
        entityManager.persist(new Book("Dune",        new BigDecimal("25.00"), "SCIFI"));
        entityManager.persist(new Book("Spring Boot", new BigDecimal("40.00"), "TECHNICAL"));
        entityManager.flush();   // force SQL insert before query

        // Act
        List<Book> result = bookRepository.findByCategory("TECHNICAL");

        // Assert
        assertThat(result).hasSize(2)
                          .extracting(Book::getTitle)
                          .containsExactlyInAnyOrder("Clean Code", "Spring Boot");
    }
}
```

**What @DataJpaTest gives you:**
- Auto-configured H2 in-memory database
- `@Entity` classes registered with Hibernate
- Transactions rolled back after each test (clean state)
- `TestEntityManager` for direct inserts without going through the repository layer

**Use it to test:** custom `@Query` methods, derived query methods with complex logic, relationship loading, `existsByXxx` derived methods.

---

## 39. @SpringBootTest Options

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookApiIntegrationTest {

    @Autowired TestRestTemplate restTemplate;

    @Test
    void createThenRetrieve_fullRoundtrip() {
        CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL");

        ResponseEntity<BookDto> createResponse = restTemplate.postForEntity("/api/books", req, BookDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Long id = createResponse.getBody().getId();
        ResponseEntity<BookDto> getResponse = restTemplate.getForEntity("/api/books/" + id, BookDto.class);
        assertEquals("Clean Code", getResponse.getBody().getTitle());
    }
}
```

**`application-test.properties` (src/test/resources):**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**WebEnvironment options:**

| Option | Description |
|--------|-------------|
| `RANDOM_PORT` | Starts real Tomcat on random port; use `TestRestTemplate` |
| `MOCK` (default) | MockMvc-style context; no real HTTP server |
| `DEFINED_PORT` | Starts on `server.port` from properties |
| `NONE` | No web environment — for non-web integration tests |

---

## 40. Testcontainers Setup

```xml
<!-- Maven dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@Testcontainers
class BookRepositoryContainerTest {

    @Container
    @ServiceConnection    // Spring Boot 3.1+ — auto-configures datasource from container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired BookRepository bookRepository;

    @Test
    void findByCategory_withRealPostgres() {
        bookRepository.save(new Book("Clean Code",  new BigDecimal("35.00"), "TECHNICAL"));
        bookRepository.save(new Book("Dune",        new BigDecimal("25.00"), "SCIFI"));

        List<Book> technical = bookRepository.findByCategory("TECHNICAL");
        assertThat(technical).hasSize(1).extracting(Book::getTitle).containsOnly("Clean Code");
    }
}
```

**Key points:**
- `static` container = shared across all tests in the class (one startup, ~2–5s)
- `@ServiceConnection` auto-configures `spring.datasource.*` — no manual `@DynamicPropertySource` needed (Spring Boot 3.1+)
- Docker must be running
- CI: use a Docker-in-Docker runner or Docker service

---

## 41. WireMock Setup and Stubbing

```xml
<dependency>
    <groupId>org.wiremock.integrations</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@EnableWireMock
class BookPricingClientTest {

    @Autowired BookPricingClient pricingClient;

    @InjectWireMock
    WireMockServer wireMockServer;

    @Test
    void getDiscount_returnsParsedDiscount_on200() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"discountRate\": 0.20}")));

        assertEquals(new BigDecimal("0.20"), pricingClient.getDiscount(MemberType.PREMIUM));
    }

    @Test
    void getDiscount_throwsException_on503() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse().withStatus(503)));

        assertThrows(ServiceUnavailableException.class,
            () -> pricingClient.getDiscount(MemberType.PREMIUM));
    }

    @Test
    void getDiscount_throwsTimeout_onDelay() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse().withFixedDelay(5000)));   // 5s delay

        assertThrows(HttpTimeoutException.class,
            () -> pricingClient.getDiscount(MemberType.PREMIUM));
    }
}
```

**What WireMock can simulate:** 200/400/404/500 responses, network delays, connection failures, response sequences (first call fails, second succeeds — for retry testing).

---

## 42. Test Coverage Strategy

**Invest by layer (highest ROI first):**

| Layer | Test type | Coverage target |
|-------|----------|-----------------|
| Service business logic | Unit (Mockito) | 100% of business rules |
| Controllers | @WebMvcTest + MockMvc | All status codes, all validation rules |
| Custom repository queries | @DataJpaTest | Every `@Query` method |
| HTTP client integrations | @SpringBootTest + WireMock | 200, 4xx, 5xx, timeout |
| Full roundtrip | @SpringBootTest + Testcontainers | 1–2 happy paths |

**JaCoCo exclusions in pom.xml:**
```xml
<configuration>
    <excludes>
        <exclude>**/dto/**</exclude>
        <exclude>**/config/**</exclude>
        <exclude>**/*Application.class</exclude>
        <exclude>**/entity/**</exclude>   <!-- if Lombok-heavy with no logic -->
    </excludes>
</configuration>
```

**Quality reminder:** 85% coverage + weak assertions = false confidence. 60% coverage + strong assertions on all business logic = genuine confidence. Target both.

---

## 43. Common Testing Mistakes

| Mistake | Fix |
|---------|-----|
| Testing implementation details (private method calls) | Test observable behavior (return values, exceptions, collaborator calls) |
| Multiple behaviors in one test | One behavior per test — split on "and" in the test name |
| Sharing mutable state between tests | Create fresh mocks in `@BeforeEach`; never use `static` mutable fields |
| `when().thenReturn()` on a spy | Use `doReturn().when(spy).method()` |
| Mixing exact values and matchers | All-or-nothing — wrap exact values with `eq()` |
| Using `@MockBean` in a plain unit test | Use `@Mock` — `@MockBean` loads Spring context unnecessarily |
| No assertion in a test | A test with no assertion never fails — it's not testing anything |
| Testing only the happy path | Test exception paths, edge cases, boundary values |
| Using `@SpringBootTest` for controller tests | Use `@WebMvcTest` — faster and more focused |
| 100% coverage with no assertions | Coverage measures execution, not correctness |

---

## 44. Looking Ahead — Day 29

Everything tested today gets secured tomorrow.

**Day 29 — Spring Security & OWASP:**
- `SecurityFilterChain` — configuring authentication and authorization rules
- `@WithMockUser` — simulating authenticated users in `@WebMvcTest` tests
- Testing secured endpoints: expect 401 without authentication, 403 without the right role
- OWASP Top 10 — the industry standard for web application security threats
- `BCryptPasswordEncoder` for password hashing
- Method-level security (`@PreAuthorize`, `@Secured`)
