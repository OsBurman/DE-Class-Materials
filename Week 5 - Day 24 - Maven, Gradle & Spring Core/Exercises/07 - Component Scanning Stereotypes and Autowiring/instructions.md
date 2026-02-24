# Exercise 07: Component Scanning, Stereotypes and Auto-Wiring

## Objective
Use Spring component scanning and stereotype annotations (`@Component`, `@Service`, `@Repository`) to register beans automatically, then wire them together with `@Autowired`.

## Background
In Exercises 05 and 06 you registered beans manually in a `@Configuration` class. In real Spring Boot projects, you almost never do that — instead you annotate your classes with **stereotype annotations** and Spring's component scanner finds and registers them automatically. Understanding which stereotype to use for which layer is a key Spring convention.

## Requirements

### Part 1 — Annotate the Classes
1. Annotate `AuthorRepository` with the correct **stereotype** for a data-access class.
2. Annotate `AuthorService` with the correct **stereotype** for a business-logic class.
3. Annotate `AuthorController` with the correct **stereotype** for a general component (not web-specific in this exercise).

### Part 2 — Enable Component Scanning
In `ScanningConfig.java`:
1. Add `@Configuration`.
2. Add `@ComponentScan("com.library")` to tell Spring to scan that package for annotated classes.

### Part 3 — Wire the Dependencies
In `AuthorService`:
- Inject `AuthorRepository` using **constructor injection** (recommended). Add `@Autowired` to the constructor.

In `AuthorController`:
- Inject `AuthorService` using **constructor injection**. Add `@Autowired` to the constructor.

### Part 4 — Implement the Methods
`AuthorRepository.findById(int id)` — return `"Author #" + id`
`AuthorService.getAuthorName(int id)` — call `repository.findById(id)` and return the result
`AuthorController.handleRequest(int id)` — call `service.getAuthorName(id)` and return `"Controller response: " + result`

### Part 5 — Main App
In `ComponentScanApp.java`, create a context with `ScanningConfig.class`, retrieve `AuthorController`, call `handleRequest(7)`, print the result, then close the context.

## Hints
- `@Repository` should be used for database-access classes — Spring also translates persistence exceptions for this stereotype.
- `@Service` should be used for business/domain logic classes.
- `@Component` is the generic stereotype for everything else.
- `@Controller` / `@RestController` are used for web layer classes in Spring MVC.
- Spring's scanner finds any class annotated with `@Component` or a meta-annotation that includes `@Component` (which all stereotypes do).

## Expected Output

```
Controller response: Author #7
```
