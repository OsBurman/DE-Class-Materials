# Exercise 05 — Pagination and Sorting

## Learning Objectives
- Use `Pageable` and `PageRequest.of()` to fetch data in pages
- Use `Sort.by()` to control ordering
- Inspect `Page<T>` metadata: `getContent()`, `getTotalPages()`, `getTotalElements()`
- Combine pagination with derived query methods

## Background

When a table has thousands of rows, fetching everything at once is inefficient.  
Spring Data's `Pageable` interface lets you request a specific *page number* and *page size*.

| Class/Method | Purpose |
|---|---|
| `PageRequest.of(page, size)` | Request page `page` (0-indexed) with `size` rows |
| `PageRequest.of(page, size, Sort)` | Same, with custom ordering |
| `Sort.by("field").ascending()` | Sort ascending by a field |
| `Sort.by("field").descending()` | Sort descending by a field |
| `page.getContent()` | The `List<T>` for this page |
| `page.getTotalElements()` | Total row count across all pages |
| `page.getTotalPages()` | Total number of pages |
| `page.getNumber()` | Current page number (0-indexed) |

## Instructions

### Step 1 — Add pageable query methods to `BookRepository`

Open `starter-code/BookRepository.java` and add:

```java
// TODO 1: Find all books matching a genre with pagination
//         Page<Book> findByGenre(String genre, Pageable pageable);

// TODO 2: No method needed for basic findAll with Pageable — it is inherited from JpaRepository
```

### Step 2 — Demonstrate pagination and sorting in `DataLoader`

Open `starter-code/DataLoader.java` and follow the TODO comments to:
1. Save 6 books spanning multiple genres
2. Fetch page 0 with size 2 (no sort) — print content and metadata
3. Fetch page 1 with size 2 (no sort) — print content
4. Fetch all books sorted by `title` ascending
5. Fetch all books sorted by `publishedYear` descending
6. Fetch page 0 of "Programming" books with page size 1

### Step 3 — Run

```
mvn spring-boot:run
```

Verify each page contains the correct subset of books.  
Notice `getTotalPages()` and `getTotalElements()` reflect the full dataset, not just the current page.

## Expected Output (example, 6 books total)

```
--- Page 0 (size 2) ---
Content: [Book{id=1...}, Book{id=2...}]
Page: 0 of 3 | Total: 6

--- Page 1 (size 2) ---
Content: [Book{id=3...}, Book{id=4...}]

--- Sorted by title ASC ---
Book{...Clean Code...}
Book{...Designing Data-Intensive Applications...}
Book{...Domain-Driven Design...}
...

--- Page 0 of Programming books (size 1) ---
Content: [Book{id=1...}]
Total Programming pages: 3
```

## Key Concepts

- `PageRequest.of(0, 2)` — first page, 2 items per page (pages are **0-indexed**)
- `Sort.by("publishedYear").descending()` — newest first; field name must match the **Java field**, not the column
- `Page<T>` contains both the data (`getContent()`) and metadata (`getTotalPages()`, `getTotalElements()`)
- `Slice<T>` is a lighter alternative that skips the `COUNT` query — useful for infinite-scroll UIs
