# Day 28 Part 1 — Testing: TDD, JUnit 5 & Code Coverage
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — The Test That Saves You at 11pm

Let me describe two developers. Developer A writes code, deploys to production, and finds out something is broken when a user reports it. Developer B writes a test before they write a single line of production code, runs it in three seconds, and finds out immediately whether their change broke anything — while still at their desk.

Today you're learning to be Developer B.

Testing is the skill that separates developers who are confident in their code from developers who cross their fingers before every deploy. It's also the skill that makes refactoring possible — you can change how something works internally and know in seconds whether you broke any external behavior. And it's the skill that will set you apart in code reviews and technical interviews, because most hiring managers can tell within minutes whether a candidate thinks about their code the way a professional does.

Part 1 covers the foundations: the test pyramid, Test Driven Development, JUnit 5, lifecycle annotations, parameterized tests, and code coverage. Part 2 picks up Mockito and Spring-specific testing tools.

---

## [02:00–08:00] Slides 2–3 — The Cost of Bugs and the Test Pyramid

Let me put a number on why testing matters. The relative cost to fix a bug caught while you're writing the code: one dollar. Same bug caught in code review: five. Caught in QA: ten. Caught in production — when a real user is hitting it — a hundred or more. Not because the code change is hard, but because of the cascade: customer impact, emergency hotfix deployment, rollback procedures, support tickets, reputation damage. Testing is not overhead. It's the cheapest debugging you'll ever do.

Beyond cost, a strong test suite gives you three things. First: confidence to refactor. Without tests, changing a class is risky — you might break something that was working and not know it until a user finds it. With tests, you run the suite after the change and get a precise answer in seconds. Second: fast feedback. A test run takes milliseconds. Deploying an application and manually clicking through a UI to verify the same thing takes minutes, sometimes longer. Over a full workday, that difference adds up to hours. Third: living documentation. A well-written test suite is a precise, executable specification of how the system is supposed to behave — more accurate than any README because it runs.

Now the test pyramid. Picture a triangle. Wide base, narrow top. The base is unit tests — tests of individual classes in complete isolation, with all dependencies replaced by mocks. These run in milliseconds. You should have hundreds of them. The middle section is integration tests — tests that bring real components together, like your repository talking to a real database or your controller handling a real HTTP request. These are slower but still automated and fast enough for a CI pipeline. The top is end-to-end tests — full system tests from HTTP request through to the database. Very few, very slow, very brittle.

The anti-pattern is inverting this pyramid. If most of your tests are end-to-end, every failure requires debugging the entire application stack. A unit test failure points you to one method. An E2E test failure could be anything. Keep the base wide — most of your investment goes into fast, focused unit tests.

---

## [08:00–20:00] Slides 4–5 — TDD and Red-Green-Refactor

Test-Driven Development. The idea is simple enough to state in one sentence: write the test before you write the production code. Not after. Before.

Why? Because the test is a specification. When you write the test first, you're forced to answer two questions before you touch implementation: what should this method accept, and what should it return? You're thinking as a consumer of the code before you're its producer. That perspective shift — routinely — leads to cleaner, more focused interfaces. Methods designed from the test outward tend to have a single clear purpose. Methods designed from the implementation outward tend to accumulate responsibilities over time.

There's also a guarantee. When you do TDD, you literally cannot write untested production code. The rule is: no production code without a failing test requiring it. That means by the time you're done, every behavior you built is covered.

The common objection is that TDD is slower. Short-term, writing the test first adds a few minutes to each feature. But the payback comes immediately — fewer debugging sessions, zero time spent manually verifying that your last change didn't break something, and a codebase you can refactor with confidence instead of fear. Most developers who do TDD seriously report being faster overall within a week or two.

Now the cycle. Red-Green-Refactor. Three steps, repeated continuously.

Red: write a test for behavior that doesn't exist yet. Run it. It must fail. If a test you just wrote for a nonexistent feature somehow passes, one of two things is wrong — the feature already exists somewhere, or the test doesn't actually test what you think. A test that can never fail is not protecting you from anything.

