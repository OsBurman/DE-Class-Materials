# Day 28 — Testing: Part 2 Walkthrough Script
## Mockito, Spring Boot Testing, Testcontainers, WireMock

**Total time:** ~90 minutes  
**Files covered:** `01-mockito-unit-tests.java`, `02-spring-boot-integration-tests.java`, `03-testcontainers-and-wiremock.java`

---

## Segment 1 — Opening & Recap (5 min)

> "Welcome back. Yesterday in Part 1 we covered the theory of testing — the pyramid, TDD, FIRST properties — and we got hands-on with JUnit 5: lifecycle annotations, assertions, parameterized tests, and @Nested. All of those were *pure unit tests* — just classes, no framework, no database, no Spring."

> "Today we go a level up. We're going to answer three questions that come up in every real Spring Boot project:
> - How do I test a class that depends on a database, without touching a real database?
> - How do I test a Spring controller without spinning up an entire application?
> - How do I test something that calls an external API I don't control?"

> "Those three questions have three specific answers: Mockito, Spring Boot test slices, and Testcontainers/WireMock. Let's go."

---

## Segment 2 — Mockito: Why Mock? (10 min)

> "Open `01-mockito-unit-tests.java`. Read the comment block at the top with me."

**Point to the mock/stub/spy distinction comment block.**

> "First: the vocabulary.
> - A **mock** is a fake object that records calls. By default it does nothing and returns null/zero/false.
> - A **stub** is a mock you've programmed to return a specific value for a specific call.
> - A **spy** wraps a *real* object. Real methods run unless you override specific ones."

> "When would you use each? Mock + stub is 90% of your tests. Spy is for when you have a class you can't easily mock — maybe a utility class with complex state, and you only want to override one specific method."

> "Now look at the class-level annotations. `@ExtendWith(MockitoExtension.class)` — this activates Mockito in JUnit 5. Without this, `@Mock` and `@InjectMocks` do nothing."

> "Here's the most important annotation pair you'll use every day:"

**Point to `@Mock BookRepository` and `@InjectMocks BookService`.**

> "`@Mock` creates a fake `BookRepository` — no database, no SQL, no connection pool. Just a fake object that records calls.
>
> `@InjectMocks` creates a real `BookService` but automatically injects the `@Mock` objects into it. Mockito looks at the service's constructor — if it takes a `BookRepository`, it injects the mock. Your service code runs for real; only its dependencies are fake."

---

## Segment 3 — Stubbing with `when().thenReturn()` (12 min)

> "Look at `shouldReturnBookWhenFound`. This is the standard pattern."

**Walk through line by line:**

> "`when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook))` — this says: *when* someone calls `findById(1L)` on our fake repository, *return* an Optional containing our test book. The actual database is never touched.
>
> Then we call `bookService.getBookById(1L)` — this is real service code executing. It calls `findById(1L)` on the mock, gets the Optional back, maps it, returns the book.
>
> Then we assert on the result."

> "Now look at `shouldThrowExceptionWhenBookNotFound`. Same pattern, but we return `Optional.empty()`. The service's `.orElseThrow()` triggers. We verify the correct exception is thrown."

> "Three more stubbing patterns worth knowing:"

**Point to `thenThrow`, `thenAnswer`:**

> "`thenThrow` — simulate a database failure. The call to `findAll()` throws `RuntimeException`. Your service should catch it and wrap it in a friendly exception.
>
> `thenAnswer` — the most flexible. The lambda receives the actual arguments that were passed to the mock. Here we use it to return the same `Book` object that was passed to `save()` — because in production, `save()` returns the entity with its database-generated ID.
>
> `when().thenReturn()` is static, `thenAnswer` is dynamic."

---

## Segment 4 — Verification and Argument Matchers (10 min)

> "Move down to the verification section. After running your code, you want to confirm your service used its dependencies correctly — not just that it returned the right value."

> "`verify(bookRepository).save(book)` — confirms `save()` was called exactly once with exactly this book object. If `save()` wasn't called at all, or was called with the wrong argument, this fails."

> "The times/never/atLeast variants:"

