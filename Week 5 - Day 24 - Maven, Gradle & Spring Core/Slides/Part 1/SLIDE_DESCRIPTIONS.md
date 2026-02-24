# Day 24 Part 1 — Maven, Gradle & Coding Standards
## Slide Descriptions

**Total slides: 16**

---

### Running Example

Throughout Part 1, we build and configure a `bookstore` project from scratch — first as a Maven project, then as a Gradle project. All POM and build script examples reference this project. Students see both tools configure the same project so comparisons are concrete.

---

### Slide 1 — Title Slide

**Title:** Maven, Gradle & Coding Standards
**Subtitle:** Build Automation · Dependency Management · Project Structure · Code Quality
**Day:** Week 5 — Day 24 | Part 1 of 2

**Objectives listed on slide:**
- Explain what a build tool does and why we need one
- Create and configure Maven projects with `pom.xml`
- Execute Maven lifecycle phases from the command line
- Manage dependencies with Maven Central coordinates
- Set up Gradle projects with `build.gradle`
- Declare Gradle dependencies and run Gradle tasks
- Compare Maven and Gradle and choose between them
- Apply Java naming conventions and package organization
- Write effective Javadoc comments

---

### Slide 2 — What Build Tools Do

**Title:** Build Automation — Why We Can't Just Use the Compiler

**The problem without a build tool:**
```
Manual workflow (painful):
1. Download 47 library JARs manually from the internet
2. Put them on the classpath somehow
3. Run javac on 200+ .java files in the right order
4. Run JUnit tests manually
5. Bundle compiled classes + resources into a JAR
6. Update the 47 JARs when new versions release
7. Do this identically on every developer's machine
8. Repeat for every CI server
```

**What a build tool does:**
```
Automated workflow (one command):
$ mvn package
  → Downloads declared dependencies automatically
  → Compiles source files in the right order
  → Runs all tests (fails build if tests fail)
  → Packages into a JAR/WAR
  → Reproducible on every machine identically
```

**Four core responsibilities of any build tool:**

| Responsibility | What it means |
|---|---|
| **Dependency management** | Declare what libraries you need; tool downloads them |
| **Compilation** | Invoke the Java compiler with the right classpath |
| **Testing** | Run unit/integration tests, fail if any fail |
| **Packaging** | Bundle into a distributable artifact (JAR, WAR) |
| **Deployment** | Publish artifact to a repository or server |

**The two dominant Java build tools:**
- **Maven** — XML-based, convention over configuration, dominant in enterprise Java since 2004
- **Gradle** — Groovy/Kotlin DSL, flexible and fast, dominant in Android and modern Spring Boot projects

---

### Slide 3 — Maven Overview and Project Structure

**Title:** Maven — Convention Over Configuration

**Core Maven philosophy:** If you follow the standard conventions, your build works with zero configuration. Every Maven project looks the same.

**Maven standard directory layout:**
```
bookstore/                        ← project root
├── pom.xml                       ← Project Object Model (the build config)
├── src/
│   ├── main/
│   │   ├── java/                 ← production source code
│   │   │   └── com/bookstore/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       └── repository/
│   │   └── resources/            ← application.properties, XML configs
│   └── test/
│       ├── java/                 ← test source code
│       │   └── com/bookstore/
│       └── resources/            ← test-specific resources
└── target/                       ← build output (generated — never commit)
    ├── classes/                  ← compiled .class files
    └── bookstore-1.0.0.jar       ← packaged artifact
```

**Creating a Maven project:**
```bash
# Generate a basic project from an archetype
mvn archetype:generate \
  -DgroupId=com.bookstore \
  -DartifactId=bookstore \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

# Or with Spring Initializr (the preferred Spring Boot approach)
# → start.spring.io generates a fully configured pom.xml
```

**Maven vs Gradle folder:** The `src/main/java` and `src/test/java` convention is shared by both Maven and Gradle. Once you learn one, the structure translates.

---

### Slide 4 — The POM File

**Title:** pom.xml — The Heart of a Maven Project

