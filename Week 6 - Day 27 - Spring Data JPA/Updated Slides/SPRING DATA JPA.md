# Spring Data JPA – 1-Hour Presentation Script
**Audience:** New students  
**Format:** Script + Slide Descriptions  
**Estimated Time:** 60 minutes

---

## SECTION 1 — Introduction & Spring Data Overview (8 minutes)

---

### SLIDE 1 — Title Slide
**Slide Content:** Title: "Spring Data JPA – From Entities to Queries"  
Subtitle: "Mapping, Repositories, and Persistence in the Spring Ecosystem"  
Your name, date, course name.

---

**Script:**

"Good morning everyone — welcome back. Over the past few sessions we've been laying the groundwork for how Java applications interact with databases. Today we're going to bring a lot of those concepts together under one very powerful umbrella: Spring Data JPA.

By the end of today's lesson, you will understand how Spring Data makes database access dramatically simpler, how JPA and Hibernate fit together, how to map your Java classes to database tables, how to define entity relationships, how to query data using multiple different approaches, and how to manage transactions properly.

This is a big lesson, so let's stay focused and move with purpose."

---

### SLIDE 2 — What is Spring Data?
**Slide Content:**  
- Spring Data is part of the larger Spring Framework ecosystem  
- Goal: eliminate boilerplate data access code  
- Provides a consistent, abstracted model for working with different data stores (relational, NoSQL, graph, etc.)  
- Spring Data JPA is the module focused on JPA-based relational databases  

---

**Script:**

"Let's start at the top. Spring Data is an umbrella project inside the Spring ecosystem. Its entire purpose is to make data access easier, faster to write, and more consistent — regardless of what kind of database you're using.

You might have Spring Data JPA for relational databases like MySQL or PostgreSQL. You might use Spring Data MongoDB for document databases. Spring Data Redis for key-value stores. They all share common patterns and abstractions, so once you learn one, picking up another is much faster.

Today we are specifically focused on Spring Data JPA — the module that sits on top of JPA and Hibernate to give you a clean, productive way to work with relational databases."

---

### SLIDE 3 — Why Spring Data JPA? The Benefits
**Slide Content:**  
- Eliminates repetitive DAO boilerplate (no more manual `EntityManager` calls for basic CRUD)  
- Automatic query generation from method names  
- Built-in pagination and sorting  
- Easy integration with Spring's transaction management  
- Supports JPQL, native SQL, and the Criteria API  
- Reduces code volume significantly — focus on business logic, not plumbing  

---

**Script:**

"Before Spring Data JPA existed, if you wanted to save a customer to the database you had to manually get an `EntityManager`, open a transaction, call persist, handle exceptions, close resources. For every single entity. Every single operation. That is a lot of repetitive code with a lot of places to make mistakes.

Spring Data JPA removes almost all of that. You define an interface. Spring generates the implementation at runtime. You call a method named `findByLastName` and Spring figures out the SQL for you. You want pagination? One line of code.

The benefit isn't just convenience — it's consistency and maintainability. Your codebase becomes much easier to read and much easier to test."

---

## SECTION 2 — JPA vs Hibernate & ORM Fundamentals (7 minutes)

---

### SLIDE 4 — What is ORM?
**Slide Content:**  
- ORM = Object-Relational Mapping  
- Bridges the gap between Java objects and relational database tables  
- Without ORM: manual SQL, manual ResultSet mapping, verbose JDBC code  
- With ORM: Java objects map directly to tables; the framework handles SQL generation  
- Key ORM concept: an entity class represents a table; an entity instance represents a row  

---

**Script:**

"Before we dive into code, we need to make sure everyone understands what ORM actually means. ORM stands for Object-Relational Mapping, and it solves a very real problem.

Relational databases think in terms of tables, rows, and columns. Java thinks in terms of objects, classes, and fields. Those two mental models don't naturally align. ORM is the bridge between them.

With ORM, you write a plain Java class — let's say a `Customer` class with fields like `id`, `firstName`, and `email`. You annotate it, and the ORM framework takes care of translating between your Java object and the corresponding row in the `customers` table. You never write a `CREATE TABLE` statement. You never manually map a `ResultSet`. The framework handles all of that."

---

### SLIDE 5 — JPA vs Hibernate
**Slide Content:**  
- **JPA (Java Persistence API):** A specification — a set of interfaces and rules defined by the Java EE / Jakarta EE standard  
- **Hibernate:** The most popular *implementation* of the JPA specification  
- JPA defines the contract (`@Entity`, `EntityManager`, JPQL, etc.)  
- Hibernate provides the actual engine that runs underneath  
- Spring Data JPA wraps both — you mostly work through JPA annotations and Spring's repository layer  
- Analogy: JPA is like JDBC (the interface), Hibernate is like a specific JDBC driver  

---

**Script:**

"This is a question I get a lot: what is the difference between JPA and Hibernate? And it's an important distinction to understand.

