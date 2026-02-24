# Walkthrough Script — Part 1: TDD, JUnit 5 & Unit Testing
## Day 28 — Week 6

**Total time:** ~90 minutes
**Files covered:** `01-tdd-and-junit5-overview.md`, `02-junit5-tests.java`

---

## SEGMENT 1 — Opening: Why Testing Matters (~8 min)

### Before opening any file

"Good morning. Today is one of the most important days of the program — not because the syntax is complicated, but because the discipline of testing is what separates professional software developers from people who just write code.

Let me ask you something: have you ever pushed a code change and immediately regretted it because something broke that you didn't expect? Everyone has. Tests are your safety net. They let you change code with confidence.

I often say: **without tests you have opinions, with tests you have evidence.** Your code either works or it doesn't, and tests tell you which — in milliseconds, before it ever reaches production."

---

### Open `01-tdd-and-junit5-overview.md` → Test Pyramid

"Let me orient you with the test pyramid.

At the base: **unit tests** — fast, isolated, test one class at a time. No database, no HTTP, no Spring context. These should make up 70–80% of your test suite and run in under a second.

In the middle: **integration tests** — test how components work together. Might need a database or Spring context. Slower but more realistic. About 15–20%.

At the top: **end-to-end tests** — test the whole system from UI to database. Very slow and brittle. Only a few of these.

The pyramid shape is intentional — many small fast tests at the base, few slow expensive tests at the top. An inverted pyramid (lots of E2E tests, few unit tests) is an anti-pattern that leads to slow, unreliable CI pipelines."

---

## SEGMENT 2 — TDD: Red-Green-Refactor (~15 min)

### TDD section in overview file

"Test-Driven Development is a practice where you write the test BEFORE the code. This sounds backwards, but it produces better designs and better test coverage.

The cycle: **Red → Green → Refactor**.

**Red:** Write a test that describes behavior you want. Run it. It fails because the code doesn't exist yet. The red state proves your test is actually testing something — a test that passes without any code is useless.

**Green:** Write the minimum code to make the test pass. Not the prettiest code. Just enough to go green. Don't over-engineer.

**Refactor:** Now that it works, clean it up. Better variable names, extract helper methods, remove duplication. Tests protect you — if you accidentally break something, the tests go red immediately.

**Ask the class:** Why do you think we write the MINIMUM code to go green, instead of the 'best' code immediately?

*(let them answer — expect: prevents over-engineering, avoids YAGNI violations)*

Exactly — if you only write code to satisfy tests, you never write code you don't need. Features are driven by requirements, which are expressed as tests.

**Ask the class:** If you write the test first, is it possible to end up with untested code?

*(answer: no — every line of production code was written to make a test pass)*

This is why TDD often results in near-100% coverage naturally."

---

### Walk through the TDD example

"Look at the TDD example in the overview. Three steps: a `PriceCalculator` that applies discounts.

**Step 1 — RED:** We write `shouldApply10PercentDiscountForPremiumMembers`. We call `calculator.calculatePrice(100, PREMIUM)` and expect `90.00`. Run it — it fails because `PriceCalculator` doesn't exist. Red is GOOD here — we've verified the test is actually executable.

**Step 2 — GREEN:** We write the bare minimum — an `if` statement, hardcode the 0.90 multiplier. Just enough to pass. Run it — green.

**Step 3 — REFACTOR:** Extract the discount constant, rename for clarity, maybe extract the discount calculation to a helper method. Run tests — still green. Refactor didn't break anything.

> **Watch Out:** A lot of developers skip the Refactor step. Green means 'it works.' Refactor means 'it works AND it's maintainable.' Both matter."

---

## SEGMENT 3 — FIRST Properties and Unit Testing Fundamentals (~7 min)

### FIRST table

"Unit tests should have five properties — the acronym FIRST.

**Fast** — milliseconds, not seconds. If your unit tests take more than a few seconds to run, they won't be run often.

**Isolated** — each test is independent. No shared mutable state between tests. If test A's result affects test B, you have a flaky test suite.

**Repeatable** — same result every time. A test that passes today but fails tomorrow because of the time or environment is useless.

