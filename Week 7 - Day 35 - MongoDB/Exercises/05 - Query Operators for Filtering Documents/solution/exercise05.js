// ============================================================
// Exercise 05 — Query Operators for Filtering Documents (SOLUTION)
// ============================================================

use bookstore

// ---- SETUP ----
db.books.drop()
db.books.insertMany([
  { title: "The Pragmatic Programmer", author: "David Thomas",       genre: "Technology",     year: 1999, price: 39.99, available: true  },
  { title: "Clean Code",               author: "Robert Martin",      genre: "Technology",     year: 2008, price: 34.99, available: true  },
  { title: "Dune",                     author: "Frank Herbert",      genre: "Science Fiction",year: 1965, price: 14.99, available: true  },
  { title: "Sapiens",                  author: "Yuval Noah Harari",  genre: "History",        year: 2011, price: 19.99, available: false, rating: 4.7 },
  { title: "The Great Gatsby",         author: "F. Scott Fitzgerald",genre: "Fiction",        year: 1925, price: 9.99,  available: true  },
  { title: "Thinking, Fast and Slow",  author: "Daniel Kahneman",   genre: "Psychology",     year: 2011, price: 24.99, available: true, rating: 4.5 },
  { title: "A Brief History of Time",  author: "Stephen Hawking",   genre: "Science",        year: 1988, price: 18.99, available: true  },
  { title: "1984",                     author: "George Orwell",     genre: "Science Fiction",year: 1949, price: 12.99, available: true  },
  { title: "Educated",                 author: "Tara Westover",     genre: "History",        year: 2018, price: 22.99, available: true, rating: 4.8 },
  { title: "Atomic Habits",            author: "James Clear",       genre: "Psychology",     year: 2018, price: 27.99, available: true  }
])
// ---- END SETUP ----


// 1. Year > 2000  →  Clean Code, Sapiens, Thinking Fast and Slow, Educated, Atomic Habits
db.books.find({ year: { $gt: 2000 } })

// 2. Price $10–$30 inclusive  →  Dune, Sapiens, Great Gatsby, Thinking Fast and Slow,
//    Brief History, 1984, Educated, Atomic Habits
db.books.find({ price: { $gte: 10, $lte: 30 } })

// 3. Technology or Science Fiction
db.books.find({ genre: { $in: ["Technology", "Science Fiction"] } })

// 4. available != false  →  all documents where available is true (or missing)
db.books.find({ available: { $ne: false } })

// 5. year > 2000 AND price < 25
// $and is explicit here; could also write as { year: { $gt: 2000 }, price: { $lt: 25 } }
db.books.find({
  $and: [
    { year:  { $gt: 2000 } },
    { price: { $lt: 25 } }
  ]
})

// 6. History genre OR published before 1970
db.books.find({
  $or: [
    { genre: "History" },
    { year: { $lt: 1970 } }
  ]
})

// 7. Only documents that have a "rating" field
// $exists: true — document must contain the field (even if null)
db.books.find({ rating: { $exists: true } })

// 8. Title starts with "T" (case-insensitive)
// /^T/i — ^ anchors to the start of the string; i flag ignores case
db.books.find({ title: { $regex: /^T/i } })

// 9. 3 cheapest books (price ascending), title + price only
db.books
  .find({}, { title: 1, price: 1, _id: 0 })
  .sort({ price: 1 })
  .limit(3)
// Expected: Great Gatsby (9.99), 1984 (12.99), Dune (14.99)
