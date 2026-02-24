// =============================================================================
// Day 35 — MongoDB Part 2: CRUD Operations & Query Operators
// Bookstore Application
//
// Run these commands in mongosh after running Part 1 inserts.
// Connect first: mongosh  →  use bookstore_db
//
// Topics covered:
//   1. Update operations (updateOne, updateMany, replaceOne)
//   2. Delete operations (deleteOne, deleteMany)
//   3. Comparison operators ($eq, $ne, $gt, $gte, $lt, $lte)
//   4. Array operators ($in, $nin, $all, $elemMatch)
//   5. Logical operators ($and, $or, $not, $nor)
//   6. Element operators ($exists, $type)
//   7. Evaluation operators ($regex, $expr)
//   8. Update operators ($set, $unset, $inc, $push, $pull, $addToSet)
// =============================================================================

// =============================================================================
// SECTION 1: Full Insert — Seed Data for This Demo
// =============================================================================
// (Run these if you're starting fresh — adds all books we'll query against)

use bookstore_db

db.books.insertMany([
  {
    title: "Clean Code",
    author: { name: "Robert C. Martin", nationality: "American" },
    genres: ["programming", "software-engineering"],
    price: 35.99,
    inStock: true,
    publishedYear: 2008,
    isbn: "978-0132350884",
    rating: 4.7,
    reviewCount: 12483,
    pageCount: 431
  },
  {
    title: "The Pragmatic Programmer",
    author: { name: "David Thomas", nationality: "British" },
    genres: ["programming", "career"],
    price: 42.00,
    inStock: true,
    publishedYear: 2019,
    isbn: "978-0135957059",
    rating: 4.8,
    reviewCount: 9821,
    pageCount: 352
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
    reviewCount: 7532,
    pageCount: 611
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
    reviewCount: 18902,
    pageCount: 662
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
    reviewCount: 22147,
    pageCount: 212
  },
  {
    title: "Atomic Habits",
    author: { name: "James Clear", nationality: "American" },
    genres: ["self-help", "productivity", "non-fiction"],
    price: 18.99,
    inStock: true,
    publishedYear: 2018,
    isbn: "978-0735211292",
    rating: 4.8,
    reviewCount: 45301,
    pageCount: 320
  },
  {
    title: "The Alchemist",
    author: { name: "Paulo Coelho", nationality: "Brazilian" },
    genres: ["fiction", "philosophy"],
    price: 12.99,
    inStock: true,
    publishedYear: 1988,
    isbn: "978-0062315007",
    rating: 4.4,
    reviewCount: 31872,
    pageCount: 197
  }
])

// =============================================================================
// SECTION 2: Update Operations
// =============================================================================

// ── 2a: updateOne — update the FIRST matching document ───────────────────
// Syntax: db.collection.updateOne( <filter>, <update>, <options> )

// Increase price of "Clean Code" by $2 using $set
db.books.updateOne(
  { title: "Clean Code" },          // filter — which document to update
  { $set: { price: 37.99 } }        // $set — set specific fields
)
// Output: { matchedCount: 1, modifiedCount: 1 }

// Update a nested field using dot notation
db.books.updateOne(
  { title: "Clean Code" },
  { $set: { "author.nationality": "American (USA)" } }
)

// ── 2b: $inc — increment a numeric field ─────────────────────────────────
// Increment review count by 1 (simulating a new review being posted)
db.books.updateOne(
  { title: "The Alchemist" },
  { $inc: { reviewCount: 1 } }      // increment reviewCount by 1
)

// Decrement — use a negative value
db.books.updateOne(
  { title: "The Alchemist" },
  { $inc: { reviewCount: -1 } }     // decrement by 1
)

// ── 2c: $unset — remove a field from a document ──────────────────────────
db.books.updateOne(
  { title: "Clean Code" },
  { $unset: { pageCount: "" } }     // removes the pageCount field entirely
)
// Verify: db.books.findOne({ title: "Clean Code" }, { pageCount: 1 })

// ── 2d: updateMany — update ALL matching documents ───────────────────────
// Add a "discounted" field to all books priced over $40
db.books.updateMany(
  { price: { $gt: 40 } },           // filter — all books over $40
  { $set: { isOnSale: false } }     // add a new field to all matches
)
// Output: { matchedCount: 2, modifiedCount: 2 }

// Mark all out-of-stock books
db.books.updateMany(
  { inStock: false },
  { $set: { restockDate: new Date("2024-03-01") } }
)

// ── 2e: Array update operators ────────────────────────────────────────────
// $push — add an element to an array
db.books.updateOne(
  { title: "Clean Code" },
  { $push: { genres: "agile" } }    // adds "agile" to genres array
)

// $addToSet — add only if not already present (no duplicates)
db.books.updateOne(
  { title: "Clean Code" },
  { $addToSet: { genres: "programming" } }  // won't add — "programming" already exists
)
db.books.updateOne(
  { title: "Clean Code" },
  { $addToSet: { genres: "clean-code" } }   // adds — "clean-code" is new
)

// $pull — remove an element from an array by value
db.books.updateOne(
  { title: "Clean Code" },
  { $pull: { genres: "agile" } }    // removes "agile" from genres
)

// ── 2f: upsert — insert if no match found ────────────────────────────────
// If a book with this isbn doesn't exist, INSERT it. Otherwise, UPDATE it.
db.books.updateOne(
  { isbn: "978-9999999999" },
  { $set: {
      title: "New Upserted Book",
      author: { name: "New Author" },
      price: 29.99,
      inStock: true
    }
  },
  { upsert: true }                  // ← creates the document if not found
)
// If matched: { matchedCount: 1, modifiedCount: 1 }
// If not found: { matchedCount: 0, upsertedId: ObjectId(...) }

