# Day 28 Part 2 — Testing Spring Boot: Mockito, MockMvc, @DataJpaTest & Testcontainers
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Testing Spring Boot Applications
**Subtitle:** Part 2: Mockito, @WebMvcTest, @DataJpaTest, Testcontainers & WireMock

**Learning objectives listed on slide:**
- Mock dependencies with Mockito and verify interactions
- Use argument matchers for flexible stubbing
- Test REST controllers with `@WebMvcTest` and MockMvc
- Test JPA repositories with `@DataJpaTest`
- Run full integration tests with `@SpringBootTest`
- Use Testcontainers for real-database integration tests
- Mock external HTTP services with WireMock

---

### Slide 2 — Why Mocking? Test Doubles
**Header:** Isolating the Unit Under Test

**The isolation problem:**
```
BookService
├── BookRepository      ← requires a database connection
├── AuthorRepository    ← requires a database connection
├── EmailService        ← requires an SMTP server
└── PricingService      ← may have non-deterministic behavior
```

Unit tests test one thing. Real dependencies make them:
- **Slow** — network calls, DB startup, external services
- **Flaky** — what if the DB is down? The network is slow?
- **Unpredictable** — what data is in the DB right now?

**Test doubles** replace real dependencies with controllable substitutes:
| Type | Definition | Example |
|------|-----------|---------|
| **Mock** | Records calls; returns configured values | `mock(BookRepository.class)` |
| **Stub** | Returns a preconfigured response for a specific call | `when(repo.findById(1L)).thenReturn(...)` |
| **Spy** | Wraps a real object; intercepts specific calls | `@Spy PricingService realPricingService` |
| **Fake** | Simplified working implementation | `Map`-backed in-memory repository |
| **Dummy** | Passed but never used in the test | `null` for an unused constructor argument |

**In everyday usage:** "mock" covers both mock and stub. Mockito creates mock objects that you configure with stubbed return values.

---

### Slide 3 — Mockito Setup: @Mock and @InjectMocks
**Header:** Mockito — Two Setup Patterns

**Option A — @ExtendWith + annotations (preferred):**
```java
@ExtendWith(MockitoExtension.class)        // activates Mockito annotations
class BookServiceTest {

    @Mock
    BookRepository bookRepository;          // Mockito creates the mock automatically

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    BookService bookService;               // Mockito instantiates BookService and injects the mocks
                                           // tries constructor first, then setter, then field

    @Test
    void findById_returnsBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book(1L, "Clean Code")));
        BookDto result = bookService.findById(1L);
        assertEquals("Clean Code", result.getTitle());
    }
}
```

**Option B — Manual mock creation in @BeforeEach (more explicit):**
```java
class BookServiceTest {
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);       // Mockito.mock()
        bookService    = new BookService(bookRepository);  // you control construction
    }
}
```

**Prefer Option B for constructor-injected services** — you can see exactly how the class is assembled, and injection issues fail at construction time rather than silently.

**`@MockBean` — Spring-only (not for plain unit tests):**
- Replaces a real Spring bean in the application context with a Mockito mock
- Used with `@WebMvcTest` and `@SpringBootTest` — not plain JUnit/Mockito unit tests
- Using `@MockBean` outside a Spring context loads the entire ApplicationContext unnecessarily

---

### Slide 4 — Stubbing: when().thenReturn()
**Header:** Configuring Mock Behavior

**Core stubbing patterns:**
```java
// Return a value when called with a specific argument
when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

// Return different values on successive calls
when(bookRepository.count())
    .thenReturn(0L)     // first call
    .thenReturn(1L)     // second call
    .thenReturn(2L);    // all subsequent calls

// Throw an exception
when(bookRepository.findById(99L))
    .thenThrow(new ResourceNotFoundException("Book not found: 99"));

// Use a lambda to compute the return value from the argument (thenAnswer)
when(bookRepository.save(any(Book.class)))
    .thenAnswer(invocation -> {
        Book b = invocation.getArgument(0);
        b.setId(1L);    // simulate the DB assigning the ID
        return b;
    });
```

**Stubbing void methods (doThrow / doNothing syntax):**
```java
// Stub void method to throw
doThrow(new RuntimeException("DB error"))
    .when(bookRepository).deleteById(1L);

// Stub void method to do nothing (the default, but explicit when needed)
doNothing().when(emailService).sendWelcomeEmail(anyString());
```

**Default return values for unstubbed calls:**
| Return type | Default |
|-------------|---------|
| Object | `null` |
| int / long | `0` |
| boolean | `false` |
| Collection / List | empty collection |
| Optional | `Optional.empty()` |

