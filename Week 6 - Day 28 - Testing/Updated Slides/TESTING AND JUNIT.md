Testing Lesson — Full Hour Script & Slide Guide
Audience: New students with some prior exposure to testing basics
Duration: ~60–70 minutes (now 28 slides with splits)
Format: Each section includes your speaking script followed by the slide content to display

SLIDE 1 — Title Slide
SLIDE CONTENT:
Software Testing: Building Confidence in Your Code
[Your Name] | [Course Name]
Today's Topics: TDD · JUnit 5 · Mockito · Spring Boot Testing · Coverage · Testcontainers · WireMock
SCRIPT (1 min):
Good [morning/afternoon], everyone. Today is one of the most practical sessions we'll have in this entire course — because everything we cover today you will use on every real project you ever work on professionally.
We've touched on some of these ideas in earlier lessons. Today we're going to pull it all together into a coherent picture. By the end of this hour you'll know why we test, how we write good tests at every level of an application, and what tools professionals use on the job. Let's get into it.

SECTION 1: WHY TESTING MATTERS & TDD PHILOSOPHY
⏱ ~7 minutes

SLIDE 2 — The Cost of Not Testing
SLIDE CONTENT:
Why Do We Test?

- Bugs found in production cost 10–100x more to fix than bugs found during development
- Tests are executable documentation — they describe what code is supposed to do
- Tests give you the confidence to change code without fear
- No tests = "works on my machine" as a permanent strategy

"Code without tests is legacy code the moment it's written." — Michael Feathers
SCRIPT:
Let me ask you — has anyone ever pushed code that broke something else? [Pause for response.] Right, everyone has. That's not a character flaw, that's just what happens when you're working in a large codebase without a safety net.
Testing is that safety net. But I want to reframe how you think about it. Most beginners think of testing as something you do after you write code to check if it works. Professional developers think of testing as something that drives how they write code. That's a fundamental shift in mindset, and it's what Test Driven Development is all about.

SLIDE 3 — TDD: Test Driven Development
SLIDE CONTENT:
Test Driven Development (TDD)

Core Principle: Write the test BEFORE you write the production code.

Why?
- Forces you to think about the interface before the implementation
- Every line of production code you write exists to make a test pass
- You naturally get high test coverage — it's built in
- Leads to cleaner, more modular design (hard-to-test code = bad design)

TDD is a design discipline, not just a testing technique.
SCRIPT:
TDD flips the script. Instead of "write code, then test it," you write the test first, watch it fail, then write just enough code to make it pass.
This sounds backwards at first. Students always say to me, "How can I write a test for code that doesn't exist yet?" But that discomfort is actually the point — it forces you to design before you build. You have to think: what should this function accept? What should it return? What are the edge cases? You answer all of those questions in the test, and then your implementation simply satisfies those answers.
The biggest side benefit: if something is hard to test, that's a signal your design is bad. TDD gives you that feedback immediately.

SLIDE 4 — The Red-Green-Refactor Cycle
SLIDE CONTENT:
Red → Green → Refactor

🔴 RED:    Write a failing test. It describes behavior that doesn't exist yet.
           The test must fail for the RIGHT reason.

🟢 GREEN:  Write the MINIMUM code to make the test pass.
           Don't over-engineer. Just make it green.

🔵 REFACTOR: Clean up the code — eliminate duplication, improve naming,
             improve structure. The tests protect you while you refactor.

Then repeat. One small cycle at a time.
SCRIPT:
This is the heartbeat of TDD — Red, Green, Refactor. Let me walk through each step.
Red: You write a test. It fails. Good. A test that fails before you write the code proves the test is actually testing something real. If you write a test and it passes immediately without any implementation, your test is probably wrong.
Green: Write the minimum code to pass the test. I want to emphasize minimum. Don't add features that don't have tests yet. Don't anticipate. Just make the red test go green.
Refactor: Now that you have a passing test as a safety net, clean up your code. Improve variable names. Extract a helper method. Remove duplication. If your refactoring breaks anything, the test catches it immediately.
Then you start the cycle again. TDD is not one big loop — it's many small, fast loops. Ideally each cycle takes just a few minutes.

SECTION 2: UNIT TESTING WITH JUNIT 5
⏱ ~12 minutes

SLIDE 5 — What Is a Unit Test?
SLIDE CONTENT:
Unit Testing Fundamentals

A unit test verifies a single "unit" of behavior in isolation.

Characteristics of a good unit test (F.I.R.S.T.):
  Fast        — runs in milliseconds
  Isolated    — no dependency on DB, network, filesystem
  Repeatable  — same result every time, any environment
  Self-validating — passes or fails, no manual inspection
  Timely      — written at the same time as the production code

