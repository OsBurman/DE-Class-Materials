# Day 28 Part 1 — Testing: TDD, JUnit 5 & Code Coverage
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Testing Java Applications
**Subtitle:** Part 1: TDD, JUnit 5, Lifecycle Annotations, Parameterized Tests & Coverage

**Learning objectives listed on slide:**
- Explain TDD principles and apply the Red-Green-Refactor cycle
- Write unit tests with JUnit 5
- Use assertions to verify expected behavior
- Apply lifecycle annotations to manage test state
- Write parameterized tests for multiple input scenarios
- Measure code coverage with JaCoCo

---

### Slide 2 — Why Testing Matters
**Header:** The Cost of Not Testing

**Cost to fix a bug by when it's discovered:**
```
Discovery stage              Relative cost
─────────────────────────────────────────
During development            $1
During code review            $5
During QA / testing           $10
In production (customer hit)  $100+
```

**What a strong test suite gives you:**
- **Confidence to refactor** — change internal structure without fear of breaking external behavior
- **Fast feedback** — a test run takes seconds; a full deploy-and-manual-verify takes minutes
- **Living documentation** — tests are a precise specification of how code is supposed to behave
- **Regression protection** — new code cannot silently break old behavior that already has a test

**The senior developer mindset:** untested code isn't finished. It's a liability you're handing to production.

---

### Slide 3 — The Test Pyramid
**Header:** Where to Invest Testing Effort

```
             ╱╲
            ╱  ╲
           ╱ E2E ╲           Few — slow, brittle, expensive
          ╱────────╲
         ╱Integration╲       Some — Spring context + real or in-memory DB
        ╱──────────────╲
       ╱                ╲
      ╱   Unit Tests      ╲  Many — one class, all deps mocked, milliseconds
     ╱────────────────────╲
```

**Unit tests (the base — most tests):**
- Test one class or method in complete isolation
- All dependencies replaced with mocks
- Run in milliseconds — fast enough to run on every save
- Goal: hundreds; test every branch of business logic

**Integration tests (middle — some):**
- Bring real Spring components together
- Slice tests (`@WebMvcTest`, `@DataJpaTest`) or full context (`@SpringBootTest`)
- Real or in-memory database; slower than unit tests
- Goal: cover the wiring between components

**E2E tests (top — few):**
- Test the full deployed system from HTTP request to database and back
- Slowest, most fragile; catch deployment and configuration issues
- Goal: a handful of critical happy-path scenarios

**The anti-pattern — the inverted pyramid:** many slow E2E tests, few unit tests. When an E2E test fails, debugging the entire stack is expensive. Keep the unit base wide.

---

### Slide 4 — TDD Principles
**Header:** Test-Driven Development — Write the Test First

**What TDD is:**
- A *development technique*, not just a testing strategy
- You write the **test before the production code** — the test specifies the expected behavior
- Production code is written only to satisfy a failing test
- You alternate: test → code → test → code (small cycles)

**Why TDD works:**
| Benefit | Why |
|---------|-----|
| Guaranteed coverage | You cannot write untested production code |
| Cleaner APIs | You think as a *consumer* of the code first — produces better interfaces |
| Smaller, focused methods | Tests for complex methods are hard to write — TDD pushes toward simplicity |
| Regression protection | Every behavior has a test; new features can't silently break old ones |

**Common misconceptions:**
- ❌ "TDD means 100% coverage" — No. It means everything you build is covered.
- ❌ "TDD is slower" — Short-term, slightly. Long-term, far fewer debugging sessions.
- ❌ "TDD means writing all tests up front" — No. One test at a time, alternating with code.

---

### Slide 5 — Red-Green-Refactor Cycle
**Header:** 🔴 Red → 🟢 Green → 🔵 Refactor

**The three steps:**

**Step 1 — 🔴 RED:** Write a test for behavior that doesn't exist yet. Run it. It **must fail** — if it passes, the feature already exists or the test is not testing what you think.