**A complete realistic pom.xml for the bookstore project:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- GAV Coordinates — uniquely identify this artifact -->
    <groupId>com.bookstore</groupId>
    <artifactId>bookstore</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Book Store Application</name>
    <description>REST API for managing books and authors</description>

    <!-- Spring Boot Parent — inherits defaults for Spring projects -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.2</version>
    </parent>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web (REST API) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Lombok — boilerplate reduction -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

**GAV coordinates — the Maven address system:**

| Coordinate | Our Value | Meaning |
|---|---|---|
| `groupId` | `com.bookstore` | Organization/team — usually reverse domain name |
| `artifactId` | `bookstore` | Project name — the JAR filename |
| `version` | `1.0.0-SNAPSHOT` | Version — `SNAPSHOT` = in development, not released |
| `packaging` | `jar` | Output type — `jar`, `war`, or `pom` |

**Version SNAPSHOT vs RELEASE:** `1.0.0-SNAPSHOT` is a mutable development version. Maven always downloads the latest SNAPSHOT. `1.0.0` (no SNAPSHOT) is an immutable release — once published, it never changes.

---

### Slide 5 — Maven Lifecycle

**Title:** Maven Build Lifecycle — Phases in Order

**Maven has three built-in lifecycles. The default lifecycle is what you'll use 99% of the time:**

```
validate   → Check project structure is correct
compile    → Compile src/main/java → target/classes
test       → Compile src/test/java and run tests (Surefire plugin)
package    → Bundle into JAR/WAR → target/bookstore-1.0.0.jar
verify     → Run integration tests and quality checks
install    → Copy JAR to local repository (~/.m2/repository)
deploy     → Upload JAR to remote repository (Nexus, Artifactory)
```

**Key rule: each phase runs all previous phases first.**

```bash
# This runs: validate → compile → test → package
mvn package

# This runs all phases through install
mvn install

# Compile only (skips test, package, etc.)
mvn compile

# Skip tests (useful for fast builds — use carefully!)
mvn package -DskipTests

# Clean build output first, then package
mvn clean package
```

**The `clean` lifecycle (separate):**
```bash
mvn clean     # deletes the target/ directory entirely
mvn clean package  # always recommended before building artifacts
```

**The `site` lifecycle (generates HTML documentation):**
```bash
mvn site      # generates Javadoc and reports in target/site/
```

**What happens on `mvn package` for the bookstore:**
```
[INFO] --- maven-compiler-plugin:3.11.0:compile ---
[INFO] Compiling 12 source files to /target/classes
[INFO] --- maven-surefire-plugin:3.1.2:test ---
[INFO] Running com.bookstore.service.BookServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0
[INFO] --- maven-jar-plugin:3.3.0:jar ---
[INFO] Building jar: /target/bookstore-1.0.0.jar
[INFO] BUILD SUCCESS
```

---

### Slide 6 — Dependency Management and Scopes

**Title:** Maven Dependencies — Coordinates, Central, and Scopes

**Finding a dependency on Maven Central:**
1. Go to `mvnrepository.com` or `search.maven.org`
2. Search for the library (e.g., "jackson databind")
3. Click the version you want
4. Copy the `<dependency>` XML block

**How Maven resolves dependencies:**
```
Your pom.xml declares:
  → spring-boot-starter-web 3.4.2

Maven downloads transitively:
  → spring-web 6.2.x
  → spring-webmvc 6.2.x
  → tomcat-embed-core 10.1.x
  → jackson-databind 2.17.x
  → ... (37 more JARs)
```
All downloaded to `~/.m2/repository` — your local cache. Second build: instant (already downloaded).

**Dependency scopes:**

| Scope | Available at compile | Available at test | Included in JAR | Use case |
|---|---|---|---|---|
| `compile` (default) | ✅ | ✅ | ✅ | Everything you use at runtime |
| `test` | ❌ | ✅ | ❌ | JUnit, Mockito, test frameworks |
| `provided` | ✅ | ✅ | ❌ | Servlet API — provided by the container |
| `runtime` | ❌ | ✅ | ✅ | JDBC drivers — needed at runtime only |
| `optional` | ✅ | ✅ | ❌ | Not inherited by dependents (Lombok) |