Unit tests are the foundation of the Testing Pyramid.
Why they matter: fast feedback, pinpoint failures, safe refactoring.
SCRIPT:
A unit test tests one thing — one behavior, one method, one responsibility — in complete isolation from everything else. No database. No network. No file system. Just the code.
The acronym F.I.R.S.T. is a great checklist for evaluating your unit tests. Fast means milliseconds, not seconds. If your unit test takes 2 seconds, something is wrong — probably you're hitting a real database or making a real HTTP call. Isolated means you've replaced all external dependencies with fakes. Repeatable means if you run it 1,000 times you get the same result 1,000 times.
The testing pyramid — which I'll show you visually — says you should have lots of unit tests at the base, fewer integration tests in the middle, and very few end-to-end tests at the top. Unit tests are cheap. Write many of them.

SLIDE 6 — JUnit 5 Architecture
SLIDE CONTENT:
JUnit 5 = Three Components

┌─────────────────────────────────────┐
│         JUnit Platform              │  ← Foundation: launches test frameworks on the JVM
│  (junit-platform-launcher, engine)  │     Test discovery, reporting, IDE/build tool integration
├─────────────────────────────────────┤
│         JUnit Jupiter               │  ← What YOU use: new JUnit 5 API
│  (junit-jupiter-api + engine)       │     @Test, @BeforeEach, assertions, extensions
├─────────────────────────────────────┤
│         JUnit Vintage               │  ← Backward compatibility
│  (junit-vintage-engine)             │     Runs JUnit 3 and JUnit 4 tests on the JUnit 5 platform
└─────────────────────────────────────┘

Most of your work happens in Jupiter. Platform is infrastructure.
Vintage exists so legacy projects can migrate gradually.
SCRIPT:
JUnit 5 is actually three separate modules, and understanding this helps when you see build errors or dependency issues.
The Platform is the engine room. It's what your IDE, Maven, and Gradle use to discover and run tests. You don't directly interact with it much.
Jupiter is the star of the show — it's the new JUnit 5 programming API. This is where all the annotations you'll use live: @Test, @BeforeEach, the assertion library, everything.
Vintage is for backward compatibility. If you're joining a project that has JUnit 4 tests, Vintage lets those old tests run on the new JUnit 5 platform without rewriting them. It's a migration bridge.
When you add JUnit to a project, you're almost always adding junit-jupiter as your main dependency.

SLIDE 7 — Writing Your First Test Class
SLIDE CONTENT:
java// Production code
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }
}

// Test class
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();   // fresh instance before each test
    }

    @Test
    @DisplayName("Adding 2 + 3 should return 5")
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    @DisplayName("Dividing by zero should throw ArithmeticException")
    void testDivideByZero() {
        assertThrows(ArithmeticException.class,
            () -> calculator.divide(10, 0));
    }
}
```

**SCRIPT:**
Let's look at a real test class. A few things to notice immediately.

First, the class name is `CalculatorTest` — by convention, test classes are named after the class they test with a `Test` suffix. Most build tools automatically discover classes matching this pattern.

`@DisplayName` on the class and on each test method gives you human-readable names in your test reports. When a test fails, you want the report to say *"Adding 2 + 3 should return 5 — FAILED"*, not *"testAdd — FAILED"*. Always use `@DisplayName`.

`@BeforeEach` runs before *every single test method* in this class. Here I'm creating a fresh `Calculator` instance. This is critical — tests must not share state. If test A modifies the calculator and test B runs after it, B might fail or pass for the wrong reasons.

The `@Test` annotation marks a method as a test. JUnit discovers it and runs it. Notice the test method is `void` — it communicates pass/fail through assertions, not return values.

`assertEquals(expected, actual)` — the convention is expected value first, actual second. This matters for meaningful error messages. `assertThrows` verifies an exception was thrown — it takes the exception class and a lambda that should throw it.

---

### SLIDE 8 — Test Lifecycle Annotations

**SLIDE CONTENT:**
```
Test Lifecycle

@BeforeAll   → runs ONCE before any test in the class (must be static)
               Use for: expensive setup like starting a server

@BeforeEach  → runs before EVERY test method
               Use for: creating fresh objects, resetting state

@AfterEach   → runs after EVERY test method
               Use for: cleanup, closing resources

@AfterAll    → runs ONCE after all tests in the class (must be static)
               Use for: tearing down shared resources

Order of execution for a class with 2 tests:
  @BeforeAll → [@BeforeEach → test1 → @AfterEach] → [@BeforeEach → test2 → @AfterEach] → @AfterAll
