// =============================================================================
// Day 35 — MongoDB Part 1: mongosh Basics
// Bookstore Application
//
// Run these commands in the MongoDB shell (mongosh)
// Connect first: mongosh  (or mongosh "mongodb://localhost:27017")
//
// Topics covered:
//   1. Connecting and navigating databases
//   2. Creating collections and inserting documents
//   3. ObjectId and _id field
//   4. Reading documents (find, findOne)
//   5. Exploring document structure in the shell
// =============================================================================

// =============================================================================
// SECTION 1: Connect and Navigate
// =============================================================================

// Start mongosh from your terminal:
//   $ mongosh
// You'll see a prompt like:  bookstore_db>

// List all existing databases
show dbs
// Output: admin   40.00 KiB
//         config  12.00 KiB
//         local   40.00 KiB
// (admin, config, local are MongoDB's internal databases — leave them alone)

// Create / switch to our bookstore database
// NOTE: MongoDB doesn't create the database until you insert your first document
use bookstore_db
// Output: switched to db bookstore_db

// Confirm current database
db
// Output: bookstore_db

// =============================================================================
// SECTION 2: Create Collections and Insert Documents
// =============================================================================

// ── 2a: Explicit collection creation (optional) ───────────────────────────
db.createCollection("books")
// Output: { ok: 1 }

// ── 2b: Implicit creation — insert directly (most common approach) ────────
// MongoDB creates the "customers" collection automatically on first insert
db.customers.insertOne({
  name: "Alice Johnson",
  email: "alice@example.com",
  memberSince: new Date("2023-01-15"),
  isPremium: true
})
// Output: {
//   acknowledged: true,
//   insertedId: ObjectId("64a1f2e3b4c5d6e7f8091234")   ← auto-generated
// }

// ── 2c: insertOne — single document ──────────────────────────────────────
db.books.insertOne({
  title: "Clean Code",
  author: {
    name: "Robert C. Martin",
    nationality: "American"
  },
  genres: ["programming", "software-engineering"],
  price: 35.99,
  inStock: true,
  publishedYear: 2008,
  isbn: "978-0132350884",
  rating: 4.7,
  reviewCount: 12483,
  editions: [
    { year: 2008, format: "hardcover" },
    { year: 2009, format: "paperback" }
  ]
})

// ── 2d: insertMany — multiple documents at once ───────────────────────────
db.books.insertMany([
  {
    title: "The Pragmatic Programmer",
    author: { name: "David Thomas", nationality: "British" },
    genres: ["programming", "career"],
    price: 42.00,
    inStock: true,
    publishedYear: 2019,
    isbn: "978-0135957059",
    rating: 4.8,
    reviewCount: 9821
  },
  {
    title: "Designing Data-Intensive Applications",
    author: { name: "Martin Kleppmann", nationality: "German" },
    genres: ["data-engineering", "distributed-systems"],
    price: 55.00,
    inStock: true,
    publishedYear: 2017,
    isbn: "978-1449373320",
    rating: 4.9,
    reviewCount: 7532
  },
  {
    title: "The Name of the Wind",
    author: { name: "Patrick Rothfuss", nationality: "American" },
    genres: ["fantasy", "fiction"],
    price: 16.99,
    inStock: false,
    publishedYear: 2007,
    isbn: "978-0756404741",
    rating: 4.5,
    reviewCount: 18902
  },
  {
    title: "A Brief History of Time",
    author: { name: "Stephen Hawking", nationality: "British" },
    genres: ["science", "physics", "non-fiction"],
    price: 14.99,
    inStock: true,
    publishedYear: 1988,
    isbn: "978-0553380163",
    rating: 4.6,
    reviewCount: 22147
  }
])
// Output: { acknowledged: true, insertedIds: { '0': ObjectId(...), '1': ObjectId(...), ... } }

// ── 2e: Insert a document WITHOUT providing _id ───────────────────────────
// MongoDB auto-generates ObjectId for _id
db.books.insertOne({ title: "No ID Provided" })
// _id is still automatically created

