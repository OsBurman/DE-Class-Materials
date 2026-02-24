# Exercise 09: Lombok Integration in Spring Projects

## Objective
Use Lombok annotations to eliminate boilerplate code from Java model and service classes, and wire the resulting classes into a Spring application.

## Background
Lombok is an annotation processor that generates repetitive Java code (getters, setters, constructors, `toString`, `equals`/`hashCode`, loggers) at compile time. In Spring Boot projects it dramatically reduces the size of entity and DTO classes. However, using it correctly — especially with Spring DI — requires understanding which annotations to apply and when.

## Requirements

### Part 1 — Annotate the `Book` Model
The `Book` class in `starter-code/Book.java` currently has no getters, setters, constructors, `toString`, or `equals`/`hashCode`. Add Lombok annotations (no manual code) to:
1. Generate getters for all fields (`@Getter`)
2. Generate setters for all non-final fields (`@Setter`)
3. Generate a no-argument constructor (`@NoArgsConstructor`)
4. Generate an all-arguments constructor (`@AllArgsConstructor`)
5. Generate `toString()` (`@ToString`)
6. Generate `equals()` and `hashCode()` based on `id` only (`@EqualsAndHashCode(of = "id")`)

### Part 2 — Use `@Data` on the `Author` Model
The `Author` class needs all of the above combined. Use the single `@Data` annotation (which bundles `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@ToString`, `@EqualsAndHashCode`). Also add `@AllArgsConstructor` and `@NoArgsConstructor` explicitly since `@Data` only generates `@RequiredArgsConstructor`.

### Part 3 — Use `@Builder` on `BookRequest`
`BookRequest` is a DTO used to create a book. Apply `@Builder` so it can be constructed with the builder pattern. Also add `@Getter` (builders typically produce immutable objects — no setters needed).

### Part 4 — Use `@RequiredArgsConstructor` in a Service
In `BookCatalogService.java`:
1. Annotate with `@Service`.
2. Declare `BookRepository` as a `private final` field.
3. Add `@RequiredArgsConstructor` to the class — this generates a constructor for all `final` fields, which Spring uses for **constructor injection** without needing an explicit `@Autowired`.
4. Add `@Slf4j` to generate a `log` field.
5. Implement `createBook(BookRequest request)`: log an INFO message `"Creating book: {}"` with the request title, then return a new `Book` built from the request data (id=0, use `request.getTitle()` and `request.getAuthorId()`).

### Part 5 — Main App
In `LombokDemoApp.java`:
1. Create an `AnnotationConfigApplicationContext` using `LombokConfig.class`.
2. Retrieve `BookCatalogService`.
3. Build a `BookRequest` using the builder: title `"Effective Java"`, authorId `1`.
4. Call `createBook(request)` and print the result using the Lombok-generated `toString()`.
5. Close the context.

## Hints
- `@RequiredArgsConstructor` generates a constructor only for `final` fields and `@NonNull`-annotated fields. Making the repository `final` is what causes Spring to inject it via the constructor.
- `@Data` is convenient but generates setters for all fields — avoid it on JPA entities where mutable setters can cause issues. Prefer `@Getter`/`@Setter` with `@Builder` for entities.
- `@Slf4j` generates `private static final Logger log = LoggerFactory.getLogger(ClassName.class);` — you can use `log.info(...)` immediately without declaring it.
- The `@Builder` pattern: `BookRequest.builder().title("X").authorId(1).build()`.

## Expected Output

```
[INFO] BookCatalogService - Creating book: Effective Java
Created: Book(id=0, title=Effective Java, authorId=1)
```
