// ============================================================
// Exercise 05 — Query Operators for Filtering Documents
// ============================================================

use bookstore

// ---- SETUP: run this block first to populate the collection ----
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


// TODO 1: Find all books published after year 2000.
//         Use the $gt (greater-than) operator.
db.books.find({ year: { ???: 2000 } })


// TODO 2: Find all books where price is between 10.00 and 30.00 (inclusive).
//         Use both $gte and $lte in the same field filter.
db.books.find({ price: { ???: 10, ???: 30 } })


// TODO 3: Find all books where genre is "Technology" OR "Science Fiction".
//         Use the $in operator with an array of values.
db.books.find({ genre: { $in: [ ???, ??? ] } })


// TODO 4: Find all books where available is NOT false.
//         Use the $ne (not-equal) operator.
db.books.find({ available: { ???: false } })


// TODO 5: Find books where year > 2000 AND price < 25.
//         Use the $and operator with an array of condition objects.
db.books.find({
  $and: [
    { year:  { ???: 2000 } },
    { price: { ???: 25   } }
  ]
})


// TODO 6: Find books where genre is "History" OR year is less than 1970.
//         Use the $or operator.
db.books.find({
  $or: [
    { genre: "???" },
    { year: { ???: 1970 } }
  ]
})


// TODO 7: Find documents that HAVE a "rating" field.
//         Use the $exists operator: { field: { $exists: true } }
db.books.find({ rating: { ???: ??? } })


// TODO 8: Find books whose title starts with the letter "T" (case-insensitive).
//         Use $regex with the /^T/i pattern.
db.books.find({ title: { $regex: ??? } })


// TODO 9: Find the 3 cheapest books. Return only title and price (exclude _id).
//         Chain .sort({ price: 1 }).limit(3) onto find().
db.books
  .find({}, { title: ___, price: ___, _id: ___ })
  .sort({ price: ??? })
  .limit(___)
