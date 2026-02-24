# Day 24 Part 1 — Maven, Gradle & Coding Standards
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Good morning. Today we start building. From Day 23 you know what REST APIs look like from the outside — the protocol, the design, the tooling. Today we go inside. The first half of today is about build tools — specifically Maven and Gradle, the two systems that manage every professional Java project. The second half is where things get really interesting: Spring Framework, IoC, dependency injection — the engine that powers Spring Boot and everything you're going to build for the rest of this course.

Let me be direct about why build tools matter. In a few days you're going to type `mvn package` or `./gradlew build` and get a running Spring application. If you understand what those commands are doing, you can fix anything that goes wrong. If you think of them as magic incantations, you're stuck every time something breaks. So we're going to understand what's actually happening.

Our running example is the bookstore project we designed yesterday. We're going to create it first as a Maven project, then see how it looks as a Gradle project, and understand why both exist.

---

### [02:00–08:00] Slide 2 — What Build Tools Do

Before we look at Maven or Gradle specifically, let's understand the problem they solve. Imagine you're building the Book Store API from scratch, pre-build-tools. You need Jackson for JSON serialization. You need Spring. You need JUnit for testing. What do you do? You go to each project's website, download a JAR file, put it somewhere on your filesystem, and then somehow tell the compiler where those JARs are. Then you need to compile your 200 source files. Then run JUnit. Then bundle everything into a JAR. Then do all of this identically on a colleague's machine. Then on the CI server. Then again when Jackson releases a security patch and you need to update.

This is not a hypothetical. This was real life before Maven. It was called "JAR hell" and it was miserable. Build tools solve this problem completely.

Here's the automated workflow. You declare your dependencies in a configuration file. The build tool downloads them from a central repository. It compiles your code with those dependencies on the classpath. It runs your tests. It packages everything into a JAR. The whole process is reproducible — same config file, same result on every machine in the world.

The slide shows four core responsibilities. Dependency management — you say what you need, the tool gets it. Compilation — the tool invokes javac with the right classpath. Testing — the tool runs your tests and fails the build if any test fails. That last point is critical: build tools treat test failures as build failures. A JAR with failing tests should never be deployed. Packaging — the tool bundles everything into a distributable artifact.

There are two dominant Java build tools. Maven has been around since 2004 and is used in the vast majority of enterprise Java projects. It uses XML configuration. Gradle came later, uses a Groovy or Kotlin programming language for configuration, and has become the standard for Spring Boot projects and Android development. You will encounter both in your career, so we're learning both today.

---

### [08:00–16:00] Slides 3 & 4 — Maven Structure and the POM

Let's create a Maven project. The first thing to understand is that Maven is built on convention. There is one correct place for your source code. One correct place for your tests. One correct place for resources. If you follow the convention, Maven works with zero extra configuration. This is called "convention over configuration" — it's one of the most important ideas in software engineering and we'll see it again when we get to Spring Boot.

The directory layout is on the slide. Your production code lives in `src/main/java`. Your tests live in `src/test/java`. Resources like property files and XML configs live in `src/main/resources`. Maven compiles everything in `src/main/java`, runs everything in `src/test/java`, and puts the output in a directory called `target`. Here's a rule: never commit the `target` directory to Git. It's generated code. Your `.gitignore` should always have `target/` in it.

The heart of a Maven project is the `pom.xml` file. POM stands for Project Object Model. It's an XML file that describes your project — what it is, what it depends on, how to build it. Every Maven project has exactly one at the root.

Let me walk through the POM on the slide. The very first element after the XML declaration and project namespace is `<modelVersion>4.0.0</modelVersion>`. This has been 4.0.0 for over 20 years and will never change in Maven 3. Then comes what Maven calls the GAV coordinates — GroupId, ArtifactId, Version. These three together uniquely identify your artifact in the entire Maven ecosystem.

GroupId is your organization identifier. Convention is reverse domain name — `com.bookstore`, just like Java package names. ArtifactId is your project name — `bookstore`. This becomes the name of your JAR file. Version is your version number — `1.0.0-SNAPSHOT`. That SNAPSHOT suffix is important. A SNAPSHOT version means this is a development build. When you declare SNAPSHOT dependencies, Maven always downloads the latest build — it never caches the final answer. When you release software, you drop the SNAPSHOT — `1.0.0` with no suffix is an immutable release. Once you deploy `1.0.0` to a repository, it never changes. This is how the entire Java ecosystem maintains reproducibility.

