// ============================================================
// Exercise 03 — Insert and Find with mongosh (SOLUTION)
// ============================================================

// 1. Switch to bookstore
use bookstore

// 2. Drop books to start fresh
db.books.drop()

// 3. Insert one book
db.books.insertOne({
  title: "The Pragmatic Programmer",
  author: "David Thomas",
  genre: "Technology",
  year: 1999,
  price: 39.99,
  available: true
})

// 4. Insert four more books
db.books.insertMany([
  {
    title: "Clean Code",
    author: "Robert Martin",
    genre: "Technology",
    year: 2008,
    price: 34.99,
    available: true
  },
  {
    title: "Dune",
    author: "Frank Herbert",
    genre: "Science Fiction",
    year: 1965,
    price: 14.99,
    available: true
  },
  {
    title: "Sapiens",
    author: "Yuval Noah Harari",
    genre: "History",
    year: 2011,
    price: 19.99,
    available: false
  },
  {
    title: "The Great Gatsby",
    author: "F. Scott Fitzgerald",
    genre: "Fiction",
    year: 1925,
    price: 9.99,
    available: true
  }
])

// 5. Retrieve all documents
// find() with no arguments returns a cursor over all documents
db.books.find()

// 6. Find the first Technology book
db.books.findOne({ genre: "Technology" })

// 7. Project only title and author; _id: 0 excludes the _id field
db.books.find(
  {},
  { title: 1, author: 1, _id: 0 }
)

// 8. Count all documents — should return 5
db.books.countDocuments({})
