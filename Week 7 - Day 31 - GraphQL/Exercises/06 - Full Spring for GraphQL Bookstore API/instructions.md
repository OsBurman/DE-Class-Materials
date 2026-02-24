# Exercise 06: Full Spring for GraphQL Bookstore API

## Objective
Build a complete Spring for GraphQL server with a multi-type schema, nested resolvers, and a comprehensive test suite — applying every topic from the day in a realistic scenario.

## Background
You are building the backend for a bookstore. The API needs to return `Book` objects that include a nested `Author` object (resolved separately), support filtering by genre, and allow books to be added and deleted. This exercise combines everything: SDL schema design, all scalar types, nested type resolvers (`@SchemaMapping`), query arguments and variables, fragments, and all three operation types.

## Requirements

### Schema (`schema.graphqls`)
1. Define `type Author { id: ID!, name: String!, nationality: String }` 
2. Define `type Book { id: ID!, title: String!, genre: String!, year: Int!, available: Boolean!, rating: Float, author: Author! }`
3. Define `type Query`:
   - `books: [Book!]!`
   - `book(id: ID!): Book`
   - `booksByGenre(genre: String!): [Book!]!`
4. Define `type Mutation`:
   - `addBook(title: String!, genre: String!, year: Int!, authorId: ID!): Book!`
   - `setAvailability(id: ID!, available: Boolean!): Book`
5. Define `type Subscription`: `bookAdded: Book!`

### Domain Records
6. Create `Author.java` — record with `String id, String name, String nationality`
7. Create `Book.java` — record with `String id, String title, String genre, int year, boolean available, Double rating, String authorId` (note: `authorId` is stored, `Author` resolved separately)

### Controller
8. Create `BookController.java` with:
   - Pre-populated `List<Author>` with 2 authors (e.g., id `"a1"` / `"a2"`)
   - Pre-populated `List<Book>` with 3 books spanning 2 genres
   - `@QueryMapping books()` — returns all books
   - `@QueryMapping book(@Argument String id)` — returns matching book or null
   - `@QueryMapping booksByGenre(@Argument String genre)` — filters by genre (case-insensitive)
   - `@SchemaMapping(typeName="Book", field="author") author(Book book)` — resolves the `Author` from the book's `authorId`
   - `@MutationMapping addBook(...)` — creates and adds a new book, emits to sink, returns it
   - `@MutationMapping setAvailability(...)` — updates available flag, returns updated book
   - `@SubscriptionMapping bookAdded()` — returns `Flux<Book>` from sink

### Tests (`BookstoreTest.java`)
9. Write **7 tests** using `@GraphQlTest(BookController.class)`:
   - `queryAllBooks_returnsPreloadedBooks` — asserts 3 books returned
   - `queryBook_byId_returnsCorrectTitle`
   - `queryBooksByGenre_returnsFilteredList` — query for one genre, assert all returned books have that genre
   - `nestedAuthorResolver_returnsAuthorName` — query `book(id) { title author { name } }`, assert `author.name` is not null
   - `addBook_returnsNewBook` — mutation with all required args, assert title and genre
   - `setAvailability_updatesFlag` — set to false, then assert `available == false`
   - `usesFragment_inBooksQuery` — query `books { ...BookFields }` with a named fragment, assert list size >= 3

## Hints
- `@SchemaMapping(typeName="Book", field="author")` takes the parent `Book` as its parameter; Spring for GraphQL injects it automatically.
- Use `case-insensitive` comparison: `genre.equalsIgnoreCase(book.genre())`.
- For `rating`, use `Double` (nullable) so books can be created without a rating.
- The fragment test verifies the whole day's skill: fragment `BookFields on Book { id title genre available }`.
- Seed data: author "a1" = Robert C. Martin, "a2" = Joshua Bloch; books: "Clean Code" (Programming, 2008), "Effective Java" (Programming, 2018), "The Pragmatic Programmer" (Programming, 1999).

## Expected Output
```
All 7 tests PASS
```
