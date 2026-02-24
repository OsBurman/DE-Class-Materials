# Spring Boot Packaging Commands — Answers

## 1. Build the executable fat JAR (skip tests)
```bash
mvn package -DskipTests
```
Output: `target/library-service-0.0.1-SNAPSHOT.jar`
This JAR contains your compiled classes AND all dependencies (including the embedded server) — it is completely self-contained.

## 2. Run the fat JAR from the command line
```bash
java -jar target/library-service-0.0.1-SNAPSHOT.jar
```

## 3. Run with a different profile (prod) from the command line
```bash
java -jar target/library-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
Command-line arguments override `application.yml` — the highest precedence in Spring Boot's property hierarchy.

## 4. Run directly with Maven (without packaging first)
```bash
mvn spring-boot:run
```
Uses the Spring Boot Maven plugin to compile and run in-place — useful during development; does NOT create a JAR.

## 5. Thin JAR vs Fat (Uber) JAR
A **thin JAR** contains only your compiled classes and resources — it requires all dependencies to be on the classpath separately (e.g., in a `lib/` folder or an application server).
A **fat (uber) JAR** bundles your code AND all dependencies (including the embedded server) into a single self-contained archive, so `java -jar app.jar` is all that's needed to run it anywhere with a JRE.
