# Exercise 03: Environment Profiles and Profile-Specific Configuration

## Objective
Use Spring Boot profiles to load different configuration values for `dev` and `prod` environments, and activate beans conditionally with `@Profile`.

## Background
Real applications run in multiple environments — local development, staging, production — each needing different database URLs, log levels, or feature flags. Spring Boot's profile system lets you maintain one codebase and swap configuration at startup time using `spring.profiles.active`.

## Requirements

### Part 1 — Profile-specific YAML files
1. Complete `application-dev.yml` with development settings:
   - `server.port: 8080`
   - `library.datasource-url: jdbc:h2:mem:devdb`
   - `library.log-level: DEBUG`
2. Complete `application-prod.yml` with production settings:
   - `server.port: 80`
   - `library.datasource-url: jdbc:postgresql://prod-db:5432/library`
   - `library.log-level: WARN`
3. In `application.yml` (the base/default file), set `spring.profiles.active: dev` so the dev profile loads by default when you run locally.

### Part 2 — Profile-specific beans with `@Profile`
4. Complete `DataSourceConfig.java`:
   - Create an inner class (or two top-level classes if you prefer) annotated `@Configuration @Profile("dev")` that defines a `@Bean` method `dataSourceDescription()` returning the String `"H2 in-memory — dev profile active"`.
   - Create a second configuration class annotated `@Configuration @Profile("prod")` that defines the **same** `@Bean` method name returning `"PostgreSQL — prod profile active"`.
5. Complete `ProfileDemoRunner.java`:
   - Inject the `String dataSourceDescription` bean and the active `Environment`.
   - In `run()`, print:
     - `Active profile: <profile name>`
     - `DataSource: <description>`

### Part 3 — Switch profiles at runtime
6. In `application.yml`, **also** add a property `library.log-level: INFO` as a base default (the profile-specific file will override it).

## Hints
- Profile-specific files follow the naming convention `application-{profile}.yml`. Spring Boot merges them with the base `application.yml` — profile values win on conflict.
- `@Profile("dev")` on a `@Configuration` class means the entire class (and all its `@Bean` methods) are only registered when the `dev` profile is active.
- Inject `org.springframework.core.env.Environment` to call `env.getActiveProfiles()` — returns a `String[]`.
- To run with the `prod` profile from the command line: `java -jar app.jar --spring.profiles.active=prod`

## Expected Output
With `spring.profiles.active=dev` (default):
```
Active profile: dev
DataSource: H2 in-memory — dev profile active
```

With `spring.profiles.active=prod` (command-line override):
```
Active profile: prod
DataSource: PostgreSQL — prod profile active
```
