# Day 28 Part 2 — Testing Spring Boot: Mockito, MockMvc, @DataJpaTest & Testcontainers
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — The Isolation Problem

Welcome back. In Part 1 we built the testing foundation — the test pyramid, TDD, JUnit 5, assertions. Now we need to talk about a practical problem that comes up immediately when you try to test a real Spring Boot service.

Your `BookService` depends on `BookRepository`, `AuthorRepository`, and maybe an `EmailService`. In a test, you don't want to spin up a database and an email server for every method call. You want to test the service in isolation — just the logic inside `BookService` itself, with complete control over what its dependencies return.

That's what mocking solves. And then after mocking, we'll look at what to do when you *do* want the real database — Spring test slices, Testcontainers, and WireMock for external APIs.

---

## [02:00–10:00] Slides 2–3 — Test Doubles and Mockito Setup

Let me give you vocabulary first, because these terms get used loosely and it helps to know them precisely.

A test double is any object you substitute for a real dependency in a test. There are five types. A **mock** is an object that records what methods were called on it, and returns values you configure in advance. A **stub** is a simplified object that returns preconfigured responses — in practice, a mock and a stub are often the same object since Mockito mocks do both. A **spy** wraps a real object and intercepts specific method calls while letting the rest run normally. A **fake** is a simplified but working implementation — like an in-memory `Map`-backed repository instead of a database-backed one. A **dummy** is an object passed as an argument but never actually used in the test — often just null.

In everyday Spring Boot testing, when people say "mock," they mean a Mockito mock that can both return stubbed values and record calls for verification. That's what we'll work with.

Mockito setup. Two patterns. The annotation-based pattern uses `@ExtendWith(MockitoExtension.class)` on the test class, then `@Mock` on each dependency field and `@InjectMocks` on the service field. Mockito reads these annotations and creates the mocks, then attempts to inject them into the service — it tries constructor injection first, then setter, then field injection. This is the most concise pattern.

The manual pattern uses `mock(BookRepository.class)` in a `@BeforeEach` method, then explicitly constructs the service with `new BookService(bookRepository)`. I actually prefer this second approach for constructor-injected services — you can see exactly how the class is assembled. If the service changes its constructor, the test breaks at construction time with a clear error, rather than silently.

One annotation to distinguish: `@MockBean`. This is NOT the same as `@Mock`. `@MockBean` is a Spring annotation that replaces a real Spring bean in the application context with a Mockito mock. You use it with `@WebMvcTest` and `@SpringBootTest` — contexts that start a real Spring container. For plain unit tests with no Spring context, use `@Mock`. Using `@MockBean` in a plain unit test would load the entire application context just to run a unit test — a mistake that makes tests ten times slower than they need to be.

---

## [10:00–20:00] Slides 4–5 — Stubbing and Argument Matchers

The core stubbing syntax: `when(mock.method(argument)).thenReturn(value)`. Read it left to right: "when the mock's `findById` is called with `1L`, then return this optional containing a book." The mock records this instruction and replays it whenever that call is made.

You can chain multiple return values with successive `.thenReturn()` calls. First call returns one value, second call returns another. This is useful when testing retry logic or when a method is called in a loop with expected changing state.

For exceptions: `.thenThrow(new ResourceNotFoundException("not found"))`. The mock will throw that exception when called.

For more complex cases, `.thenAnswer()` takes a lambda where you have access to the actual arguments passed to the mock. The canonical example: stub `bookRepository.save()` to set the ID on the book before returning it — simulating what the database does. You pull the `Book` argument out of the invocation, set its ID to 1, and return it. This lets your downstream assertions check that the returned DTO actually has an ID.

For `void` methods — methods that return nothing — the syntax flips: `doThrow(...).when(mock).deleteById(1L)` and `doNothing().when(emailService).sendWelcomeEmail(anyString())`. The `doNothing()` call is technically the default for void mocks, but making it explicit communicates intent.

Now argument matchers. The problem with exact-value stubs: they only match that specific call. `when(bookRepository.findById(1L))` only stubs the call with exactly `1L`. If the service converts the ID or wraps it somehow, the stub might not fire. Argument matchers give you flexibility.

`any(Book.class)` matches any non-null `Book`. `anyLong()` matches any long. `eq("isbn-value")` matches an exact value within a matchers context. `argThat(b -> b.getTitle().startsWith("Clean"))` lets you write a custom predicate.

There's one rule you absolutely cannot violate: all-or-nothing. If you use a matcher for any argument in a call, every argument must use a matcher. You can't mix an exact value with a matcher in the same call. The fix is `eq()` — it's the exact-value matcher. So instead of `when(service.search(anyString(), 10))`, you write `when(service.search(anyString(), eq(10)))`. JUnit will give you a clear error if you violate this, but better to understand why than to encounter the error cold.

---

## [20:00–30:00] Slides 6–7 — verify() and @Spy

Beyond what a method returns, you often want to verify it was called correctly. Two scenarios where this matters. First: `void` methods. If `emailService.sendWelcomeEmail()` returns nothing, there's no return value to assert on. The only way to verify it ran is to check the mock's call history. Second: ensuring something was NOT called. If the ISBN is already taken, `bookRepository.save()` should never run. You can't verify a negative with an assertion on a return value.