**Dependency version conflicts — nearest-wins rule:**
```
Your project → Library A 1.0 → jackson-databind 2.14
Your project → Library B 2.0 → jackson-databind 2.17

Maven uses 2.14 (nearest to root = declared directly by A)
→ Can cause runtime issues!

Fix: declare jackson-databind explicitly in your pom.xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>2.17.0</version>
</dependency>
```

**`<dependencyManagement>` block:** Declare versions without adding the dependency. Child modules inherit the version. This is how Spring Boot Parent manages 300+ library versions for you.

---

### Slide 7 — Maven Plugins and Goals

**Title:** Maven Plugins — Where the Work Happens

**Phases are executed by plugins. Every phase binds to one or more plugin goals:**

| Phase | Plugin | Goal |
|---|---|---|
| `compile` | `maven-compiler-plugin` | `compile` |
| `test` | `maven-surefire-plugin` | `test` |
| `package` | `maven-jar-plugin` | `jar` |
| `install` | `maven-install-plugin` | `install` |
| `deploy` | `maven-deploy-plugin` | `deploy` |

**Configuring the compiler plugin:**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <source>21</source>       <!-- Java version -->
                <target>21</target>
                <compilerArgs>
                    <arg>-parameters</arg> <!-- preserve param names -->
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**The Spring Boot Maven plugin (your most important plugin):**
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```

```bash
# Runs the Spring Boot application directly (no JAR needed)
mvn spring-boot:run

# Builds a fat/uber JAR with all dependencies embedded
mvn package   # → creates target/bookstore-1.0.0.jar (self-contained!)
java -jar target/bookstore-1.0.0.jar  # runs anywhere with Java installed
```

**Other useful plugins:**
- `maven-checkstyle-plugin` — enforces code style rules
- `maven-surefire-plugin` — runs JUnit 5 tests
- `maven-failsafe-plugin` — runs integration tests (`*IT.java` files)
- `jacoco-maven-plugin` — code coverage reports

---

### Slide 8 — Gradle Overview

**Title:** Gradle — Flexible, Fast, Modern

**Gradle was created to fix Maven's limitations:**
- Build logic in a real programming language (Groovy or Kotlin DSL), not XML
- Incremental builds — only recompile what changed
- Build cache — skip tasks whose inputs/outputs haven't changed
- Parallel task execution
- Better performance at scale (used by Android, Google, Netflix)

**Gradle standard directory layout:** Identical to Maven — `src/main/java`, `src/test/java`.

**build.gradle (Groovy DSL) — bookstore project:**
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.2'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.bookstore'
version = '1.0.0-SNAPSHOT'

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()     // where to download dependencies from
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()   // tell Gradle to use JUnit 5
}
```

**build.gradle.kts (Kotlin DSL) — increasingly preferred:**
```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bookstore"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Kotlin DSL** is now the default on Spring Initializr — it has IDE auto-complete, type safety, and better refactoring support.

---

### Slide 9 — Gradle Tasks and the Build Lifecycle

**Title:** Gradle Tasks — The Unit of Work

**In Gradle, everything is a task.** There are no "phases" like Maven — instead, tasks declare dependencies on other tasks and Gradle builds a task execution graph.

**Common Gradle tasks:**
```bash
# Build — compiles + tests + packages
./gradlew build

# Run tests only
./gradlew test

# Compile only
./gradlew compileJava

# Run Spring Boot app
./gradlew bootRun

# Build JAR without running tests
./gradlew build -x test

# Clean build output
./gradlew clean

# List all available tasks
./gradlew tasks

