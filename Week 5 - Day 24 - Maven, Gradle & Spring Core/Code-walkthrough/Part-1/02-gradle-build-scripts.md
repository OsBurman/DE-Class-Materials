# Gradle Build Scripts

## What Is Gradle?

Gradle is a modern, flexible build automation tool that uses a **Groovy** or **Kotlin** DSL (Domain-Specific Language) instead of XML. Gradle is the default build tool for Android development and is widely used in Java/Spring projects alongside Maven.

**Key Differences from Maven:**
- Configuration is code (Groovy/Kotlin) rather than declarative XML
- Faster incremental builds via build cache and daemon
- More flexible — you write logic, not just declarations
- Tasks are first-class citizens (vs Maven lifecycle phases)

---

## Gradle Standard Directory Layout

Gradle follows the same standard directory structure as Maven:

```
my-project/
├── build.gradle            ← Groovy DSL build script (or build.gradle.kts for Kotlin DSL)
├── settings.gradle         ← Project name, multi-project module declarations
├── gradle.properties       ← Key-value properties (versions, JVM flags)
├── gradlew                 ← Unix wrapper script (run Gradle without installing it)
├── gradlew.bat             ← Windows wrapper script
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties  ← Wrapper config (Gradle version)
└── src/
    ├── main/
    │   ├── java/           ← Production source code
    │   └── resources/      ← application.properties, logback.xml, etc.
    └── test/
        ├── java/           ← Test source code
        └── resources/      ← Test resource files
```

---

## Groovy DSL — `build.gradle`

This is the most common form of Gradle build script. It uses Apache Groovy syntax.

```groovy
// ─────────────────────────────────────────────────────────────
// PLUGINS BLOCK
// Applies plugins that add tasks and capabilities to the build
// ─────────────────────────────────────────────────────────────
plugins {
    id 'java'                         // Adds compile, test, jar tasks
    id 'application'                  // Adds run task + distribution packaging
    // id 'org.springframework.boot' version '3.2.0'   // Spring Boot plugin
}

// ─────────────────────────────────────────────────────────────
// PROJECT IDENTITY
// ─────────────────────────────────────────────────────────────
group = 'com.example'                 // Organization / namespace
version = '1.0.0'                     // Project version

// ─────────────────────────────────────────────────────────────
// JAVA TOOLCHAIN
// Declares which Java version to compile with
// ─────────────────────────────────────────────────────────────
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// ─────────────────────────────────────────────────────────────
// REPOSITORIES
// Where Gradle looks for dependencies
// ─────────────────────────────────────────────────────────────
repositories {
    mavenCentral()                    // Maven Central Repository
    // mavenLocal()                  // Local ~/.m2 cache
    // maven { url 'https://repo.spring.io/milestone' }  // Custom repo
}

// ─────────────────────────────────────────────────────────────
// DEPENDENCIES
// ─────────────────────────────────────────────────────────────
dependencies {
    // implementation  → available at compile time AND runtime
    implementation 'org.springframework:spring-context:6.1.0'
    implementation 'org.springframework:spring-core:6.1.0'

    // compileOnly     → available at compile time ONLY (e.g., Lombok annotation processor)
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'

    // runtimeOnly     → available at runtime ONLY (e.g., JDBC driver)
    runtimeOnly 'org.postgresql:postgresql:42.7.1'

    // testImplementation → available in test compile and runtime
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'

    // testRuntimeOnly → available in test runtime only
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

// ─────────────────────────────────────────────────────────────
// APPLICATION EXTENSION (only when using 'application' plugin)
// ─────────────────────────────────────────────────────────────
application {
    mainClass = 'com.example.Main'
}

// ─────────────────────────────────────────────────────────────
// TEST CONFIGURATION
// ─────────────────────────────────────────────────────────────
test {
    useJUnitPlatform()                // Use JUnit 5
    testLogging {
        events "passed", "skipped", "failed"
    }
}
```

---

## Kotlin DSL — `build.gradle.kts`

The Kotlin DSL is increasingly common, especially in Spring Boot projects. It provides type safety and IDE auto-completion.