Then we have a `<parent>` element pointing to `spring-boot-starter-parent`. This is the Spring Boot parent POM. It inherits hundreds of pre-configured settings — dependency versions, plugin configurations, encoding settings. It's what allows us to declare `spring-boot-starter-web` without specifying a version number — the parent POM manages the version for us. This is called a Bill of Materials, or BOM.

The `<dependencies>` block is where you declare what your project needs. Three dependencies here: `spring-boot-starter-web` for building REST APIs, `lombok` with `optional` scope for reducing boilerplate, and `spring-boot-starter-test` with `test` scope for testing. We'll come back to scopes in two slides.

The `<build>` section defines the Spring Boot Maven plugin. This plugin does something extraordinary — it builds a "fat JAR" that contains your code plus all your dependencies embedded inside it. You can then run your entire application with `java -jar bookstore-1.0.0.jar` on any machine with Java installed. No separate Tomcat installation, no classpath setup. This is one of the killer features of Spring Boot.

---

### [16:00–24:00] Slides 5 & 6 — Maven Lifecycle and Dependencies

The Maven build lifecycle. This is what happens when you type `mvn package`. There are seven phases in the default lifecycle: validate, compile, test, package, verify, install, deploy. They always run in that order. You can't run phase 4 without running phases 1 through 3 first.

`validate` checks that the project structure is sane. `compile` invokes javac on everything in `src/main/java` and puts the class files in `target/classes`. `test` compiles your test code and runs it — if any test fails, the build stops here and fails. `package` takes everything in `target/classes`, your resources, and bundles it all into a JAR or WAR. `verify` runs integration tests and quality checks. `install` copies the JAR to your local Maven repository — a hidden folder called `.m2` in your home directory. `deploy` pushes the artifact to a remote repository like Nexus or Artifactory for the team to share.

The most useful commands. `mvn clean package` — always start with `clean` to delete the `target` directory and guarantee a fresh build. Then `package` to compile, test, and package. `mvn clean install` — same, but also installs to `.m2`. Use this when other local projects need to depend on this one. `mvn spring-boot:run` — runs the Spring Boot app directly without building a JAR first. Great for development. And the one I reluctantly show you: `mvn package -DskipTests` — packages without running tests. Use this only when you're certain the tests are unrelated to the change you just made, which is almost never, so use it almost never.

Dependency management. When you declare a dependency, Maven downloads it from Maven Central — `central.sonatype.com` — which is the world's largest repository of open source Java libraries. It caches everything in your local `.m2` repository. First time you build takes a while. Second time it's instant because everything is cached.

Here's something that surprises developers — transitive dependencies. You declare `spring-boot-starter-web`. That one dependency pulls in Spring Web, Spring MVC, an embedded Tomcat server, Jackson for JSON, logging, and about 30 more libraries, all automatically. Maven builds the complete dependency graph. This is enormously convenient.

Dependency scopes control when a dependency is available. `compile` scope — the default — means the dependency is available at compile time, test time, and runtime, and it's included in your JAR. `test` scope means only available during tests — JUnit, Mockito, your test framework. These do not end up in your deployed JAR. That matters because your users don't need JUnit. `provided` scope means available at compile time but not included in the JAR because the runtime environment provides it — the classic case is the Servlet API when deploying to an external Tomcat. `optional` is for Lombok — it means "this project uses Lombok, but projects that depend on this project don't need Lombok."

Version conflicts. When multiple dependencies each pull in different versions of the same library, Maven uses the "nearest wins" rule. The version closest to your project in the dependency graph wins. This can cause subtle runtime errors when a library's behavior differs across versions. The fix is to declare the version explicitly in your own POM — your explicit declaration beats anything transitive.

---

### [24:00–32:00] Slides 7 & 8 — Maven Plugins and Gradle

Every phase in Maven is executed by a plugin. The compile phase runs `maven-compiler-plugin:compile`. The test phase runs `maven-surefire-plugin:test`. The package phase runs `maven-jar-plugin:jar`. Plugins are where the actual work happens. Phases are just named hooks that trigger plugins.

The most important plugin for us is the Spring Boot Maven plugin. It creates what's called a fat JAR — or uber JAR. A normal Java JAR contains only your compiled classes. A fat JAR contains your classes plus all dependency JARs embedded inside a specially structured directory. When you run `java -jar bookstore-1.0.0.jar`, the Spring Boot launcher unpacks and loads all those embedded JARs. The result is a completely self-contained executable. This is why Spring Boot applications are so easy to deploy to containers like Docker — you just `COPY` the JAR and run it.

