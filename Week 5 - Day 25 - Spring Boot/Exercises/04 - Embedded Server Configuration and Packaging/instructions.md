# Exercise 04: Embedded Server Configuration and Packaging

## Objective
Configure the embedded Tomcat server through application properties, understand how to swap to Jetty, build an executable fat JAR, and practice running a Spring Boot app from the command line.

## Background
Spring Boot ships with an embedded Tomcat server — you never need to install or configure an external application server. All server settings (port, context path, session timeout, etc.) are controlled through `application.yml`. The `spring-boot-maven-plugin` packages everything — your code, dependencies, and the embedded server — into a single self-contained JAR you can run with `java -jar`.

## Requirements

### Part 1 — Configure the embedded Tomcat server
1. In `starter-code/application.yml`, set:
   - `server.port: 9090`
   - `server.servlet.context-path: /library`
   - `server.tomcat.connection-timeout: 20s`
2. Complete `ServerInfoRunner.java`:
   - Inject `ServerProperties` (from `org.springframework.boot.autoconfigure.web.ServerProperties`).
   - In `run()`, print:
     - `Server port: <port>`
     - `Context path: <context-path>`

### Part 2 — Swap to Jetty (conceptual configuration)
3. In `starter-code/pom.xml`, the `spring-boot-starter-web` dependency is already present. Add the XML snippet that:
   - Excludes `spring-boot-starter-tomcat` from `spring-boot-starter-web`.
   - Adds `spring-boot-starter-jetty` as a separate dependency.
   (Use the TODO comment in the file as a guide.)

### Part 3 — Build and run commands
4. Complete `packaging-commands.md`: fill in the correct Maven command for each numbered step (use the TODO markers).

## Hints
- `ServerProperties` is a `@ConfigurationProperties` class already registered by Spring Boot's web auto-configuration — you can inject it directly without `@EnableConfigurationProperties`.
- The context path defaults to `/` if not set; with `/library` set, your endpoint moves from `localhost:9090/api/books` to `localhost:9090/library/api/books`.
- Excluding Tomcat and adding Jetty does **not** require any code changes — only the `pom.xml` changes; Spring Boot's auto-configuration detects Jetty on the classpath and uses it instead.
- The Spring Boot fat JAR is built with `mvn package` and run with `java -jar target/<artifactId>-<version>.jar`.

## Expected Output
```
Server port: 9090
Context path: /library
```
