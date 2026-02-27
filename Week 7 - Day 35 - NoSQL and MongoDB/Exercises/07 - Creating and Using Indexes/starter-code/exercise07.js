// ============================================================
// Exercise 07 — Creating and Using Indexes
// ============================================================

use bookstore

// ---- SETUP (re-run if needed) ----
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


// TODO 1: Run the query BEFORE creating any index.
//         Look at winningPlan.stage in the output — it should say "COLLSCAN".
db.books.find({ genre: "Technology" }).explain("executionStats")


// TODO 2: Create a SINGLE-FIELD index on the "genre" field (ascending).
//         db.books.createIndex({ <field>: <direction> })
db.books.createIndex({ ???: ??? })


// TODO 3: Run the SAME query again with explain to confirm IXSCAN is used.
db.books.find({ genre: "Technology" }).explain("executionStats")


// TODO 4: Create a COMPOUND index on genre (ascending) and year (descending).
db.books.createIndex({ genre: ___, year: ___ })


// TODO 5: Run a query that uses the compound index:
//         Find Technology books sorted by year descending.
//         Add .explain("executionStats") to confirm IXSCAN.
db.books
  .find({ genre: "???" })
  .sort({ year: ??? })
  .explain("executionStats")


// TODO 6: List all indexes on the books collection.
//         Expected: _id index + 2 you created = 3 total
db.books.___()


// TODO 7: Drop the single-field genre index.
//         dropIndex takes the same key spec you used in createIndex.
db.books.dropIndex({ genre: ??? })


// TODO 8: List indexes again to confirm only 2 remain.
db.books.getIndexes()