Now let's look at Gradle. Gradle was created to address Maven's limitations. Maven is excellent but rigid. If you need build logic beyond what XML can express — conditional compilation, custom transformations, complex multi-project builds — XML gets painful fast. Gradle uses a real programming language for build scripts. Groovy is dynamically typed and looks somewhat like Java. Kotlin DSL is statically typed with IDE autocompletion and is now the default on Spring Initializr.

Compare the POM and the Groovy `build.gradle` side by side. The same information is there: project identity, Java version, repositories, dependencies. But in Gradle it reads almost like natural language. `repositories { mavenCentral() }` — go to Maven Central for dependencies. `dependencies { implementation 'spring-boot-starter-web' }` — declare implementation dependency. You don't need to understand Groovy to read this.

The Kotlin DSL version uses parentheses and double quotes instead of single quotes, but it's otherwise similar — and unlike Groovy, it gives you IDE autocompletion. You type `implementat...` and the IDE completes it. This is why Kotlin DSL is rapidly becoming the standard.

One important note about Lombok in Gradle. Maven just needs the `<dependency>` with `optional` scope. Gradle needs two entries: `compileOnly 'org.projectlombok:lombok'` — Lombok is only needed at compile time — and `annotationProcessor 'org.projectlombok:lombok'` — Lombok is an annotation processor that runs javac. If you only declare `compileOnly` without `annotationProcessor`, Lombok's code generation silently doesn't run. This is one of the most common Gradle/Lombok bugs for beginners.

---

### [32:00–40:00] Slides 9 & 10 — Gradle Tasks and Dependencies

In Gradle, everything is a task. There are no predefined lifecycle phases. Instead, tasks declare that they depend on other tasks. When you run `./gradlew build`, Gradle analyzes the task dependency graph and runs every task that `build` depends on, in order.

That `./` prefix is important. You always run `./gradlew`, not `gradle`. The `gradlew` file is the Gradle Wrapper — a small script that downloads the exact version of Gradle specified in your project and uses it. This guarantees every developer on your team, and every CI server, uses the same Gradle version. If someone on Windows runs `gradlew.bat` and someone on Mac runs `./gradlew`, they get identical builds. Commit the wrapper files to Git. Always.

The task output I've shown on the slide is what you see when you run `./gradlew build`. Notice each task has a colon prefix — `:compileJava`, `:processResources`, `:test`, `:bootJar`, `:build`. The task graph ensures compilation happens before testing, and testing happens before packaging. If `:test` fails, `:bootJar` never runs.

The common commands. `./gradlew build` is your main command — does everything. `./gradlew bootRun` runs the application. `./gradlew test` runs only tests. `./gradlew build -x test` skips tests during the build — the `-x` flag excludes a specific task. `./gradlew tasks` lists every available task — incredibly useful when you're new to a project.

Gradle dependency configurations. The concept is the same as Maven scopes but the names differ. `implementation` is the workhorse — compile and runtime, included in the JAR. It's what you'll use for 90% of dependencies. `testImplementation` is for test dependencies. `compileOnly` for things needed at compile time only — Lombok. `runtimeOnly` for things needed at runtime but not at compile time — most JDBC drivers. `annotationProcessor` is specific to Gradle: it's for tools that process annotations to generate code at compile time.

`implementation` versus `api` is worth understanding. If you're building a library — something others depend on — and your dependency's types appear in your public method signatures, use `api` so those types are available to your dependents. If you're building an application, always use `implementation`. Using `implementation` instead of `api` means changes to that dependency don't force recompilation of everything that depends on you. This makes large builds significantly faster.

---

### [40:00–48:00] Slide 11 — Maven vs Gradle and Naming Conventions

Maven vs Gradle — when do you use which? The honest answer is: for new Spring Boot projects, Spring Initializr defaults to Gradle Kotlin DSL and that's a fine choice. For large enterprises with existing Maven infrastructure, Maven is often the choice. On the job, you'll inherit whatever the project uses. Knowing both means you're never the person who says "I don't know how to build this."

Performance difference is real but mostly relevant at scale. For a project with 50 source files, both are fast. For a monorepo with 500 modules, Gradle's incremental builds and build cache make it dramatically faster. Google's Android build system uses Gradle, and they've invested heavily in build performance tooling around it.

The practical bottom line: the concepts are the same. `pom.xml` and `build.gradle` describe the same things — project identity, dependencies, how to compile and test. The `src/main/java` structure is identical. The Maven lifecycle and Gradle task graph accomplish the same goals. Learn one deeply and you can pick up the other in an hour.

Now let's talk naming conventions — because what you name things is the first thing anyone reads when they look at your code. Java has very strong community conventions and they're not optional in a professional environment.