// ── 2g: replaceOne — replace the ENTIRE document ─────────────────────────
// ⚠️ This replaces ALL fields except _id — use carefully
db.books.replaceOne(
  { isbn: "978-9999999999" },
  { title: "Replaced Book", price: 19.99, inStock: false }
  // ← All other fields are GONE after this
)

// =============================================================================
// SECTION 3: Delete Operations
// =============================================================================

// ── 3a: deleteOne — delete the FIRST matching document ───────────────────
db.books.deleteOne({ isbn: "978-9999999999" })  // delete our test upsert
// Output: { deletedCount: 1 }

// ── 3b: deleteMany — delete ALL matching documents ───────────────────────
// Remove all books published before 2000
db.books.deleteMany({ publishedYear: { $lt: 2000 } })
// ⚠️ This would delete "A Brief History of Time" (1988) and "The Alchemist" (1988)
// Don't run this if you want to keep all books for the aggregation demo!
// Roll back by re-running the insertMany in Section 1

// ── 3c: findOneAndDelete — get the document AND delete it ────────────────
// Returns the deleted document (useful for "pop from queue" patterns)
let deletedBook = db.books.findOneAndDelete({ title: "New Upserted Book" })
printjson(deletedBook)

// =============================================================================
// SECTION 4: Comparison Operators
// =============================================================================

// $eq — equals (explicit version — same as { field: value })
db.books.find({ rating: { $eq: 4.8 } })
// Same as: db.books.find({ rating: 4.8 })

// $ne — not equals
db.books.find({ inStock: { $ne: true } })   // books NOT in stock

// $gt — greater than
db.books.find({ price: { $gt: 40 } })       // price > 40

// $gte — greater than or equal
db.books.find({ price: { $gte: 35 } })      // price >= 35

// $lt — less than
db.books.find({ price: { $lt: 20 } })       // price < 20

// $lte — less than or equal
db.books.find({ rating: { $lte: 4.5 } })    // rating <= 4.5

// Combine comparisons — books between $15 and $40
db.books.find({ price: { $gte: 15, $lte: 40 } })

// =============================================================================
// SECTION 5: Array Operators
// =============================================================================

// $in — field value is in the given array
db.books.find({ rating: { $in: [4.7, 4.8, 4.9] } })   // ratings 4.7, 4.8, or 4.9
db.books.find({ genres: { $in: ["fiction", "fantasy"] } })  // contains fiction or fantasy

// $nin — field value is NOT in the array
db.books.find({ genres: { $nin: ["programming"] } })   // not a programming book

// $all — array contains ALL of the specified values
db.books.find({ genres: { $all: ["fiction", "philosophy"] } })  // has BOTH genres

// $elemMatch — at least one array element matches ALL given conditions
// (useful when array elements are objects)
db.books.find({
  editions: {
    $elemMatch: { year: { $gte: 2010 }, format: "paperback" }
  }
})
// Find books where at least one edition was published after 2010 as a paperback

// $size — array has exactly this many elements
db.books.find({ genres: { $size: 3 } })    // books with exactly 3 genres

// =============================================================================
// SECTION 6: Logical Operators
// =============================================================================

// $and — explicit AND (implicit AND is the default when you list multiple conditions)
db.books.find({
  $and: [
    { price: { $lt: 30 } },
    { inStock: true }
  ]
})
// Equivalent to: db.books.find({ price: { $lt: 30 }, inStock: true })
// Use explicit $and when you need two conditions on the same field:
db.books.find({
  $and: [
    { genres: { $in: ["programming"] } },
    { genres: { $in: ["career"] } }
  ]
})

// $or — at least one condition must match
db.books.find({
  $or: [
    { price: { $lt: 15 } },
    { rating: { $gte: 4.9 } }
  ]
})
// Books that are either cheap (<$15) OR highly rated (4.9+)

// $nor — none of the conditions match
db.books.find({
  $nor: [
    { inStock: false },
    { price: { $gt: 50 } }
  ]
})
// Books that are NOT out of stock AND NOT over $50

// $not — negate a condition
db.books.find({ price: { $not: { $gt: 30 } } })
// Equivalent to price <= 30  (same as $lte: 30)
// $not is useful when combined with $regex or $exists

// =============================================================================
// SECTION 7: Element Operators
// =============================================================================

// $exists — check whether a field exists in the document
db.books.find({ restockDate: { $exists: true } })   // has a restockDate field
db.books.find({ pageCount: { $exists: false } })    // missing pageCount field

// $type — match documents where a field is of a specific BSON type
db.books.find({ _id: { $type: "objectId" } })       // _id is an ObjectId
db.books.find({ price: { $type: "double" } })       // price is a float
db.books.find({ title: { $type: "string" } })       // title is a string

// =============================================================================
// SECTION 8: Evaluation Operators — $regex
// =============================================================================

// $regex — regular expression matching
db.books.find({ title: { $regex: /code/i } })       // title contains "code" (case-insensitive)
db.books.find({ title: { $regex: "^The" } })        // title starts with "The"
db.books.find({ "author.name": { $regex: /martin/i } })  // author name contains "martin"

// Options: i = case-insensitive, m = multiline, s = dot matches newline
db.books.find({ title: { $regex: /data/i } })       // contains "data" any case

// ── Combining operators — realistic query ─────────────────────────────────
// "Find in-stock programming books under $40 with a rating above 4.5"
db.books.find({
  inStock: true,
  genres: { $in: ["programming"] },
  price: { $lt: 40 },
  rating: { $gt: 4.5 }
})