JPA — the Java Persistence API — is a specification. It's a document, essentially, that defines a standard set of interfaces, annotations, and behaviors. It says: 'here is what an entity looks like, here is how you query data, here is what EntityManager should do.' But JPA itself doesn't run any code. It's just the contract.

Hibernate is an implementation of that contract. It's the actual library that does the work. When you write `@Entity` or run a JPQL query, it's Hibernate under the hood that translates that to SQL and executes it against your database.

Spring Data JPA sits on top of both. When you use Spring Data JPA, you're mostly writing JPA annotations and Spring repository interfaces. Hibernate is just the engine in the background making it all work.

The analogy I like: JPA is like a blueprint for a car engine. Hibernate is the actual engine built from that blueprint. Spring Data JPA is the automatic transmission that makes driving that engine much easier."

---

### SLIDE 6 — Hibernate ORM Fundamentals
**Slide Content:**  
- Hibernate manages the **Session** (similar to JPA's EntityManager)  
- Hibernate maintains a **first-level cache** (session cache) automatically  
- Hibernate tracks **entity states**: Transient, Persistent, Detached, Removed  
- Hibernate generates DDL (table creation) and DML (insert/update/delete) automatically  
- Configuration: `hibernate.ddl-auto` setting (create, update, validate, none)  

---

**Script:**

"Let's touch on a few core Hibernate concepts that will help you understand what's happening under the hood.

Hibernate uses a Session to manage database interactions — this is the equivalent of JPA's EntityManager. The Session is where all your persistent objects live during a unit of work.

Hibernate automatically maintains a first-level cache at the session level. That means if you load the same entity twice in the same session, Hibernate won't hit the database twice. It gives you back the same object from memory.

Hibernate also tracks the state of every entity it knows about. An entity can be Transient — it's just a Java object, Hibernate doesn't know about it yet. It becomes Persistent when you associate it with a session. It becomes Detached if the session closes. And it becomes Removed if you mark it for deletion.

Finally, one of the most important settings you'll configure is `hibernate.ddl-auto`. This tells Hibernate what to do with your database schema at startup. During development you'll often use `update` or `create-drop`. In production you'll use `validate` or `none` — you never want Hibernate creating or dropping tables in a production database."

---

## SECTION 3 — Entity Classes & JPA Mappings (10 minutes)

---

### SLIDE 7 — Entity Classes & @Entity
**Slide Content:**  
```java
@Entity
@Table(name = "customers")
public class Customer {
    // fields, constructors, getters/setters
}
```
- `@Entity` marks the class as a JPA-managed entity  
- The class must have a no-arg constructor  
- Each instance maps to one row in the corresponding table  
- By default, the table name matches the class name  
- `@Table(name = "...")` overrides the default table name  

---

**Script:**

"Alright, let's start writing some code — at least conceptually.

To tell JPA that a class should be mapped to a database table, you annotate it with `@Entity`. That's it. That single annotation tells Hibernate: 'this class represents a table, manage it for me.'

By default, Hibernate assumes the table name matches the class name. So a class called `Customer` maps to a table called `customer` or `Customer` depending on your database. If you want to control that explicitly — and in most real projects you do — you add `@Table(name = 'customers')` and specify the exact table name.

One thing to be aware of: JPA requires that every entity class have a no-argument constructor. It can be protected, it doesn't have to be public, but it has to exist. Hibernate needs to be able to instantiate your class reflectively."

---

### SLIDE 8 — Primary Keys: @Id and @GeneratedValue
**Slide Content:**  
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- `@Id` marks the primary key field  
- `@GeneratedValue` delegates key generation to the database or JPA provider  
- **GenerationType strategies:**  
  - `IDENTITY` — database auto-increment (MySQL, PostgreSQL)  
  - `SEQUENCE` — uses a database sequence (Oracle, PostgreSQL)  
  - `AUTO` — JPA picks the strategy based on the database  
  - `TABLE` — uses a special key-generation table (portable, but slow)  
- Best practice: use `Long` or `UUID` for primary key type  

---

**Script:**

"Every JPA entity needs a primary key, and you mark that field with `@Id`. Simple enough.

What's more interesting is `@GeneratedValue`, which tells JPA how to generate that key for you automatically. You'll mostly use `GenerationType.IDENTITY`, which delegates to the database's auto-increment functionality — this is what you'd use with MySQL or PostgreSQL columns defined as SERIAL or AUTO_INCREMENT.

If you're using Oracle or a database with sequences, you'd use `GenerationType.SEQUENCE` and pair it with a `@SequenceGenerator` annotation. `AUTO` is the lazy option — JPA picks the strategy for you based on the underlying database. It works but can behave differently across databases.

My recommendation for most projects: use `Long` as your ID type with `GenerationType.IDENTITY`. It's simple, performant, and works well with Spring Data JPA."

---

### SLIDE 9 — Column Mappings: @Column
**Slide Content:**  
```java
@Column(name = "first_name", nullable = false, length = 100)
private String firstName;

@Column(name = "email", unique = true, nullable = false)
private String email;

@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;
```
- `@Column` customizes the column mapping  
- Key attributes: `name`, `nullable`, `unique`, `length`, `updatable`, `insertable`  
- Without `@Column`, the field name is used as the column name  

---

**Script:**

"Just like `@Table` lets you control the table name, `@Column` lets you control the column-level mappings for each field.

The `name` attribute maps your Java field to a specifically named database column. This is important because Java conventions use camelCase — `firstName` — while SQL conventions typically use snake_case — `first_name`. Use `@Column(name = 'first_name')` to bridge that gap.

The `nullable` attribute adds a NOT NULL constraint when Hibernate generates the schema. `unique` adds a unique constraint. `length` sets a VARCHAR length limit.

Two attributes worth highlighting: `updatable = false` means Hibernate will never include this column in UPDATE statements — perfect for things like a `created_at` timestamp that should only be set once. `insertable = false` means it's excluded from INSERT statements."

---

### SLIDE 10 — Entity Relationships
**Slide Content:**  
Four core relationship annotations:  
- `@OneToOne` — one entity maps to exactly one other entity  
- `@OneToMany` — one entity maps to a collection of other entities  
- `@ManyToOne` — many entities map to one parent entity  
- `@ManyToMany` — many entities map to many other entities (join table)  

Include a simple ERD diagram showing Customer → Orders → Products  

---

**Script:**

"Now we get to one of the most important — and most nuanced — topics in JPA: entity relationships.

In the real world, data is connected. A customer has many orders. An order belongs to one customer. An order contains many products. A product can appear in many orders. JPA gives us four annotations to model all of these relationships.

`@ManyToOne` is probably the most common. In an Order entity, you'd annotate the customer field with `@ManyToOne` — many orders belong to one customer. The corresponding annotation on the Customer side is `@OneToMany` — one customer has many orders.

`@OneToOne` is used for strict one-to-one pairings — like a User and their UserProfile.

`@ManyToMany` is the most complex. It requires a join table — a third table in the database that holds the foreign keys from both sides. JPA manages this join table for you, but you need to understand it's there."

---

### SLIDE 11 — Relationship Mapping in Code
**Slide Content:**  
```java
// Customer.java
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Order> orders;

// Order.java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "customer_id")
private Customer customer;
```
- `mappedBy` indicates the owning side of the relationship  
- `@JoinColumn` specifies the foreign key column  
- The owning side is always the one with `@JoinColumn`  

---

**Script:**

"Let me show you how this looks in actual code.

In the `Order` class, you declare the `customer` field as a `@ManyToOne` and add `@JoinColumn(name = 'customer_id')`. That `@JoinColumn` annotation tells Hibernate which column in the `orders` table holds the foreign key reference back to the customer.

On the `Customer` side, you declare a `List<Order>` with `@OneToMany`. The `mappedBy = 'customer'` attribute is critical — it tells JPA that the `Order` entity is the owning side of this relationship, and that the `customer` field in `Order` is already managing the join. Without `mappedBy`, JPA would create a separate join table unnecessarily.

This concept of the 'owning side' trips up a lot of new developers. The rule of thumb: the owning side is the entity whose table contains the foreign key column. That side has `@JoinColumn`. The other side uses `mappedBy`."

---

### SLIDE 12 — Fetch Types: EAGER vs LAZY
**Slide Content:**  
- **EAGER:** Related data is loaded immediately when the parent is loaded  
- **LAZY:** Related data is loaded only when accessed (on-demand)  
- Default behaviors:  
  - `@ManyToOne`, `@OneToOne` → default EAGER  
  - `@OneToMany`, `@ManyToMany` → default LAZY  
- LAZY loading is almost always preferred for collections  
- EAGER loading for collections can cause the N+1 problem  
- LAZY loading outside a session context causes `LazyInitializationException`  

---

**Script:**

"Fetch type is a concept that directly impacts your application's performance, and it's something you need to get right from the beginning.

EAGER fetching means: when you load an entity, immediately load all of its related data too. If you load a Customer with EAGER orders, Hibernate runs a JOIN query and gives you the customer and all their orders in one shot.

LAZY fetching means: load the parent entity now, and only hit the database for the related data when you actually access it in code.

For collections — `@OneToMany`, `@ManyToMany` — always prefer LAZY. Here's why: if you have 1000 customers and each has 50 orders, loading all customers with EAGER orders means loading 50,000 order records into memory just to show a customer list page. That's a performance disaster.

The main gotcha with LAZY loading is the `LazyInitializationException`. If the Hibernate session is closed before you access the lazy collection — which happens easily in web applications — Hibernate throws an exception because it can no longer load the data. The fix is typically using `@Transactional` on your service methods or using a DTO to load data eagerly before the session closes."

---

### SLIDE 13 — Cascade Operations
**Slide Content:**  
- Cascading propagates JPA operations from parent to child entities  
- `CascadeType.PERSIST` — saving parent also saves children  
- `CascadeType.MERGE` — merging parent also merges children  
- `CascadeType.REMOVE` — deleting parent also deletes children  
- `CascadeType.REFRESH` — refreshing parent also refreshes children  
- `CascadeType.ALL` — all of the above  
- `CascadeType.DETACH` — detaching parent also detaches children  
- Be cautious with `REMOVE` — deleting a parent can cascade-delete a lot of data  

---

**Script:**

"Cascading is about how JPA operations propagate through your entity graph.

Consider a Customer and their Orders. If you save a new Customer who already has some new Order objects attached to their orders list, should JPA save those orders automatically? That depends on whether you've configured `CascadeType.PERSIST`.

`CascadeType.ALL` is convenient — it means all operations cascade from parent to child. But use it carefully. `CascadeType.REMOVE` in particular can be dangerous. If you delete a parent with `CascadeType.ALL`, all its children get deleted too. For an Order and its OrderLines, that might be intentional. For a Department and its Employees, that would be catastrophic.

The practical advice: be explicit about what you want to cascade. Don't just slap `CascadeType.ALL` on every relationship without thinking through the implications."

---

## SECTION 4 — Spring Data Repositories (10 minutes)

---

### SLIDE 14 — The DAO Pattern
**Slide Content:**  
- **DAO (Data Access Object):** A design pattern that abstracts and encapsulates all database access  
- Separates business logic from data persistence logic  
- Traditional DAO: write a class that manually uses EntityManager/JDBC  
- Spring Data JPA replaces manual DAOs with repository interfaces  
- You still get the benefits of the DAO pattern — abstraction, separation of concerns — without the boilerplate  

---

**Script:**

"Before we look at Spring Data repositories specifically, let's talk about the DAO pattern, because repositories are essentially Spring's modern take on this classic pattern.

The DAO pattern says: separate your data access logic into its own dedicated class. Don't have your business service directly writing SQL or calling EntityManager. Create a dedicated object — the DAO — whose only job is talking to the database.

This gives you a clean separation of concerns. Your service classes deal with business logic. Your DAOs deal with persistence. Swapping out your database or changing your ORM strategy becomes much easier.

Spring Data JPA gives you all the benefits of the DAO pattern automatically. Your repository interfaces ARE your DAOs. They provide that clean abstraction layer, and Spring writes the implementation for you."

---

### SLIDE 15 — CrudRepository and JpaRepository
**Slide Content:**  
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
```
- **`CrudRepository<T, ID>`** — basic CRUD: `save()`, `findById()`, `findAll()`, `delete()`, `count()`  
- **`PagingAndSortingRepository<T, ID>`** — adds `findAll(Pageable)`, `findAll(Sort)`  
- **`JpaRepository<T, ID>`** — extends both above, adds JPA-specific methods: `flush()`, `saveAllAndFlush()`, `deleteAllInBatch()`  
- For most use cases, extend `JpaRepository`  
- Generic parameters: `T` = entity type, `ID` = primary key type  

---

**Script:**

"Let's look at the repository hierarchy. Spring Data provides a set of base interfaces that you extend to get database access capabilities.

`CrudRepository` is the base level. It gives you the fundamental operations: save an entity, find by ID, find all, delete, count. That's 90% of what most applications need.

`PagingAndSortingRepository` extends `CrudRepository` and adds the ability to retrieve data with pagination and sorting — extremely useful for any list view in a real application.

`JpaRepository` extends both of these and adds a few JPA-specific conveniences like `flush()` — which immediately syncs the persistence context to the database — and batch operations.

In practice, just extend `JpaRepository`. You get everything. The generic parameters are the entity type and the ID type — so `JpaRepository<Customer, Long>` gives you a repository for the `Customer` entity whose primary key is a `Long`.

And here's the magic: you don't write any implementation class. You just define this interface, add `@Repository` if you like (though Spring Data actually finds it automatically), and Spring generates a fully functional implementation at runtime."

---

### SLIDE 16 — Built-in CRUD Operations
**Slide Content:**  
```java
// Save / Update
customerRepository.save(customer);

// Find
customerRepository.findById(1L);
customerRepository.findAll();

// Delete
customerRepository.deleteById(1L);
customerRepository.delete(customer);

// Utility
customerRepository.count();
customerRepository.existsById(1L);
```
- `save()` performs both INSERT (new) and UPDATE (existing) — it checks if the ID is set  
- `findById()` returns `Optional<T>` — handle the empty case properly  

---

**Script:**

"Let's walk through the built-in operations you get for free.

`save()` is your workhorse. It handles both inserts and updates. If you pass in an entity without an ID set, Hibernate treats it as new and runs an INSERT. If it has an ID, Hibernate runs an UPDATE. One method for both operations.

`findById()` returns an `Optional<T>`, which is the modern Java way of handling the case where a record might not exist. Don't call `.get()` blindly on it — always use `.orElseThrow()` or `.orElse()` or `.ifPresent()` so you're explicitly handling the not-found case.

`findAll()` loads every row in the table. Be careful using this on large tables in production — always prefer paginated versions in real applications.

`deleteById()` deletes by primary key. `count()` gives you the total row count. `existsById()` checks existence without loading the full entity — much more efficient than `findById()` when you just want to know if something exists."

---

### SLIDE 17 — Query Methods by Naming Convention
**Slide Content:**  
```java
// Generated from method name:
List<Customer> findByLastName(String lastName);
List<Customer> findByLastNameAndCity(String lastName, String city);
List<Customer> findByAgeGreaterThan(int age);
Optional<Customer> findByEmail(String email);
List<Customer> findByLastNameContainingIgnoreCase(String partial);
long countByCity(String city);
void deleteByEmail(String email);
```
- Spring parses the method name and generates the query automatically  
- Keywords: `findBy`, `countBy`, `deleteBy`, `And`, `Or`, `GreaterThan`, `LessThan`, `Between`, `Like`, `Containing`, `IgnoreCase`, `OrderBy`, `In`, `IsNull`, `IsNotNull`  

---

**Script:**

"This is one of the most impressive features of Spring Data JPA, and it's something that genuinely delights developers when they see it for the first time.

You define a method in your repository interface, following a specific naming convention, and Spring generates the entire query for you. No SQL. No JPQL. Just a method name.

`findByLastName(String lastName)` — Spring sees 'findBy' and knows this is a SELECT query. It sees 'LastName' and knows to look for a `lastName` field on the entity. It generates a WHERE clause automatically.

`findByLastNameAndCity` — Spring sees the `And` keyword and generates `WHERE last_name = ? AND city = ?`.

`findByAgeGreaterThan` — generates `WHERE age > ?`. You have all these keyword modifiers: `LessThan`, `Between`, `Like`, `Containing`, `IgnoreCase`, `IsNull`, `In`, `OrderBy`.

`findByLastNameContainingIgnoreCase` generates a case-insensitive LIKE query. One method name, zero SQL written by you.

This approach has limits — if your query logic gets very complex, the method name becomes unreadable. But for the vast majority of simple queries, this convention-based approach is clean and fast."

---

## SECTION 5 — Advanced Querying (8 minutes)

---

### SLIDE 18 — @Query Annotation (JPQL)
**Slide Content:**  
```java
@Query("SELECT c FROM Customer c WHERE c.city = :city AND c.age > :minAge")
List<Customer> findByCityAndMinAge(@Param("city") String city, @Param("minAge") int minAge);

@Query("SELECT c FROM Customer c WHERE c.email LIKE %:domain%")
List<Customer> findByEmailDomain(@Param("domain") String domain);
```
- `@Query` accepts JPQL (Java Persistence Query Language)  
- JPQL operates on entity class names and field names — NOT table/column names  
- Named parameters with `:paramName` and `@Param`  
- Positional parameters with `?1`, `?2` also supported (less readable)  

---

**Script:**

"When query method names get too unwieldy, or when you need query logic that the naming convention can't express, you use the `@Query` annotation.

`@Query` takes a JPQL string. JPQL stands for Java Persistence Query Language, and it looks a lot like SQL — but there's a critical difference. In JPQL, you reference entity class names and field names, not table names and column names.

So instead of `SELECT * FROM customers WHERE city = ?`, you write `SELECT c FROM Customer c WHERE c.city = :city`. You're working with the Java model, not the database schema. Hibernate translates it to the appropriate SQL for your database.

Named parameters use the colon syntax — `:city` — and you bind them using `@Param('city')` on the corresponding method parameter. This is much more readable than positional `?1` style parameters, especially when you have several.

JPQL supports all the clauses you'd expect — WHERE, ORDER BY, GROUP BY, HAVING, JOINs. It's a full query language that's portable across different databases because Hibernate handles the dialect translation."

---

### SLIDE 19 — @Query with Native SQL
**Slide Content:**  
```java
@Query(value = "SELECT * FROM customers WHERE YEAR(created_at) = :year", 
       nativeQuery = true)
List<Customer> findCustomersCreatedInYear(@Param("year") int year);
```
- `nativeQuery = true` tells Spring to pass the query directly to the database  
- Use for database-specific functions, complex queries, or performance tuning  
- Downsides: not portable across databases, bypasses JPA abstraction  
- Returns results as entity objects (if mapped correctly) or `List<Object[]>` for custom projections  

---

**Script:**

"Sometimes JPQL isn't enough. Maybe you need to use a database-specific function that JPQL doesn't support, or you're optimizing a complex query and you need precise control over the SQL.

In those cases, set `nativeQuery = true` on your `@Query` annotation. Spring will pass your SQL string directly to the database, bypassing JPQL translation entirely.

The trade-off is portability. Native SQL queries are tied to your specific database. If you write a query that uses MySQL-specific syntax and then switch to PostgreSQL, that query breaks. Use native queries sparingly and document why you needed them.

The results can come back as mapped entity objects if your columns match your entity fields, or as `List<Object[]>` arrays for more custom projections. There are also Spring Data Projection interfaces that give you a cleaner way to handle custom result shapes, but that's a topic for a future lesson."

---

### SLIDE 20 — JPQL and HQL
**Slide Content:**  
- **JPQL** is part of the JPA specification — works with any JPA provider  
- **HQL (Hibernate Query Language)** is Hibernate's version — a superset of JPQL  
- HQL includes extra features not in the JPA spec  
- In practice, they are nearly identical for common use cases  
- Both use entity/field names, not table/column names  
- Both support: SELECT, FROM, WHERE, JOIN, GROUP BY, ORDER BY, aggregate functions  
- Example JPQL JOIN:  
```java
@Query("SELECT o FROM Order o JOIN o.customer c WHERE c.lastName = :name")
List<Order> findOrdersByCustomerLastName(@Param("name") String name);
```

---

**Script:**

"Let me clarify the relationship between JPQL and HQL since they come up together frequently.

JPQL is the query language defined by the JPA specification. Any JPA-compliant provider must support it. HQL is Hibernate's version of the same idea, but with a few extra Hibernate-specific extensions on top.

For everything you'll do in this course and in most professional projects, JPQL and HQL are functionally identical. You'll write queries that work for both.

The key thing to remember is that both languages operate on your Java entity model, not your database schema. When you write a JOIN in JPQL, you're joining through the relationship you defined in your entity class — `JOIN o.customer` — not through a raw foreign key join like in SQL.

This is a powerful abstraction. If you rename a database column, you update your `@Column` annotation in one place, and all your JPQL queries continue to work without any changes."

---

### SLIDE 21 — Pagination and Sorting
**Slide Content:**  
```java
// In repository
Page<Customer> findByCity(String city, Pageable pageable);
List<Customer> findAll(Sort sort);

// In service layer
Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());
Page<Customer> page = customerRepository.findByCity("Austin", pageable);

