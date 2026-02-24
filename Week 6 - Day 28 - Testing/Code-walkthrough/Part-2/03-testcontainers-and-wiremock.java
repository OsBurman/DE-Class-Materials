package com.bookstore.integration;

// =============================================================================
// TESTCONTAINERS & WIREMOCK — Real Databases and HTTP Mocking in Tests
// =============================================================================
//
// PROBLEMS WITH H2 FOR INTEGRATION TESTING:
//   - H2 doesn't support all PostgreSQL SQL syntax
//   - PostgreSQL-specific features (JSONB, window functions, ILIKE, sequences,
//     ON CONFLICT DO UPDATE) won't work in H2
//   - Flyway/Liquibase migration scripts may contain PG-specific DDL that fails
//   - Your test may pass on H2 but BREAK in production on real PostgreSQL
//
// SOLUTION: Testcontainers
//   - Spins up REAL PostgreSQL (or MySQL, Redis, Kafka, etc.) in a Docker container
//   - Container starts before tests, is destroyed after
//   - Tests run against the identical database engine as production
//   - Requires Docker running on the machine
//
// WireMock — External HTTP service mocking:
//   - Replace real 3rd-party HTTP calls (payment gateway, email API, ISBN lookup)
//   - Simulate failures: 503 errors, timeouts, malformed responses
//   - Verify that your code made the expected HTTP calls
// =============================================================================

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import com.bookstore.client.IsbnLookupClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;


// =============================================================================
// SECTION 1: Testcontainers — PostgreSQL Integration Tests
// =============================================================================
// Maven dependencies needed:
//
//   <dependency>
//       <groupId>org.testcontainers</groupId>
//       <artifactId>junit-jupiter</artifactId>
//       <scope>test</scope>
//   </dependency>
//   <dependency>
//       <groupId>org.testcontainers</groupId>
//       <artifactId>postgresql</artifactId>
//       <scope>test</scope>
//   </dependency>
//   <!-- BOM manages all testcontainers versions -->
//   <dependencyManagement>
//       <dependencies>
//           <dependency>
//               <groupId>org.testcontainers</groupId>
//               <artifactId>testcontainers-bom</artifactId>
//               <version>1.19.3</version>
//               <type>pom</type>
//               <scope>import</scope>
//           </dependency>
//       </dependencies>
//   </dependencyManagement>
// =============================================================================

@Testcontainers               // activates Testcontainers JUnit 5 extension
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
@DisplayName("PostgreSQL Testcontainers Integration Tests")
class BookPostgresIntegrationTest {

    // @Container — JUnit 5 manages the container lifecycle:
    //   static field  → ONE container shared across ALL tests in the class (faster, recommended)
    //   instance field → NEW container per test method (slower, more isolated)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        // Optional customization:
        .withDatabaseName("bookstore_test")
        .withUsername("testuser")
        .withPassword("testpass");
        // .withInitScript("init-test-data.sql")  // run a SQL script on startup
        // .withReuse(true)   // keep container alive between test runs (requires ~/.testcontainers.properties)

    // @DynamicPropertySource — overrides Spring datasource properties AFTER the container
    // starts, injecting the actual host/port Docker assigned to the container.
    // This is necessary because Docker assigns a random port — you can't hardcode it.
    @DynamicPropertySource
    static void configurePostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Override H2 driver with real PostgreSQL driver
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("Container should be running before tests execute")
    void containerShouldBeRunning() {
        // Basic smoke test — if this passes, Docker is running and PG is healthy
        assertTrue(postgres.isRunning());
        System.out.println("PostgreSQL container URL: " + postgres.getJdbcUrl());
    }

    @Test
    @DisplayName("Should save and retrieve book from real PostgreSQL")
    void shouldSaveAndRetrieveFromPostgres() {
        // ARRANGE — a book that uses a PostgreSQL-specific constraint (unique ISBN)
        Book book = new Book("Effective Java", "Joshua Bloch", new BigDecimal("49.99"));
        book.setIsbn("978-0-134-68599-1");

        // ACT
        Book saved = bookRepository.save(book);

        // ASSERT — ID auto-generated by PostgreSQL sequence (not H2 auto-increment)
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Optional<Book> retrieved = bookRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Effective Java", retrieved.get().getTitle());
    }