**Step 2 — 🟢 GREEN:** Write the **minimum production code** to make the test pass. No more. Don't optimize. Don't add features not required by a test.

**Step 3 — 🔵 REFACTOR:** Clean up. Extract constants. Rename for clarity. Remove duplication. The tests are your safety net — if they stay green, the refactoring is provably safe.

**Full cycle example:**
```java
// ── 🔴 RED ─────────────────────────────────────────────────────────────────
@Test
@DisplayName("applies 20% discount for PREMIUM members")
void shouldApplyDiscountForPremiumMember() {
    PricingService pricingService = new PricingService();
    BigDecimal result = pricingService.calculatePrice(new BigDecimal("100.00"), MemberType.PREMIUM);
    assertEquals(new BigDecimal("80.00"), result);
}
// PricingService doesn't exist → fails to compile = RED ✅

// ── 🟢 GREEN ────────────────────────────────────────────────────────────────
public class PricingService {
    public BigDecimal calculatePrice(BigDecimal price, MemberType memberType) {
        if (memberType == MemberType.PREMIUM) {
            return price.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
// Test passes = GREEN ✅

// ── 🔵 REFACTOR ─────────────────────────────────────────────────────────────
public class PricingService {
    private static final BigDecimal PREMIUM_DISCOUNT_RATE = new BigDecimal("0.80");

    public BigDecimal calculatePrice(BigDecimal price, MemberType memberType) {
        return switch (memberType) {
            case PREMIUM -> applyDiscount(price, PREMIUM_DISCOUNT_RATE);
            default      -> price;
        };
    }

    private BigDecimal applyDiscount(BigDecimal price, BigDecimal rate) {
        return price.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
// Tests still pass = REFACTOR ✅
```

**Target cycle time:** 2–5 minutes. Short cycles = tight feedback loop.

---

### Slide 6 — Arrange-Act-Assert
**Header:** AAA — Every Test Has Exactly Three Sections

**The structure:**
```
Arrange   →  Set up the state and inputs (create objects, configure mocks)
Act       →  Call the single method under test (usually one line)
Assert    →  Verify the result (return value, side effects, exceptions)
```

**Full annotated example:**
```java
@Test
@DisplayName("findById returns BookDto when book exists in repository")
void findById_returnsBookDto_whenBookExists() {

    // ── Arrange ─────────────────────────────────────────────────────────────
    Book expectedBook = new Book(1L, "Clean Code", new BigDecimal("35.00"), "TECHNICAL");
    when(bookRepository.findById(1L)).thenReturn(Optional.of(expectedBook));

    // ── Act ──────────────────────────────────────────────────────────────────
    BookDto result = bookService.findById(1L);

    // ── Assert ───────────────────────────────────────────────────────────────
    assertNotNull(result);
    assertEquals("Clean Code", result.getTitle());
    assertEquals(new BigDecimal("35.00"), result.getPrice());
}
```

**Rules:**
- **One behavior per test** — if the method name contains "and", it's probably two tests
- **Act should be one line** — if Act is three lines, you're testing multiple things
- **Blank lines between sections** — visual convention; helps future readers scan at a glance
- **Use `assertAll()`** when verifying multiple properties of the same result (all failures reported, not just the first)

---

### Slide 7 — JUnit 5 Architecture
**Header:** JUnit 5 = Three Modules

```
JUnit 5
├── JUnit Platform       ← foundation layer
│   - Launches test frameworks
│   - Test discovery and execution engine
│   - Maven / Gradle / IDE integration
│   - ConsoleLauncher for CLI
│
├── JUnit Jupiter         ← the new programming model (what you write)
│   - All @Test, @BeforeEach, @AfterEach, @ParameterizedTest annotations
│   - Extension model: @ExtendWith
│   - Test engine that runs on the Platform
│
└── JUnit Vintage         ← backward compatibility
    - Runs JUnit 3 and JUnit 4 tests on the JUnit 5 Platform
    - Migration bridge for legacy codebases — no test rewriting needed
```