page.getContent();      // The actual list
page.getTotalPages();   // Total number of pages
page.getTotalElements(); // Total record count
page.getNumber();       // Current page number
```

---

**Script:**

"In any real application, you cannot load thousands of records all at once. You need pagination. Spring Data JPA makes pagination remarkably simple.

Add a `Pageable` parameter to any repository method, and Spring handles the rest. You create a `PageRequest` object specifying the page number — zero-indexed — the page size, and optionally the sort order. Pass it to your repository method, and get back a `Page` object.

The `Page` object is rich. It contains the actual list of results for that page, the total number of elements across all pages, the total number of pages, and the current page number. Everything you need to build a pagination UI is right there.

`Sort.by('lastName').ascending()` handles ordering. You can chain multiple sorts — `Sort.by('lastName').ascending().and(Sort.by('firstName').ascending())`.

This is truly one of Spring Data JPA's standout features. What would have taken significant custom SQL and result-counting queries in plain JDBC is reduced to a few lines of clean Java code."

---

## SECTION 6 — Criteria API & Transaction Management (7 minutes)

---

### SLIDE 22 — Criteria API Basics
**Slide Content:**  
```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
Root<Customer> root = query.from(Customer.class);

query.select(root)
     .where(cb.equal(root.get("city"), "Austin"),
            cb.greaterThan(root.get("age"), 25));