**Self-validating** — the test either passes or fails with no manual inspection. No 'look at the output and judge for yourself.'

**Timely** — written at the same time as the code, not months later when you've forgotten how it works.

> **Watch Out:** 'Isolated' doesn't mean one assertion per test — it means one *concept* per test. You can have multiple `assertAll` assertions verifying a single action."

---

## SEGMENT 4 — JUnit 5 Architecture (~5 min)

### JUnit 5 diagram

"JUnit 5 is not a single jar — it's a three-part platform.

**Platform** — the launcher. IDEs and build tools use this to discover and run tests. You don't write Platform code — it's infrastructure.

**Jupiter** — this is the JUnit 5 API you actually use: `@Test`, `@BeforeEach`, `@ParameterizedTest`. When people say 'JUnit 5,' they usually mean Jupiter.

**Vintage** — runs old JUnit 3 and JUnit 4 tests. If you're migrating a legacy codebase, Vintage lets old tests run alongside new Jupiter tests.

The Maven dependency: `spring-boot-starter-test` includes JUnit 5, Mockito, AssertJ, and Hamcrest automatically. If you're in a Spring Boot project, you don't need to add anything.

One important gotcha: if you're using Maven, you need Surefire plugin version 2.22+. Older versions don't know how to run JUnit 5."

---

## SEGMENT 5 — Writing Tests: Lifecycle Annotations (~10 min)

### Open `02-junit5-tests.java` → class declaration + lifecycle section

"Open `02-junit5-tests.java`. Let's look at the lifecycle annotations first.

`@BeforeAll` — static method, runs ONCE before all tests in the class. Use for expensive shared setup: starting a database, loading a large fixture file. Because it runs only once, it's fast — but the resource is shared across all tests in the class, so be careful with shared mutable state.

`@AfterAll` — also static, runs once after all tests complete. Use for cleanup: close connections, delete temp files.

`@BeforeEach` — runs before EVERY test. This is where you typically create fresh object instances. Each test gets its own clean copy of the objects. This is the most commonly used lifecycle annotation.

`@AfterEach` — runs after every test. Less commonly needed, but useful for clearing caches or resetting counters.

**Ask the class:** What's the risk of NOT using `@BeforeEach` and instead declaring `private Book book = new Book()` as a field?

*(let them think)*

Great — if you create the book once as a field and one test modifies it, the NEXT test sees the modified version. Tests affect each other. That's flaky. `@BeforeEach` ensures every test starts with a clean slate."

---

## SEGMENT 6 — Assertions (~15 min)

### Section 3 — @Test and assertions walkthrough

"Now the assertions. JUnit 5 has about a dozen assertion methods. Let's walk through the important ones.

`assertEquals(expected, actual, message)` — most common. Two things that should be equal. Note the ORDER: **expected value first, actual value second**. This matters because error messages like 'expected <Clean Code> but was <null>' are only meaningful if you follow the convention.

> **Watch Out:** A very common mistake is `assertEquals(actual, expected)` — swapped. The error message then says 'expected <actual value> but was <expected value>' which is confusing. Always expected first, actual second.

`assertThrows(ExceptionClass, executable)` — this is how you test that code THROWS an exception. You pass a lambda — if the lambda throws the expected exception, the test PASSES. If it doesn't throw, it FAILS. If it throws a DIFFERENT exception, it also fails.

Look at `shouldThrowWhenTitleIsBlank`: we create a lambda `() -> new Book(blankTitle, ...)` and pass it to `assertThrows`. The test captures the exception so we can also verify the message.

`assertAll("group name", () -> ..., () -> ...)` — this is underrated. Normally, if the first assertion fails, the test stops — you don't see if the second and third also fail. `assertAll` runs ALL assertions and reports all failures at once. Use this when you're verifying multiple properties of the same result.

**Ask the class:** When would `assertSame` fail even when `assertEquals` passes?

*(answer: when comparing two different objects with the same values — different references but equal values)*

`assertTrue` / `assertFalse` — for boolean conditions. `assertNotNull` / `assertNull` — for null checks. `assertNotSame` — for reference inequality.

`assumeTrue` — this is an ASSUMPTION, not an assertion. If false, the test is SKIPPED not failed. Use this for tests that only make sense in certain environments."

