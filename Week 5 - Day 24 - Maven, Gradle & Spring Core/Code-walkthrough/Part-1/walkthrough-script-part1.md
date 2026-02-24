# Day 24 — Part 1 Walkthrough Script
## Maven, Gradle & Coding Standards
**Duration:** ~90 minutes | **Delivery:** Live code walkthrough + lecture

---

## Instructor Preparation

**Files to have open before class:**
- `Part-1/01-build-automation-maven.md`
- `Part-1/02-gradle-build-scripts.md`
- `Part-1/03-coding-standards.md`

**Terminal ready with:**
```bash
mvn --version
java --version
gradle --version   # or ./gradlew --version inside a project
```

**Optional: Have IntelliJ IDEA open with:**
- A sample Maven project (or be ready to generate one from Spring Initializr)
- The Maven panel visible (View → Tool Windows → Maven)

---

## Segment 1 — Opening: Why Build Automation? (10 min)

### Talking Points

Open by asking students what they think happens when they click "Run" in an IDE.

> "Before we talk about Maven and Gradle specifically, let's talk about what a build tool is actually doing for us. When you press run in IntelliJ, a lot happens behind the scenes. Somebody wrote a tool that handles all of that — and that tool is either Maven or Gradle in the Java world."

Walk through the problem that build tools solve:

- **Without a build tool**, you'd have to:
  - Manually compile every `.java` file with `javac`
  - Manually download JAR dependencies and add them to your classpath
  - Manually run tests with `java -cp ... org.junit.platform.console.ConsoleLauncher`
  - Manually package your app into a JAR or WAR with `jar cf ...`

- **With a build tool:**
  - One command: `mvn package` or `./gradlew build`
  - Dependencies are declared — the tool downloads and caches them
  - Build is reproducible — same command, same result, on any machine

> "The build tool is what makes a Java project portable. You can clone a repo, run one command, and have a working app. That's the goal."

**Transition:**
> "There are two major build tools in the Java world — Maven and Gradle. You'll see both in your career, often on the same team. Let's start with Maven because it's simpler to read and the most common for enterprise Java."

---

## Segment 2 — Maven: Project Object Model (15 min)

### Talking Points

Reference `01-build-automation-maven.md`.

> "Maven is built around one file: `pom.xml`. POM stands for Project Object Model — it's the complete description of your project: what it is, who built it, what it depends on, and how to build it."

**Open the `pom.xml` example.** Walk through each section with students.

**On coordinates (groupId / artifactId / version):**
> "Every Maven project — and every Maven dependency — has three coordinates. Think of it like a shipping address: group tells you the organization, artifact tells you the specific project, version tells you which snapshot in time you want. If you can read these three values, you can find any dependency on Maven Central."

**On `<properties>`:**
> "The properties block is where you declare variables. This is where you set the Java version and pin your dependency versions so they're in one place. Instead of hardcoding `17` in five places, you set it once here."

**On `<dependencies>`:**
> "Dependencies are what your code needs at build time or runtime. Notice the `<scope>` tag — this is Maven's way of saying when a dependency is available. `compile` means it's always available. `test` means it only exists during tests — it won't end up in your production JAR. `provided` means the environment provides it, like a web server providing the Servlet API."

**On the standard directory structure:**
> "Maven enforces a standard project layout. `src/main/java` is your production code. `src/test/java` is your test code. `src/main/resources` holds your config files like `application.properties`. Maven knows where to find all of these automatically — no configuration needed."

**❓ Check-in question:**
> "What's the difference between `test` scope and `provided` scope in Maven?"

*Expected answer: `test` scope is only available during testing and not in production output; `provided` scope means the dependency is expected to be available in the runtime environment (like a container) and is not bundled in the JAR.*

---

## Segment 3 — Maven Lifecycle Phases (10 min)

### Talking Points

Draw the lifecycle on the board or show it from the guide:

```
validate → compile → test → package → verify → install → deploy
```

> "Maven has a build lifecycle — a fixed sequence of phases. When you run a phase, all previous phases run first. If you run `mvn package`, Maven first validates, then compiles, then tests, then packages. You can't skip to the middle."

**Key phases to emphasize:**

