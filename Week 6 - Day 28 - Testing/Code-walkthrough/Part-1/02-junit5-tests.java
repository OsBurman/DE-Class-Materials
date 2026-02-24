package com.bookstore.service;

// =============================================================================
// JUNIT 5 COMPLETE DEMONSTRATION
// =============================================================================
// This file tests PriceCalculator and BookService using JUnit 5 (Jupiter API).
// It covers every major JUnit 5 feature used in real projects:
//   - @Test, assertions (assertEquals, assertThrows, assertAll, etc.)
//   - @BeforeEach, @AfterEach, @BeforeAll, @AfterAll
//   - @DisplayName for readable test names
//   - @ParameterizedTest (ValueSource, CsvSource, MethodSource)
//   - @Nested for organizing related tests
//   - @Disabled, @Tag for categorization
//   - Arrange-Act-Assert pattern throughout
//   - Test suites via @Suite
// =============================================================================

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


// =============================================================================
// PRODUCTION CLASSES (normally in src/main/java — included here for clarity)
// =============================================================================

class Book {
    private String title;
    private String author;
    private BigDecimal price;
    private String genre;
    private boolean available;

    public Book(String title, String author, BigDecimal price) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be blank");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.title = title;
        this.author = author;
        this.price = price;
        this.available = true;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

enum MemberType { STANDARD, PREMIUM, VIP }

class PriceCalculator {
    public BigDecimal calculatePrice(BigDecimal originalPrice, MemberType memberType) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        return switch (memberType) {
            case PREMIUM -> originalPrice.multiply(new BigDecimal("0.90")).setScale(2, java.math.RoundingMode.HALF_UP);
            case VIP     -> originalPrice.multiply(new BigDecimal("0.80")).setScale(2, java.math.RoundingMode.HALF_UP);
            case STANDARD-> originalPrice.setScale(2, java.math.RoundingMode.HALF_UP);
        };
    }
}


// =============================================================================
// SECTION 1: BASIC TEST CLASS STRUCTURE
// =============================================================================

@DisplayName("Book Entity Tests")      // class-level display name shown in test reports
class BookTest {

    // =========================================================================
    // SECTION 2: LIFECYCLE ANNOTATIONS
    // =========================================================================
    // Execution order: @BeforeAll → [@BeforeEach → @Test → @AfterEach] × N → @AfterAll

    // @BeforeAll — runs ONCE before all tests in this class
    // Must be static (unless @TestInstance(Lifecycle.PER_CLASS) is used)
    @BeforeAll
    static void initializeTestEnvironment() {
        System.out.println("=== BookTest suite starting ===");
        // Use for: starting shared resources (e.g., test containers, database connections)
        // that are expensive to create and safe to share across tests
    }

    // @AfterAll — runs ONCE after all tests in this class complete
    @AfterAll
    static void tearDownTestEnvironment() {
        System.out.println("=== BookTest suite complete ===");
        // Use for: shutting down shared resources, closing connections
    }

    // @BeforeEach — runs before EVERY single test method
    // Instance variables declared here are freshly initialized for each test
    @BeforeEach
    void setUp() {
        // Create fresh objects before each test so tests don't share state
        // ⚠️ Sharing mutable state between tests causes flaky tests
        System.out.println("--- Preparing test ---");
    }

    // @AfterEach — runs after EVERY single test method
    @AfterEach
    void tearDown() {
        System.out.println("--- Test complete ---");
        // Use for: clearing caches, resetting counters, cleaning temp files
    }


    // =========================================================================
    // SECTION 3: @Test AND ASSERTIONS
    // =========================================================================

    @Test
    @DisplayName("Book should be created with valid title and price")
    void shouldCreateBookWithValidInputs() {
        // ARRANGE
        String title = "Clean Code";
        String author = "Robert C. Martin";
        BigDecimal price = new BigDecimal("35.99");

        // ACT
        Book book = new Book(title, author, price);

        // ASSERT — basic equality
        assertEquals(title, book.getTitle(), "Title should match");   // message is shown on failure
        assertEquals(author, book.getAuthor());
        assertEquals(0, book.getPrice().compareTo(price));            // BigDecimal compare by value
        assertTrue(book.isAvailable(), "New books should be available by default");
    }

    @Test
    @DisplayName("Book title should not be null or blank")
    void shouldThrowWhenTitleIsBlank() {
        // ARRANGE
        String blankTitle = "   ";
        BigDecimal price = new BigDecimal("10.00");

        // ACT + ASSERT — assertThrows catches the expected exception
        // If the code does NOT throw, the test FAILS
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Book(blankTitle, "Author", price),
            "Should throw when title is blank"
        );

