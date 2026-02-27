// ============================================================
// Exercise 06 — Aggregation Pipeline Stages (SOLUTION)
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
db.orders.drop()
db.orders.insertMany([
  { customerId: "C001", bookTitle: "Clean Code",               orderDate: new Date("2024-01-10") },
  { customerId: "C002", bookTitle: "Atomic Habits",            orderDate: new Date("2024-01-12") },
  { customerId: "C003", bookTitle: "Clean Code",               orderDate: new Date("2024-01-15") },
  { customerId: "C001", bookTitle: "Dune",                     orderDate: new Date("2024-02-01") },
  { customerId: "C004", bookTitle: "The Pragmatic Programmer", orderDate: new Date("2024-02-05") }
])
// ---- END SETUP ----


// 1. Count available books by genre (descending)
// $match first reduces the working set before grouping — more efficient
db.books.aggregate([
  { $match: { available: true } },
  { $group: { _id: "$genre", count: { $sum: 1 } } },
  { $sort:  { count: -1 } }
])

// 2. Average price of all books, rounded to 2 decimal places
// _id: null means we group all documents into one bucket
db.books.aggregate([
  { $group: { _id: null, avgPrice: { $avg: "$price" } } },
  { $project: { avgPrice: { $round: ["$avgPrice", 2] }, _id: 0 } }
])

// 3. Genre revenue report — available books only
db.books.aggregate([
  { $match: { available: true } },
  { $group: {
      _id: "$genre",
      totalRevenue: { $sum: "$price" },
      bookCount:    { $sum: 1 }
  }},
  // Rename _id to genre for cleaner output
  { $project: { genre: "$_id", totalRevenue: 1, bookCount: 1, _id: 0 } },
  { $sort:    { totalRevenue: -1 } }
])

// 4. How many times each title was ordered
db.orders.aggregate([
  { $group: { _id: "$bookTitle", orderedCount: { $sum: 1 } } },
  { $sort:  { orderedCount: -1 } }
])
// Expected: Clean Code x2, then Atomic Habits, Dune, The Pragmatic Programmer x1 each

// 5. Join orders → books using $lookup
// $lookup produces an array; $unwind flattens it to a single embedded doc
db.orders.aggregate([
  { $lookup: {
      from:         "books",      // join with books collection
      localField:   "bookTitle",  // field in orders
      foreignField: "title",      // field in books
      as:           "bookDetails" // name for the joined array
  }},
  { $unwind: "$bookDetails" },   // flatten the 1-element array
  { $project: {
      customerId: 1,
      bookTitle:  1,
      price:      "$bookDetails.price",  // pull price out of nested doc
      _id:        0
  }}
])