---

### Slide 5 — Argument Matchers
**Header:** Flexible Stubbing and Verification

**The problem with exact-value stubs:**
```java
// Only matches the EXACT call with id == 1L
when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
// What if the service internally converts the ID? Or you want to stub ALL calls?
```

**Argument matchers from `org.mockito.ArgumentMatchers`:**
```java
// any() — any non-null argument of the right type
when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

// anyLong(), anyString(), anyInt(), anyList()...
when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

// eq() — exact value within a matchers context
when(bookRepository.findByIsbn(eq("978-0132350884"))).thenReturn(Optional.of(book));

// isNull() / isNotNull()
when(bookRepository.findByIsbn(isNull())).thenReturn(Optional.empty());

// argThat() — custom condition as a lambda
when(bookRepository.save(argThat(b -> b.getPrice().compareTo(BigDecimal.ZERO) > 0)))
    .thenReturn(savedBook);

// String-specific matchers
when(emailService.sendEmail(contains("bookstore"))).thenReturn(true);
when(emailService.sendEmail(startsWith("Welcome"))).thenReturn(true);
```

**Critical rule — all or nothing:**
```java
// ❌ WRONG — mixing exact value and matcher in same call
when(service.search(anyString(), 10)).thenThrow(...);

// ✅ CORRECT — all arguments must use matchers if any do
when(service.search(anyString(), eq(10))).thenThrow(...);
```

---

### Slide 6 — verify(): Interaction Testing
**Header:** Verifying What Your Mock Was Called With

**Why verify?** Some methods return `void` — no return value to assert on. Or you want to confirm a method was called (or NOT called) with specific arguments.

```java
// Verify called exactly once (default)
verify(bookRepository).save(any(Book.class));
verify(bookRepository, times(1)).save(any(Book.class));

// Verify exact number of calls
verify(emailService, times(3)).sendNotification(anyString());

// Verify NEVER called — critical for early-exit paths
verify(bookRepository, never()).save(any(Book.class));

// Verify at least / at most N times
verify(emailService, atLeast(1)).sendEmail(anyString());
verify(emailService, atMost(2)).sendEmail(anyString());

// Capture the actual argument passed to the mock
ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
verify(bookRepository).save(captor.capture());
Book savedBook = captor.getValue();
assertEquals("Clean Code", savedBook.getTitle());     // inspect what was actually saved
assertEquals("TECHNICAL",  savedBook.getCategory());

// Verify call ORDER across multiple mocks
InOrder inOrder = inOrder(bookRepository, emailService);
inOrder.verify(bookRepository).save(any(Book.class));  // save must happen before email
inOrder.verify(emailService).sendWelcomeEmail(anyString());

// Strict: fail if any unexpected calls were made
verifyNoMoreInteractions(bookRepository);
```

**`ArgumentCaptor` is the most powerful pattern here** — instead of just confirming the mock was called, you capture and assert on the actual object that was passed in.

---

### Slide 7 — @Spy: Partial Mocking
**Header:** @Spy — A Real Object with Selective Overrides

A **spy wraps a real object**. All methods execute real code unless explicitly stubbed. Use a spy when you need a real implementation for most methods but want to override one.

```java
@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Spy
    PricingService pricingService = new PricingService();   // real object, not a mock

    @Test
    void calculateTotal_usesStubbedDiscountRate() {
        // Override one method while keeping all others real
        doReturn(new BigDecimal("0.80"))
            .when(pricingService).getDiscountRate(MemberType.PREMIUM);

        BigDecimal total = pricingService.calculateTotal(new BigDecimal("100.00"), MemberType.PREMIUM);

        assertEquals(new BigDecimal("80.00"), total);
        // getDiscountRate() was intercepted; calculateTotal() ran the real code
    }
}
```

**Critical: always use `doReturn().when()` syntax on spies — NOT `when().thenReturn()`:**
```java
// ❌ WRONG on a spy — calls the real method DURING setup (before stubbing completes)
when(spy.expensiveRealMethod()).thenReturn(value);

// ✅ CORRECT on a spy — no real call during setup
doReturn(value).when(spy).expensiveRealMethod();
```

**When to use a spy:**
- Testing a method that calls another method in the same class
- Legacy code where extracting dependencies isn't feasible
- Partial mocking of an otherwise-useful real implementation

**Prefer full mocks over spies for new code.** Spies are a pragmatic tool, not a design goal.

---

### Slide 8 — Spring Boot Test Slices
**Header:** Load Only What You Need

