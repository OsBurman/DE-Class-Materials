# TDD Principles, Red-Green-Refactor & JUnit 5 Architecture

## Why Test?

Without tests you have **opinions**. With tests you have **evidence**.

Tests give you:
- **Confidence to change code** — if all tests pass after a refactor, you didn't break anything
- **Documentation** — well-named tests describe exactly how the system should behave
- **Design feedback** — code that is hard to test is usually badly designed
- **Regression protection** — catch bugs before they reach production

---

## The Test Pyramid

```
         /\
        /  \
       / E2E\         ← Few, slow, expensive (browser/full-system tests)
      /──────\
     / Integr \       ← Some (test multiple components together)
    /──────────\
   /    Unit    \     ← Many, fast, isolated (one class / one function)
  ──────────────────
```

- **Unit tests:** Test a single class/function in isolation. No database, no network, no Spring context. Sub-millisecond speed. Should make up the bulk of your suite (70–80%).
- **Integration tests:** Test how components work together. May use a real database or Spring context. Slower (seconds). About 15–20%.
- **End-to-end (E2E) tests:** Test the whole system from UI to database. Very slow. ~5%.

> **Rule of thumb:** Run unit tests constantly (on every save). Run integration tests on CI. Run E2E tests before release.

---

## Test-Driven Development (TDD)

TDD flips the usual order: **write the test BEFORE writing the implementation**.