List<Customer> results = entityManager.createQuery(query).getResultList();
```
- Type-safe, programmatic query construction  
- Useful when queries need to be built dynamically at runtime  
- More verbose than JPQL/HQL but completely type-safe  
- Often combined with Spring Data JPA `Specification<T>` for dynamic queries  

---

**Script:**

"The Criteria API is the programmatic, type-safe way to build JPA queries in Java code without writing any query strings.

The use case for Criteria API is dynamic queries. Imagine a search form where users can filter by city, age range, name, or any combination of those. You don't know at compile time which filters will be applied. You can't write a single JPQL string that handles every combination. The Criteria API lets you build the query object programmatically, adding predicates conditionally based on what filters the user has actually provided.

The trade-off is verbosity. As you can see, even a simple query with two conditions is several lines of code. For simple static queries, always prefer JPQL or naming conventions. Reserve the Criteria API for cases where dynamic query construction is genuinely necessary.

Spring Data JPA also offers a `Specification<T>` abstraction built on top of the Criteria API that makes dynamic queries a bit cleaner, but that's a deeper topic. What's important today is that you understand what the Criteria API is, why it exists, and when you'd reach for it."

---

### SLIDE 23 — Transaction Management with @Transactional
**Slide Content:**  
```java
@Service
public class CustomerService {