**Walk through quickly:**
- `times(1)` — exactly once (default if no modifier)
- `never()` — confirms a method was NEVER called — great for security checks
- `atLeastOnce()` — called one or more times
- `atMost(2)` — called at most twice

> "When do you use `never()`? Classic example: `deleteById` should never be called during an *update* operation. If your service has a bug where it deletes before updating, `verify(repo, never()).deleteById(anyLong())` catches it."

> "Now argument matchers — look at the matchers section."

> "`any(Book.class)` — matches any Book argument, regardless of fields.
> `eq(\"Fiction\")` — exact String match.
> `anyString()`, `anyLong()` — type-based matchers.
> `contains(\"clean\")` — String contains match.
>
> **Critical rule:** If you use any matcher in a verify or stub, ALL arguments must use matchers. You can't mix a literal and a matcher. If you need an exact value alongside other matchers, use `eq(\"literal\")` instead of writing the literal bare."

> "ArgumentCaptor — this is powerful. Look at the test."

**Point to `ArgumentCaptor` section:**

> "We capture the actual `Book` object that was passed to `save()`. Then we assert on its fields. This is the right way to test a service that *transforms* an object before saving it — you want to confirm the transformation was correct, not just that `save()` was called."

---

## Segment 5 — @WebMvcTest + MockMvc (15 min)

> "Switch to `02-spring-boot-integration-tests.java`. Section 1."

> "Now we're moving to the web layer. `@WebMvcTest` loads *only* the controller. No service bean, no repository, no database. Just the HTTP layer."

> "Two things are auto-configured: `MockMvc` (auto-wired for you) and Jackson's `ObjectMapper`. For the service, we use `@MockBean` — this is different from `@Mock`."

**Important distinction:**
> "**`@Mock`** (Mockito) works only with `@ExtendWith(MockitoExtension.class)`. It doesn't know about Spring.
>
> **`@MockBean`** (Spring Boot) creates a Mockito mock AND registers it as a bean in the Spring test context. Use `@MockBean` whenever you're testing with Spring slices like `@WebMvcTest`."

> "Now let's read a MockMvc test together — `shouldReturnAllBooks`."

**Walk through step by step:**

> "`when(bookService.getAllBooks()).thenReturn(List.of(testBook))` — same Mockito stubbing we've seen.
>
> `mockMvc.perform(get(\"/api/v1/books\").contentType(APPLICATION_JSON))` — this simulates an HTTP GET request to that URL. No real HTTP. No server socket.
>
> `.andDo(print())` — prints the full request and response to the console. Use this when debugging a failing test.
>
> `.andExpect(status().isOk())` — asserts HTTP 200.
> `.andExpect(content().contentType(MediaType.APPLICATION_JSON))` — response is JSON.
> `.andExpect(jsonPath(\"$\", hasSize(1)))` — JSON array has exactly 1 element.
> `.andExpect(jsonPath(\"$[0].title\", is(\"Clean Code\")))` — first element's title field."

> "JSONPath is a JSON query language. `$` means the root. `$[0].title` means first element's title. `$.id` means the id field at the root. You'll use this constantly."

**Point to 404 test:**

> "The 404 test. Service throws `BookNotFoundException`. The controller's `@ExceptionHandler` (or `@ControllerAdvice`) catches it and returns 404. We verify both the status code AND that the error response body contains the book ID."

**Point to POST test:**

> "The POST test. We use `objectMapper.writeValueAsString(dto)` to serialize our DTO to JSON and pass it as the request body. `.andExpect(status().isCreated())` checks for HTTP 201.
>
> Notice the validation test — we send JSON missing the `title` field. Spring's `@Valid` should reject it with 400 before it even reaches the service layer. The service mock is never called. `@Valid` tests are critical — don't skip them."

---

## Segment 6 — @DataJpaTest (10 min)

> "Section 2 — `@DataJpaTest`. This is the repository layer slice."

> "`@DataJpaTest` auto-configures H2 in-memory database, your JPA repositories, and `TestEntityManager`. No controllers, no services, no HTTP."