# See task dependency graph
./gradlew dependencies
```

**`./gradlew` vs `gradle`:** Always use `./gradlew` (the Gradle Wrapper). It downloads and uses the exact Gradle version specified in `gradle/wrapper/gradle-wrapper.properties`. This guarantees everyone on the team uses the same version. The wrapper files should be committed to Git.

**Task graph for `./gradlew build`:**
```
:compileJava
:processResources
:classes
:compileTestJava
:processTestResources
:testClasses
:test
:check
:bootJar
:build
```

**Defining a custom task:**
```groovy
tasks.register('greet') {
    group = 'custom'
    description = 'Prints a greeting'
    doLast {
        println "Hello from the bookstore build!"
    }
}
```
```bash
./gradlew greet
# > Task :greet
# Hello from the bookstore build!
```

---

### Slide 10 — Gradle Dependency Configurations

**Title:** Gradle Dependency Configurations

**Gradle dependency configurations (equivalent to Maven scopes):**

| Gradle Configuration | Maven Equivalent | Use Case |
|---|---|---|
| `implementation` | `compile` | Core dependencies — not exposed to dependents |
| `api` | `compile` | Dependencies exposed in your API (library projects) |
| `compileOnly` | `provided` | Compile-time only (Lombok, annotations) |
| `runtimeOnly` | `runtime` | Runtime only (JDBC drivers) |
| `testImplementation` | `test` | Test-only dependencies |
| `testCompileOnly` | `test` + `provided` | Test compile-time only |
| `annotationProcessor` | N/A (special) | Annotation processors (Lombok, MapStruct) |

**`implementation` vs `api`:** Use `implementation` for application projects. Use `api` only when building a library and your dependency's types appear in your public API. `implementation` hides the dependency from consumers, reducing coupling and speeding up compilation.

**Dependency with explicit version (when not using Spring BOM):**
```groovy
dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.17.0'
    implementation group: 'org.slf4j', name: 'slf4j-api', version: '2.0.16'
}
```

**Viewing the dependency tree:**
```bash
./gradlew dependencies --configuration runtimeClasspath
# Shows full transitive dependency tree — useful for diagnosing version conflicts
```

**Excluding a transitive dependency:**
```groovy
implementation('org.springframework.boot:spring-boot-starter-web') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
}
implementation 'org.springframework.boot:spring-boot-starter-undertow'
```

---

### Slide 11 — Maven vs Gradle Comparison

**Title:** Maven vs Gradle — Choosing the Right Tool

| Aspect | Maven | Gradle |
|---|---|---|
| **Configuration format** | XML (`pom.xml`) | Groovy/Kotlin DSL (`build.gradle`) |
| **Learning curve** | Lower — XML is familiar | Higher — requires understanding DSL |
| **Build logic** | Limited — XML only | Full programming language |
| **Performance** | Slower — no incremental builds | Faster — incremental + caching |
| **Ecosystem** | Enormous — 20+ years of plugins | Growing — Android + Spring Boot |
| **IDE support** | Excellent | Excellent |
| **Spring Initializr default** | Both offered | Kotlin DSL now default |
| **Enterprise preference** | Very common | Growing rapidly |
| **Android development** | Rare | Required (only option) |
| **Flexibility** | Convention-heavy | Highly customizable |

**When to choose Maven:**
- Legacy enterprise project that already uses Maven
- Team is unfamiliar with Groovy/Kotlin
- Heavily plugin-dependent workflow (Maven plugin ecosystem is larger)
- Want strict convention enforcement

**When to choose Gradle:**
- New Spring Boot project (Spring Initializr default)
- Android development (no choice)
- Need build performance (CI speed matters)
- Complex multi-module project with custom build logic

**The practical answer for this course:** You'll use both. Spring Initializr gives you the choice. Understanding both means you can work in any codebase. The concepts — dependency declarations, lifecycle phases vs tasks, the `src/main/java` structure — are essentially the same.

**Multi-module projects:** Both tools support them. Maven uses `<modules>` in a parent POM. Gradle uses `settings.gradle` with `include` statements. Both allow sharing dependencies and build config across modules.

---

### Slide 12 — Java Naming Conventions

**Title:** Coding Standards — Naming Conventions

**Java naming conventions (enforced by the community and Checkstyle):**

| Element | Convention | Examples |
|---|---|---|
| **Class** | `UpperCamelCase` | `BookService`, `HttpRequestHandler`, `UserRepository` |
| **Interface** | `UpperCamelCase` | `Runnable`, `BookRepository`, `PaymentProcessor` |
| **Enum** | `UpperCamelCase` | `Genre`, `OrderStatus`, `HttpMethod` |
| **Method** | `lowerCamelCase` | `findById()`, `calculateTotal()`, `isAvailable()` |
| **Variable** | `lowerCamelCase` | `bookTitle`, `totalPrice`, `authorId` |
| **Constant** | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| **Package** | `lowercase.dotted` | `com.bookstore.service`, `com.bookstore.model` |
| **Test class** | Matches subject + `Test` | `BookServiceTest`, `UserRepositoryTest` |

**Method naming conventions:**

| Purpose | Convention | Examples |
|---|---|---|
| Returns boolean | `is` / `has` / `can` prefix | `isAvailable()`, `hasStock()`, `canDelete()` |
| Gets a value | `get` prefix | `getTitle()`, `getPrice()` |
| Sets a value | `set` prefix | `setTitle()`, `setPrice()` |
| Creates something | `create` / `build` / `make` | `createOrder()`, `buildRequest()` |
| Finds from storage | `find` / `get` | `findById()`, `findAllByGenre()` |
| Saves to storage | `save` / `store` | `save()`, `saveAll()` |
| Removes | `delete` / `remove` | `deleteById()`, `removeAll()` |

**What NOT to do:**
```java
// ❌ Wrong
class book_service {}              // snake_case class name
void CalculateTotal() {}           // capital first letter
int MAX_retry = 3;                 // inconsistent constant
String BookTitle = "Clean Code";   // capital variable