**What this means in practice:**
- You write with **Jupiter** — imports from `org.junit.jupiter.api.*`
- The **Platform** is what Maven and Gradle talk to when running `mvn test`
- **Vintage** is transparent — old JUnit 4 tests work without any changes

**Spring Boot dependency — includes everything:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**What `spring-boot-starter-test` bundles:**
| Library | Purpose |
|---------|---------|
| JUnit Jupiter | Test framework (annotations, assertions) |
| Mockito | Mocking framework |
| AssertJ | Fluent assertion library |
| JSONPath | JSON assertions in MockMvc tests |
| Spring Test | Spring TestContext framework |
| Hamcrest | Matcher library |

No additional test dependencies needed for unit tests, Mockito tests, or Spring slice tests.

---

### Slide 8 — Writing Test Classes and @Test
**Header:** Test Class Conventions

**File location — mirror main sources:**
```
src/main/java/com/bookstore/service/BookService.java
                                    ↓ same package, test source tree
src/test/java/com/bookstore/service/BookServiceTest.java
```

**Test class structure:**
```java
class BookServiceTest {    // no public keyword needed in JUnit 5

    private BookRepository bookRepository;
    private BookService    bookService;

    @BeforeEach
    void setUp() {
        // Fresh mocks before each test — prevents inter-test contamination
        bookRepository = mock(BookRepository.class);
        bookService    = new BookService(bookRepository);
    }

    @Test
    void findById_returnsBook_whenBookExists() {
        // Arrange
        Book book = new Book(1L, "Clean Code", new BigDecimal("35.00"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        BookDto result = bookService.findById(1L);

        // Assert
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void findById_throwsException_whenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }
}
```

**Test method naming convention:** `methodName_expectedBehavior_whenCondition`
- `findById_returnsBook_whenBookExists`
- `createBook_throwsException_whenIsbnDuplicate`
- `calculatePrice_appliesDiscount_forPremiumMembers`

**One behavior per test.** If a test has five `when()` calls and ten assertions across different methods, split it. Precise tests produce precise failure messages.

---

### Slide 9 — JUnit 5 Assertions
**Header:** Verifying Expected Behavior

**Core JUnit assertions (`org.junit.jupiter.api.Assertions`):**
```java
// Equality
assertEquals(expected, actual);
assertEquals(expected, actual, "message shown on failure");
assertNotEquals(unexpected, actual);

// Nullity
assertNull(value);
assertNotNull(value);

// Booleans
assertTrue(condition);
assertFalse(condition);

// Exception testing — most important for error paths
ResourceNotFoundException ex = assertThrows(
    ResourceNotFoundException.class,
    () -> bookService.findById(99L));
assertEquals("Book not found: 99", ex.getMessage());

// Confirm no exception is thrown
assertDoesNotThrow(() -> bookService.findById(1L));

// Multiple assertions — ALL run; ALL failures reported together
assertAll("BookDto properties",
    () -> assertNotNull(result.getId()),
    () -> assertEquals("Clean Code", result.getTitle()),
    () -> assertEquals(new BigDecimal("35.00"), result.getPrice()));
```

**AssertJ (`org.assertj.core.api.Assertions`) — fluent and more expressive:**
```java
// Equality
assertThat(result.getTitle()).isEqualTo("Clean Code");
assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("35.00"));

// Collections
assertThat(books)
    .hasSize(3)
    .extracting(BookDto::getTitle)
    .containsExactlyInAnyOrder("Clean Code", "Dune", "Spring Boot");

// Exceptions
assertThatThrownBy(() -> bookService.findById(99L))
    .isInstanceOf(ResourceNotFoundException.class)
    .hasMessageContaining("99");

// Null + chaining
assertThat(result)
    .isNotNull()
    .extracting(BookDto::getTitle)
    .isEqualTo("Clean Code");
```