    @Transactional
    public void transferCustomer(Long fromId, Long toId) {
        Customer from = customerRepository.findById(fromId).orElseThrow();
        Customer to = customerRepository.findById(toId).orElseThrow();
        // business logic...
        customerRepository.save(from);
        customerRepository.save(to);
    }

    @Transactional(readOnly = true)
    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findByCity(city);
    }
}
```
- `@Transactional` ensures all operations in the method run in one transaction  
- If an exception occurs, the entire transaction is rolled back  
- `readOnly = true` optimizes read-only operations — no dirty checking, potential performance gain  
- Spring Data JPA repository methods are already `@Transactional` by default  
- Place `@Transactional` on your **service layer**, not your repository  

---

**Script:**

"Transaction management is critical for data integrity, and `@Transactional` is how Spring handles it.

A transaction is a unit of work that either fully completes or fully rolls back. The classic example is a bank transfer: you debit one account and credit another. If the debit succeeds but the credit fails due to an exception, you don't want to leave the debit in place. The whole thing should roll back as if nothing happened.

By annotating a service method with `@Transactional`, Spring wraps it in a database transaction. If any unchecked exception bubbles out of that method, the entire transaction rolls back automatically. All or nothing.

The important architectural note: put `@Transactional` on your **service layer**, not your repository. Your repository methods are already transactional individually by default. But if your service calls three repository methods and needs them all in one transaction, the `@Transactional` at the service level creates one shared transaction for all three.

`readOnly = true` is a valuable optimization for query-only methods. It tells Hibernate not to track entity changes for dirty checking, which reduces overhead. Always mark pure read operations with `readOnly = true`."

---

## SECTION 7 — Putting It All Together & Wrap-Up (10 minutes)

---

### SLIDE 24 — Architecture Overview
**Slide Content:**  
Diagram showing the full stack:  
```
[Controller Layer]
       ↓