> "**TestEntityManager** — why do we use this instead of the repository to set up test data? Because we're *testing* the repository. If we used `bookRepository.save()` to set up data AND to test the find methods, we'd be testing two things at once. `TestEntityManager` is a raw entity manager — it bypasses the repository under test."

> "Look at the tests. They're straightforward: insert data with TestEntityManager, then call a repository method, then assert the result."

> "`.flush()` after `entityManager.persist()` — this forces H2 to write the row immediately. Without flush, the data lives in the JPA first-level cache and might not be visible to a native query in the same transaction."

> "**What's really being tested here:** the correctness of your JPQL or derived query method names. `findByGenre` → does it generate `WHERE genre = ?` correctly? `findByTitleContainingIgnoreCase` → does it generate `WHERE LOWER(title) LIKE ?` correctly? `countByGenre` → does it return a long? These are things you CAN'T verify just by looking at the method name."

> "Transactions: each `@DataJpaTest` method runs in a transaction that is rolled back after the test. Your `@BeforeEach` inserts data, the test runs, then the rollback wipes it. Clean slate for every test."

---

## Segment 7 — @SpringBootTest (8 min)

> "Section 3 — `@SpringBootTest`. This is the big one."

> "Notice the annotation: `@SpringBootTest(webEnvironment = RANDOM_PORT)`. This starts your entire application — all beans, all configurations, the JPA layer, the web layer — on a random available port. This is as close to production as you can get in a test."

> "The tradeoff: speed. A `@WebMvcTest` starts in under a second. A `@SpringBootTest` with a real database can take 10–30 seconds. Use these sparingly."

> "We inject `TestRestTemplate` — this makes real HTTP calls to our running test server. Unlike MockMvc, the request actually travels through the network stack, authentication filters, everything."

> "`@ActiveProfiles(\"test\")` — this activates `application-test.properties`. Point at the bottom of the file."

**Read the comment block for application-test.properties:**

> "Your test profile should: point to H2 or your test database, suppress noisy logging, turn off anything that would slow tests or require external services. Keep this file lean."

> "**When to use @SpringBootTest:** One or two smoke tests per module. Verify the app starts, verify a full round-trip works. Don't write 50 `@SpringBootTest` tests — you'll spend more time waiting for startup than writing code."

---

## Segment 8 — Testcontainers (12 min)

> "Switch to `03-testcontainers-and-wiremock.java`. Let's talk about the problem first."

> "H2 is convenient, but it's not PostgreSQL. If your application uses PostgreSQL features — `ILIKE`, `ON CONFLICT DO UPDATE`, JSONB columns, custom sequences, window functions — H2 will either silently behave differently or outright fail. Your tests pass in CI and blow up in production."

> "Testcontainers fixes this. It spins up a **real** Docker container running the **exact same** PostgreSQL version as your production database. Tests run against it. Then the container is destroyed."

> "Requirements: Docker must be running. Most CI/CD pipelines — GitHub Actions, GitLab CI, Jenkins — already have Docker available."

**Walk through the annotations:**

> "`@Testcontainers` — the JUnit 5 extension that manages container lifecycle.
>
> `@Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(\"postgres:15-alpine\")` — the container definition. `static` means ONE container for the entire class, not one per test. Starting a database container takes 5–10 seconds. You don't want to do that for every test method."

> "`.withDatabaseName()`, `.withUsername()`, `.withPassword()` — optional customization. The defaults work fine."

**Point to `@DynamicPropertySource`:**

> "This is the key piece. When the container starts, Docker assigns a random port. We can't hardcode `spring.datasource.url=jdbc:postgresql://localhost:5432/...` in properties because the port is unknown until runtime.
>
> `@DynamicPropertySource` runs AFTER the container starts but BEFORE the Spring context initializes. It injects the container's actual JDBC URL, username, and password into the Spring environment. This is how your app knows to connect to the container."

> "Look at the `shouldSupportILikeSearch` test. This calls `searchByTitleNative(\"clean\")`, which uses PostgreSQL's `ILIKE` operator. If you ran this test with H2, it would throw an error — H2 doesn't have `ILIKE`. With Testcontainers, it works perfectly because we're talking to real PostgreSQL."