SCRIPT:
These four annotations control the lifecycle of your test class. Think of them as hooks into the test runner.
The key distinction is @BeforeAll and @AfterAll run exactly once for the entire class — they're for expensive setup like creating a database connection. They must be static because they run before any instance of the class exists.
@BeforeEach and @AfterEach run around every single test. @BeforeEach is where you do your setup — create objects, reset mock state. @AfterEach is where you clean up.
The execution order I've shown at the bottom is important to memorize. Draw a picture of it if you need to. Tests should be completely independent — if your test suite is failing when run in one order but passing in another order, you have a lifecycle problem.

SLIDE 9A — Core Assertions
SLIDE CONTENT:
java// Equality and truth
assertEquals(expected, actual)
assertNotEquals(unexpected, actual)
assertTrue(condition)
assertFalse(condition)

// Null checks
assertNull(object)
assertNotNull(object)

// Exceptions
assertThrows(Exception.class, () -> codeToRun())

// Collections and arrays
assertArrayEquals(expectedArray, actualArray)
assertIterableEquals(expectedList, actualList)

// Custom failure message (last argument — always add when reason isn't obvious)
assertEquals(5, result, "Addition result was wrong");
SCRIPT:
These are your bread-and-butter assertions. You already know assertEquals, but let me walk through the ones you'll reach for constantly.
assertThrows is essential for testing error handling — pass it the exception class and a lambda with the code that should throw. It returns the exception object so you can also assert on its message if needed.
assertArrayEquals and assertIterableEquals exist because a regular assertEquals on two different list objects would compare references, not contents. These compare element by element.
Always add a custom failure message as the last argument when the failure reason wouldn't be obvious — especially inside loops or parameterized tests. "Expected 5 but was 4" is unhelpful at midnight during a CI failure. "Shopping cart total after discount was wrong" tells you exactly where to look.

SLIDE 9B — assertAll: Test Everything, Report Everything
SLIDE CONTENT:
java// WITHOUT assertAll: if getName() fails, getAge() and getEmail() never run
assertEquals("John", user.getName());
assertEquals(25, user.getAge());
assertNotNull(user.getEmail());

// WITH assertAll: ALL three run, ALL failures reported at once
assertAll("user validation",
    () -> assertEquals("John", user.getName()),
    () -> assertEquals(25, user.getAge()),
    () -> assertNotNull(user.getEmail())
);

// Why this matters:
// Without assertAll: fix one failure, re-run, find the next, repeat.
// With assertAll:    see ALL failures in a single run.
SCRIPT:
assertAll is underused by beginners and beloved by experienced developers — so let me give it its own moment.
Normally if the first assertion in a test fails, the test stops immediately. You fix it, re-run, and discover the next failure. If you have an object with five fields to validate, this becomes five separate fix-and-rerun cycles.
assertAll solves this. It runs every assertion inside it and collects all failures, then reports them together. When you're validating an object with multiple fields — like a user, an order, or an API response — wrap everything in assertAll.
The string you pass as the first argument ("user validation" in this example) is the group label that appears in the failure report, so make it descriptive.

SLIDE 10 — Parameterized Tests
SLIDE CONTENT:
java@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 5, 8, 13, 21})
@DisplayName("Fibonacci numbers should be positive")
void testFibonacciPositive(int number) {
    assertTrue(number > 0);
}

