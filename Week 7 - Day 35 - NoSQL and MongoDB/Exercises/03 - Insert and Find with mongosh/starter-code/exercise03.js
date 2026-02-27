// ============================================================
// Exercise 03 — Insert and Find with mongosh
// Run each block in order in your mongosh session.
// ============================================================

// TODO 1: Switch to the bookstore database.
use ___

// TODO 2: Drop the books collection to start fresh.
db.books.___()

// TODO 3: Insert ONE book using insertOne.
//         Required fields: title, author, genre, year, price, available
db.books.insertOne({
  title: "???",
  author: "???",
  genre: "???",
  year: ???,
  price: ???,
  available: ???
})

// TODO 4: Insert FOUR more books in a single insertMany call.
//         Use different genres (e.g., Fiction, History, Science Fiction),
//         years between 1990 and 2023, and different prices.
db.books.insertMany([
  // book 2
  { title: "???", author: "???", genre: "???", year: ???, price: ???, available: ??? },
  // book 3
  { title: "???", author: "???", genre: "???", year: ???, price: ???, available: ??? },
  // book 4
  { title: "???", author: "???", genre: "???", year: ???, price: ???, available: ??? },
  // book 5
  { title: "???", author: "???", genre: "???", year: ???, price: ???, available: ??? }
])

// TODO 5: Find ALL documents in the books collection.
db.books.___()

// TODO 6: Find the first document where genre is "Technology".
db.books.___(  )

// TODO 7: Find all books but return only title and author (exclude _id).
//         Projection syntax: db.books.find( <filter>, <projection> )
db.books.find(
  {},
  { title: ___, author: ___, _id: ___ }
)

// TODO 8: Count total documents in the collection.
//         Use countDocuments({})
db.books.___({})
