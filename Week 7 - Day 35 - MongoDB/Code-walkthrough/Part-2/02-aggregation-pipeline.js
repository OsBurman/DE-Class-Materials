// =============================================================================
// Day 35 — MongoDB Part 2: Aggregation Framework & Pipeline
// Bookstore Application
//
// Run these commands in mongosh: use bookstore_db
// Assumes seed data from 01-crud-and-query-operators.js is loaded.
//
// Topics covered:
//   1. What is the aggregation pipeline and why use it?
//   2. $match — filter documents (like WHERE)
//   3. $group — group and aggregate (like GROUP BY)
//   4. $project — reshape output (like SELECT)
//   5. $sort — order results
//   6. $limit and $skip — pagination
//   7. $unwind — flatten arrays
//   8. $lookup — join with another collection
//   9. Multi-stage pipeline examples
// =============================================================================

use bookstore_db

// =============================================================================
// SECTION 1: What Is the Aggregation Pipeline?
// =============================================================================
//
// The aggregation pipeline is a sequence of stages that process documents.
// Each stage receives documents, transforms them, and passes results to the next.
//
// Think of it like a Unix pipe:
//   collection  →  $match  →  $group  →  $project  →  $sort  →  result
//
// SQL equivalent:
//   SELECT genre, AVG(price), COUNT(*)
//   FROM books
//   WHERE inStock = true
//   GROUP BY genre
//   ORDER BY AVG(price) DESC
//
// MongoDB equivalent:
//   db.books.aggregate([
//     { $match: { inStock: true } },
//     { $unwind: "$genres" },
//     { $group: { _id: "$genres", avgPrice: { $avg: "$price" }, count: { $sum: 1 } } },
//     { $sort: { avgPrice: -1 } }
//   ])

// =============================================================================
// SECTION 2: $match — Filter Documents (like SQL WHERE)
// =============================================================================

// $match always comes first (or early) in the pipeline — filter early to reduce work
db.books.aggregate([
  { $match: { inStock: true } }
])
// Returns only in-stock books — identical to db.books.find({ inStock: true })

// $match with multiple conditions
db.books.aggregate([
  { $match: {
      inStock: true,
      price: { $lt: 40 },
      rating: { $gte: 4.5 }
  }}
])

// =============================================================================
// SECTION 3: $group — Aggregate Data (like SQL GROUP BY)
// =============================================================================
//
// $group syntax:
//   { $group: { _id: <expression>,    ← the field(s) to group by
//               <field>: { <accumulator>: <expression> } } }
//
// Accumulators:  $sum, $avg, $min, $max, $count, $push, $addToSet, $first, $last

// Count all books (group by null = entire collection)
db.books.aggregate([
  { $group: {
      _id: null,
      totalBooks: { $sum: 1 },           // count documents
      avgPrice: { $avg: "$price" },       // average price
      maxRating: { $max: "$rating" },     // highest rating
      totalReviews: { $sum: "$reviewCount" }
  }}
])

// Group by inStock status — count and average price per group
db.books.aggregate([
  { $group: {
      _id: "$inStock",                   // group by the inStock field
      count: { $sum: 1 },
      avgPrice: { $avg: "$price" },
      titles: { $push: "$title" }        // collect all titles into an array
  }}
])
// Two groups: { _id: true, count: 6, ... } and { _id: false, count: 1, ... }

// Group by published year with multiple aggregations
db.books.aggregate([
  { $group: {
      _id: "$publishedYear",
      booksPublished: { $sum: 1 },
      avgRating: { $avg: "$rating" },
      cheapestBook: { $min: "$price" }
  }},
  { $sort: { _id: 1 } }                 // sort by year ascending
])

// =============================================================================
// SECTION 4: $project — Reshape Output (like SQL SELECT)
// =============================================================================
//
// 1 = include field,  0 = exclude field
// You can also compute new fields in $project

// Include only specific fields
db.books.aggregate([
  { $project: {
      _id: 0,
      title: 1,
      price: 1,
      rating: 1
  }}
])

// Compute a new field — discounted price
db.books.aggregate([
  { $project: {
      title: 1,
      originalPrice: "$price",
      discountedPrice: { $multiply: ["$price", 0.9] },   // 10% off
      authorName: "$author.name"                          // flatten nested field
  }}
])

// Combine $match and $project
db.books.aggregate([
  { $match: { inStock: true } },
  { $project: {
      _id: 0,
      title: 1,
      author: "$author.name",
      price: 1,
      rating: 1,
      pricePerPage: { $divide: ["$price", "$pageCount"] }  // price per page
  }}
])

// =============================================================================
// SECTION 5: $sort, $limit, $skip
// =============================================================================

// Sort all books by rating descending, then title ascending
db.books.aggregate([
  { $sort: { rating: -1, title: 1 } }
])

// Top 3 most expensive books
db.books.aggregate([
  { $sort: { price: -1 } },
  { $limit: 3 }
])

// Pagination — page 2 with page size 3
db.books.aggregate([
  { $sort: { title: 1 } },
  { $skip: 3 },
  { $limit: 3 }
])

// =============================================================================
// SECTION 6: $unwind — Flatten Arrays
// =============================================================================
//
// $unwind "deconstructs" an array field:
// Each array element becomes a SEPARATE document.
//
// Before $unwind:
//   { title: "Clean Code", genres: ["programming", "software-engineering"] }
//
// After { $unwind: "$genres" }:
//   { title: "Clean Code", genres: "programming" }
//   { title: "Clean Code", genres: "software-engineering" }