`verify(bookRepository).save(any(Book.class))` — verifies the mock was called exactly once with any `Book` argument. This is the default, equivalent to `verify(mock, times(1))`.

`verify(bookRepository, never()).save(any())` — verifies it was never called. This is one of the most useful verifications you'll write: test an early-exit path and confirm that the downstream operation didn't happen.

`verify(emailService, times(3)).sendNotification(anyString())` — exact count. Useful for batch operations.

`ArgumentCaptor` is the most powerful verification tool. Instead of just verifying the mock was called, you capture the actual argument that was passed in and assert on it. This is how you test what was actually saved to the repository — not just that save was called, but that the object passed to save had the right title, the right author reference, the right category. Create a `forClass(Book.class)` captor, pass `captor.capture()` as the matcher in `verify()`, then call `captor.getValue()` to get the book that was actually passed. Then assert on its fields.

`inOrder` verification: when you need to verify that method A was called before method B — say, the book must be saved before the welcome email is sent — create an `InOrder` object with both mocks and call `verify` on each in order.

`@Spy`. A spy is a partial mock — it wraps a real object and passes all calls through to the real implementation by default. You can then stub specific methods to override them. The typical use case: you have a class with five methods, and you want to test method A, which internally calls method B. You don't want the real method B to run in this test. Wrap the object in a spy and stub just method B.

The critical syntax difference for spies: always use `doReturn().when(spy).method()` — never `when(spy.method()).thenReturn()`. The difference: with the second form, the real method executes during the stubbing setup itself before the stub is registered. With spies wrapping methods that do real work — database calls, network calls — the second form would trigger the real call during test setup, which is almost never what you want.

---

## [30:00–40:00] Slides 8–9 — Spring Slice Tests and @WebMvcTest

Now we leave pure Mockito and enter Spring Boot testing. The question is: how much of the application do you need to start for a given test?

`@SpringBootTest` loads everything — the entire application context, all beans, full auto-configuration, a database connection. It's the most accurate test environment but the slowest. Tests that require `@SpringBootTest` take seconds to start.

Slice tests are the middle ground. Spring Boot ships with a set of slice annotations that each load only the components relevant to a specific layer. `@WebMvcTest` loads the controller layer — controllers, controller advice, filter chains, Jackson, Bean Validation — and nothing else. No services, no repositories, no database. Whatever services your controller needs, you provide as `@MockBean`. The result: controller tests that start in under a second and test exactly what a controller is responsible for — HTTP request parsing, validation, response mapping, status codes, and error handling.

`@WebMvcTest` takes the controller class as a parameter: `@WebMvcTest(BookController.class)`. This scopes the slice to only that controller. Inject `MockMvc` and `ObjectMapper` with `@Autowired` — Spring provides them automatically. Declare `@MockBean BookService bookService` — Spring replaces the real service bean with a Mockito mock.

The test for a GET endpoint. `mockMvc.perform(get("/api/books/1").contentType(APPLICATION_JSON))` builds and fires the request. The chain of `.andExpect()` calls are the assertions. `status().isOk()` checks the HTTP status is 200. `jsonPath("$.id").value(1)` uses JSONPath — a path expression language — to navigate the JSON response body and assert the `id` field equals 1. `jsonPath("$.title").value("Clean Code")` asserts the title.

The test for a POST endpoint adds a request body. `objectMapper.writeValueAsString(request)` serializes the `CreateBookRequest` Java object to JSON. Pass it with `.content()`. Assert the response status is 201 Created. Assert the Location header contains the path to the created resource. Assert the response body has the correct ID.

Validation tests are critical. Send an invalid request — blank title. Expect 400 Bad Request. Assert the error response body has the field name in the errors array. This test verifies your `@Valid` annotation on the controller method and your `@NotBlank` on the request class are both in place and your `@ControllerAdvice` produces the expected error format.

The 404 test: stub the service to throw `ResourceNotFoundException`. Run the GET request. Assert 404. Assert the error message is in the response body. This verifies your exception handler maps the exception to the right HTTP status.

---

## [40:00–50:00] Slides 10–11 — @DataJpaTest and @SpringBootTest

`@DataJpaTest`. This slice loads only your `@Entity` classes and `@Repository` interfaces, configures Hibernate, and sets up an in-memory H2 database. No controllers, no services, no full application context. And critically: each test method runs in a transaction that is rolled back after the test completes. You can insert data in one test, and the next test starts with a completely empty database.

Inject your repository with `@Autowired`. Also inject `TestEntityManager` — Spring provides this in `@DataJpaTest` contexts as a helper for inserting test data directly into the persistence context without going through the repository layer. `entityManager.persist(new Book(...))` followed by `entityManager.flush()` forces the insert to actually hit the database before the query runs.

Then call the actual repository method — `bookRepository.findByCategory("TECHNICAL")` — and assert on the result. This test is verifying the SQL or JPQL that Spring Data generated is actually correct.