// Multiple arguments with @CsvSource
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "0, 0, 0",
    "-1, 1, 0",
    "100, 200, 300"
})
@DisplayName("add(a, b) should return expected sum")
void testAdd(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

// External CSV file
@ParameterizedTest
@CsvFileSource(resources = "/test-data/add-cases.csv")
void testAddFromFile(int a, int b, int expected) { ... }
```

**SCRIPT:**
Parameterized tests are how you avoid writing essentially the same test five times with different values.

Without parameterized tests, testing the `add` method with four different inputs means four separate test methods. With `@ParameterizedTest` and `@CsvSource`, you write the logic once and JUnit runs it once per row of data.

`@ValueSource` is for a single parameter. `@CsvSource` is for multiple parameters per test case — each string in the array is one row, values separated by commas.

For larger datasets, `@CsvFileSource` reads from a CSV file in your test resources folder. This is excellent for data-driven testing where a non-developer might maintain the test data.

In your test report, each parameterized run shows up as a separate test with its input values in the name — so when one fails you know exactly which input caused the problem.

---

### SLIDE 11 — Arrange-Act-Assert Pattern & Test Organization

**SLIDE CONTENT:**
```
Arrange-Act-Assert (AAA) Pattern

Every test should have exactly three phases:

// ARRANGE — set up everything needed for the test
Calculator calculator = new Calculator();
int a = 10, b = 3;

// ACT — execute the single thing being tested
int result = calculator.add(a, b);

// ASSERT — verify the outcome
assertEquals(13, result);

Rules:
- One logical assertion per test (use assertAll for multi-field checks)
- Test one behavior, not one method (a method can have multiple tests)
- Test names should be: methodName_condition_expectedResult
  e.g.: add_negativeNumbers_returnsNegativeSum()
- Keep tests in the same package structure as production code (in /test/)
```

**SCRIPT:**
AAA is the single most important structural pattern for writing readable tests. Arrange, Act, Assert. Every test you write should follow this structure, and you can even use comments to separate the three sections when you're learning.

**Arrange** — set up your objects, inputs, and any fakes.
**Act** — call the one thing you're testing. Just one call. This is important.
**Assert** — verify the result.

The reason to separate Act from Arrange is clarity: it forces you to identify *what you're actually testing*. If your Act section has three lines, you're probably testing too much in one test.

For naming conventions, the `methodName_condition_expectedResult` format is very readable. `add_withNegativeNumbers_returnsNegativeSum` tells you everything about the test before you even read the code.

---

## SECTION 3: CODE COVERAGE
**⏱ ~4 minutes**

---

### SLIDE 12 — Code Coverage Metrics

**SLIDE CONTENT:**
```
Code Coverage — Measuring What You've Tested

Line Coverage (Statement Coverage)
  → What % of lines were executed by your tests?
  → Easy to achieve, easiest to game
  → Tool: JaCoCo in Java

Branch Coverage
  → What % of decision branches were taken?
  → For every if/switch, did tests cover both true AND false paths?
  → More meaningful than line coverage

Example:
  if (user.isAdmin()) {           // branch: admin = true
      grantAccess();              //   ← covered
  } else {
      denyAccess();               //   ← branch: admin = false — covered?
  }

Target: >85% branch coverage is a healthy professional standard
High coverage ≠ good tests. Low coverage definitely = bad tests.
```

**SCRIPT:**
Code coverage tells you what percentage of your code is exercised when your tests run. The two most important metrics are line coverage and branch coverage.

Line coverage is the simpler one — it just checks if a line was executed. Branch coverage is more meaningful. For every `if` statement, did your tests cover *both* the true and false paths? For a `switch`, did you hit every case?

Here's the classic gotcha: you can have 100% line coverage and terrible tests. How? Write a test that calls every method but makes no assertions. Every line runs, coverage is 100%, but you're not actually checking anything.

The 85% target I'm showing you is a professional standard, not a magic number. It's a health indicator. Below 70% and you almost certainly have large untested areas. 100% sounds great but is often counterproductive — you end up writing low-value tests just to hit the number.

The tool for Java is JaCoCo — it integrates with Maven and Gradle and produces HTML reports showing exactly which lines and branches are uncovered. We'll use it in your projects.

---

## SECTION 4: MOCKING WITH MOCKITO
**⏱ ~10 minutes**

---

### SLIDE 13 — Why Mocking?

**SLIDE CONTENT:**
```
The Problem: Real Dependencies

class OrderService {
    private EmailService emailService;     // sends real emails
    private PaymentGateway payment;        // charges real credit cards
    private UserRepository userRepository; // hits real database

    public Order placeOrder(Cart cart) {
        User user = userRepository.findById(cart.getUserId());
        payment.charge(user.getCard(), cart.getTotal());
        emailService.sendConfirmation(user.getEmail());
        return new Order(cart);
    }
}

How do you unit test placeOrder() without sending real emails,
making real charges, or needing a real database?

Answer: Replace real dependencies with controlled fakes → Mocks
SCRIPT:
Here's the problem mocking solves. You have an OrderService that depends on an email service, a payment gateway, and a database repository. In a unit test, you don't want to send real emails, charge real cards, or hit a real database.
Even if you had a test database, using it in a unit test violates the I in F.I.R.S.T. — Isolated. Your test would be slow, it would depend on the database being running and in the right state, and if the test fails you don't know if the failure is in OrderService or the database.
Mocking lets you replace those real dependencies with controlled fakes that you configure to return whatever values you need. You can test OrderService in complete isolation.

SLIDE 14 — Mockito Basics: @Mock and @InjectMocks
SLIDE CONTENT:
java@ExtendWith(MockitoExtension.class)   // enables Mockito annotations
class OrderServiceTest {

    @Mock
    private EmailService emailService;      // Mockito creates a fake

    @Mock
    private PaymentGateway paymentGateway;  // Mockito creates a fake

    @Mock
    private UserRepository userRepository;  // Mockito creates a fake

    @InjectMocks
    private OrderService orderService;      // Mockito injects the mocks above

    // Mockito automatically injects the 3 mocks into orderService's constructor/fields

    @Test
    void placeOrder_validCart_chargesPayment() {
        // Now we can test OrderService in complete isolation
    }
}
SCRIPT:
With Mockito, you annotate dependencies with @Mock — Mockito creates a fake implementation of that interface or class. It doesn't do anything by default, it just exists.
Then you annotate the class under test with @InjectMocks. Mockito creates an instance of OrderService and injects all the mocks into it — through the constructor, setter methods, or direct field injection, in that order of preference.
@ExtendWith(MockitoExtension.class) at the top of the class activates Mockito's JUnit 5 integration so the annotations work.
The key insight: your test is completely isolated. OrderService thinks it has a real EmailService and a real PaymentGateway, but it actually has fakes you control completely.

SLIDE 15A — Stubbing: Controlling What Mocks Return
SLIDE CONTENT:
java@Test
void placeOrder_validUser_sendsConfirmationEmail() {
    // ARRANGE
    User user = new User("alice@example.com");
    Cart cart = new Cart(user.getId(), 99.99);

    // STUBBING: "when this mock method is called, return this value"
    when(userRepository.findById(user.getId())).thenReturn(user);

    // ACT
    orderService.placeOrder(cart);
}

// Stubbing a void method (returns nothing)
doNothing().when(emailService).sendConfirmation(any());

// Stubbing to throw an exception
when(paymentGateway.charge(any(), anyDouble()))
    .thenThrow(new PaymentException("Card declined"));

// Stubbing consecutive calls (first call returns X, second returns Y)
when(userRepository.findById(1L))
    .thenReturn(user)
    .thenThrow(new RuntimeException("Cache miss"));
SCRIPT:
There are two core operations with Mockito mocks: stubbing and verifying. Let's take them one at a time.
Stubbing with when().thenReturn() means: "when the mock's method is called with this argument, return this value." This is how you control what data flows through your class under test.
By default, a mock returns zero/null/false/empty depending on the return type. Stubbing overrides that default. Here, userRepository.findById would return null by default — we stub it to return our test user so placeOrder can proceed.
You can also stub to throw exceptions — this is how you test your error-handling paths. And you can chain .thenReturn().thenThrow() to make a mock return different values on consecutive calls.

SLIDE 15B — Verifying: Asserting Mocks Were Called Correctly
SLIDE CONTENT:
java@Test
void placeOrder_validUser_sendsConfirmationEmail() {
    // ... (arrange and act from previous slide)

    // VERIFY: assert the mock was called with the right argument
    verify(emailService).sendConfirmation("alice@example.com");

    // VERIFY with argument matchers
    verify(paymentGateway).charge(any(CreditCard.class), eq(99.99));

    // VERIFY a method was NEVER called
    verify(emailService, never()).sendError(any());

    // VERIFY called exactly N times
    verify(emailService, times(1)).sendConfirmation(any());
}

// ⚠️ Argument matcher rule:
// If you use ANY matcher in a call, you must use matchers for ALL arguments.
// Wrong:  verify(mock).method(any(), 99.99)       ← mixing matcher + raw value
// Right:  verify(mock).method(any(), eq(99.99))   ← wrap raw value with eq()
```

**SCRIPT:**
Now for the other half — **verifying**.

`verify()` asserts that a mock method was actually called. After calling `placeOrder`, we verify that `emailService.sendConfirmation` was invoked with Alice's email address. If `placeOrder` had a bug and forgot to call `sendConfirmation`, this verification fails the test.

**Argument matchers** let you be flexible about what arguments you check. `any(CreditCard.class)` matches any CreditCard. `eq(99.99)` matches exactly that value.

The critical rule — and this trips everyone up — is that you cannot mix raw values and argument matchers in the same method call. If you use `any()` for one argument, you must use matchers for *all* arguments in that call. Wrap specific values with `eq()`.

`verify(mock, never())` is for negative assertions — confirming a method was deliberately NOT called. This is essential for testing that error branches don't trigger success paths.

---

## SECTION 5: TESTING SPRING BOOT APPLICATIONS
**⏱ ~10 minutes**

---

### SLIDE 16 — Spring Boot Testing Overview

**SLIDE CONTENT:**
```
Spring Boot Test Slices — Right-Sized Tests

                        @SpringBootTest
                   ┌────────────────────────┐
                   │  Full application       │  → Slow, tests everything together
                   │  context loads          │
                   └────────────────────────┘

          @WebMvcTest              @DataJpaTest
     ┌──────────────────┐     ┌──────────────────┐
     │  Controller layer│     │  Repository layer │  → Faster, focused
     │  only (no DB)    │     │  + embedded DB    │
     └──────────────────┘     └──────────────────┘

         Unit Tests (JUnit 5 + Mockito)
     ┌─────────────────────────────────────┐
     │  Individual classes in isolation    │  → Fastest, most numerous
     └─────────────────────────────────────┘

Key principle: use the lightest slice that tests what you need to test.
SCRIPT:
Spring Boot gives you test slices — pre-configured test contexts that load only the parts of your application relevant to what you're testing.
@WebMvcTest loads only the web layer: your controllers, filters, and security. No service layer, no database. Fast. Use it to test your REST API endpoints.
@DataJpaTest loads only the JPA/database layer with an embedded in-memory database. No web layer, no services. Use it to test your repository queries.
@SpringBootTest loads the entire application context — all beans, everything. Use it for true integration tests where you need everything working together. It's slower, so reserve it for scenarios where you need end-to-end verification.
The principle is: always use the lightest slice that proves what you need to prove.

SLIDE 17 — MockMvc for REST Controller Testing
SLIDE CONTENT:
java@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean                           // Spring's version of @Mock
    private UserService userService;    // mocked so no real DB needed

    @Test
    @DisplayName("GET /users/{id} returns 200 with user JSON when found")
    void getUser_existingId_returns200() throws Exception {
        User user = new User(1L, "Alice", "alice@example.com");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("GET /users/{id} returns 404 when user not found")
    void getUser_nonExistentId_returns404() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/99"))
            .andExpect(status().isNotFound());
    }
}
SCRIPT:
MockMvc is Spring's tool for testing REST controllers without starting a real HTTP server. You perform requests programmatically and chain assertions on the response.
@WebMvcTest(UserController.class) tells Spring: load only this controller and its web layer dependencies.
@MockBean is the Spring version of @Mock — it creates a Mockito mock and registers it as a Spring bean, replacing any real implementation. Here we mock UserService so we don't need a real database.
Then mockMvc.perform(get("/users/1")) simulates an HTTP GET request. .andExpect(status().isOk()) asserts the response code is 200. .andExpect(jsonPath("$.name").value("Alice")) checks the JSON response body using JSONPath expressions — $.name means "the top-level field called name."
The second test is just as important — always test your error paths. When the service throws UserNotFoundException, does your controller correctly return 404? Mock the exception, verify the status.