// Unwind genres and count books per genre
db.books.aggregate([
  { $unwind: "$genres" },               // one doc per genre per book
  { $group: {
      _id: "$genres",                   // group by the single genre value
      count: { $sum: 1 },
      avgPrice: { $avg: "$price" },
      books: { $push: "$title" }
  }},
  { $sort: { count: -1 } }             // most common genres first
])
// Output: [
//   { _id: "programming", count: 2, avgPrice: 38.99, books: ["Clean Code", "The Pragmatic..."] }
//   { _id: "non-fiction",  count: 2, ... }
//   ...
// ]

// Unwind with preserveNullAndEmptyArrays (keep docs without the array field)
db.books.aggregate([
  { $unwind: { path: "$genres", preserveNullAndEmptyArrays: true } }
])

// =============================================================================
// SECTION 7: $lookup — Join With Another Collection
// =============================================================================
//
// First, create an orders collection with references to customers
db.customers.insertMany([
  { _id: ObjectId("aaa111aaa111aaa111aaa111"), name: "Alice Johnson", email: "alice@example.com", isPremium: true },
  { _id: ObjectId("bbb222bbb222bbb222bbb222"), name: "Bob Smith", email: "bob@example.com", isPremium: false }
])

db.orders.insertMany([
  {
    customerId: ObjectId("aaa111aaa111aaa111aaa111"),
    orderDate: new Date("2024-01-15"),
    items: [
      { bookIsbn: "978-0132350884", title: "Clean Code", price: 35.99, qty: 1 },
      { bookIsbn: "978-0135957059", title: "The Pragmatic Programmer", price: 42.00, qty: 2 }
    ],
    totalAmount: 119.99,
    status: "delivered"
  },
  {
    customerId: ObjectId("bbb222bbb222bbb222bbb222"),
    orderDate: new Date("2024-02-03"),
    items: [
      { bookIsbn: "978-0735211292", title: "Atomic Habits", price: 18.99, qty: 1 }
    ],
    totalAmount: 18.99,
    status: "shipped"
  },
  {
    customerId: ObjectId("aaa111aaa111aaa111aaa111"),
    orderDate: new Date("2024-02-20"),
    items: [
      { bookIsbn: "978-1449373320", title: "Designing Data-Intensive Applications", price: 55.00, qty: 1 }
    ],
    totalAmount: 55.00,
    status: "processing"
  }
])

// $lookup — join orders with customers
db.orders.aggregate([
  {
    $lookup: {
      from: "customers",              // collection to join with
      localField: "customerId",       // field in orders
      foreignField: "_id",            // field in customers
      as: "customerInfo"              // name of the new array field added
    }
  },
  { $project: {
      orderDate: 1,
      totalAmount: 1,
      status: 1,
      "customerInfo.name": 1,
      "customerInfo.isPremium": 1,
      items: 1
  }}
])
// Each order document now has a "customerInfo" array with the matching customer

// $unwind after $lookup to flatten the single-element array
db.orders.aggregate([
  {
    $lookup: {
      from: "customers",
      localField: "customerId",
      foreignField: "_id",
      as: "customer"
    }
  },
  { $unwind: "$customer" },           // flatten [customerObj] → customerObj
  { $project: {
      _id: 0,
      "customer.name": 1,
      "customer.isPremium": 1,
      orderDate: 1,
      totalAmount: 1,
      status: 1
  }}
])

// =============================================================================
// SECTION 8: Multi-Stage Pipeline — Real Business Queries
// =============================================================================

// ── 8a: Total revenue and order count per customer ───────────────────────
db.orders.aggregate([
  { $lookup: {
      from: "customers",
      localField: "customerId",
      foreignField: "_id",
      as: "customer"
  }},
  { $unwind: "$customer" },
  { $group: {
      _id: "$customerId",
      customerName: { $first: "$customer.name" },
      totalOrders: { $sum: 1 },
      totalSpent: { $sum: "$totalAmount" },
      avgOrderValue: { $avg: "$totalAmount" }
  }},
  { $sort: { totalSpent: -1 } }
])

// ── 8b: Genre popularity report ──────────────────────────────────────────
// Show each genre with average price and review count
db.books.aggregate([
  { $match: { inStock: true } },             // only in-stock books
  { $unwind: "$genres" },                    // one doc per genre
  { $group: {
      _id: "$genres",
      bookCount: { $sum: 1 },
      avgPrice: { $avg: "$price" },
      avgRating: { $avg: "$rating" },
      totalReviews: { $sum: "$reviewCount" }
  }},
  { $project: {
      genre: "$_id",
      bookCount: 1,
      avgPrice: { $round: ["$avgPrice", 2] },  // round to 2 decimal places
      avgRating: { $round: ["$avgRating", 1] },
      totalReviews: 1,
      _id: 0
  }},
  { $sort: { avgRating: -1 } }
])

// ── 8c: Find customers who spent more than $50 total ─────────────────────
db.orders.aggregate([
  { $group: {
      _id: "$customerId",
      totalSpent: { $sum: "$totalAmount" }
  }},
  { $match: { totalSpent: { $gt: 50 } } },   // filter AFTER grouping
  { $lookup: {
      from: "customers",
      localField: "_id",
      foreignField: "_id",
      as: "customer"
  }},
  { $unwind: "$customer" },
  { $project: {
      _id: 0,
      customerName: "$customer.name",
      totalSpent: 1
  }}
])