```kotlin
// ─────────────────────────────────────────────────────────────
// PLUGINS BLOCK
// ─────────────────────────────────────────────────────────────
plugins {
    java
    application
    // id("org.springframework.boot") version "3.2.0"
    // id("io.spring.dependency-management") version "1.1.4"
}

// ─────────────────────────────────────────────────────────────
// PROJECT IDENTITY
// ─────────────────────────────────────────────────────────────
group = "com.example"
version = "1.0.0"

// ─────────────────────────────────────────────────────────────
// JAVA TOOLCHAIN
// ─────────────────────────────────────────────────────────────
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// ─────────────────────────────────────────────────────────────
// REPOSITORIES
// ─────────────────────────────────────────────────────────────
repositories {
    mavenCentral()
}

// ─────────────────────────────────────────────────────────────
// DEPENDENCIES
// ─────────────────────────────────────────────────────────────
dependencies {
    implementation("org.springframework:spring-context:6.1.0")
    implementation("org.springframework:spring-core:6.1.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    runtimeOnly("org.postgresql:postgresql:42.7.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ─────────────────────────────────────────────────────────────
// APPLICATION EXTENSION
// ─────────────────────────────────────────────────────────────
application {
    mainClass.set("com.example.Main")
}

// ─────────────────────────────────────────────────────────────
// TEST CONFIGURATION
// ─────────────────────────────────────────────────────────────
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
```

---

## `settings.gradle` (and `settings.gradle.kts`)

The settings file is **required** — it declares the project name. It also defines modules in a multi-project build.

```groovy
// settings.gradle (Groovy DSL)

rootProject.name = 'bookstore'

// For multi-project builds:
// include 'bookstore-api'
// include 'bookstore-domain'
// include 'bookstore-data'
```

```kotlin
// settings.gradle.kts (Kotlin DSL)

rootProject.name = "bookstore"

// include("bookstore-api")
// include("bookstore-domain")
```

---

## `gradle.properties`

Key-value property file. Commonly used to declare versions and JVM tuning flags.

```properties
# Dependency versions
springVersion=6.1.0
lombokVersion=1.18.30

# JVM tuning
org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m

# Enable Gradle build cache (speeds up repeated builds)
org.gradle.caching=true

# Enable Gradle configuration cache
org.gradle.configuration-cache=true
```

Reference properties in `build.gradle`:
```groovy
implementation "org.springframework:spring-context:${springVersion}"
```

---

## Gradle Dependency Configurations

| Configuration | Available At | Use Case |
|---|---|---|
| `implementation` | Compile + Runtime | Standard app dependency |
| `api` | Compile + Runtime (exported) | Library API (requires `java-library` plugin) |
| `compileOnly` | Compile time only | Annotation processors, Lombok |
| `annotationProcessor` | Annotation processing | Lombok, MapStruct |
| `runtimeOnly` | Runtime only | JDBC drivers, logging implementations |
| `testImplementation` | Test compile + Test runtime | JUnit, Mockito, AssertJ |
| `testRuntimeOnly` | Test runtime only | JUnit Platform Launcher |

---

## Gradle Tasks

Gradle's build model is **task-based**. Tasks are units of work. The `java` plugin adds a set of standard tasks.

### Core Tasks Added by the `java` Plugin

| Task | Description |
|---|---|
| `compileJava` | Compiles `src/main/java` to `build/classes/java/main` |
| `processResources` | Copies `src/main/resources` to `build/resources/main` |
| `classes` | Depends on `compileJava` + `processResources` |
| `compileTestJava` | Compiles `src/test/java` |
| `test` | Runs all tests with the configured test framework |
| `jar` | Packages compiled classes into a `.jar` in `build/libs/` |
| `build` | Runs `assemble` + `check` (full build including tests) |
| `clean` | Deletes the `build/` directory |
| `javadoc` | Generates Javadoc HTML in `build/docs/javadoc/` |

### Defining Custom Tasks (Groovy DSL)

```groovy
// Simple custom task
tasks.register('greet') {
    group = 'Custom'
    description = 'Prints a greeting message'
    doLast {
        println "Hello from Gradle!"
    }
}

// Task with doFirst and doLast
tasks.register('deployApp') {
    group = 'Deploy'
    description = 'Packages and prepares the app for deployment'
    dependsOn 'build'              // runs 'build' task first

    doFirst {
        println "Starting deployment process..."
    }
    doLast {
        println "Deployment package ready in build/libs/"
    }
}

// Task that copies files
tasks.register('copyConfig', Copy) {
    from 'src/main/resources'
    into 'build/config'
    include '*.properties'
}
```

