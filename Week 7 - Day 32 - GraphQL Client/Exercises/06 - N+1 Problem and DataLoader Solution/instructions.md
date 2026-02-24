# Exercise 06: N+1 Problem and DataLoader Solution

## Objective
Identify the N+1 query problem in a GraphQL nested resolver, then refactor to a `DataLoader`-based batch resolver that eliminates the extra database calls.

## Background
The N+1 problem occurs when fetching a list of N items triggers N additional individual lookups for a related field. In GraphQL, this typically happens when a nested resolver (e.g., `Book.author`) is called once per book — so fetching 10 books causes 10 separate author lookups. `DataLoader` solves this by collecting all keys requested in a single execution tick and issuing a single batched lookup. It is framework-agnostic and can be used inside Spring for GraphQL's `@BatchMapping` annotation.

## Requirements

1. The project builds on the Spring for GraphQL bookstore from Day 31 Exercise 06. The `pom.xml`, `schema.graphqls`, `Author.java`, and `Book.java` are already provided.

2. In `BookControllerNaive.java`, implement the **naive** nested resolver:
   - `@QueryMapping List<Book> books()` — returns all books from an in-memory list
   - `@SchemaMapping(typeName="Book", field="author") Author author(Book book)` — looks up the author by `authorId` one at a time, printing a log line `"[NAIVE] Looking up author: " + book.authorId()` each time it is called
   - Add a `@QueryMapping Author authorById(@Argument String id)` method for single lookup

3. In `BatchBookController.java`, implement the **batch** resolver:
   - `@QueryMapping List<Book> books()` — same as above
   - `@BatchMapping List<Author> author(List<Book> books)` — receives ALL books at once, prints `"[BATCH] Loading " + books.size() + " authors in one call"`, and returns a `List<Author>` in the same order as the input `books` list

4. In `NaiveVsBatchTest.java`, write **two** `@GraphQlTest` tests:
   - `naiveResolver_triggersNPlusOneLog` — calls `@GraphQlTest(BookControllerNaive.class)`, executes a `{ books { title author { name } } }` query, asserts that the first book's author name is correct
   - `batchResolver_loadsAllAuthorsOnce` — calls `@GraphQlTest(BatchBookController.class)`, executes the same query, asserts the same result

5. The seed data (in-memory, no database) must contain at least **3 books** across **2 authors**.

## Hints
- `@BatchMapping` is the Spring for GraphQL annotation that automatically collects all parent objects (books) and calls your method once with the full list.
- The return type of `@BatchMapping` must be a `List<ChildType>` in the **same order** as the input list — Spring matches them by index.
- Use `System.out.println(...)` for the log lines — no logging framework needed.
- Both controllers can share the same in-memory seed data — define it as a `static final List<Book>` in a separate `SeedData.java` class or inline in each controller.

## Expected Output

When running the test for the **naive** resolver, the console shows (3 books = 3 separate author calls):
```
[NAIVE] Looking up author: a1
[NAIVE] Looking up author: a2
[NAIVE] Looking up author: a1
```

When running the test for the **batch** resolver, the console shows (all 3 books in a single call):
```
[BATCH] Loading 3 authors in one call
```

Both tests assert:
```
books[0].author.name = "Robert C. Martin"
```
