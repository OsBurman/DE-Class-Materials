# Exercise 06 — Entity Relationships

## Learning Objectives
- Map `@OneToMany` / `@ManyToOne` bidirectional relationships
- Map a `@ManyToMany` relationship with a `@JoinTable`
- Understand `mappedBy` and owning vs. inverse sides
- Persist related entities and navigate associations

## Background

JPA models database foreign-key relationships as Java object references.

| Annotation | Relationship | Owning side |
|---|---|---|
| `@ManyToOne` | Many books → one author | The `@ManyToOne` side |
| `@OneToMany(mappedBy=…)` | One author → many books | Inverse side (uses `mappedBy`) |
| `@ManyToMany` | Books ↔ Tags | Declared with `@JoinTable` |
| `@JoinTable` | Defines the join table and FK columns | Owning side of `@ManyToMany` |

**Bidirectional `@OneToMany`/`@ManyToOne`:**
```java
// Author (inverse side)
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
private List<Book> books = new ArrayList<>();

// Book (owning side)
@ManyToOne
@JoinColumn(name = "author_id")
private Author author;
```

**`@ManyToMany`:**
```java
// Book (owning side)
@ManyToMany
@JoinTable(
    name = "book_tags",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();

// Tag (inverse side)
@ManyToMany(mappedBy = "tags")
private List<Book> books = new ArrayList<>();
```

## Instructions

### Step 1 — Explore the starter entities

Open `starter-code/` and review:
- `Book.java` — already has `@ManyToOne Author` and `@ManyToMany List<Tag>` fields with TODOs
- `Author.java` — has a TODO to add `@OneToMany(mappedBy = "author")`
- `Tag.java` — has a TODO to add `@ManyToMany(mappedBy = "tags")`

### Step 2 — Complete the relationship annotations

Fill in every `// TODO` in `Book.java`, `Author.java`, and `Tag.java`.

### Step 3 — Complete `DataLoader`

Follow the TODO comments to:
1. Create 2 authors
2. Create 3 books, each assigned to an author
3. Create 2 tags and assign them to books
4. Save authors (cascade will save books); save tags separately
5. Print each author and their books
6. Print each tag and the books that carry it

### Step 4 — Run

```
mvn spring-boot:run
```

Inspect the SQL output — notice `author_id` FK column in `books` and the `book_tags` join table.

## Expected Output (example)

```
--- Authors and their books ---
Author: Martin Fowler
  Book{id=1, title='Refactoring', ...}
  Book{id=2, title='Patterns of Enterprise Application Architecture', ...}
Author: Robert C. Martin
  Book{id=3, title='Clean Code', ...}

--- Tags and their books ---
Tag: Best Practices
  Refactoring, Clean Code
Tag: Architecture
  Patterns of Enterprise Application Architecture
```

## Key Concepts

- **`mappedBy`** tells JPA which side owns the foreign key — the owning side has `@JoinColumn`, the inverse side has `mappedBy`
- **Cascade on the inverse side** (`cascade = CascadeType.ALL` on `@OneToMany`) propagates save/delete from parent to children
- **`@JoinTable`** names the many-to-many bridge table and its FK columns
- Always add entities to **both sides** of a bidirectional relationship to keep the in-memory object graph consistent