**Prefer AssertJ** for complex assertions — same library, better error messages, more readable chains.

---

### Slide 10 — Test Lifecycle Annotations
**Header:** Controlling Setup and Teardown

**Four annotations and their execution order:**
```java
class BookServiceTest {

    @BeforeAll                        // ← runs ONCE before any test in the class
    static void globalSetUp() {       //   MUST be static (unless @TestInstance(PER_CLASS))
        // start shared expensive resource
    }

    @BeforeEach                       // ← runs before EACH @Test method
    void setUp() {                    //   most common — reset mocks and service here
        bookRepository = mock(BookRepository.class);
        bookService    = new BookService(bookRepository);
    }

    @Test void test1() { ... }
    @Test void test2() { ... }

    @AfterEach                        // ← runs after EACH @Test method
    void tearDown() {                 //   release per-test resources (files, streams)
    }

    @AfterAll                         // ← runs ONCE after all tests complete
    static void globalTearDown() {    //   close shared resources from @BeforeAll
    }
}
```

**Execution output:**
```
@BeforeAll
  @BeforeEach → test1 → @AfterEach
  @BeforeEach → test2 → @AfterEach
@AfterAll
```

**@TestInstance(Lifecycle.PER_CLASS):** annotate the class to remove the `static` requirement from `@BeforeAll` / `@AfterAll` — JUnit creates one class instance shared across all tests. Default is `PER_METHOD` (fresh instance per test — the safer default).

**Critical rule:** tests must be independent. Reset mocks in `@BeforeEach`. The result of test A must never affect test B — tests must produce the same result whether run alone or in any order.

---

### Slide 11 — @DisplayName and @Nested
**Header:** Readable Test Output

**@DisplayName — replace method names with human sentences:**
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

**Test report output:**
```
BookService — findById
  ✅ returns BookDto with correct fields when book exists
  ✅ throws ResourceNotFoundException when book ID does not exist
```

**@Nested — group related tests into inner classes:**
```java
@DisplayName("BookService")
class BookServiceTest {

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test @DisplayName("returns book when exists") void whenExists() { ... }
        @Test @DisplayName("throws when not found")   void whenNotFound() { ... }
    }

    @Nested
    @DisplayName("createBook")
    class CreateBook {
        @BeforeEach void setUpCreateScenario() { /* specific setup */ }
        @Test @DisplayName("saves and returns dto")        void savesBook() { ... }
        @Test @DisplayName("throws when ISBN duplicate")   void duplicateIsbn() { ... }
    }
}
```

**Output:**
```
BookService
  findById
    ✅ returns book when exists
    ✅ throws when not found
  createBook
    ✅ saves and returns dto
    ✅ throws when ISBN duplicate
```

`@Nested` classes can have their own `@BeforeEach` — it runs *after* the outer class's `@BeforeEach`.

---

### Slide 12 — @ParameterizedTest
**Header:** Testing Multiple Inputs Without Duplication

**The problem without parameterized tests (duplication):**
```java
@Test void rejectsZeroPrice()    { assertThrows(..., () -> service.setPrice(0)); }
@Test void rejectsNegativePrice(){ assertThrows(..., () -> service.setPrice(-1)); }
@Test void rejectsLargePrice()   { assertThrows(..., () -> service.setPrice(999999)); }
// Identical structure; only the input differs
```

**@ValueSource — single value per test case:**
```java
@ParameterizedTest(name = "rejects price {0}")
@ValueSource(ints = {0, -1, -100, 999999})
void shouldRejectInvalidPrices(int invalidPrice) {
    assertThrows(InvalidPriceException.class, () -> service.setPrice(invalidPrice));
}
```

