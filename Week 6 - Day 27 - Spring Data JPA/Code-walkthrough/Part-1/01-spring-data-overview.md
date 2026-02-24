# Spring Data Overview & Repository Hierarchy

## What Is Spring Data?

Spring Data is a family of projects under the Spring umbrella that makes it dramatically easier
to work with data stores (relational databases, NoSQL, cloud, etc.).

Its core goal: **eliminate boilerplate data-access code** so you focus on your business logic.

---

## The Problem Without Spring Data

Without Spring Data you have to write:

```java
// JDBC (raw) — 20+ lines just to find one row
Connection conn = dataSource.getConnection();
PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE id = ?");
ps.setLong(1, bookId);
ResultSet rs = ps.executeQuery();
Book book = null;
if (rs.next()) {
    book = new Book();
    book.setId(rs.getLong("id"));
    book.setTitle(rs.getString("title"));
    // ... map every column manually
}
rs.close();
ps.close();
conn.close();
```

Spring Data replaces ALL of that with one line: `bookRepository.findById(bookId)`

---

## Spring Data Benefits

| Benefit                       | Description                                                             |
|-------------------------------|-------------------------------------------------------------------------|
| **Zero boilerplate**          | No JDBC/SQL for standard CRUD — Spring generates implementations       |
| **Query derivation**          | Write method name → Spring generates the SQL automatically             |
| **Pagination/Sorting built-in** | `Pageable` and `Sort` parameters work out of the box                 |
| **Multi-store support**       | Same programming model for JPA, MongoDB, Redis, Cassandra, etc.        |
| **Auditing support**          | `@CreatedDate`, `@LastModifiedDate` auto-populated                     |
| **Projections**               | Return only the fields you need — no over-fetching                     |
| **Consistent abstraction**    | Switch from MySQL to PostgreSQL to H2 without changing repository code |

---

## Repository Hierarchy (Spring Data Commons)

```
Repository<T, ID>                            ← Marker interface only
└── CrudRepository<T, ID>                    ← Basic CRUD (save, findById, findAll, delete…)
    └── PagingAndSortingRepository<T, ID>    ← Adds findAll(Pageable), findAll(Sort)
        └── JpaRepository<T, ID>             ← JPA-specific: flush, saveAllAndFlush, getById, batch ops
```

### `CrudRepository` key methods

| Method                        | Description                          |
|-------------------------------|--------------------------------------|
| `save(S entity)`              | Insert or update (upsert by ID)      |
| `saveAll(Iterable<S>)`        | Batch insert/update                  |
| `findById(ID id)`             | Returns `Optional<T>`                |
| `existsById(ID id)`           | Returns boolean                      |
| `findAll()`                   | Returns all entities                 |
| `findAllById(Iterable<ID>)`   | Returns entities matching IDs        |
| `count()`                     | Row count                            |
| `deleteById(ID id)`           | Delete by primary key                |
| `delete(T entity)`            | Delete entity                        |
| `deleteAll()`                 | Delete everything                    |

### `JpaRepository` additions (most commonly used)

| Method                         | Description                                       |
|--------------------------------|---------------------------------------------------|
| `flush()`                      | Force pending changes to DB immediately           |
| `saveAndFlush(T)`              | Save + flush in one call                          |
| `deleteInBatch(Iterable<T>)`   | Single DELETE statement for a collection          |
| `getById(ID)`                  | Returns lazy proxy (throws if not found on access)|
| `findAll(Sort)`                | All records with sorting                          |
| `findAll(Pageable)`            | Paginated result set                              |

> **Rule of thumb:** Almost always extend `JpaRepository` in Spring Boot apps — it gives you everything.

---

## How Automatic Implementation Works

Spring Data uses **Java Dynamic Proxies** at startup:

1. Spring scans for interfaces extending `Repository` (or its sub-types)
2. For each interface, it generates a proxy class at runtime that implements all the methods
3. The proxy delegates to `SimpleJpaRepository` under the hood (you can view its source)
4. Your `@Autowired BookRepository` gets the proxy injected — you never write an implementation class

```
Your Interface           Spring-Generated Proxy         SimpleJpaRepository
BookRepository    →      BookRepositoryImpl (proxy)  →  uses EntityManager internally
                         (generated at startup)
```

---

## Project Setup (pom.xml snippets)

```xml
<!-- Spring Data JPA starter — includes Hibernate, Spring Data, JPA API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 in-memory database (great for dev/testing) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL driver (swap for production) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
# application.yml (H2 dev setup)
spring:
  datasource:
    url: jdbc:h2:mem:bookstoredb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop   # create schema on startup, drop on shutdown
    show-sql: true            # print SQL to console — invaluable for learning
    properties:
      hibernate:
        format_sql: true      # pretty-print multi-line SQL
  h2:
    console:
      enabled: true           # visit http://localhost:8080/h2-console
```

---

## Summary

- Spring Data eliminates JDBC boilerplate by generating repository implementations automatically
- `CrudRepository` → basic CRUD; `PagingAndSortingRepository` → adds pagination; `JpaRepository` → adds JPA-specific ops
- Implementations are created via dynamic proxies at Spring startup — you only write interfaces
- Next: we'll look at the full `BookRepository` with query methods, `@Query`, and pagination