Green: write the minimum production code to make the test pass. Not the best code. Not the most elegant code. Just enough. Don't add a feature not covered by a test. Don't optimize. Get green as fast as possible.

Refactor: now clean up. Extract constants. Rename for clarity. Remove duplication. Improve structure. And here's what makes this safe: the tests are running the whole time. As long as they stay green, every refactoring step is provably safe. You didn't break anything — you have proof.

Let me walk through the code on slide 5. The feature: a pricing service that applies a 20% discount for premium members.

Red first. I write the test: create a `PricingService`, call `calculatePrice(100.00, MemberType.PREMIUM)`, assert the result is 80.00. I run it. The test doesn't even compile — `PricingService` doesn't exist. Compilation failure counts as red.

Green. I create `PricingService` with a `calculatePrice` method. Check if the type is `PREMIUM`; multiply by 0.80 with scale 2; return the result. Run the test. It passes. Green.

Refactor. Now I make it better. I extract `0.80` as a named constant: `PREMIUM_DISCOUNT_RATE`. I convert the `if` statement to a `switch` expression — cleaner, exhaustive, extensible. I extract the discount calculation to a private `applyDiscount` method — now other discount rates can reuse it. I run the test again. Still green. The refactoring is verified safe.

Keep cycles short. Two to five minutes per cycle. If you're spending twenty minutes on a single red phase, you're writing too much at once. One behavior, get it green, refactor, move on.

---

## [20:00–26:00] Slide 6 — Arrange-Act-Assert

Every test you write has three sections. Arrange, Act, Assert.

Arrange is setup. Create the object under test. Create any inputs or data objects the method needs. Configure mocks — tell them what to return when called. Put the system in the exact state needed for this specific test.

Act is the thing you're actually testing. Usually one line: call the method, store the result. If Act has two calls, you're testing two things. Split the test.

Assert is verification. Did the method return the right value? Did it throw the right exception? Did it call the right collaborators with the right arguments?

Look at the example on slide 6. Arrange: create a `Book` object, stub the repository's `findById` to return it. Act: call `bookService.findById(1L)`, store the result. Assert: verify the title is "Clean Code" and the price is 35.00.

One behavior per test. If your test name contains the word "and" — "finds book and throws exception if not found" — that's two tests. Write them separately. Separate tests produce separate, precise failure messages.

The blank lines between sections are a visual convention that matters. When someone reads this test six months from now, the blank lines tell them immediately where setup ends and the real call happens, and where the call ends and verification begins. It takes two seconds to add blank lines and they save minutes of reading confusion later.

Use `assertAll` when you have multiple properties of the same result to check. The normal `assertEquals` stops at the first failure — you fix it, run again, find the next failure, repeat. `assertAll` runs all assertions and reports all failures at once. More efficient debugging.

---

## [26:00–32:00] Slide 7 — JUnit 5 Architecture

JUnit 5 is actually three modules working together, and understanding the structure explains why it works the way it does.

JUnit Platform is the foundation. It's the layer that discovers tests and launches test frameworks. This is what Maven, Gradle, and your IDE talk to when you run tests. The Platform is intentionally framework-agnostic — it defines a test engine API that any testing framework can implement. JUnit Jupiter is one implementation. TestNG could be another.

JUnit Jupiter is the new programming model introduced in JUnit 5. All the annotations you'll write — `@Test`, `@BeforeEach`, `@AfterEach`, `@ParameterizedTest`, `@ExtendWith` — come from Jupiter. Jupiter is both the API you write against and the engine that runs your tests on the Platform. Your imports come from `org.junit.jupiter.api`.

JUnit Vintage is the backward compatibility bridge. If a codebase has hundreds of JUnit 4 tests and the team upgrades to JUnit 5, Vintage lets those old tests keep running without any rewriting. It implements the test engine API so the Platform can execute JUnit 4 tests alongside JUnit 5 tests. You'll see it in the classpath of older projects — if you see `junit-vintage-engine` in the dependencies, that's what it is.

