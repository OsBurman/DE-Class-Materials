// =============================================================================
// Day 35 — MongoDB Part 2: Indexes & MongoDB Atlas Overview
// Bookstore Application
//
// Run these commands in mongosh: use bookstore_db
//
// Topics covered:
//   1. Why indexes matter — the collection scan problem
//   2. Creating single-field indexes
//   3. Compound indexes (multiple fields)
//   4. Text indexes for full-text search
//   5. explain() — understanding query performance
//   6. Index management (listing and dropping)
//   7. MongoDB Atlas — cloud-hosted MongoDB overview
// =============================================================================

use bookstore_db

// =============================================================================
// SECTION 1: Why Indexes Matter
// =============================================================================
//
// Without an index, MongoDB performs a COLLECTION SCAN:
//   → Read every single document in the collection
//   → Compare each document against your filter
//   → Return matches
//
// For 100 books: fine. For 10 million products: catastrophically slow.
//
// An index is a sorted data structure (B-tree) on one or more fields.
// MongoDB follows the index to find matching documents directly.
//
// SQL equivalent: CREATE INDEX ON books(title)
//
// The tradeoff:
//   ✅ Reads become much faster
//   ⚠️ Writes become slightly slower (index must be updated on every insert/update)
//   ⚠️ Indexes consume memory and disk space

// =============================================================================
// SECTION 2: The Default Index — _id
// =============================================================================

// Every collection automatically has an index on _id
// You never need to create this — MongoDB does it for you
db.books.getIndexes()
// Output: [ { v: 2, key: { _id: 1 }, name: "_id_" } ]
// This is why findOne({ _id: ObjectId("...") }) is always fast

// =============================================================================
// SECTION 3: Single-Field Index
// =============================================================================

// ── 3a: See query WITHOUT an index ────────────────────────────────────────
// explain("executionStats") shows HOW MongoDB executed the query
db.books.find({ title: "Clean Code" }).explain("executionStats")
// Look for:
//   "stage": "COLLSCAN"     ← full collection scan (BAD for large collections)
//   "totalDocsExamined": 7  ← scanned all 7 documents to find 1 match

// ── 3b: Create an index on the title field ────────────────────────────────
// 1 = ascending index, -1 = descending index
db.books.createIndex({ title: 1 })
// Output: title_1    ← the auto-generated index name

// ── 3c: See query WITH the index ──────────────────────────────────────────
db.books.find({ title: "Clean Code" }).explain("executionStats")
// Now look for:
//   "stage": "IXSCAN"       ← index scan (FAST)
//   "totalDocsExamined": 1  ← scanned only 1 document — the exact match

// ── 3d: More single-field indexes ─────────────────────────────────────────
db.books.createIndex({ price: 1 })          // ascending — efficient for range queries
db.books.createIndex({ rating: -1 })        // descending — efficient for "top rated" queries
db.books.createIndex({ isbn: 1 }, { unique: true })   // unique constraint on isbn
// unique: true prevents duplicate ISBNs — MongoDB throws an error on duplicate insert

// Index on nested field using dot notation
db.books.createIndex({ "author.name": 1 })

// =============================================================================
// SECTION 4: Compound Index (Multiple Fields)
// =============================================================================
//
// A compound index covers queries that filter or sort on multiple fields.
// Rule: create a compound index matching the field order and sort direction of your query.

// Index for "find in-stock books sorted by price"
db.books.createIndex({ inStock: 1, price: 1 })

// Now this query uses the compound index — no collection scan
db.books.find({ inStock: true }).sort({ price: 1 }).explain("executionStats")

// Another compound index: genre + rating (for genre browsing sorted by rating)
db.books.createIndex({ genres: 1, rating: -1 })

// ⚠️ Index prefix rule: a compound index on {a, b, c} can serve queries on:
//    {a}, {a, b}, {a, b, c}   — but NOT on {b} or {c} alone
//    So { inStock: 1, price: 1 } helps queries filtering on inStock,
//    but NOT queries filtering on price alone.

