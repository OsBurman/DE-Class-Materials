# Exercise 01 — JUnit 5 Basics: Assertions and Lifecycle

## Objective
Write JUnit 5 unit tests using the Arrange-Act-Assert pattern, core assertion methods, and lifecycle annotations to verify a simple `Calculator` class.

## Background
Test Driven Development (TDD) follows a Red-Green-Refactor cycle: write a failing test (Red), write the minimum code to make it pass (Green), then improve the code without breaking tests (Refactor). JUnit 5 (Jupiter) is the standard Java testing framework. Every test should follow the **Arrange-Act-Assert** (AAA) pattern: set up the state, invoke the behaviour, assert the outcome.

## Requirements

1. Open `starter-code/Calculator.java` — it contains a `Calculator` class with four methods: `add`, `subtract`, `multiply`, and `divide`.
2. Open `starter-code/CalculatorTest.java` and complete every `// TODO` so that:
   - `@BeforeAll` prints `"=== Calculator tests starting ==="` (must be `static`)
   - `@BeforeEach` creates a fresh `Calculator` instance before each test
   - `@AfterEach` prints `"Test completed"` after each test
   - `@AfterAll` prints `"=== All tests done ==="` (must be `static`)
3. Write a `@Test` method named `testAdd` that asserts `add(3, 4)` returns `7`
4. Write a `@Test` method named `testSubtract` that asserts `subtract(10, 4)` returns `6`
5. Write a `@Test` method named `testMultiply` that asserts `multiply(3, 5)` returns `15`
6. Write a `@Test` method named `testDivide` that asserts `divide(10, 2)` returns `5.0`
7. Write a `@Test` method named `testDivideByZero` that asserts `divide(5, 0)` throws `ArithmeticException`
8. Add a `@DisplayName` to each test with a human-readable sentence (e.g., `"3 + 4 should equal 7"`)
9. Run all tests with `mvn test` — all 5 should pass

## Hints
- Use `assertEquals(expected, actual)` for value comparisons; use `assertThrows(ExceptionType.class, () -> ...)` for expected exceptions
- `@BeforeAll` and `@AfterAll` methods must be `static` (unless using `@TestInstance(Lifecycle.PER_CLASS)`)
- `@BeforeEach` runs before **every** test method — use it to reset shared state
- The AAA pattern: Arrange (create objects/inputs), Act (call the method), Assert (check the result)

## Expected Output

When you run `mvn test` the JUnit output should show 5 passing tests, e.g.:

```
=== Calculator tests starting ===
Test completed
Test completed
Test completed
Test completed
Test completed
=== All tests done ===

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```