**@CsvSource — multiple values per test case:**
```java
@ParameterizedTest(name = "{0} → category should be {2}")
@CsvSource({
    "Clean Code,   35.00, TECHNICAL",
    "Dune,         25.00, SCIFI",
    "Foundation,   22.50, SCIFI"
})
void shouldCreateBookWithCorrectCategory(String title, String price, String expectedCategory) {
    BookDto result = service.createBook(new CreateBookRequest(title, new BigDecimal(price), expectedCategory));
    assertEquals(expectedCategory, result.getCategory());
}
```

**@MethodSource — complex objects / full control:**
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

**@NullAndEmptySource — test null and empty in one annotation:**
```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "\t"})
void shouldRejectBlankTitle(String blankTitle) {
    assertThrows(ValidationException.class,
        () -> service.createBook(new CreateBookRequest(blankTitle, new BigDecimal("10"), "FICTION")));
}
```

---

### Slide 13 — Test Organization and Suites
**Header:** Structuring Tests for a Growing Project

**Package structure — mirror main sources exactly:**
```
src/
├── main/java/com/bookstore/
│   ├── controller/BookController.java
│   ├── service/BookService.java
│   └── repository/BookRepository.java
└── test/java/com/bookstore/
    ├── controller/
    │   └── BookControllerTest.java       ← @WebMvcTest slice test (Part 2)
    ├── service/
    │   └── BookServiceTest.java          ← pure unit test (Mockito only)
    └── repository/
        └── BookRepositoryTest.java       ← @DataJpaTest slice test (Part 2)
```

**Naming convention:**
| Suffix | Meaning | Speed |
|--------|---------|-------|
| `Test` | Unit or slice test | Fast (ms to ~3s) |
| `IT` | Full integration test | Slower (Spring context + real DB) |

**JUnit 5 tags — run test categories selectively:**
```java
@Tag("unit")
class BookServiceTest { ... }

@Tag("integration")
class BookRepositoryIT { ... }
```

**Maven Surefire (unit) vs Failsafe (integration):**
```xml
<!-- Surefire: fast tests on every build -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <excludes><exclude>**/*IT.java</exclude></excludes>
    </configuration>
</plugin>
```

**Test independence rule:** no shared mutable state between tests. No test should depend on the execution order of other tests or rely on side effects from a previous test.

---

### Slide 14 — Code Coverage with JaCoCo
**Header:** Measuring What Your Tests Actually Execute

**Coverage types:**
| Metric | What it measures |
|--------|-----------------|
| **Line coverage** | % of executable source lines executed at least once |
| **Branch coverage** | % of conditional branches (both sides of every `if`/`else`/`switch`) taken |
| **Method coverage** | % of methods called during tests |

**Branch coverage matters more than line coverage.** An `if` with no `else` shows 100% line coverage even if the `false` branch is never tested.

**JaCoCo Maven setup:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution><goals><goal>prepare-agent</goal></goals></execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules><rule><limits><limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.85</minimum>
                </limit></limits></rule></rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Run `mvn test` → report at `target/site/jacoco/index.html`.

**What to test vs skip:**
| Target | Test? |
|--------|-------|
| Service business logic | ✅ Always — highest ROI |
| Custom `@Query` repository methods | ✅ `@DataJpaTest` |
| Controller request/response mapping | ✅ `@WebMvcTest` |
| Input validation logic | ✅ Unit test |
| Lombok-generated getters/setters | ❌ Skip |
| Spring `@Configuration` classes | ❌ Skip |
| Simple DTOs with no logic | ❌ Skip |
| `main()` method | ❌ Skip |

**Coverage ≠ quality.** A test that calls every method and makes zero assertions produces 100% line coverage with zero confidence. The assertion is the test.

---

### Slide 15 — TDD Full Walkthrough: BookService.createBook
**Header:** Building a Feature Test-First

**Requirements for `createBook`:**
1. Save the book and return a `BookDto`
2. Throw `DuplicateIsbnException` if the ISBN is already registered
3. Throw `AuthorNotFoundException` if the author ID doesn't exist

