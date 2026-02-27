// ============================================================
// Exercise 06 — Aggregation Pipeline Stages
// ============================================================

use bookstore

// ---- SETUP: run this first ----
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


// TODO 1: Count available books by genre, sorted by count descending.
//   Pipeline stages:
//     Stage 1 — $match: available books only
//     Stage 2 — $group: group by "$genre", count with { $sum: 1 }
//     Stage 3 — $sort: by count descending (-1)
db.books.aggregate([
  { $match: { available: ??? } },
  { $group: { _id: "???", count: { $sum: ??? } } },
  { $sort:  { count: ??? } }
])


// TODO 2: Compute the average price of ALL books (no filter).
//   Pipeline stages:
//     Stage 1 — $group with _id: null, avgPrice using $avg on "$price"
//     Stage 2 — $project: show avgPrice rounded to 2dp using $round; hide _id
db.books.aggregate([
  { $group: { _id: null, avgPrice: { $avg: "???" } } },
  { $project: { avgPrice: { $round: ["$avgPrice", ???] }, _id: ??? } }
])


// TODO 3: Genre revenue report for AVAILABLE books (sorted by totalRevenue desc).
//   Pipeline stages:
//     Stage 1 — $match: available books
//     Stage 2 — $group by genre: totalRevenue ($sum of "$price"), bookCount ($sum 1)
//     Stage 3 — $project: rename _id to "genre", show totalRevenue and bookCount
//     Stage 4 — $sort: totalRevenue descending
db.books.aggregate([
  { $match: { ??? } },
  { $group: { _id: "$genre", totalRevenue: { $sum: "???" }, bookCount: { $sum: 1 } } },
  { $project: { genre: "???", totalRevenue: 1, bookCount: 1, _id: ??? } },
  { $sort:    { totalRevenue: ??? } }
])


// TODO 4: Count how many times each bookTitle appears in the orders collection.
//   Pipeline stages:
//     Stage 1 — $group by "$bookTitle", count with { $sum: 1 } as orderedCount
//     Stage 2 — $sort: orderedCount descending
db.orders.aggregate([
  { $group: { _id: "???", orderedCount: { $sum: 1 } } },
  { $sort:  { orderedCount: ??? } }
])


// TODO 5: Join orders with books using $lookup.
//   Pipeline stages:
//     Stage 1 — $lookup: from "books", localField "bookTitle", foreignField "title", as "bookDetails"
//     Stage 2 — $unwind: "$bookDetails"  (flatten the single-element array)
//     Stage 3 — $project: customerId, bookTitle, price: "$bookDetails.price", _id: 0
db.orders.aggregate([
  { $lookup: {
      from: "???",
      localField: "???",
      foreignField: "???",
      as: "bookDetails"
  }},
  { $unwind: "???" },
  { $project: { customerId: 1, bookTitle: 1, price: "???", _id: 0 } }
])