> "The `shouldRaiseConstraintViolationOnDuplicateIsbn` test. We save two books with the same ISBN, then call `.flush()`. The `flush()` is critical — without it, JPA batches the operations and the constraint check might not happen until the transaction commits. With `flush()`, we force the SQL `INSERT` immediately and get the exception in this test method."

---

## Segment 9 — WireMock (12 min)

> "Section 2 — WireMock. Different problem: external HTTP services."

> "Imagine your `BookService` calls an ISBN lookup API — an external website that returns book metadata given an ISBN. You don't want to call that real API in tests. It's slow, it might be down, it costs money, it might rate-limit you."

> "WireMock starts a local HTTP server on your machine. You tell it: *when you receive GET /api/isbn/..., respond with this JSON*. Your client code calls WireMock instead of the real API."

**Walk through setup:**

> "`@RegisterExtension static WireMockExtension wireMockServer = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build()` — starts WireMock on a random port before any tests run.
>
> `@DynamicPropertySource` — just like Testcontainers, we inject WireMock's URL into Spring properties so our `IsbnLookupClient` points to WireMock instead of the real API."

**Walk through stubbing:**

> "`wireMockServer.stubFor(get(urlEqualTo(\"/api/isbn/978-...\")).willReturn(aResponse().withStatus(200).withHeader(...).withBody(...)))` — three pieces: the request matcher, the response builder. The static imports from WireMock make this very readable."

> "The failure scenarios. This is where WireMock really shines."

**Point to each failure test:**

> "503 Service Unavailable — does your client wrap 5xx errors into a domain exception? If your error handling code has never been triggered in tests, you don't know if it works.
>
> Timeout — `withFixedDelay(5000)` makes WireMock wait 5 seconds before responding. If your RestTemplate timeout is 3 seconds, it should throw. This verifies your timeout configuration is correct.
>
> Malformed JSON — does your Jackson deserialization error handling work? What does your client return when the API sends garbage? Test it."

**Point to verification:**

> "`wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(\"/api/isbn/...\")))` — confirms the HTTP call was made. Same idea as Mockito's `verify()`, but for HTTP requests.
>
> You can also verify headers — `.withHeader(\"Accept\", equalTo(\"application/json\"))`. This catches bugs where your client forgets to set the right Accept header."

**Point to scenario/retry test:**

> "The retry scenario. WireMock has a state machine concept. First call → state is `Started` → respond with 503 → advance to state `Retry`. Second call → state is `Retry` → respond with 200. If your client has Spring Retry or Resilience4J configured, this test verifies the retry logic end-to-end."

---

## Segment 10 — Coverage Targets & JaCoCo (6 min)

> "Last section — coverage. We talked about JaCoCo in Part 1. Let me be concrete about strategy."

**Read through the coverage comment block:**

> "85% line coverage, 80% branch coverage, 90% method coverage. These are reasonable thresholds for a production Spring Boot service.
>
> What gets you there fast: service layer tests (where your business logic lives), controller tests (every endpoint + every error path), and repository query tests."

> "What NOT to test obsessively: Lombok-generated getters/setters (they work — Lombok has its own tests), main method in `BookstoreApplication`, simple DTOs with no logic.
>
> JaCoCo lets you `<exclude>` classes from the measurement. Use it for entities and DTOs that are just data containers."

> "Run `mvn clean verify` to execute tests AND check coverage. If you're below the threshold, Maven fails the build — which means it fails in CI too. Coverage gates prevent gradual coverage erosion."

> "Run `mvn jacoco:report` and open `target/site/jacoco/index.html` in a browser. You'll see a color-coded view of every class. Red lines = never executed. Yellow = partially covered (one branch taken, not both). Green = fully covered. This is how you find gaps."

---

## Segment 11 — Wrap-Up & Interview Questions (5 min)

> "Let's recap Part 2:
> - **Mockito** — mock dependencies, stub return values, verify interactions, capture arguments
> - **@WebMvcTest + MockMvc** — test controllers without a real server or database
> - **@MockBean** — register Mockito mocks inside the Spring test context
> - **@DataJpaTest** — test repositories with H2 and TestEntityManager
> - **@SpringBootTest** — full context, use sparingly for smoke tests
> - **Testcontainers** — real Docker databases, `@DynamicPropertySource` for dynamic URLs
> - **WireMock** — stub external HTTP calls, simulate failures, verify outbound requests"