- `compile` — just compiles the `.java` files, no tests run
- `test` — compiles AND runs all unit tests; build fails if tests fail
- `package` — creates the artifact (`.jar` or `.war`) in `target/`
- `install` — installs into your local `~/.m2` repository (makes it available for other local projects)
- `deploy` — pushes to a remote artifact repository (CI/CD server)

> "You'll use `mvn compile` when you just want to check that code compiles. You'll use `mvn test` to run your test suite. You'll use `mvn package` to create a deployable artifact. In CI/CD pipelines, you'll almost always see `mvn install` or `mvn clean install`."

**On `mvn clean`:**
> "Clean deletes the `target/` directory — it's not part of the default lifecycle. You run `mvn clean install` when you want to guarantee a fresh build from scratch. During development, you can skip clean to speed things up. In CI, always use clean."

**❓ Check-in question:**
> "If I run `mvn test`, will Maven also compile my code first?"

*Expected answer: Yes — running any lifecycle phase runs all preceding phases. `test` depends on `compile`, so Maven compiles before testing.*

---

## Segment 4 — Maven Plugins and Dependency Management (10 min)

### Talking Points

**On plugins:**
> "Maven's actual work is done by plugins. Each phase is bound to a plugin goal. The `compile` phase uses `maven-compiler-plugin`. The `test` phase uses `maven-surefire-plugin`. The `package` phase uses `maven-jar-plugin`. Maven ships with default bindings, but you can configure or override them in your POM."

Show the `maven-compiler-plugin` configuration block:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
    </configuration>