For your Spring Boot projects, you don't configure any of this manually. `spring-boot-starter-test` with test scope pulls in JUnit Jupiter, Mockito, AssertJ, JSONPath, Spring Test, and Hamcrest — everything you need for any kind of test — in a single dependency. Spring Boot manages all the compatible versions. You don't version any of these individually.

---

## [32:00–46:00] Slides 8–9 — Writing Tests and Assertions

Let's write actual tests. The convention for where they live: `src/test/java` mirrors `src/main/java`. Same package hierarchy. `BookService` in `com.bookstore.service` gets `BookServiceTest` in `com.bookstore.service` in the test source tree. Mirroring the package means your test class can access package-private members of the class it's testing — which is sometimes useful.

The class doesn't need to be `public`. This is different from JUnit 4, which required public. JUnit 5 uses reflection and doesn't care about access modifiers on the test class or methods. You'll see some older tutorials still marking everything public out of habit — it's unnecessary.

Naming convention for test methods: `methodName_expectedBehavior_whenCondition`. So: `findById_returnsBook_whenBookExists`. `createBook_throwsException_whenIsbnDuplicate`. `calculatePrice_appliesDiscount_forPremiumMembers`. This convention means you can read the method name and understand exactly what the test does without reading the body. In a test report with a hundred tests, this is invaluable.

Look at the pattern in slide 8. The class has a `@BeforeEach` method that creates fresh mocks and a fresh service instance before every test. This is critical. If test A stubs the mock to return a specific value and test B doesn't reset the mock, test B may behave differently depending on test execution order. By creating brand new mocks in `@BeforeEach`, each test starts from a clean, predictable state.

Two tests shown. The happy path: stub `findById` to return a book, call the service, assert the title is correct. The error path: stub `findById` to return empty, call the service wrapped in `assertThrows`, expect `ResourceNotFoundException`.

For assertions, you have two good options: JUnit's built-in `Assertions` class and AssertJ.

JUnit assertions. `assertEquals(expected, actual)` — note the argument order: expected value first, actual result second. The error message will say "expected `Clean Code` but was `null`" — that order makes the message read correctly. `assertThrows` takes the expected exception class and a lambda that should throw it. If no exception is thrown, the test fails. If the wrong type is thrown, the test fails. You can capture the return value of `assertThrows` to inspect the exception message afterward. `assertAll` takes a display label and a varargs of lambdas — runs all of them, collects all failures, reports them together. When you have five properties to check on a result object, use `assertAll` so one failure doesn't hide the other four.

AssertJ. Fluent. Start with `assertThat(value)` and chain. `assertThat(result.getTitle()).isEqualTo("Clean Code")`. For collections: `assertThat(books).hasSize(3).extracting(BookDto::getTitle).containsExactlyInAnyOrder(...)` — that's the sort of chained assertion you can't express cleanly with JUnit's built-ins. For exceptions: `assertThatThrownBy(() -> bookService.findById(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("99")`. Better failure messages and more readable chains.

My recommendation: use AssertJ for anything beyond simple equality checks. Both are in `spring-boot-starter-test` — no extra dependency needed.

---

## [46:00–52:00] Slide 10 — Lifecycle Annotations

Four annotations manage the lifecycle of your test class.

`@BeforeAll` runs once before any test method in the class. It must be a static method by default — because JUnit creates a new instance of the test class for each test method by default, so a non-static `@BeforeAll` would run on a different instance than the tests. Use `@BeforeAll` for expensive shared resources: starting a database connection that all tests reuse, loading a large dataset into memory once. Use it sparingly — shared state between tests is a source of subtle, order-dependent bugs.

`@BeforeEach` runs before every single test method. This is where you reset your mocks and create your object under test. Everything in `@BeforeEach` happens fresh for each test. This is by far the most-used lifecycle annotation — most of your setup logic lives here.