        // Optionally verify the exception message
        assertTrue(exception.getMessage().contains("Title cannot be blank"));
    }

    @Test
    @DisplayName("Book with null title should throw IllegalArgumentException")
    void shouldThrowWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Book(null, "Author", new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Book should not allow negative price")
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> new Book("Title", "Author", new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("assertAll — verify multiple conditions atomically")
    void shouldVerifyMultiplePropertiesAtOnce() {
        // ARRANGE
        Book book = new Book("Effective Java", "Joshua Bloch", new BigDecimal("45.00"));

        // ASSERT — assertAll runs ALL assertions even if the first one fails
        // Without assertAll, a failing assertion would abort and hide other failures
        assertAll("book properties",
            () -> assertEquals("Effective Java", book.getTitle()),
            () -> assertEquals("Joshua Bloch", book.getAuthor()),
            () -> assertTrue(book.isAvailable()),
            () -> assertNotNull(book.getPrice()),
            () -> assertEquals(0, book.getPrice().compareTo(new BigDecimal("45.00")))
        );
    }

    @Test
    @DisplayName("assertNotNull and assertNull examples")
    void shouldDemonstrateNullChecks() {
        Book book = new Book("Test", "Author", new BigDecimal("10.00"));

        assertNotNull(book.getTitle());        // passes — title was set
        assertNotNull(book.getPrice());        // passes — price was set
        assertNull(book.getGenre());           // passes — genre was never set (null field)
    }

    private String genre;
    private Book getGenre() { return null; }

    @Test
    @DisplayName("assertSame vs assertEquals — reference vs value equality")
    void shouldDifferentiateReferenceAndValueEquality() {
        Book book1 = new Book("Title", "Author", new BigDecimal("10.00"));
        Book book2 = new Book("Title", "Author", new BigDecimal("10.00"));
        Book book3 = book1;   // same reference

        // assertEquals uses .equals() — value comparison
        assertEquals("Title", book1.getTitle());

        // assertSame uses == — reference comparison
        assertSame(book1, book3);      // passes — same object reference
        assertNotSame(book1, book2);   // passes — different objects, even with same data
    }

    @Test
    @DisplayName("Assumptions — skip test based on runtime condition")
    void shouldSkipIfNotRunningOnCI() {
        // assumeTrue — if false, test is SKIPPED (not failed)
        // Useful for tests that only make sense in certain environments
        assumeTrue(System.getenv("CI") != null,
            "This test only runs on CI (skipping locally)");

        // If we get here, CI env var is set
        assertTrue(true, "Reached CI-only code");
    }

    @Test
    @Disabled("Disabled until BUG-1234 is fixed")  // skips this test — explains why
    void shouldHandleEdgeCaseXYZ() {
        // This test is intentionally disabled — it will be shown as SKIPPED in reports
        fail("This should not run");
    }

    @Test
    @Tag("slow")   // tag for filtering — e.g., run only @Tag("fast") on CI
    @Tag("database")
    void shouldBeTaggedForFiltering() {
        // Run specific tags: mvn test -Dgroups=fast
        // Exclude tags:      mvn test -DexcludedGroups=slow
        assertTrue(true);
    }
}


// =============================================================================
// SECTION 4: @ParameterizedTest — Test with Multiple Input Values
// =============================================================================

@DisplayName("PriceCalculator Parameterized Tests")
class PriceCalculatorTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();   // fresh instance before each test
    }

    // --- @ValueSource — single parameter from a list of literals ---
    @ParameterizedTest(name = "Price ${0} should always produce a non-negative result")
    @ValueSource(doubles = {0.0, 1.0, 9.99, 100.0, 999.99})
    @DisplayName("Non-negative prices should return non-negative results for STANDARD members")
    void shouldReturnNonNegativeForValidPrices(double priceValue) {
        // ARRANGE
        BigDecimal price = BigDecimal.valueOf(priceValue);

        // ACT
        BigDecimal result = calculator.calculatePrice(price, MemberType.STANDARD);

        // ASSERT
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0,
            "Result should be non-negative for price: " + priceValue);
    }

    // --- @CsvSource — multiple parameters per test case ---
    @ParameterizedTest(name = "{0} member gets {2}% discount on ${1}")
    @CsvSource({
        "STANDARD, 100.00, 100.00",    // no discount
        "PREMIUM,  100.00,  90.00",    // 10% off
        "VIP,      100.00,  80.00",    // 20% off
        "PREMIUM,   50.00,  45.00",    // 10% of 50
        "VIP,       50.00,  40.00",    // 20% of 50
    })
    @DisplayName("Discount should be correctly applied per member type")
    void shouldApplyCorrectDiscount(String memberTypeStr, String originalPrice, String expectedPrice) {
        // ARRANGE
        MemberType memberType = MemberType.valueOf(memberTypeStr);
        BigDecimal price = new BigDecimal(originalPrice);
        BigDecimal expected = new BigDecimal(expectedPrice);

        // ACT
        BigDecimal result = calculator.calculatePrice(price, memberType);

        // ASSERT
        assertEquals(0, expected.compareTo(result),
            () -> "Expected " + expected + " for " + memberType + " but got " + result);
    }

    // --- @MethodSource — parameters from a static method (complex objects) ---
    @ParameterizedTest(name = "Test case: {0}")
    @MethodSource("provideDiscountTestCases")
    @DisplayName("Discount calculator handles all member types correctly")
    void shouldHandleAllMemberTypes(String testName, BigDecimal price,
                                    MemberType type, BigDecimal expected) {
        BigDecimal result = calculator.calculatePrice(price, type);
        assertEquals(0, expected.compareTo(result), testName);
    }

    // Static method providing test data — returns Stream<Arguments>
    static Stream<Arguments> provideDiscountTestCases() {
        return Stream.of(
            Arguments.of("Standard member - no discount",
                new BigDecimal("100.00"), MemberType.STANDARD, new BigDecimal("100.00")),
            Arguments.of("Premium member - 10% discount",
                new BigDecimal("200.00"), MemberType.PREMIUM, new BigDecimal("180.00")),
            Arguments.of("VIP member - 20% discount",
                new BigDecimal("500.00"), MemberType.VIP, new BigDecimal("400.00")),
            Arguments.of("VIP with decimal price",
                new BigDecimal("33.33"), MemberType.VIP, new BigDecimal("26.66"))
        );
    }

    // --- @EnumSource — test with every enum value ---
    @ParameterizedTest(name = "MemberType.{0} should not return a negative price")
    @EnumSource(MemberType.class)   // iterates over all MemberType enum values
    @DisplayName("No member type should ever produce a negative price")
    void shouldNeverReturnNegativePriceForAnyMemberType(MemberType memberType) {
        BigDecimal result = calculator.calculatePrice(new BigDecimal("50.00"), memberType);
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
    }

    // --- Exception testing in parameterized test ---
    @ParameterizedTest(name = "Negative price {0} should throw")
    @ValueSource(strings = {"-0.01", "-1.00", "-100.00"})
    @DisplayName("Negative prices should throw IllegalArgumentException")
    void shouldThrowForNegativePrices(String priceStr) {
        BigDecimal negativePrice = new BigDecimal(priceStr);
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculatePrice(negativePrice, MemberType.STANDARD));
    }
}


