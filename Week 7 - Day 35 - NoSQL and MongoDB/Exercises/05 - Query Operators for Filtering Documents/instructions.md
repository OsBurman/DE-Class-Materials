# Exercise 05: Query Operators for Filtering Documents

## Objective
Write MongoDB queries using comparison, logical, element, and evaluation operators to precisely filter documents.

## Background
The bookstore database has grown to dozens of books. Instead of reading everything and filtering in application code, you will write targeted MQL queries using MongoDB's rich set of query operators to ask the database to do the work.

## Requirements

First, run the setup block in the starter file to populate the collection with 10 sample books. Then write queries for each TODO.

1. **Comparison — $gt / $lt:** Find all books where `year` is greater than `2000`.
2. **Comparison — $gte / $lte:** Find all books where `price` is between `10.00` and `30.00` (inclusive on both ends).
3. **Comparison — $in:** Find all books where `genre` is either `"Technology"` or `"Science Fiction"`.
4. **Comparison — $ne:** Find all books where `available` is **not** `false` (i.e., all available books).
5. **Logical — $and:** Find books where `year` is greater than `2000` **and** `price` is less than `25`.
6. **Logical — $or:** Find books where `genre` is `"History"` **or** `year` is less than `1970`.
7. **Element — $exists:** Find all documents that **have** a `rating` field.
8. **Evaluation — $regex:** Find all books whose `title` starts with the letter `"T"` (case-insensitive).
9. **Sort + Limit:** Find the **3 cheapest** books (sort by `price` ascending, limit to 3). Return only `title` and `price`.

## Hints
- Multiple conditions in a `find` filter object are implicitly `$and`-ed: `{ a: 1, b: 2 }` means `a=1 AND b=2`.
- `$in` takes an array: `{ genre: { $in: ["A", "B"] } }`.
- `$regex` supports JS-style flags: `{ title: { $regex: /^t/i } }` — the `i` flag makes it case-insensitive.
- Chain `.sort({ price: 1 }).limit(3)` onto `find()` for sorted, limited results.

## Expected Output

```js
// Query 1 — year > 2000
// Returns books published after 2000

// Query 2 — price between 10 and 30
// Returns books priced $10–$30

// Query 3 — $in genre
// Returns Technology and Science Fiction books

// Query 4 — $ne false (available books)
// Returns all books where available != false

// Query 5 — year > 2000 AND price < 25
// Returns books matching both conditions

// Query 6 — History OR year < 1970
// Returns History books plus older books

// Query 7 — $exists rating
// Returns only books that have a rating field

// Query 8 — title starts with T (case-insensitive)
// e.g., "The Pragmatic Programmer", "The Great Gatsby"

// Query 9 — 3 cheapest books
[
  { title: 'The Great Gatsby', price: 9.99 },
  { title: 'Dune', price: 14.99 },
  { title: '...', price: ... }
]
```