SLIDE 18 — Integration Testing with @DataJpaTest and Profiles
SLIDE CONTENT:
java// Testing your JPA repository
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_existingEmail_returnsUser() {
        // Uses an embedded H2 database — no setup needed
        userRepository.save(new User("Alice", "alice@example.com"));

        Optional<User> result = userRepository.findByEmail("alice@example.com");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }
}

// Full integration test with a test profile
@SpringBootTest
@ActiveProfiles("test")   // loads application-test.yml
class OrderIntegrationTest {
    // application-test.yml points to a test DB, disables email sending, etc.
}
```

**SCRIPT:**
`@DataJpaTest` configures an embedded H2 database, scans for JPA entities and repositories, and that's it. You don't need a running PostgreSQL or MySQL. Your repository queries run against a real (but in-memory) database. Each test runs in a transaction that's rolled back after the test, so tests don't pollute each other's data.

This is excellent for testing custom JPQL queries — if you write a `findByEmailAndStatus` method on your repository, write a `@DataJpaTest` for it to make sure the query is correct.

For full integration tests with `@SpringBootTest`, use `@ActiveProfiles("test")` to load a test-specific configuration file. In `application-test.yml` you point to a test database, disable email sending, set API keys to test values. This is how you run real integration tests without affecting production systems.

---

## SECTION 6: ACHIEVING HIGH COVERAGE & ADVANCED TOPICS
**⏱ ~10 minutes**

---

### SLIDE 19 — Strategies for >85% Test Coverage

**SLIDE CONTENT:**
```
Achieving High Coverage Professionally