// =============================================================================
// SECTION 5: @Nested — Organize related tests in groups
// =============================================================================

@DisplayName("Book Availability Tests")
class BookAvailabilityTest {

    // @Nested creates a logical sub-group of tests — shown as a tree in the IDE
    @Nested
    @DisplayName("When book is initially created")
    class WhenNewlyCreated {

        private Book book;

        @BeforeEach
        void setUp() {
            book = new Book("Domain-Driven Design", "Eric Evans", new BigDecimal("55.00"));
        }

        @Test
        @DisplayName("should be available")
        void shouldBeAvailable() {
            assertTrue(book.isAvailable());
        }

        @Test
        @DisplayName("should have the correct title")
        void shouldHaveCorrectTitle() {
            assertEquals("Domain-Driven Design", book.getTitle());
        }
    }

    @Nested
    @DisplayName("When book is marked unavailable")
    class WhenMarkedUnavailable {

        private Book book;

        @BeforeEach
        void setUp() {
            book = new Book("Test Title", "Author", new BigDecimal("20.00"));
            book.setAvailable(false);  // ACT at setup level for this nested context
        }

        @Test
        @DisplayName("should report as unavailable")
        void shouldBeUnavailable() {
            assertFalse(book.isAvailable());
        }

        @Test
        @DisplayName("other properties should be unchanged")
        void shouldRetainOtherProperties() {
            assertEquals("Test Title", book.getTitle());
            assertNotNull(book.getPrice());
        }
    }
}


// =============================================================================
// SECTION 6: Test Suite — Run multiple test classes together
// =============================================================================

@Suite
@SelectClasses({
    BookTest.class,
    PriceCalculatorTest.class,
    BookAvailabilityTest.class
})
@DisplayName("Bookstore Unit Test Suite")
class BookstoreUnitTestSuite {
    // Empty — @Suite configuration only
    // Run with: mvn test -Dtest=BookstoreUnitTestSuite
}
