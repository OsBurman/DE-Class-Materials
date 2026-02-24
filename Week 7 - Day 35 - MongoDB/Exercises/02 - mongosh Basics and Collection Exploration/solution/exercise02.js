// ============================================================
// Exercise 02 — mongosh Basics and Collection Exploration (SOLUTION)
// ============================================================

// 1. List all databases
show dbs
// Expected output includes: admin, config, local (and bookstore once populated)

// 2. Switch to the bookstore database
// MongoDB creates it automatically on first write
use bookstore

// 3. Show collections (empty at this point)
show collections
// No output — bookstore has no collections yet

// 4. Insert one document into books
// insertOne() returns { acknowledged: true, insertedId: ObjectId('...') }
db.books.insertOne({
  title: "The Pragmatic Programmer",
  author: "David Thomas",
  genre: "Technology",
  year: 1999,
  available: true
})

// 5. Show collections again — books now appears
show collections
// Output: books

// 6. Retrieve the inserted document
// findOne() with no arguments returns the first document in the collection
db.books.findOne()

// 7. Show database statistics
// Look for the "collections" (1) and "dataSize" fields
db.stats()