    @Test
    @DisplayName("PostgreSQL ILIKE should work for case-insensitive search")
    void shouldSupportILikeSearch() {
        // ARRANGE — save a book with mixed-case title
        Book book = new Book("Clean Code", "Robert Martin", new BigDecimal("35.00"));
        book.setIsbn("978-0-132-35088-4");
        bookRepository.save(book);

        // ACT — uses JPQL with LOWER() or native query with ILIKE
        // NOTE: This would FAIL with H2 if you wrote: WHERE title ILIKE :pattern
        // (H2 does not support ILIKE — PostgreSQL-specific operator)
        List<Book> results = bookRepository.searchByTitleNative("clean");  // uses PG ILIKE

        // ASSERT
        assertFalse(results.isEmpty());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should handle database constraint violation correctly")
    void shouldRaiseConstraintViolationOnDuplicateIsbn() {
        // ARRANGE — same ISBN twice
        Book book1 = new Book("Book One", "Author A", new BigDecimal("20.00"));
        book1.setIsbn("978-DUPLICATE");
        bookRepository.save(book1);

        Book book2 = new Book("Book Two", "Author B", new BigDecimal("25.00"));
        book2.setIsbn("978-DUPLICATE");   // same ISBN — violates unique constraint

        // ACT + ASSERT — real PG raises DataIntegrityViolationException
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
            () -> {
                bookRepository.save(book2);
                bookRepository.flush();   // must flush to trigger the constraint check
            }
        );
    }

    @Test
    @DisplayName("Flyway migrations should run successfully on test container")
    void flywayMigrationsShouldRunOnTestContainer() {
        // This test simply verifies the application context loaded without errors.
        // If Flyway migrations contain PostgreSQL-specific DDL (JSONB, sequences, etc.),
        // they will PASS here on real PG but would FAIL with H2.
        // The fact that the context loaded (and the container started) is the assertion.
        assertNotNull(bookRepository);
    }
}


// =============================================================================
// SECTION 2: WireMock — Mocking External HTTP Services
// =============================================================================
// Use case: Your BookService calls an external ISBN lookup API.
// You don't want to hit the real API in tests — it's slow, unreliable,
// costs money, or has rate limits.
//
// WireMock starts a local HTTP server and lets you:
//   - Define stub responses for specific URLs/methods
//   - Verify your code made the expected requests
//   - Simulate failure scenarios
//
// Maven dependency:
//   <dependency>
//       <groupId>com.github.tomakehurst</groupId>
//       <artifactId>wiremock-jre8</artifactId>   <!-- or wiremock-standalone -->
//       <version>2.35.0</version>
//       <scope>test</scope>
//   </dependency>
//   OR (Spring Boot 3.x compatible):
//   <dependency>
//       <groupId>org.springframework.cloud</groupId>
//       <artifactId>spring-cloud-contract-wiremock</artifactId>
//       <scope>test</scope>
//   </dependency>
// =============================================================================

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("WireMock External API Tests")
class IsbnLookupServiceWireMockTest {

    // @RegisterExtension — starts WireMock server before tests, stops after.
    // wireMockConfig().dynamicPort() assigns a random available port.
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    @Autowired
    private IsbnLookupClient isbnLookupClient;

    @DynamicPropertySource
    static void configureWireMockBaseUrl(DynamicPropertyRegistry registry) {
        // Point the IsbnLookupClient's base URL to WireMock's local server
        registry.add("isbn.lookup.base-url", wireMockServer::baseUrl);
    }

    // =========================================================================
    // Stubbing HTTP responses
    // =========================================================================