// ✅ Right
class BookService {}
void calculateTotal() {}
int MAX_RETRY_COUNT = 3;
String bookTitle = "Clean Code";
```

---

### Slide 13 — Package Structure

**Title:** Package Organization — Layer vs Feature

**Two common package structure strategies:**

**Strategy 1 — Package by Layer (traditional):**
```
com.bookstore
├── controller/       ← all REST controllers
│   ├── BookController.java
│   └── AuthorController.java
├── service/          ← all service classes
│   ├── BookService.java
│   └── AuthorService.java
├── repository/       ← all data access
│   ├── BookRepository.java
│   └── AuthorRepository.java
├── model/            ← all entities/domain objects
│   ├── Book.java
│   └── Author.java
└── dto/              ← all data transfer objects
    ├── BookRequest.java
    └── BookResponse.java
```

**Strategy 2 — Package by Feature (modern/modular):**
```
com.bookstore
├── book/
│   ├── BookController.java
│   ├── BookService.java
│   ├── BookRepository.java
│   ├── Book.java
│   └── BookDto.java
├── author/
│   ├── AuthorController.java
│   ├── AuthorService.java
│   └── Author.java
└── order/
    ├── OrderController.java
    ├── OrderService.java
    └── Order.java
```

**Comparison:**

| Aspect | By Layer | By Feature |
|---|---|---|
| **For small projects** | ✅ Simple and familiar | Overkill |
| **For large projects** | Becomes unwieldy | ✅ Better cohesion |
| **Adding a feature** | Touch every package | Touch one package |
| **Spring Boot default** | ✅ Used in most tutorials | Used in mature codebases |

**For this course:** Package by layer. It's what Spring Boot tutorials and most job interview codebases use. As projects grow, package by feature becomes increasingly attractive.

**Standard Spring Boot package structure:**
```
com.bookstore                      ← base package (contains @SpringBootApplication)
├── BookstoreApplication.java      ← main class at the ROOT of base package
├── controller/
├── service/
├── repository/
├── model/ (or domain/)
├── dto/
├── exception/
└── config/
```

---

### Slide 14 — Code Comments and Javadoc

**Title:** Documentation — Comments That Add Value

**The rule:** Comments explain **why**, not **what**. If your code needs a comment to explain what it does, your code should be clearer. Comments should explain intent, trade-offs, and non-obvious decisions.

**Bad comments (noise):**
```java
// ❌ Restates what the code does — adds no value
// increment i by 1
i++;

