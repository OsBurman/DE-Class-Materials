# Exercise 08: Bean Scopes in Spring

## Objective
Observe the difference between Spring's `singleton` and `prototype` bean scopes by requesting the same bean multiple times and checking whether the same object is returned.

## Background
By default every Spring bean is a **singleton** — the container creates exactly one instance and returns the same object every time it is requested. A **prototype** bean creates a fresh instance every time it is requested. Choosing the wrong scope causes subtle bugs: shared mutable state in a singleton used across requests, or unnecessary object creation with prototype when singleton would suffice. `request` and `session` scopes are used in web applications (one instance per HTTP request/session) but require a web context — this exercise focuses on singleton and prototype.

## Requirements

### Part 1 — Singleton Scope (default)
1. Annotate `CartService` with `@Component` and `@Scope("singleton")` (or omit `@Scope` since singleton is default — add it explicitly here for clarity).
2. Implement `addItem(String item)` to add the item to an internal `List<String>`.
3. Implement `getItems()` to return the list as a formatted string.

### Part 2 — Prototype Scope
1. Annotate `RequestContext` with `@Component` and `@Scope("prototype")`.
2. Implement a `requestId` field set to a unique value in the constructor (`UUID.randomUUID().toString().substring(0, 8)`).
3. Implement `getRequestId()` to return that ID.

### Part 3 — Demonstrate the Difference
In `BeanScopeApp.java`:
1. Create a Spring context using `ScopeConfig.class`.
2. Retrieve `CartService` twice as two separate variables. Call `addItem("Book A")` on the first reference. Call `getItems()` on the *second* reference and confirm both show the same list — proving they are the same object.
3. Retrieve `RequestContext` twice as two separate variables. Print the `requestId` from each — they must be different, proving new instances were created.
4. Print a separator line between the two demonstrations.
5. Close the context.

### Part 4 — Scope Reference Table
Complete the table in `starter-code/ScopeWorksheet.md`.

## Hints
- `@Scope("singleton")` and `@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)` are equivalent.
- `@Scope("prototype")` means Spring creates a new instance for *every* `context.getBean()` call.
- Two variables pointing to the same singleton will always have the same object reference (`==` comparison returns true).
- `request` and `session` scopes require a Spring web context (`WebApplicationContext`); they can't be used in a standalone `AnnotationConfigApplicationContext`.

## Expected Output

```
=== Singleton Scope ===
cart1.addItem("Book A")
cart2.getItems() → [Book A]   (same instance — cart1 and cart2 are the same object)

=== Prototype Scope ===
requestContext1 ID: a1b2c3d4
requestContext2 ID: e5f6g7h8   (different IDs — two separate instances)
```
*(The actual UUID substrings will differ each run — what matters is that they are different from each other.)*
