# Exercise 03: Insert and Find with mongosh

## Objective
Insert single and multiple documents into a MongoDB collection, and retrieve them using `find`, `findOne`, and projection.

## Background
You are building a `bookstore` database. The `books` collection will hold the store's inventory. You need to populate it with sample data and then write queries that return exactly the fields your application needs.

## Requirements

1. Switch to the `bookstore` database (create it if it does not exist).
2. Drop the `books` collection if it already contains data from a previous exercise: `db.books.drop()`.
3. Use `insertOne` to insert **one** book with these fields: `title`, `author`, `genre`, `year`, `price`, `available`.
4. Use `insertMany` to insert **four more** books in a single command. Use a variety of genres, years (ranging from 1990 to 2023), and prices.
5. Use `find()` with no arguments to retrieve **all five** books.
6. Use `findOne({ genre: "Technology" })` to retrieve the first technology book.
7. Use `find()` with a **projection** that returns only `title` and `author`, excluding `_id`. The result should show just those two fields for all five books.
8. Use `find().count()` (or `countDocuments({})`) to confirm 5 documents exist.

## Hints
- `insertMany` takes an **array** of document objects: `db.collection.insertMany([{...}, {...}])`.
- Projection syntax: `db.books.find({}, { title: 1, author: 1, _id: 0 })` — `1` includes, `0` excludes.
- `find()` returns a cursor. In mongosh, it pretty-prints the first 20 results automatically.
- `countDocuments({})` is the modern replacement for the deprecated `.count()`.

## Expected Output

```js
// find() — all 5 books (abbreviated)
[
  { _id: ObjectId('...'), title: 'The Pragmatic Programmer', author: 'David Thomas', ... },
  { _id: ObjectId('...'), title: 'Clean Code', author: 'Robert Martin', ... },
  ...
]

// findOne({ genre: "Technology" })
{ _id: ObjectId('...'), title: 'The Pragmatic Programmer', genre: 'Technology', ... }

// find({}, { title: 1, author: 1, _id: 0 }) — projected
[
  { title: 'The Pragmatic Programmer', author: 'David Thomas' },
  { title: 'Clean Code', author: 'Robert Martin' },
  { title: 'Dune', author: 'Frank Herbert' },
  { title: 'Sapiens', author: 'Yuval Noah Harari' },
  { title: 'The Great Gatsby', author: 'F. Scott Fitzgerald' }
]

// countDocuments({})
5
```