What to always test:
  ✓ Happy path (valid inputs, expected behavior)
  ✓ All error/exception paths
  ✓ Boundary conditions (0, -1, empty string, null, max value)
  ✓ Every branch of every if/switch
  ✓ Business rules and validation logic

What NOT to chase coverage on:
  ✗ Auto-generated code (getters/setters, Lombok)
  ✗ Framework boilerplate (Spring config classes)
  ✗ Simple DTOs / data-only classes

How to find gaps:
  → Run JaCoCo HTML report
  → Look for red/yellow lines (uncovered)
  → Ask: "what test would make this line red turn green?"
  → Write that test

The number is a result of good testing habits, not a goal in itself.
```

**SCRIPT:**
You'll hit 85% almost naturally if you develop good habits. Test the happy path first — that gets your baseline. Then ask yourself: what are all the ways this could go wrong? Null input? Empty list? Negative number? Invalid state? Write a test for each.

Every time you write an `if` statement, that's two branches. Make sure you have tests that exercise both sides.

The JaCoCo HTML report is your friend. It highlights uncovered lines in red and partially covered branches in yellow. When you see a red line, ask: "what would I need to do to make this code execute?" That question almost always leads directly to a missing test case you *should* have written anyway.

Don't feel compelled to test auto-generated getters and setters from Lombok. Most teams configure JaCoCo to exclude those. Put your testing effort where the actual logic lives.

---

### SLIDE 20 — Testcontainers

**SLIDE CONTENT:**
```
⚠️  OPTIONAL / ADVANCED — Requires Docker installed and running

