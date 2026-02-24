// ============================================================
// Exercise 04 — Update and Delete Operations (SOLUTION)
// ============================================================

use bookstore

// 1. Update the price of The Great Gatsby to 12.99
// updateOne modifies only the first matching document
db.books.updateOne(
  { title: "The Great Gatsby" },
  { $set: { price: 12.99 } }
)

// 2. Mark Sapiens as unavailable
db.books.updateOne(
  { title: "Sapiens" },
  { $set: { available: false } }
)

// 3. Add onSale: true to all books priced under $20
// $lt (less-than) operator matches price < 20
// Affected books: Dune (14.99), Sapiens (19.99), Great Gatsby (12.99)
db.books.updateMany(
  { price: { $lt: 20 } },
  { $set: { onSale: true } }
)

// 4. Remove the onSale field from The Great Gatsby using $unset
// The value "" is conventional — any value works with $unset
db.books.updateOne(
  { title: "The Great Gatsby" },
  { $unset: { onSale: "" } }
)

// 5. Delete Clean Code
db.books.deleteOne({ title: "Clean Code" })

// 6. Delete all unavailable books (available: false)
// Sapiens is the only one — deleteMany handles multiple matches safely
db.books.deleteMany({ available: false })

// 7. Confirm final state — 3 books remain:
// The Pragmatic Programmer, Dune (with onSale: true), The Great Gatsby
db.books.find()

// Bonus: verify count
db.books.countDocuments({})
// Expected: 3
