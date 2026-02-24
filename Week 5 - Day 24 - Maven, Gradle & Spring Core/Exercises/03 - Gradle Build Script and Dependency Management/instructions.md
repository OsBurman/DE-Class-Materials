# Exercise 03: Gradle Build Script and Dependency Management

## Objective
Write a complete Gradle `build.gradle` (Groovy DSL) that configures a Java project with dependencies, a Java version, and custom tasks.

## Background
Gradle is a modern build tool that uses a Groovy (or Kotlin) DSL instead of XML. Unlike Maven's rigid lifecycle, Gradle uses a **task graph** — you define tasks and their dependencies, and Gradle executes only what is needed. Gradle is the default build tool for Android and is increasingly common in enterprise Spring Boot projects.

## Requirements
1. Apply the `java` plugin to enable Java compilation.
2. Set `sourceCompatibility` and `targetCompatibility` to Java 17.
3. Declare the **Maven Central** repository as the source for dependencies.
4. Add the following dependencies in the correct configurations:
   - **JUnit Jupiter API** (group: `org.junit.jupiter`, name: `junit-jupiter-api`, version: `5.10.2`, configuration: `testImplementation`)
   - **JUnit Jupiter Engine** (group: `org.junit.jupiter`, name: `junit-jupiter-engine`, version: `5.10.2`, configuration: `testRuntimeOnly`)
   - **SLF4J API** (group: `org.slf4j`, name: `slf4j-api`, version: `2.0.13`, configuration: `implementation`)
   - **Logback Classic** (group: `ch.qos.logback`, name: `logback-classic`, version: `1.5.6`, configuration: `implementation`)
5. Configure the `test` task to use the **JUnit Platform** (`useJUnitPlatform()`).
6. Write a custom Gradle task named `hello` of type `DefaultTask` that:
   - Is in the group `"custom"`
   - Has the description `"Prints a greeting from Gradle"`
   - When executed, prints: `Hello from Gradle! Java version: 17`
7. Write a custom task named `projectInfo` that prints the project `name`, `group`, and `version` when run.

Set the project `group` to `com.library` and `version` to `1.0.0-SNAPSHOT`.

## Hints
- The `plugins {}` block (using the new plugins DSL) is the modern way to apply plugins: `id 'java'`
- Gradle dependency configurations: `implementation` = compile + runtime; `testImplementation` = test compile; `testRuntimeOnly` = test runtime only.
- Custom tasks use `task taskName { doLast { /* code */ } }` syntax. The `doLast` block runs the action.
- `project.name`, `project.group`, and `project.version` are properties available inside any task.
- `useJUnitPlatform()` inside the `test { }` block is all that's needed to enable JUnit 5.

## Expected Output

Running `./gradlew hello`:
```
> Task :hello
Hello from Gradle! Java version: 17

BUILD SUCCESSFUL
```

Running `./gradlew projectInfo`:
```
> Task :projectInfo
Project: library-management
Group:   com.library
Version: 1.0.0-SNAPSHOT

BUILD SUCCESSFUL
```

Running `./gradlew dependencies --configuration implementation` should list:
```
implementation - ...
+--- org.slf4j:slf4j-api:2.0.13
\--- ch.qos.logback:logback-classic:1.5.6
```