</plugin>
```

> "This is how you tell Maven which Java version to use when compiling. Without this, Maven might default to Java 8 or 11 even if your machine has 17 installed."

**On Maven Central:**
> "Maven Central is the world's largest repository of Java libraries. When you add a `<dependency>` to your POM, Maven downloads the JAR from Maven Central and caches it in `~/.m2/repository` on your machine. The next project that needs the same dependency uses the cached copy — it doesn't download it again."

**On transitive dependencies:**
> "Here's something important: when you declare a dependency, you also get *its* dependencies automatically. These are called transitive dependencies. If you add Spring Context, you automatically get Spring Core, Spring Beans, Spring AOP, and more — because Spring Context needs them. Maven resolves the entire dependency tree for you."

---

## Segment 5 — Gradle: Build Scripts (15 min)

### Talking Points

Reference `02-gradle-build-scripts.md`.

> "Gradle does the same job as Maven — it builds your project, manages dependencies, runs tests. But instead of XML, Gradle uses a programming language: Groovy or Kotlin. This makes Gradle's build scripts more flexible and often easier to read once you know the syntax."

**Compare the same dependency in Maven vs Gradle:**
```xml
<!-- Maven pom.xml -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.0</version>
</dependency>
```
```groovy
// Gradle build.gradle
implementation 'org.springframework:spring-context:6.1.0'
```
> "Same coordinates — group, artifact, version — but Gradle uses a one-line string syntax instead of XML tags. Most developers find the Gradle syntax cleaner."

**On the plugins block:**
> "Instead of Maven's lifecycle, Gradle works with plugins and tasks. The `java` plugin adds tasks like `compileJava`, `test`, `jar`, and `build`. It's the equivalent of Maven's built-in lifecycle, but declared explicitly."

**On dependency configurations:**
> "Gradle uses *configurations* where Maven uses *scopes*. The key ones:
> - `implementation` = Maven's `compile` — available at compile and runtime
> - `compileOnly` = Maven's `provided` — available at compile time, not in output
> - `runtimeOnly` = Maven's `runtime` — only available at runtime
> - `testImplementation` = Maven's `test` scope
> 
> Notice that Gradle's `implementation` hides transitive dependencies from consumers by default — this is intentional. For libraries, you'd use `api` instead to expose them."

**On the Gradle Wrapper:**
> "Always use `./gradlew` instead of `gradle`. The wrapper is a script that downloads and uses the exact Gradle version pinned in `gradle/wrapper/gradle-wrapper.properties`. This guarantees everyone on your team — and your CI server — uses the same Gradle version. No 'works on my machine' issues."

**On Kotlin DSL vs Groovy DSL:**
> "If you see `build.gradle`, that's Groovy DSL. If you see `build.gradle.kts`, that's Kotlin DSL. Kotlin DSL gives you type safety and better IDE auto-completion, which is why newer Spring projects tend to use it. The concepts are identical — it's just syntax."

**❓ Check-in question:**
> "What is the Gradle equivalent of Maven's `<scope>test</scope>`?"

*Expected answer: `testImplementation` configuration in Gradle.*

---

## Segment 6 — Maven vs Gradle Comparison (5 min)

### Talking Points

Pull up the comparison table from `02-gradle-build-scripts.md`.

> "Let me give you the honest comparison. Maven is more predictable — same lifecycle, same conventions, easy to reason about. Gradle is more powerful — faster builds, more flexible, and the build script is code so you can do anything. For most jobs, you'll see both. On a given team, the choice is usually historical — whatever they started with."

| Use Maven when... | Use Gradle when... |
|---|---|
| Team is already on Maven | Building Android apps |
| Simple, standard build | Complex multi-module projects |
| Enterprise XML tooling | Faster incremental builds needed |
| | New Spring Boot project (both are fine) |

> "Spring Boot's own starter templates on start.spring.io let you pick Maven or Gradle — they're fully equivalent. When you get to your first job, just use whatever the team uses."

---

## Segment 7 — Coding Standards: Naming Conventions (10 min)

### Talking Points

Reference `03-coding-standards.md`. This segment often gets interactive — ask students to name things live.

> "Now let's talk about coding standards. This is the stuff that makes your code look professional — and it's surprisingly easy to get right if you just internalize a few rules. The Java community has had these conventions since the 1990s and they haven't changed."

**Quick naming drill — ask the class:**

> "I'm writing a class that processes payments. What should I name it?"
- *Expected: `PaymentProcessor`*

> "I have a method that checks if an order has been shipped. What should I name it?"
- *Expected: `isShipped()` or `hasBeenShipped()`*

> "I have a constant for the maximum number of retry attempts — the value is 3. How should I declare it?"
- *Expected: `public static final int MAX_RETRY_COUNT = 3;`*

> "I'm naming a package for my service classes. What's the full package name for a company called Revature, project called Bookstore?"
- *Expected: `com.revature.bookstore.service`*

Walk through the naming table in the guide. Emphasize:
- PascalCase → classes and interfaces
- camelCase → methods and variables  
- UPPER_SNAKE_CASE → constants
- lowercase → packages

> "Naming matters because reading code is 80% of programming. If your variable is named `d` or `temp`, the next developer wastes time figuring out what it is. If it's named `discountedPrice`, it's instantly clear."

---

## Segment 8 — Package Structure and File Organization (5 min)

### Talking Points

Show the layered package structure from the guide.

> "Package structure is how you organize your project at a higher level than files. The most common structure in Spring applications is the **layered architecture**: controller, service, repository, model, dto, exception, config, util. Each package has one job."

> "In your first week on a new job, you'll clone the project, look at the package structure, and immediately understand how the code is organized. That's the goal. Organized packages are a form of documentation."

**On the one-class-per-file rule:**
> "Java requires that a public class match its filename exactly — `BookService.java` contains `public class BookService`. This is enforced by the compiler. But the deeper principle is that each file should have one job — one class, one responsibility."

---

## Segment 9 — Comments and Javadoc (10 min)

### Talking Points

Reference the comments and Javadoc section of `03-coding-standards.md`.

> "Now the most underrated part of coding standards: documentation. Let me be direct — most junior developers either write too many comments or not enough. Let's talk about the right approach."

**The golden rule of comments:**
> "Comments explain *why*, not *what*. Your code shows what you're doing — a good comment explains why you're doing it. If someone has to ask 'why is this line here?' — that's where a comment belongs."

Show the bad vs good examples:
```java
// ❌ Obvious
i++;   // increment i by 1