// ── 2f: Insert a document WITH a custom _id ───────────────────────────────
db.books.insertOne({ _id: "isbn-978-0132350884", title: "Clean Code (custom ID)" })
// This book uses the ISBN string as its _id instead of ObjectId

// =============================================================================
// SECTION 3: ObjectId — Exploring Document Identifiers
// =============================================================================

// Create an ObjectId manually
let id = new ObjectId()
id                          // ObjectId("64a1f2e3b4c5d6e7f8091234")
id.toString()               // "64a1f2e3b4c5d6e7f8091234" — the hex string
id.getTimestamp()           // ISODate("2024-07-02T...") ← creation time embedded!

// ObjectIds sort chronologically — earlier inserts have smaller ObjectIds
let allIds = db.books.find({}, { _id: 1 }).toArray()
// The first document inserted has the smallest (earliest) ObjectId

// Look up a document by its ObjectId
// ⚠️ You MUST wrap the hex string in ObjectId() — it's not a plain string
db.books.findOne({ _id: ObjectId("64a1f2e3b4c5d6e7f8091234") })

// =============================================================================
// SECTION 4: Reading Documents
// =============================================================================

// ── 4a: find() — returns all documents (as a cursor) ─────────────────────
db.books.find()
// In mongosh, the cursor auto-iterates and shows the first 20 results
// Type  it  to see the next batch

// ── 4b: find() with a filter ──────────────────────────────────────────────
db.books.find({ inStock: true })              // all books in stock
db.books.find({ publishedYear: 2019 })        // books from 2019
db.books.find({ "author.name": "Stephen Hawking" })  // nested field query
//                 ↑ dot notation for nested objects

// ── 4c: find() with projection — select which fields to return ───────────
// 1 = include field,  0 = exclude field
db.books.find({}, { title: 1, price: 1, _id: 0 })
// Returns only title and price — _id is excluded

db.books.find({ inStock: true }, { title: 1, author: 1, rating: 1 })
// In-stock books, showing only title, author, and rating

// ── 4d: findOne() — returns the first matching document ──────────────────
db.books.findOne({ title: "Clean Code" })     // first match
db.books.findOne({ inStock: false })          // first out-of-stock book

// ── 4e: Counting documents ────────────────────────────────────────────────
db.books.countDocuments()              // total count
db.books.countDocuments({ inStock: true })    // count matching filter

// ── 4f: Sorting results ───────────────────────────────────────────────────
db.books.find().sort({ price: 1 })     // ascending by price (cheapest first)
db.books.find().sort({ price: -1 })    // descending by price (most expensive first)
db.books.find().sort({ rating: -1, title: 1 })  // sort by rating desc, then title asc

// ── 4g: Limiting and skipping ────────────────────────────────────────────
db.books.find().limit(3)               // first 3 documents
db.books.find().skip(2).limit(3)       // skip 2, return next 3 (pagination)
db.books.find().sort({ rating: -1 }).limit(1)  // single highest-rated book

// ── 4h: Chaining cursor methods ──────────────────────────────────────────
// Typical pagination pattern:
let page = 1
let pageSize = 2
db.books.find({ inStock: true })
        .sort({ title: 1 })
        .skip((page - 1) * pageSize)
        .limit(pageSize)

// =============================================================================
// SECTION 5: Exploring the Shell Further
// =============================================================================

// Check all collections in current database
show collections
// Output: books
//         customers

// Get collection statistics
db.books.stats()

// View distinct values for a field
db.books.distinct("publishedYear")    // [1988, 2007, 2008, 2017, 2019]
db.books.distinct("genres")           // unique genre tags across all books

// Convert cursor to array (useful in shell scripts)
let allBooks = db.books.find({}, { title: 1 }).toArray()
allBooks.length                        // number of books
allBooks[0]                            // first book in array

// Exit the shell
// exit
// or press Ctrl+D