    @Test
    @DisplayName("Should return book details when ISBN API responds with 200")
    void shouldReturnBookDetailsFromIsbnApi() {
        // ARRANGE — define what WireMock should return when this URL is called
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "isbn": "978-0-132-35088-4",
                                "title": "Clean Code",
                                "pageCount": 431,
                                "language": "English"
                            }
                            """)
                )
        );

        // ACT — client makes an actual HTTP call to WireMock's port
        BookDetails details = isbnLookupClient.lookupByIsbn("978-0-132-35088-4");

        // ASSERT — response was deserialized correctly
        assertNotNull(details);
        assertEquals("Clean Code", details.getTitle());
        assertEquals(431, details.getPageCount());
    }

    @Test
    @DisplayName("Should return null when ISBN is not found (404)")
    void shouldReturnNullWhenIsbnNotFound() {
        // ARRANGE
        wireMockServer.stubFor(
            get(urlPathMatching("/api/isbn/.*"))   // matches any ISBN path
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withBody("{\"error\": \"ISBN not found\"}")
                )
        );

        // ACT + ASSERT — client should handle 404 gracefully
        BookDetails details = isbnLookupClient.lookupByIsbn("978-0-000-00000-0");
        assertNull(details);
    }

    // =========================================================================
    // Simulating failures
    // =========================================================================

    @Test
    @DisplayName("Should throw exception when ISBN API returns 503 Service Unavailable")
    void shouldHandleServiceUnavailable() {
        // ARRANGE
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .willReturn(
                    aResponse()
                        .withStatus(503)
                        .withBody("Service Unavailable")
                )
        );

        // ACT + ASSERT — our client should wrap 5xx into a known exception
        assertThrows(ExternalApiException.class,
            () -> isbnLookupClient.lookupByIsbn("978-0-132-35088-4")
        );
    }

    @Test
    @DisplayName("Should throw exception when ISBN API times out")
    void shouldHandleTimeout() {
        // ARRANGE — add a delay longer than the client's timeout setting
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("{}")
                        .withFixedDelay(5000)   // 5 second delay
                )
        );

        // ACT + ASSERT — RestTemplate/WebClient timeout is typically 3s; this should timeout
        assertThrows(RuntimeException.class,
            () -> isbnLookupClient.lookupByIsbn("978-0-132-35088-4")
        );
    }

    @Test
    @DisplayName("Should handle malformed JSON response gracefully")
    void shouldHandleMalformedJsonResponse() {
        // ARRANGE
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("THIS IS NOT VALID JSON {{{")  // malformed
                )
        );

        // ACT + ASSERT — client should handle deserialization failure
        assertThrows(RuntimeException.class,
            () -> isbnLookupClient.lookupByIsbn("978-0-132-35088-4")
        );
    }

    // =========================================================================
    // Verifying HTTP calls were made
    // =========================================================================

    @Test
    @DisplayName("Should verify the correct URL was called exactly once")
    void shouldVerifyHttpCallWasMade() {
        // ARRANGE
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-201-61622-4"))
                .willReturn(aResponse().withStatus(200).withBody("{}"))
        );

        // ACT
        isbnLookupClient.lookupByIsbn("978-0-201-61622-4");

        // ASSERT — verify WireMock received exactly 1 request to this URL
        wireMockServer.verify(
            exactly(1),
            getRequestedFor(urlEqualTo("/api/isbn/978-0-201-61622-4"))
        );
    }

    @Test
    @DisplayName("Should verify correct headers were sent to ISBN API")
    void shouldSendCorrectHeaders() {
        // ARRANGE
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-201-61622-4"))
                .withHeader("Accept", equalTo("application/json"))   // require this header
                .withHeader("X-Api-Key", matching(".*"))              // any non-empty API key
                .willReturn(aResponse().withStatus(200).withBody("{}"))
        );

        // ACT
        isbnLookupClient.lookupByIsbn("978-0-201-61622-4");

        // ASSERT — this passes only if the stub was matched (i.e., headers were correct)
        wireMockServer.verify(
            getRequestedFor(urlEqualTo("/api/isbn/978-0-201-61622-4"))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

    @Test
    @DisplayName("Should call fallback on first failure, then succeed on retry")
    void shouldRetryOnFirstFailure() {
        // ARRANGE — first call returns 503, second call returns 200
        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("Retry")   // advance to next state after this response
        );

        wireMockServer.stubFor(
            get(urlEqualTo("/api/isbn/978-0-132-35088-4"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Retry")
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("{\"isbn\":\"978-0-132-35088-4\",\"title\":\"Clean Code\"}"))
        );

        // ACT — if the client has retry logic (e.g., Spring Retry), the second call succeeds
        BookDetails details = isbnLookupClient.lookupByIsbnWithRetry("978-0-132-35088-4");

        // ASSERT
        assertNotNull(details);
        assertEquals("Clean Code", details.getTitle());

        // Verify it was called twice total (1 failure + 1 retry)
        wireMockServer.verify(
            exactly(2),
            getRequestedFor(urlEqualTo("/api/isbn/978-0-132-35088-4"))
        );
    }
}


// =============================================================================
// SECTION 3: Combined — Testcontainers + WireMock in One Test Class
// =============================================================================
// A realistic integration test: real database + mocked external service

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@DisplayName("Combined Testcontainers + WireMock Tests")
class BookFullIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Real PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // WireMock for external HTTP
        registry.add("isbn.lookup.base-url", wireMock::baseUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        wireMock.resetAll();   // clear all stubs between tests
    }

    @Test
    @DisplayName("Creating a book should enrich it with ISBN data from external API")
    void shouldEnrichBookWithIsbnData() {
        // ARRANGE — stub the ISBN API
        wireMock.stubFor(
            get(urlPathMatching("/api/isbn/.*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                        {
                            "isbn": "978-0-132-35088-4",
                            "pageCount": 431,
                            "language": "English",
                            "publisher": "Prentice Hall"
                        }
                        """))
        );

        // ACT — create a book; the service should call the ISBN API to enrich it
        var request = new java.util.HashMap<String, Object>();
        request.put("title", "Clean Code");
        request.put("author", "Robert Martin");
        request.put("price", 35.00);
        request.put("isbn", "978-0-132-35088-4");

        ResponseEntity<Book> response = restTemplate.postForEntity(
            "/api/v1/books", request, Book.class
        );

        // ASSERT — book was saved to real PostgreSQL AND enriched with external data
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Book created = response.getBody();
        assertNotNull(created);
        assertEquals(431, created.getPageCount());   // set from ISBN API response
        assertEquals("English", created.getLanguage());

        // Verify the ISBN API was called
        wireMock.verify(
            postRequestedFor(urlEqualTo("/api/isbn/978-0-132-35088-4"))
        );
    }
}