**The problem with `@SpringBootTest`:** it loads the entire application context — all beans, all auto-configuration, full datasource. Accurate, but slow: 5–30 seconds to start.

**Test slices load only the relevant layer:**
```
@WebMvcTest
  Loads:   @Controller, @ControllerAdvice, filter chain, Jackson, Bean Validation
  Mocks:   service and repository layers (you @MockBean them)
  Use for: HTTP request/response mapping, validation, status codes, error responses

@DataJpaTest
  Loads:   @Entity classes, @Repository interfaces, JPA/Hibernate, embedded H2
  Mocks:   nothing — real repositories talk to a real (in-memory) database
  Use for: custom @Query methods, repository logic, relationship loading

@JsonTest
  Loads:   Jackson ObjectMapper, @JsonComponent
  Use for: testing DTO serialization/deserialization

@RestClientTest
  Loads:   RestTemplate / WebClient configuration
  Use for: testing HTTP client classes that call external APIs

@SpringBootTest
  Loads:   full application context — all beans
  Use for: full integration tests spanning controller → service → repository → DB
```

**Rule:** use the narrowest slice that still tests what you need. Narrower = faster = better feedback loop.

---

### Slide 9 — @WebMvcTest + MockMvc
**Header:** Testing REST Controllers

**Setup:**
```java
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean BookService bookService;    // replaces real BookService bean
```

**GET — happy path:**
```java
    @Test
    @DisplayName("GET /api/books/{id} returns 200 and BookDto when book exists")
    void getBook_returns200_whenExists() throws Exception {
        BookDto dto = new BookDto(1L, "Clean Code", new BigDecimal("35.00"));
        when(bookService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.price").value(35.00));
    }
```

**POST — with request body and Location header:**
```java
    @Test
    @DisplayName("POST /api/books returns 201 when request is valid")
    void createBook_returns201_whenValid() throws Exception {
        CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL");
        BookDto created = new BookDto(1L, "Clean Code", new BigDecimal("35.00"));
        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/books/1")))
            .andExpect(jsonPath("$.id").value(1));
    }
```

**Validation error (400) and exception mapping (404):**
```java
    @Test
    @DisplayName("POST /api/books returns 400 when title is blank")
    void createBook_returns400_whenTitleBlank() throws Exception {
        CreateBookRequest invalid = new CreateBookRequest("", new BigDecimal("35.00"), "TECHNICAL");
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    @Test
    @DisplayName("GET /api/books/{id} returns 404 when book not found")
    void getBook_returns404_whenNotFound() throws Exception {
        when(bookService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Book not found: 99"));

        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Book not found: 99"));
    }
}
```

---

### Slide 10 — @DataJpaTest
**Header:** Testing Repositories with a Real Database

**What `@DataJpaTest` provides:**
- Loads only `@Entity` classes and `@Repository` interfaces
- Auto-configures an **in-memory H2 database** — no real DB needed
- Each test method runs in a transaction that is **rolled back after the test** — clean state per test
- Does NOT load controllers, services, or most auto-configuration

```java
@DataJpaTest
class BookRepositoryTest {

    @Autowired BookRepository bookRepository;
    @Autowired TestEntityManager entityManager;   // inserts test data directly, bypasses repo

    @Test
    @DisplayName("findByCategory returns only books matching the given category")
    void findByCategory_returnsMatchingBooks() {
        // Arrange — insert test data directly into persistence context
        entityManager.persist(new Book("Clean Code",  new BigDecimal("35.00"), "TECHNICAL"));
        entityManager.persist(new Book("Dune",        new BigDecimal("25.00"), "SCIFI"));
        entityManager.persist(new Book("Spring Boot", new BigDecimal("40.00"), "TECHNICAL"));
        entityManager.flush();

        // Act
        List<Book> result = bookRepository.findByCategory("TECHNICAL");

        // Assert
        assertThat(result)
            .hasSize(2)
            .extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Clean Code", "Spring Boot");
    }

    @Test
    @DisplayName("existsByIsbn returns true when ISBN is present")
    void existsByIsbn_returnsTrue_whenIsbnExists() {
        entityManager.persist(new Book("Clean Code", new BigDecimal("35.00"), "TECHNICAL", "978-01"));
        entityManager.flush();

        assertTrue(bookRepository.existsByIsbn("978-01"));
    }
}
```

**Use `@DataJpaTest` to test:**
- Custom `@Query` methods — verify the JPQL/SQL is correct
- Derived query methods with complex logic
- Relationship fetching behavior (N+1 detection)
- Custom repository implementations (`BookRepositoryCustomImpl`)

