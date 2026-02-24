# Exercise 01: Maven Project Structure and POM Configuration

## Objective
Configure a complete Maven `pom.xml` for a library management application, declaring the correct project metadata, dependencies, Java version, and build plugin.

## Background
Maven is a build automation tool that manages your project's structure, dependencies, and build lifecycle through a single file: the **Project Object Model** (`pom.xml`). Every Maven project has a standard directory layout and a `pom.xml` that acts as its single source of truth for what the project is, what it depends on, and how to build it.

## Requirements
1. Fill in the `pom.xml` `<groupId>`, `<artifactId>`, `<version>`, `<packaging>`, `<name>`, and `<description>` with the values shown in the Expected Output section.
2. Set the Java source and target version to **17** using the `maven-compiler-plugin` (version `3.11.0`).
3. Add the following dependencies from Maven Central:
   - **JUnit Jupiter** (group: `org.junit.jupiter`, artifact: `junit-jupiter`, version: `5.10.2`, scope: `test`)
   - **SLF4J API** (group: `org.slf4j`, artifact: `slf4j-api`, version: `2.0.13`)
   - **Logback Classic** (group: `ch.qos.logback`, artifact: `logback-classic`, version: `1.5.6`)
4. Add the `maven-surefire-plugin` (version `3.2.5`) so JUnit 5 tests are discovered and run correctly by `mvn test`.
5. Wrap the compiler and surefire plugins inside a `<build><plugins>` block.
6. Ensure the project uses `<maven.compiler.release>17</maven.compiler.release>` inside a `<properties>` block (in addition to the plugin configuration).

## Hints
- Every `<dependency>` needs exactly three children: `<groupId>`, `<artifactId>`, and `<version>`. Test-scoped dependencies also need `<scope>test</scope>`.
- The `maven-compiler-plugin` uses `<configuration><release>17</release></configuration>` (not `<source>/<target>` — `<release>` is the modern equivalent).
- The `maven-surefire-plugin` version `3.2.5` supports JUnit 5 natively without any extra configuration beyond declaring it.
- Group related elements: put all `<dependencies>` in one `<dependencies>` block; all plugins in one `<build><plugins>` block.

## Expected Output
When you run `mvn validate` (just validates the POM without compiling), you should see:

```
[INFO] BUILD SUCCESS
```

When you run `mvn dependency:list`, the output should include lines like:
```
[INFO]    ch.qos.logback:logback-classic:jar:1.5.6:compile
[INFO]    org.slf4j:slf4j-api:jar:2.0.13:compile
[INFO]    org.junit.jupiter:junit-jupiter:jar:5.10.2:test
```

The POM metadata block should resolve to:
```
Group:    com.library
Artifact: library-management
Version:  1.0.0-SNAPSHOT
Name:     Library Management System
```