[Service Layer] ← @Transactional lives here
       ↓
[Repository Layer] ← Spring Data JPA interfaces
       ↓
[JPA / Spring Data JPA]
       ↓
[Hibernate ORM]
       ↓
[Database]
```
Entities are shared between Repository and Service layers.

---

**Script:**

"Let's step back and see how everything we've discussed today fits together in a typical Spring application.

At the top you have your controller layer — REST controllers or MVC controllers handling HTTP requests. They call your service layer.

Your service layer is where your business logic lives. This is where `@Transactional` goes. Services call repositories to load and save data.

Your repository layer is where Spring Data JPA lives. These are the interfaces extending `JpaRepository`. Spring generates the implementations. Repositories talk to the JPA layer.

JPA then talks to Hibernate, which translates everything into SQL and executes it against your database.

Your entity classes — annotated with `@Entity`, `@Table`, `@Column`, `@OneToMany` and so on — are shared across these layers. They're the common language between your database and your Java application.

This layered architecture is the standard for Spring applications. Each layer has a clear, single responsibility."

---

### SLIDE 25 — Performing Database Operations — Code Summary
**Slide Content:**  
```java
// 1. Define entity
@Entity @Table(name="customers")
public class Customer { @Id @GeneratedValue ... }

// 2. Create repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByCity(String city);
    
    @Query("SELECT c FROM Customer c WHERE c.age BETWEEN :min AND :max")
    List<Customer> findByAgeRange(@Param("min") int min, @Param("max") int max);
    
    Page<Customer> findAll(Pageable pageable);
}

