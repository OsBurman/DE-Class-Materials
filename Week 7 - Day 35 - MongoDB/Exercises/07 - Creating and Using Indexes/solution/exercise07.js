// ============================================================
// Exercise 07 — Creating and Using Indexes (SOLUTION)
// ============================================================

use bookstore

// ---- SETUP ----
db.books.drop()
db.books.insertMany([
  { title: "The Pragmatic Programmer", genre: "Technology",     year: 1999, price: 39.99, available: true  },
  { title: "Clean Code",               genre: "Technology",     year: 2008, price: 34.99, available: true  },
  { title: "Dune",                     genre: "Science Fiction",year: 1965, price: 14.99, available: true  },
  { title: "Sapiens",                  genre: "History",        year: 2011, price: 19.99, available: false },
  { title: "The Great Gatsby",         genre: "Fiction",        year: 1925, price: 9.99,  available: true  },
  { title: "Thinking, Fast and Slow",  genre: "Psychology",     year: 2011, price: 24.99, available: true  },
  { title: "A Brief History of Time",  genre: "Science",        year: 1988, price: 18.99, available: true  },
  { title: "1984",                     genre: "Science Fiction",year: 1949, price: 12.99, available: true  },
  { title: "Educated",                 genre: "History",        year: 2018, price: 22.99, available: true  },
  { title: "Atomic Habits",            genre: "Psychology",     year: 2018, price: 27.99, available: true  }
])
// ---- END SETUP ----


// 1. COLLSCAN baseline — no index on genre yet
// Look for: executionStats.executionStages.stage === "COLLSCAN"
// and totalDocsExamined === 10
db.books.find({ genre: "Technology" }).explain("executionStats")


// 2. Single-field index on genre (ascending)
// MongoDB names it automatically: "genre_1"
db.books.createIndex({ genre: 1 })


// 3. Confirm IXSCAN after index creation
// totalDocsExamined should now equal only the matching docs (2)
db.books.find({ genre: "Technology" }).explain("executionStats")


// 4. Compound index: genre ascending, year descending
// Useful for queries like "get all Technology books newest first"
db.books.createIndex({ genre: 1, year: -1 })


// 5. Query using the compound index — find Technology books, newest first
// MongoDB should pick the compound index: IXSCAN on genre_1_year_-1
db.books
  .find({ genre: "Technology" })
  .sort({ year: -1 })
  .explain("executionStats")


// 6. List all indexes — should show 3: _id_,  genre_1,  genre_1_year_-1
db.books.getIndexes()


// 7. Drop the single-field genre index by its key spec
db.books.dropIndex({ genre: 1 })


// 8. Confirm 2 indexes remain: _id_  and  genre_1_year_-1
db.books.getIndexes()
