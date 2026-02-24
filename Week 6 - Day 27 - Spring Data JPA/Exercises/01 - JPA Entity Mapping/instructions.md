# Exercise 01 — JPA Entity Mapping

## Objective
Map a Java class to a relational database table using JPA annotations: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, and `@Column`.

## Background
JPA (Java Persistence API) lets you map Java objects to database rows using annotations. Spring Boot auto-configures the schema when using an H2 in-memory database. Each annotated class becomes a table; each field becomes a column.

## Requirements
1. Complete `pom.xml` — add `spring-boot-starter-data-jpa` and `h2` dependencies.
2. Complete `LibraryApplication.java` — add `@SpringBootApplication` and `SpringApplication.run(...)`.
3. Complete `Book.java`:
   - Annotate the class with `@Entity` and `@Table(name = "books")`
   - Annotate `id` with `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`
   - Annotate `title` with `@Column(nullable = false, length = 200)`
   - Annotate `genre` with `@Column(nullable = false, length = 100)`
   - Annotate `publishedYear` with `@Column(name = "published_year")`
   - Add a no-arg constructor (required by JPA)
   - Add an all-args constructor for convenience
   - Add getters and a `toString()` override
4. Complete `DataLoader.java` — on startup, persist three `Book` objects using `EntityManager` and print the saved entities.

## Hints
- `GenerationType.IDENTITY` delegates id generation to the database (auto-increment).
- JPA requires a no-argument constructor on every entity — it can be `protected`.
- `@Column(name = "published_year")` maps the Java field to a snake_case column name.
- Use `@Transactional` on the data-loading method so the `EntityManager` write is within a transaction.

## Expected Output
```
Saved: Book{id=1, title='Clean Code', genre='Programming', publishedYear=2008}
Saved: Book{id=2, title='Dune', genre='Science Fiction', publishedYear=1965}
Saved: Book{id=3, title='The Pragmatic Programmer', genre='Programming', publishedYear=1999}
```