// ❌ Commented-out code — use version control instead
// Book oldBook = findBook(id);

// ❌ Obvious
// Constructor
public BookService() {}
```

**Good comments (value):**
```java
// ✅ Explains why, not what
// Use LinkedHashMap to preserve insertion order for consistent pagination
private final Map<Long, Book> cache = new LinkedHashMap<>();

// ✅ Documents a known limitation
// Spring Security requires this method to return UserDetails — not our domain User
@Override
public UserDetails loadUserByUsername(String username) { ... }

// ✅ Documents a non-obvious business rule
// ISBN-13 requires Luhn-like check digit validation before storage
public void validateIsbn(String isbn) { ... }
```

**Javadoc — for public APIs:**
```java
/**
 * Finds all books matching the given genre, ordered by title ascending.
 *
 * <p>Results are paginated. An empty page is returned (not {@code null})
 * when no books match the filter.
 *
 * @param genre  the genre to filter by; must not be {@code null}
 * @param pageable  pagination and sorting parameters
 * @return a page of matching books, never {@code null}
 * @throws IllegalArgumentException if genre is blank
 */
public Page<Book> findByGenre(String genre, Pageable pageable) { ... }
```

**Javadoc rules:**
- Required on all `public` and `protected` methods in library/API code
- `@param` — document each parameter, including null constraints
- `@return` — document what is returned, including null/empty behavior
- `@throws` — document checked exceptions and important unchecked ones
- Generate with: `mvn javadoc:javadoc` or `./gradlew javadoc`

---

### Slide 15 — Code Quality Tools

**Title:** Enforcing Standards — Checkstyle and Static Analysis

**Checkstyle:** Enforces style rules at build time. Build fails if code doesn't meet standards.

**Adding Checkstyle to Maven:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.4.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <failsOnError>true</failsOnError>
    </configuration>
    <executions>
        <execution>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

**Common Checkstyle rules:**
- Line length (Google style: 100 chars)
- Indentation (4 spaces, not tabs)
- Braces on same line as `if`/`for`/`while`
- Every `if` block must have braces
- No wildcard imports (`import java.util.*` is banned)
- Javadoc on public methods

**SpotBugs / FindBugs:** Static analysis — finds potential bugs at compile time without running code.
```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.6</version>
</plugin>
```
```bash
mvn spotbugs:check
```
Catches: null pointer dereferences, resource leaks, infinite loops, SQL injection vulnerabilities, and more.

**IDE integration:** IntelliJ IDEA has built-in inspection. Install the Checkstyle-IDEA and SpotBugs plugins to see violations inline as you type.

**The goal:** Code quality gates that run in CI. Every pull request passes Checkstyle + SpotBugs before merging. You can't ship code that doesn't meet the team's standards.

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Complete — Build Tools & Standards Reference

**Maven quick reference:**
```bash
mvn clean package      # clean → compile → test → package
mvn clean install      # ... + install to ~/.m2
mvn spring-boot:run    # run Spring Boot app
mvn -DskipTests package  # skip tests (emergency only)
```

**Gradle quick reference:**
```bash
./gradlew build        # compile + test + package
./gradlew bootRun      # run Spring Boot app
./gradlew test         # tests only
./gradlew clean build  # fresh build
```

**Key file locations:**
```
Maven:  pom.xml
Gradle: build.gradle (Groovy) or build.gradle.kts (Kotlin)
Both:   src/main/java/  src/test/java/  src/main/resources/
Maven output:  target/
Gradle output: build/
```

**Naming conventions at a glance:**
- Classes/Interfaces/Enums: `UpperCamelCase`
- Methods/variables: `lowerCamelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: `lowercase.dotted`

**Package structure:** Package by layer (`controller/`, `service/`, `repository/`, `model/`, `dto/`, `exception/`, `config/`)

**Coming up — Part 2:** Spring Framework architecture · IoC container · Dependency Injection · Spring beans · Component scanning · `@Autowired` · Bean scopes · Lombok