**Interview questions — ask the class:**

> 1. "What's the difference between `@Mock` and `@MockBean`?"  
>    *(Mock = Mockito only, MockBean = Spring context-aware mock)*

> 2. "When would you use `@WebMvcTest` vs `@SpringBootTest`?"  
>    *(WebMvcTest = fast, controller only; SpringBootTest = full context, integration smoke tests)*

> 3. "Why might your tests pass with `@DataJpaTest` (H2) but fail in production (PostgreSQL)?"  
>    *(H2 lacks PostgreSQL-specific SQL features)*

> 4. "What does `@DynamicPropertySource` do?"  
>    *(Injects runtime-determined values like Docker container ports into the Spring environment before context loads)*

> 5. "What does `verify(repo, never()).deleteById(anyLong())` test?"  
>    *(That the delete method was NEVER called — useful for asserting safety in non-destructive operations)*

> 6. "How does WireMock let you test retry logic?"  
>    *(Using scenarios with state machines — first call returns 503, second returns 200)*

> "Great work today. Combined with Part 1, you now have a complete testing toolkit for professional Spring Boot development. The next session is Spring Security — and yes, you'll be writing tests for secured endpoints."

---

## Timing Reference

| Segment | Topic | Time |
|---------|-------|------|
| 1 | Opening & recap | 5 min |
| 2 | Mockito: why mock, @Mock/@InjectMocks | 10 min |
| 3 | Stubbing: thenReturn/thenThrow/thenAnswer | 12 min |
| 4 | Verification + argument matchers | 10 min |
| 5 | @WebMvcTest + MockMvc | 15 min |
| 6 | @DataJpaTest + TestEntityManager | 10 min |
| 7 | @SpringBootTest | 8 min |
| 8 | Testcontainers + DynamicPropertySource | 12 min |
| 9 | WireMock: stubs, failures, verification | 12 min |
| 10 | Coverage targets + JaCoCo | 6 min |
| 11 | Wrap-up + interview questions | 5 min |
| **Total** | | **~105 min** |

---

## Quick Reference Card

```text
MOCKITO CHEAT SHEET
────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)   → activate Mockito
@Mock                                 → create fake object
@InjectMocks                          → create real object, inject mocks
@Spy                                  → wrap real object, override selectively
@MockBean                             → Spring-aware mock (use with slices)

Stubbing:
  when(mock.method(arg)).thenReturn(value)
  when(mock.method(arg)).thenThrow(exception)
  when(mock.method(arg)).thenAnswer(inv -> inv.getArgument(0))
  doNothing().when(mock).voidMethod()

Verification:
  verify(mock).method(arg)
  verify(mock, times(n)).method(arg)
  verify(mock, never()).method(arg)
  verify(mock, atLeastOnce()).method(arg)

Matchers:
  any(Type.class)  anyString()  anyLong()  eq("value")  contains("str")

SPRING TEST SLICE CHEAT SHEET
────────────────────────────────────────────────────────
@WebMvcTest(MyController.class)  → controller + MockMvc only
  @MockBean MyService service    → mock service for the controller

@DataJpaTest                     → repositories + H2 + EntityManager
  @Autowired TestEntityManager   → raw insert test data

@SpringBootTest(webEnvironment=RANDOM_PORT)  → full app + TestRestTemplate
  @ActiveProfiles("test")                    → use application-test.properties

TESTCONTAINERS CHEAT SHEET
────────────────────────────────────────────────────────
@Testcontainers
@Container static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:15");
@DynamicPropertySource
static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", db::getJdbcUrl);
}

WIREMOCK CHEAT SHEET
────────────────────────────────────────────────────────
@RegisterExtension
static WireMockExtension wm = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort()).build();

wm.stubFor(get(urlEqualTo("/path"))
    .willReturn(aResponse().withStatus(200).withBody("...")));

wm.verify(exactly(1), getRequestedFor(urlEqualTo("/path")));
```