Testcontainers — Real Services in Tests

Problem: @DataJpaTest uses H2, but production uses PostgreSQL.
         What if your query works in H2 but fails in Postgres?

Solution: Run the REAL database in a Docker container during tests.

@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Now tests run against real PostgreSQL, not H2
}
```

**SCRIPT:**
Before I start — this slide is marked optional because Testcontainers requires Docker running on your machine. If you haven't set up Docker yet, file this away as "something to come back to" once you have. It's a tool worth knowing.

With that said — here's the problem it solves. `@DataJpaTest` uses H2 by default. H2 is not PostgreSQL. It doesn't support all the same SQL syntax, functions, or constraints. Tests can pass in H2 and fail in production PostgreSQL. That's terrifying.

Testcontainers fixes this by spinning up a *real* PostgreSQL Docker container when your test starts, running your tests against it, and tearing it down when done. No mocking, no H2 dialect issues — actual PostgreSQL.

`@Container` declares the container. The `@DynamicPropertySource` method wires the container's connection details into Spring's configuration so your app connects to it automatically.

The first run is slower because it pulls the Docker image. Subsequent runs are fast because Docker caches it. You can use Testcontainers for any service you depend on — MySQL, Redis, Kafka, Elasticsearch, MongoDB — if there's a Docker image, there's a Testcontainers module for it.

---

### SLIDE 21 — WireMock for External HTTP Services

**SLIDE CONTENT:**
```
WireMock — Mock External HTTP APIs

Problem: Your service calls an external payment API, weather service, etc.
         You can't call real external APIs in tests — slow, costs money, unreliable.

Solution: WireMock starts a fake HTTP server that responds however you configure it.

@SpringBootTest
@AutoConfigureWireMock(port = 0)    // random port, avoids conflicts
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void processPayment_apiSuccess_returnsConfirmation() {
        stubFor(post(urlEqualTo("/v1/charges"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\": \"ch_123\", \"status\": \"succeeded\"}")));

        PaymentResult result = paymentService.processPayment(100.00, "tok_visa");

        assertEquals("succeeded", result.getStatus());
    }

    @Test
    void processPayment_apiTimeout_throwsPaymentException() {
        stubFor(post(urlEqualTo("/v1/charges"))
            .willReturn(aResponse().withFixedDelay(5000)));  // 5 second delay

        assertThrows(PaymentException.class,
            () -> paymentService.processPayment(100.00, "tok_visa"));
    }
}
```

**SCRIPT:**
WireMock solves the external API problem. Your application probably calls external services — payment processors, shipping APIs, weather services. You cannot call those in tests. They cost money, they're slow, they return different data every call, and they might be down.

WireMock starts a fake HTTP server that you configure to respond with whatever you specify. Your application code doesn't know it's talking to a fake — it just makes HTTP calls and gets responses.

The second test is where WireMock really shines: testing failure modes. What happens when the payment API times out? What happens when it returns a 500 error? What happens when it returns malformed JSON? With a real external service you can't reliably simulate these scenarios. With WireMock you can reproduce any failure condition in a deterministic, repeatable test.

`withFixedDelay(5000)` simulates a 5-second timeout. If your `PaymentService` has a timeout configured at 3 seconds, this test verifies that the timeout triggers correctly and a `PaymentException` is thrown.

This is the difference between software that works in happy-path demos and software that's resilient in production.

---

## SECTION 7: PUTTING IT ALL TOGETHER
**⏱ ~5 minutes**

---

### SLIDE 22 — The Testing Pyramid (Full Picture)

**SLIDE CONTENT:**
```
Your Testing Strategy

                        /\
                       /  \
                      / E2E\        ← Few. Slow. Test critical user journeys.
                     /  Tests\        Selenium, Playwright
                    /──────────\
                   /Integration \    ← Some. Medium speed.
                  /    Tests     \     @SpringBootTest, Testcontainers, WireMock
                 /────────────────\
                /   Unit  Tests    \  ← Many. Fast. JUnit 5 + Mockito.
               /────────────────────\

Fast  ← ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ → Slow
Cheap ← ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ → Expensive
Many  ← ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ → Few

