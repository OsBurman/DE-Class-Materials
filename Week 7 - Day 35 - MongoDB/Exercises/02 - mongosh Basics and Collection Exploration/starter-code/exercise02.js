// ============================================================
// Exercise 02 — mongosh Basics and Collection Exploration
// Run each command one at a time in your mongosh session.
// ============================================================

// TODO 1: List all databases on the server.
//         Command: show ___


// TODO 2: Switch to the "bookstore" database.
//         Command: use ___


// TODO 3: Show all collections in the current database.
//         Command: show ___
//         (Expected: empty — the database has no collections yet)


// TODO 4: Insert one document into the "books" collection.
//         The document must include: title, author, genre, year, available
//         Command: db.books.insertOne({ ... })
db.books.insertOne({
  title: "???",
  author: "???",
  genre: "???",
  year: ???,
  available: ???
})


// TODO 5: Show all collections again.
//         Command: show ___
//         (Expected: books)


// TODO 6: Retrieve the document you just inserted.
//         Command: db.books.___()


// TODO 7: Show database statistics.
//         Command: db.___()
//         Look at the "collections" and "dataSize" fields in the output.