Classes, interfaces, and enums use UpperCamelCase. `BookService`, `UserRepository`, `Genre`. No exceptions. Methods and variables use lowerCamelCase. `findById`, `calculateTotal`, `bookTitle`. Constants — `static final` fields — use UPPER_SNAKE_CASE. `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`. Packages use lowercase dotted notation — `com.bookstore.service`. Test classes are named after the class they test with a `Test` suffix — `BookServiceTest`.

Method naming matters too. Boolean-returning methods should start with `is`, `has`, or `can` — `isAvailable()`, `hasStock()`, `canDelete()`. Repository methods that retrieve data should use `find` — `findById()`, `findAllByGenre()`. This aligns with Spring Data JPA naming conventions that we'll use on Day 27.

The wrong examples on the slide — `book_service` in snake_case, `CalculateTotal` with a capital first letter — these make your code immediately look unprofessional. Checkstyle will flag them. Code reviewers will catch them. They're easy to get right from day one.

---

### [48:00–56:00] Slides 12–15 — Package Structure, Javadoc, and Code Quality

Package structure. Two strategies. Package by layer — all controllers in `controller/`, all services in `service/`, and so on. Package by feature — all book-related classes in `book/`, all author-related in `author/`. For this course, we use package by layer. It's the convention in Spring Boot tutorials and most job interview codebases. When you see a project with `controller/`, `service/`, `repository/` packages, you immediately know where to find things. 

One non-negotiable rule: your `@SpringBootApplication` main class lives at the ROOT of your base package. If your base package is `com.bookstore`, the main class is in `com.bookstore`, not in any sub-package. Spring Boot's component scan starts from the main class's package and scans downward. If your main class is in a sub-package, beans in sibling packages won't be found. This is a silent bug that wastes hours.

Comments. The rule is simple: comments explain why, not what. If I have to comment `// increment i` above `i++`, the comment is worthless noise. Anyone who reads Java knows what `i++` does. But if I have a linked hash map and the comment says "preserve insertion order for consistent pagination," that's gold — it explains the design decision, the reasoning, the constraint. Future you (and future colleagues) will thank past you.

Commented-out code is a code smell. Use version control. If you commented it out, delete it. If you need it back, check Git history. The worst thing in a codebase is blocks of commented-out code with no explanation of when it was written, why it was removed, or if it's safe to delete permanently.

Javadoc for public APIs. In library or API code, every public method should have a Javadoc comment. The format: `/**` to open, `*/` to close. `@param` for each parameter — document what null behavior is. `@return` for the return value — document whether null or empty is possible. `@throws` for exceptions — both checked and important unchecked. You can run `mvn javadoc:javadoc` or `./gradlew javadoc` to generate HTML documentation from your Javadoc comments. When you start using Spring Data JPA, those repository interfaces have excellent Javadoc that you'll read constantly.

Code quality tools — Checkstyle and SpotBugs. Checkstyle enforces style rules. The Google style guide is the most widely used ruleset. A hundred character line limit, four-space indentation, braces required on all control structures, no wildcard imports. You can configure Checkstyle to fail the build if violations are found — then bad style literally can't be merged.

SpotBugs is static analysis. It finds bugs without running your code. Null pointer dereferences — calling a method on something that might be null. Resource leaks — opening a stream and not closing it. Infinite loops. SQL injection vulnerabilities. It finds real bugs that slip through code review.

In a professional team, both tools run in CI. Every pull request must pass Checkstyle and SpotBugs before it can be merged. You can't ship code that violates the team's standards. This isn't gatekeeping — it's how teams maintain a consistent, professional codebase as headcount grows.

---

### [56:00–60:00] Slide 16 — Part 1 Wrap-Up

Let's recap. Build tools solve the dependency management and build reproducibility problem. Maven uses `pom.xml` with XML configuration and a fixed lifecycle: `clean → compile → test → package → install → deploy`. Gradle uses `build.gradle` with Groovy or Kotlin DSL and a task graph. Both use `src/main/java` and `src/test/java`. Maven output goes to `target/`, Gradle to `build/`.

Classes are UpperCamelCase, methods and variables are lowerCamelCase, constants are UPPER_SNAKE_CASE, packages are lowercase. Your main class is at the root of your base package. Package by layer: `controller/`, `service/`, `repository/`, `model/`, `dto/`, `exception/`, `config/`. Comments explain why, not what. Javadoc on public APIs.

Take ten minutes. When you come back, we go deep on Spring. You're going to understand IoC — Inversion of Control — which sounds intimidating but is actually a beautifully simple idea. You'll understand what the Spring container is, why dependency injection exists, and how Spring wires an application together from annotated Java classes. Everything after today builds on what you're about to learn. See you in ten.