### The Red-Green-Refactor Cycle

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   🔴 RED    → Write a failing test for the next small feature  │
│              (test can't pass — the code doesn't exist yet)     │
│                                                                 │
│   🟢 GREEN  → Write the MINIMUM code to make the test pass     │
│              (it doesn't have to be pretty — just green)        │
│                                                                 │
│   🔵 REFACTOR → Clean up the code without breaking tests       │
│              (rename, extract method, remove duplication)       │
│                    ↑                                            │
│                    └──── repeat for the next feature ──────────┘
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Why Write Tests First?

1. **Forces you to think about the API before writing it** — what should this method accept? What should it return? What edge cases exist?
2. **Prevents over-engineering** — you only write code needed to pass the test
3. **100% test coverage by construction** — you can't have untested code because you wrote the test first
4. **Instant regression detection** — every test you've ever written runs every time

### TDD Example Walkthrough

**Scenario:** We need a `PriceCalculator` class that applies discounts.

**Step 1 — 🔴 RED: Write the test first**
```java
@Test
void shouldApply10PercentDiscountForPremiumMembers() {
    PriceCalculator calculator = new PriceCalculator();
    BigDecimal discounted = calculator.calculatePrice(new BigDecimal("100.00"), MemberType.PREMIUM);
    assertEquals(new BigDecimal("90.00"), discounted);
}
// This test FAILS because PriceCalculator doesn't exist yet ✅
```

**Step 2 — 🟢 GREEN: Write minimal code to pass**
```java
public class PriceCalculator {
    public BigDecimal calculatePrice(BigDecimal price, MemberType memberType) {
        if (memberType == MemberType.PREMIUM) {
            return price.multiply(new BigDecimal("0.90"));
        }
        return price;
    }
}
// Test passes now ✅
```

**Step 3 — 🔵 REFACTOR: Clean up**
```java
public class PriceCalculator {
    private static final BigDecimal PREMIUM_DISCOUNT = new BigDecimal("0.10");

    public BigDecimal calculatePrice(BigDecimal originalPrice, MemberType memberType) {
        if (memberType == MemberType.PREMIUM) {
            BigDecimal discount = originalPrice.multiply(PREMIUM_DISCOUNT);
            return originalPrice.subtract(discount);
        }
        return originalPrice;
    }
}
// Tests still pass. Code is cleaner ✅
```

---

## Unit Testing Fundamentals

### What Is a Unit Test?

A unit test verifies that a single **unit of code** (usually one method or class) behaves correctly in isolation.

**"In isolation" means:**
- No real database (use mocks or in-memory)
- No real HTTP calls (use mocks)
- No Spring context spinning up (just `new ClassName()`)
- No filesystem I/O (unless testing file utilities)

### Good Unit Test Properties (F.I.R.S.T.)

| Property    | Meaning                                                              |
|-------------|----------------------------------------------------------------------|
| **F**ast    | Runs in milliseconds — never blocks on network or disk              |
| **I**solated| Each test is independent — can run in any order, result unchanged    |
| **R**epeatable | Same result every run — no flakiness based on time or environment|
| **S**elf-validating | Pass or fail with no manual inspection needed               |
| **T**imely  | Written when the code is written (or before, with TDD)              |

---

## JUnit 5 Architecture

JUnit 5 is the current major version. It is NOT a single jar — it is a platform with three sub-projects:

```
JUnit 5
├── JUnit Platform    — Foundation layer: discovers and launches tests
│                       Used by IDEs (IntelliJ, Eclipse) and build tools (Maven, Gradle)
│                       Defines the TestEngine API
│
├── JUnit Jupiter     — The new JUnit 5 API for writing tests
│                       @Test, @BeforeEach, @ParameterizedTest, @ExtendWith, etc.
│                       This is what you actually use in your test classes
│
└── JUnit Vintage     — Backwards compatibility bridge
                        Runs JUnit 3 and JUnit 4 tests on the JUnit 5 Platform
                        Useful during migration of legacy test suites
```

### Maven Dependencies (Spring Boot auto-configures this)

```xml
<!-- spring-boot-starter-test includes JUnit 5, Mockito, AssertJ, Hamcrest, and more -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 standalone (if not using Spring Boot) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

```xml
<!-- Maven Surefire plugin must be version 2.22+ to run JUnit 5 tests -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
</plugin>
```

---

## Arrange-Act-Assert (AAA) Pattern

Every well-structured test follows this three-part layout:

```java
@Test
void exampleTest() {
    // ARRANGE — set up data, create objects, prepare the world
    Book book = new Book("Clean Code", "Robert Martin", new BigDecimal("35.00"));
    PriceCalculator calculator = new PriceCalculator();

    // ACT — call the method under test (ONE action per test)
    BigDecimal result = calculator.calculatePrice(book.getPrice(), MemberType.PREMIUM);

    // ASSERT — verify the expected outcome
    assertEquals(new BigDecimal("31.50"), result);
}
```

Rules:
- One `// ARRANGE` block per test
- ONE `// ACT` — test one thing at a time
- One or more `// ASSERT` — all assertions verify the single action

---

## Code Coverage Metrics

Code coverage measures how much of your production code is exercised by tests.

### Types of Coverage

| Type              | What It Measures                                              | Example                                  |
|-------------------|---------------------------------------------------------------|------------------------------------------|
| **Line coverage** | % of lines executed by at least one test                      | 85% of 200 lines were run                |
| **Branch coverage**| % of branches (if/else paths) taken by tests                 | both `if` and `else` paths were tested   |
| **Method coverage**| % of methods called by at least one test                     | 90% of methods were invoked              |
| **Statement coverage** | % of individual statements executed                     | Similar to line, more granular           |

### Industry Targets

- **>80% line coverage** — minimum acceptable for a production codebase
- **>85% branch coverage** — recommended target (this day's objective)
- **100% is not always the goal** — getters/setters and framework boilerplate don't need testing
- Focus coverage efforts on **business logic, edge cases, and error paths**

### Tools

- **JaCoCo** — the standard Java coverage tool. Integrates with Maven (`mvn test jacoco:report`).
- IntelliJ built-in coverage runner
- SonarQube for team-wide quality gates

```xml
<!-- JaCoCo Maven plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <!-- Enforce minimum coverage threshold -->
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
                                <minimum>0.85</minimum>  <!-- fail build if < 85% line coverage -->
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## Test Organization

### File Layout Convention

```
src/
├── main/java/com/bookstore/
│   ├── service/BookService.java
│   └── entity/Book.java
└── test/java/com/bookstore/          ← mirrors main package structure
    ├── service/BookServiceTest.java  ← tests for BookService
    └── entity/BookTest.java          ← tests for Book entity logic
```

### Naming Conventions

| Convention                    | Example                                            |
|-------------------------------|----------------------------------------------------|
| Test class name               | `{ClassUnderTest}Test`                             |
| Test method — what + when     | `shouldReturnDiscountedPrice_whenPremiumMember()`  |
| Test method — should pattern  | `should_applyDiscount_for_premiumMembers()`        |
| Test method — given/when/then | `givenPremiumMember_whenCalculatePrice_thenDiscount()` |

### Test Suites

```java
// @Suite groups multiple test classes to run together
@Suite
@SelectClasses({BookServiceTest.class, BookRepositoryTest.class, PriceCalculatorTest.class})
class BookstoreTestSuite {
    // empty — just configuration
}

// Or select by package
@Suite
@SelectPackages("com.bookstore.service")
class ServiceTestSuite {}
```