### Defining Custom Tasks (Kotlin DSL)

```kotlin
tasks.register("greet") {
    group = "Custom"
    description = "Prints a greeting message"
    doLast {
        println("Hello from Gradle!")
    }
}

tasks.register("deployApp") {
    group = "Deploy"
    dependsOn("build")
    doLast {
        println("Deployment package ready in build/libs/")
    }
}
```

---

## Gradle Wrapper (`gradlew`)

The Gradle Wrapper lets you run Gradle without installing it globally. Every project should use the wrapper.

### `gradle/wrapper/gradle-wrapper.properties`

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

The `distributionUrl` pins the exact Gradle version for the project.

### Generating a Wrapper (one-time setup)

```bash
gradle wrapper --gradle-version 8.5
```

This creates `gradlew`, `gradlew.bat`, and the `gradle/wrapper/` directory.

---

## Common Gradle Commands

```bash
# List all available tasks
./gradlew tasks --all

# Compile source code
./gradlew compileJava

# Run all tests
./gradlew test

# Build the project (compile + test + jar)
./gradlew build

# Clean build artifacts
./gradlew clean

# Clean then rebuild
./gradlew clean build

# Run application (requires 'application' plugin)
./gradlew run

# Build without running tests
./gradlew build -x test

# See detailed output / stacktrace
./gradlew build --info
./gradlew build --stacktrace

# Run a specific test class
./gradlew test --tests "com.example.BookServiceTest"

# Run a custom task
./gradlew greet
```

---

## Maven vs Gradle Comparison

| Feature | Maven | Gradle |
|---|---|---|
| **Configuration format** | XML (`pom.xml`) | Groovy or Kotlin DSL |
| **Build model** | Fixed lifecycle phases | DAG of tasks (flexible) |
| **Performance** | Slower (no incremental build cache by default) | Faster (incremental builds, build cache, daemon) |
| **Flexibility** | Convention-based (less flexible) | Highly flexible (logic in build scripts) |
| **Learning curve** | Lower (declarative XML is familiar) | Higher (requires learning DSL) |
| **IDE support** | Excellent | Excellent (especially Kotlin DSL) |
| **Android support** | Not used | Official Android build system |
| **Spring Boot default** | Used in official starters | Also supported |
| **Dependency syntax** | `<groupId>:<artifactId>:<version>` in XML | `'group:artifact:version'` string |
| **Plugin ecosystem** | Mature and large | Growing, excellent coverage |
| **Multi-project builds** | Supported (modules) | Superior support |
| **Build caching** | Limited | Built-in, distributed cache available |

### When to Choose Maven
- Team is already familiar with Maven
- Project requires strict, predictable build lifecycle
- Working in an enterprise with XML-based tooling
- Simpler projects with standard build requirements

### When to Choose Gradle
- New Spring Boot projects (Spring Initializr offers both)
- Android development (required)
- Need faster builds with incremental compilation
- Complex multi-module projects with shared logic
- Team prefers code over configuration

---

## Equivalent Commands: Maven vs Gradle

| Goal | Maven Command | Gradle Command |
|---|---|---|
| Compile | `mvn compile` | `./gradlew compileJava` |
| Run tests | `mvn test` | `./gradlew test` |
| Package to JAR | `mvn package` | `./gradlew jar` |
| Full build | `mvn install` | `./gradlew build` |
| Clean | `mvn clean` | `./gradlew clean` |
| Clean + build | `mvn clean install` | `./gradlew clean build` |
| Skip tests | `mvn install -DskipTests` | `./gradlew build -x test` |
| List tasks/phases | `mvn help:describe` | `./gradlew tasks --all` |

---

## Key Takeaways

- Gradle uses **Groovy or Kotlin DSL** — configuration is actual code, not XML
- The `build.gradle` (or `build.gradle.kts`) is the central config file; `settings.gradle` declares project name
- **Dependency configurations** (`implementation`, `compileOnly`, `runtimeOnly`, `testImplementation`) replace Maven's `<scope>` tags
- Gradle's **task graph** is more flexible than Maven's fixed lifecycle; you can define and chain custom tasks
- Always use the **Gradle Wrapper** (`gradlew`) so all developers use the same Gradle version
- Gradle is **faster** than Maven for incremental builds due to the build daemon and caching
- Both tools produce the same output — choosing between them is primarily a team/project preference