Use `@DataJpaTest` for every custom `@Query` method you wrote in Day 27. You wrote the query; write a test that proves it returns the right data. Cover the happy path, the empty result case, and any edge cases in the query logic.

`@SpringBootTest` is for when you need the whole picture. The common pattern: annotate with `@SpringBootTest(webEnvironment = RANDOM_PORT)` to start a real embedded Tomcat on a random port. Annotate with `@ActiveProfiles("test")` to load `application-test.properties` which points to H2 instead of PostgreSQL. Inject `TestRestTemplate` — it's a test-aware `RestTemplate` that makes real HTTP calls to the local server.

The integration test: call `POST /api/books`, capture the created resource's ID from the response, then call `GET /api/books/{id}`, verify the response. This test exercises the full stack — the controller parsing the request, the service applying business logic, the repository persisting to the database — in one flow.

Within `@SpringBootTest` you can still use `@MockBean` to replace specific beans. If your service calls an external payment API, mock that client bean. Everything else — the controller, the service, the repository, the database — runs for real.

---

## [50:00–58:00] Slides 12–13 — Testcontainers and WireMock

Testcontainers addresses the one weakness of `@DataJpaTest` and `@SpringBootTest` with H2: H2 is not PostgreSQL. Queries that work on H2 may fail on PostgreSQL because the SQL dialects differ. JSON column operations, array types, specific aggregate functions, case-sensitive identifier handling — all of these can behave differently. Finding these differences in a CI pipeline, or worse in production, is painful.

Testcontainers spins up a real PostgreSQL Docker container, runs your tests against it, then stops it. All automated. The annotations: `@Testcontainers` on the class, `@Container` on a static field holding a `PostgreSQLContainer<>`. Make the field `static` so one container is shared across all test methods in the class — starting a container takes a few seconds, so you don't want a new one per test.

Spring Boot 3.1 added `@ServiceConnection`. When you put this alongside `@Container`, Spring Boot automatically reads the container's hostname, port, database name, username, and password, and configures your `spring.datasource.*` properties accordingly. No manual property overrides needed. The datasource just points to the live container.

The test itself looks identical to a `@DataJpaTest` test — insert data, call the repository, assert. The difference is what's running underneath: a real PostgreSQL process in a Docker container instead of H2.

Requirement: Docker must be running. In local development, you need Docker Desktop. In GitHub Actions and most CI systems, a Docker runner is available. Some CI environments require explicit Docker service configuration.

WireMock. Many Spring Boot services call external APIs — payment processors, shipping calculators, weather data, third-party book metadata. In tests, you don't want real calls to these services. They're slow. They require credentials. They might fail. And you can't test error scenarios — you can't ask a real API to return a 503 just for your test.

WireMock starts a local HTTP server on your machine. You configure it to respond to specific requests with whatever response you need. Your service calls WireMock thinking it's the real API. You control the response completely.

`@EnableWireMock` on the test class, `@InjectWireMock` on a `WireMockServer` field. Then `wireMockServer.stubFor(get(urlEqualTo("/pricing/PREMIUM")).willReturn(aResponse().withStatus(200).withHeader(...).withBody(...)))`.

The error scenario test: stub the same URL to return a 503. Your service should handle that and throw a `ServiceUnavailableException`. Verify the exception is thrown. This test proves your error handling code actually runs — you couldn't write this test against a real API.

The timeout test: `withFixedDelay(5000)` — WireMock adds a five second delay. Your HTTP client has a configured timeout of two seconds. The test verifies that your client's timeout fires and throws the right exception. This confirms your timeout configuration is actually respected at runtime.

---

## [58:00–60:00] Slides 14–15 — Coverage Strategy and Summary

Where to put your testing effort. Service business logic gets unit tests with Mockito — every business rule, every exception path. Controllers get `@WebMvcTest` — every status code variant, every validation rule, every error response format. Custom repository queries get `@DataJpaTest` — every `@Query` method you wrote on Day 27. External HTTP clients get `@SpringBootTest` plus WireMock — success path, error codes, timeouts. One or two full roundtrips get `@SpringBootTest` plus Testcontainers — the happy path for each major feature.

Exclude generated code from JaCoCo: DTOs, configuration classes, MapStruct mappers, the main method. These don't need testing and inflating your coverage number with untestable code masks real gaps.

The quality reminder: eighty-five percent with weak assertions is false confidence. Sixty percent with strong assertions covering all your business logic is genuine confidence. Target both high coverage and strong assertions — they're not in conflict.

Test the observable behavior, not the implementation. Assert on return values, exceptions, and interactions with collaborators. Avoid asserting on private internal state. Tests that know too much about how a method works break every time you refactor, which defeats the purpose.

That's Day 28 complete. You now have the full testing toolkit for a professional Spring Boot application. Tomorrow, Day 29: Spring Security and OWASP. The same `@WebMvcTest` tests you wrote today will gain `@WithMockUser` to simulate authenticated users. You'll secure the endpoints you built this week, walk through the OWASP Top 10, and implement Spring Security's `SecurityFilterChain`.