// 3. Use in service
@Service public class CustomerService {
    @Transactional
    public Customer createCustomer(Customer c) { return repo.save(c); }
    
    @Transactional(readOnly=true)
    public Page<Customer> getPage(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }
}
```

---

**Script:**

"Here's the complete picture in a condensed form. This is the workflow you'll follow in virtually every Spring Data JPA project.

First, define your entity class. Annotate it, map your columns and relationships.

Second, create a repository interface extending `JpaRepository`. Add custom query methods using naming conventions or `@Query` as needed.

Third, create a service class that injects the repository. Annotate transactional boundaries appropriately.

That's the whole pattern. Three components. The entity, the repository, the service. Spring wires everything together and generates what you haven't written yourself.

When you sit down to build something with Spring Data JPA, this is your mental checklist: Do I have the entity mapped correctly? Do I have a repository that can get the data I need? Is my service method transactional? If you can answer yes to all three, you're in good shape."

---

### SLIDE 26 — Common Mistakes & Best Practices
**Slide Content:**  
- ❌ Using `FetchType.EAGER` on collections — use LAZY  
- ❌ Forgetting `@Transactional` when modifying data outside a repository method  
- ❌ Using `CascadeType.ALL` with `@ManyToMany` — can lead to accidental deletions  
- ❌ Calling `findAll()` without pagination on large tables  
- ❌ Accessing lazy collections outside a transaction — `LazyInitializationException`  
- ✅ Use `readOnly = true` on read-only service methods  
- ✅ Use `Optional` properly with `findById()` — always handle the empty case  
- ✅ Keep `@Transactional` on the service layer, not the repository  
- ✅ Use DTOs for API responses instead of exposing entity objects directly  
- ✅ Validate with `validate` or `none` for `ddl-auto` in production  

---

**Script:**

"Let me leave you with the most common pitfalls I see developers run into with Spring Data JPA — things you now have the knowledge to avoid.

The biggest performance mistake is using EAGER fetch on collections. Don't do it. Default to LAZY and load eagerly only when you explicitly need it for a specific use case.

The second biggest is forgetting transactions when you need them. If your service method calls two repository saves and the second one fails, the first one still commits unless you have `@Transactional` on the method. Always think about your transaction boundaries.

Using `CascadeType.ALL` on `@ManyToMany` relationships is dangerous and rarely correct. Think carefully before applying that.

On the positive side: always use `readOnly = true` for queries, always handle `Optional` properly, always paginate large result sets, and never use `create` or `create-drop` for `ddl-auto` in a production database. That is how you lose data.

And a final best practice that will serve you well: don't expose your JPA entities directly from your REST API. Use Data Transfer Objects. Entities have Hibernate proxy baggage, lazy collections, and bidirectional relationships that cause serialization problems. Keep your persistence model separate from your API contract."

---

### SLIDE 27 — Summary & What's Next
**Slide Content:**  
**Today we covered:**  
- Spring Data overview and repository hierarchy  
- JPA vs Hibernate and ORM fundamentals  
- Entity mapping: `@Entity`, `@Table`, `@Id`, `@Column`  
- Relationships: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`  
- Fetch types and cascade operations  
- CRUD operations with `JpaRepository`  
- Custom queries: naming convention, `@Query` JPQL, native SQL  
- JPQL and HQL  
- Criteria API basics  
- Pagination and sorting  
- Transaction management with `@Transactional`  
- The DAO pattern and layered architecture  

