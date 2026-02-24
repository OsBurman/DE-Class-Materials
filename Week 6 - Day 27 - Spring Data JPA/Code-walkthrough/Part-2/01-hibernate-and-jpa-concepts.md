# Hibernate ORM Fundamentals & JPA vs Hibernate

## What Is an ORM?

**ORM = Object-Relational Mapper**

An ORM bridges the gap between two fundamentally different worlds:

| Object-Oriented World (Java)      | Relational World (Database)        |
|-----------------------------------|------------------------------------|
| Classes                           | Tables                             |
| Objects (instances)               | Rows                               |
| Fields/properties                 | Columns                            |
| References between objects        | Foreign keys / JOINs               |
| Inheritance hierarchies           | Table-per-hierarchy or table-per-class |
| Collections (`List<Order>`)       | One-to-many relationship tables    |

Without an ORM, you write SQL to SELECT rows and manually map each column to a field.
With an ORM, you work with Java objects — the ORM generates and executes the SQL for you.

---

## The Object-Relational Impedance Mismatch

The core challenge ORMs solve is that objects and relational tables don't naturally align:

- A `Customer` object might hold a `List<Order>` — but the database stores orders in a separate table with a foreign key
- Java supports inheritance — relational databases don't natively
- Identity: two Java objects with the same state are NOT equal by default; two database rows with the same PK ARE considered the same record

ORMs like Hibernate handle this translation transparently.

---

## JPA vs Hibernate

```
┌─────────────────────────────────────────────────────────────────────┐
│                    What You Actually Interact With                  │
│                                                                     │
│   JPA (Jakarta Persistence API)                                     │
│   ─────────────────────────────                                     │
│   A SPECIFICATION — defines interfaces, annotations, and behavior   │
│   No runtime code. Just a contract (javax.persistence / jakarta.persistence) │
│                                                                     │
│       ↕  implemented by                                             │
│                                                                     │
│   Hibernate ORM                                                     │
│   ──────────────                                                    │
│   The most popular JPA IMPLEMENTATION                               │
│   Actual runtime code that talks to the database                    │
│   Also has Hibernate-specific extensions beyond the JPA spec        │
│                                                                     │
│       ↕  abstracted by                                              │
│                                                                     │
│   Spring Data JPA                                                   │
│   ─────────────────                                                 │
│   Adds the repository pattern on top of JPA                         │
│   You write interfaces — Spring generates repositories using JPA API│
└─────────────────────────────────────────────────────────────────────┘
```

### Analogy

| Layer              | Analogy                                                    |
|--------------------|------------------------------------------------------------|
| **JPA**            | The USB standard — defines the plugs and protocols         |
| **Hibernate**      | A USB device manufacturer — implements the standard        |
| **Spring Data JPA**| A hub that makes it easy to use multiple USB devices       |

### Why Does This Matter?

- All annotations (`@Entity`, `@Id`, `@OneToMany`, etc.) come from the JPA spec — they work with **any** JPA provider (Hibernate, EclipseLink, OpenJPA)
- Hibernate-specific features (like `@Formula`, `@Where`, `@Cache`) only work with Hibernate — avoid for portability
- In practice: Spring Boot uses Hibernate by default, so you'll always be using Hibernate under the hood
- Code to the JPA interfaces (`EntityManager`, not `Session`) for portability

---

## Hibernate ORM Fundamentals

### Key Concepts

**Persistence Context (EntityManager)**
- A cache and change-tracking unit managed by Hibernate
- All entities loaded within a transaction are "managed" — Hibernate watches them for changes
- When the transaction commits, Hibernate flushes all changes (dirty checking) to the database
- After the transaction ends, entities become "detached" — Hibernate no longer tracks them

**Entity States**

```
New/Transient  → (persist/save)  → Managed  → (transaction commits) → Detached
                                      ↓ (delete)
                                   Removed
```

- **Transient:** Object exists in memory; not associated with any row; Hibernate ignores it
- **Managed:** Loaded from DB or just saved; Hibernate tracks changes; attached to persistence context
- **Detached:** Was managed, but transaction ended; changes NOT tracked; must `merge()` to re-attach
- **Removed:** Marked for deletion; DELETE issued on commit

**Session vs EntityManager**
- `Session` = Hibernate-native API (older, Hibernate-specific)
- `EntityManager` = JPA-standard API (recommended — portable)
- In Spring, `EntityManager` is injected with `@PersistenceContext`
- `Session` can be obtained from `EntityManager.unwrap(Session.class)` if you need Hibernate-specific features

---

## Hibernate's First-Level Cache

Hibernate maintains a first-level cache per persistence context (per transaction by default):

```java
Book book1 = em.find(Book.class, 1L);  // → SQL: SELECT * FROM books WHERE id = 1
Book book2 = em.find(Book.class, 1L);  // → No SQL! Returns cached object (same reference)
System.out.println(book1 == book2);    // true — SAME object from first-level cache
```

This means within a single transaction, Hibernate never issues two SELECTs for the same entity.

---

## Common Hibernate Configuration Properties

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate     # Options: none, validate, update, create, create-drop
    show-sql: true           # Log all SQL to console
    properties:
      hibernate:
        format_sql: true     # Pretty-print SQL
        use_sql_comments: true  # Add JPQL source as comments in SQL
        jdbc:
          batch_size: 30     # Batch INSERT/UPDATE for performance
        order_inserts: true  # Group INSERT batches by entity type
        order_updates: true  # Group UPDATE batches by entity type
        cache:
          use_second_level_cache: false  # Second-level cache (EhCache, Redis) — advanced topic
```

### `ddl-auto` values explained

| Value         | Effect                                                                      |
|---------------|-----------------------------------------------------------------------------|
| `none`        | Do nothing — manage schema yourself                                         |
| `validate`    | Verify schema matches entities — throw if mismatch (good for production)    |
| `update`      | Alter schema to match entities (risky in production — may lose data)        |
| `create`      | Drop and recreate schema on startup                                         |
| `create-drop` | Create on startup, drop on shutdown (ideal for tests)                       |

> **Best practice:** Never use `create`, `update`, or `create-drop` in production.
> Use `validate` with a proper migration tool (Flyway or Liquibase).