// ✅ Explains a business decision
// Skip first row — it's always a header in the uploaded CSV, not data
for (int i = 1; i < rows.size(); i++) { ... }
```

**On Javadoc:**
> "Javadoc is Java's built-in documentation system. You write `/** */` comments above public classes and methods, and the JDK tools generate HTML documentation from them. If you've ever looked at the Java API docs at docs.oracle.com — that's all generated from Javadoc comments. Same tool, same format."

Walk through the method-level Javadoc example:
```java
/**
 * Retrieves a book by its unique identifier.
 *
 * @param bookId  the unique ID; must not be null
 * @return        the Book with the specified ID
 * @throws BookNotFoundException  if no book exists with the given ID
 */
public Book findById(Long bookId) { ... }
```

> "The tags you need to know: `@param` for every parameter, `@return` for the return value if not void, `@throws` for checked and important unchecked exceptions. Public APIs should always have Javadoc — it's how other developers know how to use your code without reading it."

**❓ Check-in question:**
> "What is the difference between a `//` comment and a `/** */` Javadoc comment?"

*Expected answer: `//` is an inline comment visible only in source code; `/** */` is Javadoc — it generates external HTML documentation and shows up in IDE tooltips.*

---

## Segment 10 — Anti-Patterns and Wrap-up (5 min)

### Talking Points

> "Let me leave you with the most common mistakes I see in junior developer code — and how to fix them."

**Magic numbers:**
> "A magic number is a numeric literal in your code with no explanation. `if (status == 3)` — what is 3? A shipped order? A cancelled order? An error code? Use a constant: `if (status == STATUS_SHIPPED)`. Instantly clear."

**Commented-out code:**
> "Do not comment out code and leave it in the file. Delete it. We have Git — if you ever need that code again, look in the commit history. Commented-out code is noise. It confuses readers and suggests the code might come back. Just delete it."

**Abbreviations:**
> "Don't abbreviate variable names to save typing. `custNm` is not shorter than `customerName` in any meaningful way — your IDE autocompletes both. `customerName` is instantly readable. `custNm` requires mental decoding."

**Summary:**
> "Coding standards are how you show respect for the people who will read your code — including yourself in six months. Learn these rules until they're automatic. They're not arbitrary — each one exists because someone had to debug code that didn't follow it."

---

## Part 1 Summary

| Topic | Key Takeaway |
|---|---|
| Build Automation | Build tools automate compile → test → package → deploy reproducibly |
| Maven POM | `pom.xml` = project identity + dependencies + plugins + build config |
| Maven Lifecycle | Phases run in order; `compile → test → package → install → deploy` |
| Maven Dependencies | Coordinates = groupId:artifactId:version; scopes control availability |
| Gradle Build Script | `build.gradle` uses Groovy/Kotlin DSL; same concepts as Maven |
| Gradle Tasks | Task-based model; `./gradlew build` = full build including tests |
| Maven vs Gradle | Maven = predictable, Gradle = faster/flexible; both are industry-standard |
| Naming Conventions | PascalCase classes, camelCase methods/vars, UPPER_SNAKE_CASE constants |
| Package Structure | Layered architecture: controller/service/repository/model/dto/config |
| Javadoc | `/** */` documents public API; generates HTML docs; use @param/@return/@throws |

**Break before Part 2:** 10–15 minutes

---

## Q&A Prompts for Part 1

1. "What happens if two dependencies in your Maven project require different versions of the same transitive dependency?"
   - *Maven uses the 'nearest wins' strategy — whichever version is closest to your project in the dependency tree wins. This can be overridden with `<dependencyManagement>`.*

2. "If I want to build my Maven project without running tests, what command do I use?"
   - *`mvn package -DskipTests` or `mvn package -Dmaven.test.skip=true`*

3. "Why does Gradle use `implementation` instead of `compile` for dependencies?"
   - *`compile` was deprecated because it exposed transitive dependencies to consumers. `implementation` hides internals by default, leading to faster builds and cleaner dependency contracts.*

4. "Can you have both a `pom.xml` AND a `build.gradle` in the same project?"
   - *Technically yes, but you'd only use one at a time. In practice, you pick one build tool per project.*

5. "What does `./gradlew clean build` do differently from `./gradlew build`?"
   - *`clean` deletes the `build/` directory first. Running `clean build` guarantees a completely fresh compilation — no stale cached class files. Important in CI/CD, optional during normal development.*