// =============================================================================
// SECTION 5: Text Index — Full-Text Search
// =============================================================================
//
// Text indexes allow full-text search on string fields.
// Only ONE text index per collection is allowed.

// Create a text index on title and genres
db.books.createIndex({ title: "text", genres: "text" })
// "text" (not 1 or -1) signals a text index

// Full-text search using $text and $search
db.books.find({ $text: { $search: "pragmatic programmer" } })
db.books.find({ $text: { $search: "data" } })

// Text search is case-insensitive and ignores stop words (a, the, and, etc.)
db.books.find({ $text: { $search: "the clean" } })  // finds "Clean Code" — ignores "the"

// Sort by text relevance score
db.books.find(
  { $text: { $search: "programming software" } },
  { score: { $meta: "textScore" }, title: 1, _id: 0 }
).sort({ score: { $meta: "textScore" } })
// Returns most relevant matches first

// =============================================================================
// SECTION 6: explain() — Analyzing Query Performance
// =============================================================================

// explain() levels:
//   "queryPlanner"     — what plan MongoDB chose (default)
//   "executionStats"   — how the plan actually ran (how many docs scanned, time)
//   "allPlansExecution" — compared all candidate plans

// Check if a query uses an index
db.books.find({ price: { $gt: 30 } }).explain("executionStats")
// Key fields to look for:
//   winningPlan.stage: "IXSCAN" (good) vs "COLLSCAN" (bad)
//   totalDocsExamined: should be close to nReturned
//   executionTimeMillis: query execution time in ms

// Hint — force a specific index (for testing)
db.books.find({ price: { $gt: 30 } }).hint({ price: 1 }).explain("executionStats")

// =============================================================================
// SECTION 7: Index Management
// =============================================================================

// List all indexes on a collection
db.books.getIndexes()

// Get index sizes
db.books.stats().indexSizes

// Drop a specific index by name
db.books.dropIndex("title_1")

// Drop ALL indexes except _id
db.books.dropIndexes()

// =============================================================================
// SECTION 8: MongoDB Atlas — Cloud-Hosted MongoDB
// =============================================================================
//
// MongoDB Atlas is the managed cloud service for MongoDB.
// Instead of running mongod on your own server, Atlas runs it for you.
//
// ─── What Atlas Provides ──────────────────────────────────────────────────
//
//   ✅ Fully managed: automatic backups, patching, monitoring, failover
//   ✅ Multi-cloud: runs on AWS, Azure, or GCP
//   ✅ Global clusters: replicate data across regions for low-latency reads
//   ✅ Free tier (M0): 512 MB storage — perfect for dev/learning projects
//   ✅ Atlas Search: full-text search powered by Lucene (more powerful than text indexes)
//   ✅ Atlas Vector Search: vector store for AI/RAG applications
//   ✅ Atlas Data API: REST API to query MongoDB without a driver
//
// ─── Getting Started with Atlas (Free) ───────────────────────────────────
//
//   1. Sign up at cloud.mongodb.com
//   2. Create a free cluster (M0 Sandbox)
//   3. Create a database user and set a password
//   4. Allow your IP address in Network Access
//   5. Get your connection string:
//        mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/
//   6. Connect via mongosh:
//        mongosh "mongodb+srv://user:password@cluster0.xxxxx.mongodb.net/bookstore_db"
//   7. Or add the connection string to Spring Boot application.properties
//
// ─── Spring Boot connection to Atlas ─────────────────────────────────────
//
//   # application.properties
//   spring.data.mongodb.uri=mongodb+srv://user:${MONGO_PASSWORD}@cluster0.xxxxx.mongodb.net/bookstore_db
//
// ─── Atlas vs Self-Hosted Decision ───────────────────────────────────────
//
//   Use Atlas when:
//     • You don't want to manage infrastructure
//     • You need built-in replication and failover
//     • You want Atlas Search, Vector Search, or Charts
//     • Startup/prototyping — free tier is instant
//
//   Self-host when:
//     • Cost at very high scale
//     • Data sovereignty / compliance requirements
//     • Full control over hardware and configuration