`@AfterEach` runs after every test method. Use it to release resources you opened in `@BeforeEach` — files, streams, network connections. With Mockito mocks you rarely need it; Mockito cleans up automatically. But if you're testing file system operations and you create a temporary file in `@BeforeEach`, delete it in `@AfterEach`.

`@AfterAll` runs once after all tests complete. Clean up the shared resource you started in `@BeforeAll`. Also static by default.

Execution order: `@BeforeAll` once, then for each test method: `@BeforeEach`, then the `@Test` method itself, then `@AfterEach`. Finally `@AfterAll` once.

The `@TestInstance(Lifecycle.PER_CLASS)` annotation on the test class tells JUnit to create one instance shared across all methods — removes the static requirement from `@BeforeAll` and `@AfterAll`. Useful when you have shared mutable state that can't easily be made static. The default is `PER_METHOD` — new instance per test — which is the safer option because it prevents any accidental sharing between tests.

---

## [52:00–57:00] Slides 11–12 — @DisplayName, @Nested, and @ParameterizedTest

`@DisplayName`. Test method names like `findById_returnsBook_whenBookExists` are good for developers reading the code, but in a CI test report with three hundred test names, human sentences are cleaner. `@DisplayName("returns BookDto with correct fields when book exists")` produces that readable output. Put it on both the class and the method. The class-level name describes what's being tested; the method-level name describes the specific scenario.

`@Nested` takes readability further. Annotate an inner class with `@Nested` and give it a `@DisplayName`. Group all `findById` tests in one nested class, all `createBook` tests in another. The test report shows a structured hierarchy instead of a flat list — it reads like a requirements document. Nested classes can also have their own `@BeforeEach` that runs after the outer class's `@BeforeEach`. This is powerful for scenario-specific setup: all "createBook with valid input" tests share one context, all "createBook with invalid input" tests share another.

`@ParameterizedTest`. The situation: you have a validation rule that should reject several different invalid inputs. Without parameterized tests, you copy the same test structure three, four, five times and only change the input value. Any change to the test structure requires updating every copy.

`@ValueSource` for a single parameter: list the values in the annotation, and the test runs once per value. The invalid price test runs four times — once for zero, once for negative one, once for negative one hundred, once for the too-large value — with one test method.

`@CsvSource` for multiple parameters: each string is a comma-separated row of arguments. The test runs once per row. Great for testing combinations of title, price, and category.

`@MethodSource` for complex objects: point it at a static method that returns a `Stream<Arguments>`. Each `Arguments.of(...)` call is one test invocation. This is the most flexible option — you can pass fully constructed request objects, expected exception types, expected error messages — anything you can build in Java.

---

## [57:00–60:00] Slides 13–16 — Organization, Coverage, and Summary

Quickly on organization. Mirror your main source tree in `src/test/java`. Name unit tests and slice tests with the `Test` suffix. Name full integration tests — the ones that start a real Spring context and talk to a real database — with `IT` suffix. Maven Surefire picks up `*Test.java` files by default and runs them fast on every build. Maven Failsafe picks up `*IT.java` files and can be run separately in a CI step after build validation. Keeping them separate means your fast feedback loop stays fast.

JaCoCo gives you coverage numbers. Add the plugin to your Maven POM — three executions: `prepare-agent` instruments the bytecode, `report` generates the HTML report after tests run, and `check` enforces a minimum threshold. The visual report at `target/site/jacoco/index.html` is color-coded — green for covered lines, red for uncovered, yellow for partially covered branches. Run `mvn test` to generate it.

But the important thing to understand: coverage measures whether your tests ran the code, not whether they verified anything meaningful. A test that calls every method and asserts nothing gives you 100% line coverage and zero protection. Eighty-five percent with strong assertions beats one hundred percent with no assertions. Target both: coverage as a floor, quality assertions as the actual goal.

Slide 15 shows a complete TDD-built `createBook` method — three behaviors, three tests written first, one minimal implementation that passes all three, then a small refactor. That's the complete loop.

Part 2 starts now. Mockito in depth, Spring slice tests, Testcontainers, WireMock. See you there.