**🔴 RED — three tests written before any implementation:**
```java
@Test
@DisplayName("creates book and returns DTO when request is valid")
void createBook_returnsDto_whenValid() {
    CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL", "978-01", 1L);
    when(bookRepository.existsByIsbn("978-01")).thenReturn(false);
    when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author(1L, "Robert Martin")));
    when(bookRepository.save(any(Book.class))).thenReturn(new Book(1L, "Clean Code", new BigDecimal("35.00")));

    BookDto result = bookService.createBook(req);

    assertNotNull(result.getId());
    assertEquals("Clean Code", result.getTitle());
    verify(bookRepository).save(any(Book.class));
}

@Test
@DisplayName("throws DuplicateIsbnException when ISBN already registered")
void createBook_throws_whenIsbnDuplicate() {
    CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL", "978-01", 1L);
    when(bookRepository.existsByIsbn("978-01")).thenReturn(true);

    assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(req));
    verify(bookRepository, never()).save(any());
}

@Test
@DisplayName("throws AuthorNotFoundException when author does not exist")
void createBook_throws_whenAuthorNotFound() {
    CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL", "978-01", 99L);
    when(bookRepository.existsByIsbn("978-01")).thenReturn(false);
    when(authorRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(AuthorNotFoundException.class, () -> bookService.createBook(req));
}
```

**🟢 GREEN — minimal implementation to pass all three:**
```java
public BookDto createBook(CreateBookRequest req) {
    if (bookRepository.existsByIsbn(req.getIsbn()))
        throw new DuplicateIsbnException("ISBN already registered: " + req.getIsbn());
    Author author = authorRepository.findById(req.getAuthorId())
        .orElseThrow(() -> new AuthorNotFoundException("Author not found: " + req.getAuthorId()));
    Book saved = bookRepository.save(
        new Book(req.getTitle(), req.getPrice(), req.getCategory(), req.getIsbn(), author));
    return BookMapper.toDto(saved);
}
```

**🔵 REFACTOR:** extract ISBN check to a private `validateIsbnUnique()` method. Done. Tests still green.

---

### Slide 16 — Part 1 Summary
**Header:** Part 1 Complete — Testing Foundations

**What you covered:**
```
Test Pyramid         unit (many, fast) → integration (some) → E2E (few, slow)
TDD                  write the test first; code only to make it pass
Red-Green-Refactor   🔴 fail → 🟢 pass → 🔵 clean; target 2–5 min cycles
AAA                  Arrange / Act / Assert — one behavior per test, blank lines between
JUnit 5              Platform (launcher) + Jupiter (annotations) + Vintage (JUnit 4 compat)
@Test                marks a test method; no public needed in JUnit 5
Assertions           assertEquals, assertThrows, assertAll / AssertJ: assertThat(x).isEqualTo(y)
@BeforeEach          reset mocks before every test — test independence
@BeforeAll           runs once; static (unless @TestInstance(PER_CLASS))
@DisplayName         human-readable test names in reports
@Nested              group tests by scenario; inner class @BeforeEach runs after outer
@ParameterizedTest   @ValueSource / @CsvSource / @MethodSource — no copy-paste tests
Organization         *Test = unit/slice; *IT = integration; @Tag for selective runs
JaCoCo               line + branch coverage; >85% minimum; report at target/site/jacoco
```

**Part 2 preview:**
- Mocking deep dive: `@Mock`, `@InjectMocks`, `when/thenReturn/thenThrow/thenAnswer`, `verify()`
- Argument matchers: `any()`, `eq()`, `argThat()`, `ArgumentCaptor`
- `@Spy` for partial mocking
- `@WebMvcTest` + MockMvc for controller tests
- `@DataJpaTest` for repository layer
- `@SpringBootTest` for full integration tests
- Testcontainers for real-database tests
- WireMock for mocking external HTTP services