// =============================================================================
// SECTION 4: Code Coverage — JaCoCo Targets and Strategies
// =============================================================================
/*
Coverage targets:
  - Line coverage:   >= 85%   (every statement line covered)
  - Branch coverage: >= 80%   (both sides of if/else, switch, try/catch covered)
  - Method coverage: >= 90%   (every method called at least once)

What to test (prioritize for coverage):
  ✅ Service layer — all business rules, especially conditional branches
  ✅ Controller layer — every endpoint, every HTTP status code path
  ✅ Repository custom queries — verify JPQL/SQL correctness
  ✅ Exception handling — verify @ExceptionHandler and @ControllerAdvice
  ✅ Validators — valid input, boundary values, invalid input
  ✅ Edge cases — empty lists, null values, zero amounts, max values

What NOT to prioritize (diminishing returns):
  ❌ Simple getters/setters generated by Lombok (@Data, @Getter, @Setter)
  ❌ Configuration classes (@Configuration) — usually verified by context loading
  ❌ Main application class (BookstoreApplication) — hard to cover main()
  ❌ Auto-generated code (mappers, DTOs)

JaCoCo Maven plugin enforcement (add to pom.xml):

  <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
      <version>0.8.11</version>
      <executions>
          <execution>
              <goals>
                  <goal>prepare-agent</goal>  <!-- instruments bytecode -->
              </goals>
          </execution>
          <execution>
              <id>report</id>
              <phase>test</phase>
              <goals>
                  <goal>report</goal>  <!-- generates target/site/jacoco/index.html -->
              </goals>
          </execution>
          <execution>
              <id>check</id>
              <phase>verify</phase>
              <goals>
                  <goal>check</goal>  <!-- FAILS build if below threshold -->
              </goals>
              <configuration>
                  <rules>
                      <rule>
                          <element>BUNDLE</element>
                          <limits>
                              <limit>
                                  <counter>LINE</counter>
                                  <value>COVEREDRATIO</value>
                                  <minimum>0.85</minimum>
                              </limit>
                              <limit>
                                  <counter>BRANCH</counter>
                                  <value>COVEREDRATIO</value>
                                  <minimum>0.80</minimum>
                              </limit>
                          </limits>
                      </rule>
                  </rules>
              </configuration>
          </execution>
      </executions>
      <configuration>
          <excludes>
              <!-- Exclude Lombok-generated, main class, and DTOs from coverage -->
              <exclude>**/BookstoreApplication.class</exclude>
              <exclude>**/dto/**</exclude>
              <exclude>**/entity/**</exclude>   <!-- Lombok @Data -->
          </excludes>
      </configuration>
  </plugin>

Run with:
  mvn clean verify           # runs tests + coverage check
  mvn jacoco:report          # generates HTML report
  open target/site/jacoco/index.html   # view in browser
*/