---

### Slide 11 — @SpringBootTest with Test Profiles
**Header:** Full Integration Tests

**When to use `@SpringBootTest`:**
- Testing across multiple layers (controller → service → repository)
- Verifying Spring auto-configuration is correct
- Testing startup behavior

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")    // loads application-test.properties / application-test.yml
class BookApiIntegrationTest {

    @Autowired TestRestTemplate restTemplate;    // makes real HTTP calls to the test server

    @Test
    @DisplayName("create then retrieve — full HTTP roundtrip")
    void createThenRetrieve_fullRoundtrip() {
        CreateBookRequest req = new CreateBookRequest("Clean Code", new BigDecimal("35.00"), "TECHNICAL");

        ResponseEntity<BookDto> createResponse = restTemplate.postForEntity("/api/books", req, BookDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long id = createResponse.getBody().getId();

        ResponseEntity<BookDto> getResponse = restTemplate.getForEntity("/api/books/" + id, BookDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Clean Code", getResponse.getBody().getTitle());
    }
}
```

**`src/test/resources/application-test.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**WebEnvironment options:**
| Option | Description |
|--------|-------------|
| `RANDOM_PORT` | Real servlet container, random port; use `TestRestTemplate` |
| `MOCK` (default) | No real HTTP server; use `MockMvc` with `@AutoConfigureMockMvc` |
| `DEFINED_PORT` | Real server on `server.port` |
| `NONE` | No web environment (non-web integration tests) |

**`@MockBean` within `@SpringBootTest`:** replace specific beans (e.g., external service clients) while keeping everything else real.

---

### Slide 12 — Testcontainers
**Header:** Real Databases in Integration Tests

**The problem with H2:** H2 behaves differently from PostgreSQL and MySQL. SQL dialect differences, type handling, JSON column support, constraint behavior — tests that pass with H2 can fail against the real production database.

**Testcontainers** spins up a real database Docker container for your tests, then stops and removes it when tests finish.

**Maven dependencies:**
```xml
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

**Spring Boot 3.1+ — `@ServiceConnection`:**
```java
@SpringBootTest
@Testcontainers
class BookRepositoryIntegrationTest {

    @Container
    @ServiceConnection                       // Spring Boot reads host/port/credentials and
    static PostgreSQLContainer<?> postgres   // auto-configures spring.datasource.* for you
        = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired BookRepository bookRepository;

    @Test
    void findByCategory_withRealPostgres() {
        bookRepository.save(new Book("Clean Code",  new BigDecimal("35.00"), "TECHNICAL"));
        bookRepository.save(new Book("Dune",        new BigDecimal("25.00"), "SCIFI"));

        List<Book> technical = bookRepository.findByCategory("TECHNICAL");

        assertThat(technical)
            .hasSize(1)
            .extracting(Book::getTitle)
            .containsOnly("Clean Code");
    }
}
```

**`static` container = one container shared across all tests in the class.** Starting a PostgreSQL container takes 2–5 seconds; creating a new one per test would be extremely slow.

**Requirement:** Docker must be running on the test machine. In CI, use a Docker-in-Docker runner or a GitHub Actions service container.

---

### Slide 13 — WireMock: Mocking External HTTP Services
**Header:** WireMock — Test Your HTTP Client Without the Real Server

**When you need WireMock:** your service calls an external REST API (payment provider, shipping service, third-party data). In tests, you don't want real calls — too slow, requires credentials, costs money, can't simulate error responses or network failures.

**WireMock starts a local HTTP server** that you configure to return whatever responses you need.

**Maven dependency:**
```xml
<dependency>
    <groupId>org.wiremock.integrations</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <scope>test</scope>
</dependency>
```

**WireMock with Spring Boot:**
```java
@SpringBootTest
@EnableWireMock
class BookPricingClientTest {

    @Autowired   BookPricingClient pricingClient;
    @InjectWireMock WireMockServer wireMockServer;

    @Test
    @DisplayName("parses discount from 200 response")
    void getDiscount_returnsParsedDiscount_whenApiResponds200() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"discountRate\": 0.20}")));

        BigDecimal discount = pricingClient.getDiscount(MemberType.PREMIUM);
        assertEquals(new BigDecimal("0.20"), discount);
    }

    @Test
    @DisplayName("throws ServiceUnavailableException on 503")
    void getDiscount_throws_whenApiReturns503() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse().withStatus(503)));

        assertThrows(ServiceUnavailableException.class,
            () -> pricingClient.getDiscount(MemberType.PREMIUM));
    }

    @Test
    @DisplayName("handles network timeout gracefully")
    void getDiscount_throws_onNetworkDelay() {
        wireMockServer.stubFor(
            get(urlEqualTo("/pricing/PREMIUM"))
                .willReturn(aResponse()
                    .withFixedDelay(5000)));    // 5 second delay — triggers client timeout

        assertThrows(HttpTimeoutException.class,
            () -> pricingClient.getDiscount(MemberType.PREMIUM));
    }
}
```

**What WireMock lets you simulate:**
- Any HTTP status code (200, 400, 404, 500, 503)
- Specific response headers and body
- Network timeout / connection refused
- Slow responses (test your client's timeout configuration)
- Response sequences (first call fails, second succeeds — for retry testing)

---

### Slide 14 — Coverage Strategy: Achieving >85%
**Header:** What to Test and What to Skip

**Invest testing effort by layer (highest ROI first):**
| Layer | Test type | What to cover |
|-------|----------|---------------|
| Service business logic | Unit test (Mockito) | Every business rule, every exception path |
| Controller request mapping | `@WebMvcTest` | Status codes, validation, error responses |
| Custom repository queries | `@DataJpaTest` | Every `@Query` method, complex derived queries |
| HTTP client integrations | `@SpringBootTest` + WireMock | Success, error codes, timeouts |
| Full roundtrip | `@SpringBootTest` + Testcontainers | Happy path per major feature |

**What to exclude from coverage:**
```xml
<!-- pom.xml JaCoCo exclusions -->
<configuration>
    <excludes>
        <exclude>**/dto/**</exclude>           <!-- simple DTOs -->
        <exclude>**/config/**</exclude>        <!-- Spring configuration -->
        <exclude>**/*Application.class</exclude>
        <exclude>**/mapper/**</exclude>        <!-- generated MapStruct code -->
    </excludes>
</configuration>
```

**The quality reminder:**
```
85% coverage  +  weak assertions   = false confidence
60% coverage  +  strong assertions = real confidence
85% coverage  +  strong assertions = the actual target
```

Test the *what*, not the *how*. Test observable behavior — return values, exceptions, side effects — not internal implementation details. Tests that assert on private fields or mock every internal call are fragile and will break on every refactor.

---

### Slide 15 — Full Day Summary
**Header:** Day 28 Complete — Testing Reference

**Part 1 — Foundations:**
```
Test Pyramid       unit (many, fast) → integration (some) → E2E (few, slow)
TDD                🔴 write failing test → 🟢 min code to pass → 🔵 refactor
AAA                Arrange / Act (1 line) / Assert
JUnit 5            Platform + Jupiter + Vintage; spring-boot-starter-test bundles all
@Test              marks test method; no public needed
Assertions         assertEquals, assertThrows, assertAll / AssertJ fluent chains
@BeforeEach        reset mocks + service instance before every test
@DisplayName       human sentences in test reports
@Nested            group tests by scenario; own @BeforeEach per group
@ParameterizedTest @ValueSource / @CsvSource / @MethodSource — no copy-paste
JaCoCo             >85% line + branch; report at target/site/jacoco; coverage ≠ quality
```

**Part 2 — Mockito + Spring:**
```
Test doubles       mock / stub / spy / fake / dummy
@Mock + @InjectMocks  Mockito annotations (with @ExtendWith(MockitoExtension.class))
@MockBean          replace Spring bean in context (@WebMvcTest / @SpringBootTest only)
Stubbing           when(mock.method(args)).thenReturn() / thenThrow() / thenAnswer()
Void stubs         doNothing() / doThrow().when(mock).voidMethod()
Arg matchers       any(), anyLong(), eq(), argThat() — all-or-nothing rule
verify()           verify(mock, times(n)).method(args); never(); ArgumentCaptor
@Spy               partial mock — real code unless stubbed; use doReturn().when()
@WebMvcTest        controller slice — MockMvc for HTTP tests; @MockBean services
@DataJpaTest       JPA slice — H2 + real repositories; each test rolled back
@SpringBootTest    full context; RANDOM_PORT + TestRestTemplate; @ActiveProfiles
Testcontainers     @Container + @ServiceConnection → real PostgreSQL in tests
WireMock           local HTTP stub server — 200/500/timeout from external APIs
```

**Looking ahead — Day 29:** Spring Security and OWASP. The same `@WebMvcTest` tests you wrote today will add `@WithMockUser` to simulate authenticated requests. You'll secure the endpoints tested today with `SecurityFilterChain`, and walk through the OWASP Top 10 — the industry standard list of what every production application must defend against.