A healthy project: ~70% unit, ~20% integration, ~10% E2E
```

**SCRIPT:**
Let me bring everything together with the testing pyramid.

The bottom is your foundation — unit tests with JUnit 5 and Mockito. Fast, cheap, many. These catch most of your bugs at the lowest cost.

The middle is integration tests — `@WebMvcTest`, `@DataJpaTest`, Testcontainers, WireMock. These verify that your components work correctly together and that you're talking to real databases and services correctly. Slower, fewer.

The top is end-to-end tests — testing the whole system from the user interface down. These are the most expensive and slowest, so you write the fewest of them, only for critical user journeys.

A common mistake is an inverted pyramid — mostly E2E tests and few unit tests. That suite is slow, fragile, and expensive to maintain. Invert it back.

---

### SLIDE 23A — TDD Workflow in Practice

**SLIDE CONTENT:**
```
Putting TDD into Practice — The Workflow

1. Pick the smallest next piece of behavior to implement
2. Write a @Test for it (it will fail — that's good, that's RED)
3. Run the test, confirm it fails for the RIGHT reason
4. Write MINIMUM production code to make it pass (GREEN)
5. Run all tests — confirm everything passes
6. REFACTOR — improve code quality, names, structure
7. Run all tests — confirm everything still passes
8. Repeat

Signs you're doing TDD right:
  ✓ You almost never run the app manually to "check if it works"
  ✓ Every new line of code has a test
  ✓ Refactoring feels safe, not scary
  ✓ Your test names tell the story of your feature
```

**SCRIPT:**
This is the complete workflow. Notice that you almost never run the application manually when you're doing TDD well. Your tests give you confidence about whether the code works. Running the app is for exploration and UI polish, not for verifying logic.

The sign that you've truly internalized TDD is when refactoring stops feeling scary. Before tests, "refactoring" means "changing code and hoping nothing breaks." With a comprehensive test suite, refactoring means making a change and immediately seeing green tests confirm that nothing broke. That's a fundamentally different — and much more professional — way to work.

---

### SLIDE 23B — Your Testing Toolkit at a Glance

**SLIDE CONTENT:**
```
Tools Summary — What to Reach For

  What you're testing             →  Tool to use
  ──────────────────────────────────────────────────
  A single class in isolation     →  JUnit 5 + Mockito
  A REST controller / endpoint    →  @WebMvcTest + MockMvc
  A database repository / query   →  @DataJpaTest
  Full application flow           →  @SpringBootTest + @ActiveProfiles
  A real DB (not H2)              →  Testcontainers  ⚠️ needs Docker
  An external HTTP API / timeout  →  WireMock
  Coverage visibility             →  JaCoCo (HTML report)
```

**SCRIPT:**
Before we move to summary, here's a quick reference you can keep. When you sit down to write a test and you're not sure which tool to reach for, this table answers that question.

The left column is what you're trying to verify. The right column is the right tool. Start here, then look up the specifics. Over time this becomes instinct.

---

### SLIDE 24 — Summary & What's Coming Next

**SLIDE CONTENT:**
```
Today's Key Takeaways

TDD & Mindset:
  • Write tests first — Red → Green → Refactor
  • Testing is a design discipline

JUnit 5:
  • @Test, @DisplayName, lifecycle annotations
  • AAA pattern, parameterized tests, assertions
  • Code coverage: aim for >85% branch coverage

Mockito:
  • @Mock, @InjectMocks
  • Stubbing: when().thenReturn() / thenThrow()
  • Verifying: verify(), never(), argument matchers

Spring Boot Testing:
  • @WebMvcTest + MockMvc for controllers
  • @DataJpaTest for repositories
  • @SpringBootTest for full integration

Advanced:
  • Testcontainers: real services in Docker (needs Docker)
  • WireMock: mock external HTTP APIs and failure modes

Coming Up Next: [your next lesson topic]
SCRIPT:
Let's recap what we covered today.
We started with the philosophy — TDD isn't just a technique, it's a design discipline that produces better code. Red-Green-Refactor keeps you moving in small, confident steps.
JUnit 5 gives you everything you need to write expressive, well-organized unit tests — lifecycle management, parameterized tests, readable display names, and a rich assertion library including assertAll.
Mockito lets you isolate units from their dependencies — stubbing controls what your fakes return, verifying confirms your code called the right things.
Spring Boot's test slices — @WebMvcTest, @DataJpaTest, @SpringBootTest — let you test each layer of your application at the appropriate level of integration.
And Testcontainers and WireMock elevate your integration tests from "good enough" to "production realistic."
The best way to absorb all of this is to practice. Start your next feature with a failing test. Build the habit. It feels slow at first and then becomes second nature.
Any questions?