**Coming up:** [Fill in with your next lesson topics]  

---

**Script:**

"Let's recap what we covered today, because it was a lot.

We started with Spring Data and why it exists — to eliminate boilerplate and give us a consistent, productive model for database access. We clarified the JPA vs Hibernate relationship — JPA is the spec, Hibernate is the engine. We covered ORM fundamentals and how entities map to tables.

We went deep on entity mapping — `@Entity`, `@Table`, `@Column`, `@Id`, `@GeneratedValue` — and on relationships with all four association types. We talked about fetch types and why LAZY is almost always right for collections. We discussed cascade operations and when to be careful.

We explored Spring Data repositories — the CrudRepository and JpaRepository hierarchy, automatic CRUD operations, query methods by naming convention, and the `@Query` annotation for JPQL and native SQL. We touched on the Criteria API for dynamic queries. We covered pagination with `Pageable` and `Page`.

And we closed with transaction management — arguably the most important thing to get right in any data-driven application.

That is a dense set of topics, and you won't internalize all of it just from listening. The way to solidify this is to build something. Create entities, define relationships, write repository methods, query your data, break things, fix them. That's how this becomes muscle memory.

In our next session we'll be covering [your next topic]. If you have questions, I'm here. Let's take a short break."

---

## TIMING GUIDE

| Section | Topic | Time |
|---|---|---|
| 1 | Intro & Spring Data Overview | 8 min |
| 2 | JPA vs Hibernate & ORM | 7 min |
| 3 | Entity Classes & JPA Mappings | 10 min |
| 4 | Spring Data Repositories & CRUD | 10 min |
| 5 | Advanced Querying | 8 min |
| 6 | Criteria API & Transactions | 7 min |
| 7 | Summary & Best Practices | 10 min |
| **Total** | | **~60 min** |

---

## SLIDE COUNT SUMMARY

| Slide # | Title |
|---|---|
| 1 | Title Slide |
| 2 | What is Spring Data? |
| 3 | Spring Data JPA Benefits |
| 4 | What is ORM? |
| 5 | JPA vs Hibernate |
| 6 | Hibernate ORM Fundamentals |
| 7 | Entity Classes & @Entity |
| 8 | Primary Keys: @Id and @GeneratedValue |
| 9 | Column Mappings: @Column |
| 10 | Entity Relationships (with ERD) |
| 11 | Relationship Mapping in Code |
| 12 | Fetch Types: EAGER vs LAZY |
| 13 | Cascade Operations |
| 14 | The DAO Pattern |
| 15 | CrudRepository and JpaRepository |
| 16 | Built-in CRUD Operations |
| 17 | Query Methods by Naming Convention |
| 18 | @Query Annotation (JPQL) |
| 19 | @Query with Native SQL |
| 20 | JPQL and HQL |
| 21 | Pagination and Sorting |
| 22 | Criteria API Basics |
| 23 | Transaction Management |
| 24 | Architecture Overview |
| 25 | Code Summary |
| 26 | Common Mistakes & Best Practices |
| 27 | Summary & What's Next |

---

## INSTRUCTOR NOTES

**Missing:** `@Version` for optimistic locking is a useful real-world addition that helps students understand how concurrent database access is managed. Pagination with `Pageable` and `Page` appears to be included in the slide list (slide 21) — confirm it receives a full practical example, as it is one of the first things students need in real API development.

**Unnecessary/Too Advanced:** The Criteria API Basics (slide 22) is an advanced topic that most Spring developers rarely write manually (Querydsl or derived query methods are preferred). Consider marking it as optional or a brief awareness mention only.

**Density:** Well-paced based on what was reviewed. At 876 lines, this is a long session — verify it is intentionally scheduled as a 90-minute session or has a clear trimming strategy. The entity relationship mapping section (`@OneToMany`, `@ManyToOne`, etc.) is the densest part and commonly causes confusion around fetch type and cascade settings.