---

## SEGMENT 7 — Parameterized Tests (~15 min)

### Section 4 — @ParameterizedTest

"This is one of the most powerful JUnit 5 features. Instead of writing five nearly identical tests, you write one test and provide multiple data sets.

Look at `shouldApplyCorrectDiscount` with `@CsvSource`. The test method runs three, four, five times — once for each row of CSV data. The column values map to method parameters by position.

`@ValueSource` — simplest. One parameter, a list of primitives or strings.

`@CsvSource` — multiple parameters per row, inline in the annotation. Great for simple input/output pairs.

`@MethodSource` — parameters come from a static factory method that returns `Stream<Arguments>`. Use this when test data is complex objects or when you need more than a few cases.

`@EnumSource` — runs the test for every value of an enum. Perfect for 'no member type should ever produce a negative price' tests — you don't want to forget a new enum value added in the future.

The `name` attribute on `@ParameterizedTest` customizes what appears in the test report. `{0}`, `{1}` refer to the parameter values. When a test fails, the report shows exactly which input caused it.

> **Watch Out:** `@CsvSource` values are strings — JUnit converts them to the parameter types automatically. But BigDecimal and custom objects need special treatment or use `@MethodSource`."

---

## SEGMENT 8 — @Nested, @Disabled, @Tag (~8 min)

### Sections 5 and 6

"`@Nested` creates sub-groups within a test class. In your IDE, nested classes appear as a tree — you can see 'BookAvailabilityTest > When newly created > should be available' which reads like a specification.

Each `@Nested` class can have its own `@BeforeEach` — fresh setup for that context. The outer `@BeforeEach` still runs, then the inner one. So nested classes have layered setup.

This pattern is great for 'given [state] when [action] then [result]' — the `@Nested` class represents the state, the test methods represent the assertions.

`@Disabled("reason")` — temporarily skips a test. Always add a comment explaining WHY. 'Disabled until BUG-1234 is fixed' is much better than just `@Disabled`. Disabled tests show as SKIPPED in reports — they don't fail.

`@Tag("slow")` — you can tag tests and filter by tag. `mvn test -Dgroups=fast` runs only fast tests. Useful for CI: run only unit tests on every commit, run slow integration tests on the nightly build."

---

## SEGMENT 9 — Test Suite and Wrap-Up (~7 min)

### Section 6 — @Suite

"The `@Suite` annotation groups multiple test classes. Running `BookstoreUnitTestSuite` runs all three classes inside it.

This is useful for: running all service tests together, running a specific subset for a feature, or grouping by layer (all repository tests, all service tests, all controller tests).

Let me summarize Part 1:

✅ **TDD** — Red-Green-Refactor: write the test first, make it pass, then clean it up  
✅ **Test Pyramid** — many unit tests, fewer integration, fewer E2E  
✅ **FIRST properties** — Fast, Isolated, Repeatable, Self-validating, Timely  
✅ **JUnit 5 = Platform + Jupiter + Vintage**  
✅ **Lifecycle** — `@BeforeAll`, `@AfterAll` once; `@BeforeEach`, `@AfterEach` per test  
✅ **Assertions** — `assertEquals`, `assertThrows`, `assertAll`, `assertTrue`, `assertNotNull`, `assumeTrue`  
✅ **AAA pattern** — Arrange, Act, Assert in every test  
✅ **@ParameterizedTest** — `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`  
✅ **@Nested** — hierarchical test organization  
✅ **Coverage** — line coverage, branch coverage, JaCoCo, 85% target  

**Common interview questions:**
- 'What is the difference between @BeforeAll and @BeforeEach?' — once vs per test
- 'What does assertThrows do?' — verifies that code throws the expected exception
- 'What is the AAA pattern?' — Arrange, Act, Assert
- 'Why use @ParameterizedTest?' — test multiple inputs without duplicate code
- 'What is line coverage vs branch coverage?' — lines executed vs both paths of every if/else

Part 2: we'll add mocking with Mockito, test Spring Boot controllers with MockMvc, write integration tests, and use Testcontainers and WireMock for realistic testing."

---

*[15 min break]*
