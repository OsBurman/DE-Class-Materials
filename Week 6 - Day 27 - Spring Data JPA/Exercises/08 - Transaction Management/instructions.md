# Exercise 08 — Transaction Management

## Learning Objectives
- Apply `@Transactional` to service methods and understand its default behaviour
- Use `readOnly = true` for query-only methods to optimize performance
- Demonstrate automatic rollback on `RuntimeException`
- Understand `Propagation.REQUIRED` (default) vs `Propagation.REQUIRES_NEW`

## Background

A **transaction** is an all-or-nothing unit of work. If any step fails, the entire operation is rolled back.

| Attribute | Default | Purpose |
|---|---|---|
| `propagation` | `REQUIRED` | Join an existing transaction or start a new one |
| `readOnly` | `false` | Hint to the database for read-only optimization |
| `rollbackFor` | `RuntimeException` | Which exception types trigger rollback |
| `noRollbackFor` | _(none)_ | Exception types that should NOT cause rollback |

**`Propagation.REQUIRED`** (default) — reuses the caller's transaction if one exists.  
**`Propagation.REQUIRES_NEW`** — always suspends the caller's transaction and starts a fresh one.

## Instructions

### Step 1 — Annotate `BookService` methods

Open `starter-code/BookService.java` and complete each TODO:

```java
// TODO 1: Mark saveAll() with @Transactional — all books saved or none
// TODO 2: Mark findAll() with @Transactional(readOnly = true)
// TODO 3: Mark saveWithFailure() with @Transactional
//          This method saves book1 successfully, then throws RuntimeException
//          Verify that book1 is NOT persisted (whole transaction rolled back)
```

### Step 2 — Observe behaviour in `DataLoader`

Follow the TODO comments to:
1. Call `saveAll()` with 3 valid books and verify count
2. Call `saveWithFailure()` inside a try/catch and verify the count did NOT change
3. Add a `@Transactional(readOnly = true)` method and confirm no write operations are needed

### Step 3 — Run

```
mvn spring-boot:run
```

After calling `saveWithFailure()` you should see a `ROLLBACK` message in the SQL output and the book count should stay at 3.

## Expected Output (example)

```
--- After saveAll ---
Book count: 3

--- After saveWithFailure (should rollback) ---
Exception caught: Simulated failure after first save
Book count: 3   ← unchanged because rollback occurred

--- Read-only findAll ---
Book{id=1, title='Clean Code', ...}
Book{id=2, title='Refactoring', ...}
Book{id=3, title='The Pragmatic Programmer', ...}
```

## Key Concepts

- `@Transactional` on a Spring bean method wraps execution in a transaction via a proxy
- `readOnly = true` allows the JPA provider to skip dirty-checking, reducing overhead
- Any unchecked (`RuntimeException`) causes automatic rollback; checked exceptions do NOT by default
- To rollback on a checked exception: `@Transactional(rollbackFor = Exception.class)`
- Self-invocation (calling a `@Transactional` method from within the same class) bypasses the proxy — the annotation has no effect
