# Exercise 02 — Parameterized Tests and Test Organization

## Objective
Write `@ParameterizedTest` methods to test multiple inputs with a single test method, and organize tests using nested classes and `@DisplayName`.

## Background
Repeating nearly identical test methods for different inputs is a code smell. JUnit 5's `@ParameterizedTest` lets you define a test once and drive it with multiple data sets via `@ValueSource`, `@CsvSource`, or `@MethodSource`. Well-organized test suites use `@Nested` classes to group related tests and `@DisplayName` to produce readable output in CI reports.

## Requirements

1. Open `starter-code/StringUtils.java` — it has three methods: `isPalindrome(String)`, `countVowels(String)`, and `capitalize(String)`.
2. Open `starter-code/StringUtilsTest.java` and complete every `// TODO`:
   - **Palindrome tests** — use `@ParameterizedTest` with `@CsvSource` supplying at least 4 input/expected pairs (e.g., `"racecar,true"`, `"hello,false"`)
   - **Vowel count tests** — use `@ParameterizedTest` with `@CsvSource` supplying at least 3 input/count pairs (e.g., `"hello,2"`)
   - **Capitalize tests** — use `@ParameterizedTest` with `@ValueSource(strings = {...})` for at least 3 inputs; assert the first character is uppercase
   - **Null/empty edge cases** — group these in a `@Nested` class named `EdgeCaseTests` with at least 2 tests
3. Every test method must have a `@DisplayName`
4. Run `mvn test` — all parameterized tests must pass

## Hints
- `@CsvSource({"input,expected", ...})` provides comma-separated pairs; use method parameters `(String input, boolean expected)` to receive them
- `@ValueSource(strings = {"a","b"})` provides a single value per run; the test method takes one `String` parameter
- `@Nested` classes let you group tests logically — they are regular inner classes annotated with `@Nested`
- Code coverage improves when parameterized tests exercise multiple branches with a single method

## Expected Output

```
Tests run: 12+, Failures: 0, Errors: 0, Skipped: 0

StringUtilsTest
  PalindromeTests
    'racecar' is a palindrome ✔
    'hello' is not a palindrome ✔
    ...
  VowelCountTests
    ...
  EdgeCaseTests
    ...
```
