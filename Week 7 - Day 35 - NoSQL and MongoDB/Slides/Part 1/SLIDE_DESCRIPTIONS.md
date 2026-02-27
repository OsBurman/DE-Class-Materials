# Day 35 Part 1 — MongoDB: NoSQL Fundamentals, Document Model, and mongosh Basics
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 35 Part 1: MongoDB — The Document Database

**Subtitle:** NoSQL concepts, document model, BSON, collections, and the mongosh shell

**Learning Objectives:**
- Explain what NoSQL databases are and the four main categories
- Compare SQL and NoSQL across data model, schema, scaling, and use cases
- Describe MongoDB's architecture: mongod, replica sets, and sharding
- Understand the document-oriented data model and flexible schema
- Explain BSON and its type system
- Organize data into collections and databases
- Understand the ObjectId structure and how it is generated
- Install MongoDB locally and connect with mongosh
- Navigate databases and collections using the mongosh shell
- Understand the core design choice: embedding vs. referencing

---

### Slide 2 — What Is NoSQL? Four Categories

**Title:** NoSQL — Four Categories of Non-Relational Databases

**Content:**

"NoSQL" does not mean "no SQL at all" — it means "not only SQL." It is a broad term covering database systems that depart from the relational model of tables, rows, and fixed schemas.

NoSQL databases emerged around 2007–2010 when large-scale web companies (Amazon, Google, Facebook) found that relational databases couldn't scale horizontally fast enough to meet their needs. They built different systems optimized for different problem shapes.

**The Four Main Categories:**

| Category | Storage Model | Examples | Best For |
|----------|--------------|---------|---------|
| **Document** | JSON-like documents | MongoDB, CouchDB, Firestore | Content, catalogs, user profiles, hierarchical data |
| **Key-Value** | Key → Value (blob) | Redis, DynamoDB, Riak | Sessions, caches, shopping carts, leaderboards |
| **Column-Family** | Rows with column families | Apache Cassandra, HBase | Time-series, IoT, high-write analytics |
| **Graph** | Nodes + Edges | Neo4j, Amazon Neptune | Social networks, recommendations, fraud detection |

**Why So Many Types?**

Each NoSQL category was optimized for a specific access pattern. A social graph doesn't fit naturally into a table. Time-series sensor data doesn't need ACID transactions. A product catalog with deeply nested variant attributes is awkward in columns. NoSQL categories match the data structure to the problem structure.

**Today's Focus:** MongoDB — the most widely used document database, and the category most relevant to full-stack development with Spring Boot.

---

### Slide 3 — SQL vs. NoSQL — When to Use Which

**Title:** SQL vs. NoSQL — A Practical Comparison

**Content:**

Neither is universally better. The right choice depends on your data shape, access patterns, consistency requirements, and team expertise.

**Comparison Table:**

| Dimension | SQL (Relational) | NoSQL (Document — MongoDB) |
|-----------|-----------------|---------------------------|
| **Data model** | Tables, rows, columns | JSON-like documents |
| **Schema** | Fixed, enforced at DB level | Flexible, document-level |
| **Relationships** | JOINs between normalized tables | Embedding or `$lookup` (no joins) |
| **Transactions** | ACID, multi-table | ACID at document level; multi-doc transactions since v4 |
| **Scaling** | Primarily vertical; sharding complex | Designed for horizontal sharding |
| **Query language** | SQL — declarative standard | MongoDB Query Language (MQL); JSON-based |
| **Consistency** | Strong by default | Configurable: strong (default w/ replica) or eventual |
| **Best for** | Financial systems, ERP, complex relations | Catalogs, content, user data, rapidly evolving schemas |

**When SQL Wins:**
- Complex relationships with many JOIN operations
- Strict ACID requirements (banking, payments)
- Well-defined, stable schema
- Strong reporting/analytics requirements (BI tools expect SQL)

**When MongoDB Wins:**
- Hierarchical or deeply nested data
- Schema varies per record (product variants, user-configurable fields)
- Read-heavy workloads needing fast document retrieval
- Need to iterate quickly on data model without migrations
- Horizontal scaling requirements

**Real-World Reality:** Most production architectures use both. A financial application might use PostgreSQL for transactions and MongoDB for a product catalog or user activity feed.

---

### Slide 4 — MongoDB Overview and Architecture

**Title:** MongoDB — Architecture Overview

**Content:**

MongoDB is an open-source, document-oriented database released in 2009 by MongoDB, Inc. It is the most popular NoSQL database (consistently #1 in DB-Engines document database rankings).

**Core Architectural Components:**

```
┌─────────────────────────────────────────┐
│              MongoDB Deployment          │
│                                         │
│  ┌──────────┐   ┌──────────┐           │
│  │ mongod   │   │ mongod   │  ←── Replica Set
│  │(Primary) │   │(Secondary)│          │
│  └──────────┘   └──────────┘           │
│        ↑                               │
│  ┌──────────┐                          │
│  │  mongos  │  ← Query Router          │
│  │  (shard  │    (for sharded clusters)│
│  │  router) │                          │
│  └──────────┘                          │
└─────────────────────────────────────────┘
```

**Key Processes:**

| Process | Role |
|---------|------|
| `mongod` | The core database server daemon — stores data, handles queries |
| `mongos` | Query router for sharded clusters — directs queries to correct shard |
| `mongosh` | The interactive shell — JavaScript/Node.js REPL for interacting with MongoDB |

**Replica Sets:**
A replica set is a group of `mongod` instances that maintain the same data. It provides:
- **High availability:** Automatic failover — if the primary goes down, a secondary is elected primary
- **Read scaling:** Applications can read from secondaries
- **Data redundancy:** Multiple copies of data on different hardware

Minimum recommended production configuration: **3-node replica set** (1 primary + 2 secondaries).

**Sharding:**
When a single replica set can't hold all data (or handle all write throughput), MongoDB distributes data across multiple replica sets (shards) based on a **shard key**. This is MongoDB's horizontal scaling mechanism. Covered at awareness level — production-level sharding is an advanced topic.

**Default Port:** `27017`

---

### Slide 5 — The Document-Oriented Data Model

**Title:** Documents, Not Rows — The Document Data Model

**Content:**

In a relational database, data lives in rows across normalized tables, connected by foreign keys. In MongoDB, data lives in **documents** — rich JSON-like objects that can contain nested objects, arrays, and arrays of objects.

**A Relational Approach — Author + Books (3 tables):**
```sql
authors:  id | name          | email
books:    id | title         | isbn         | author_id
reviews:  id | book_id       | reviewer     | rating | text
```

**The Same Data as a MongoDB Document:**
```json
{
  "_id": ObjectId("64f2a3b1c9e2f10012345678"),
  "name": "Robert Martin",
  "email": "uncle.bob@example.com",
  "books": [
    {
      "title": "Clean Code",
      "isbn": "978-0132350884",
      "reviews": [
        { "reviewer": "Alice", "rating": 5, "text": "Life changing." },
        { "reviewer": "Bob",   "rating": 4, "text": "Very practical." }
      ]
    },
    {
      "title": "The Clean Coder",
      "isbn": "978-0137081073",
      "reviews": []
    }
  ]
}
```

**Key Properties of Documents:**
- Documents are self-contained — everything about an author is in one document
- Arrays and nested objects are first-class citizens
- Documents in the same collection can have **different fields** — flexible schema
- Maximum document size: **16 MB**
- Documents are stored in BSON (Binary JSON) — more types than JSON

**The Core Trade-off:**
You trade JOIN operations (relational) for document completeness (document). Reading a full author with all books and reviews is one query to one document. But updating a reviewer's name across all reviews requires updating every document that contains it.

---

### Slide 6 — BSON — Binary JSON

**Title:** BSON — The Wire and Storage Format

**Content:**

BSON stands for **Binary JSON**. It is the format MongoDB uses to store documents on disk and transmit them over the network. You write JSON-like syntax in your queries and code; MongoDB converts it to BSON internally.

**Why BSON Instead of Plain JSON?**

| Feature | JSON | BSON |
|---------|------|------|
| Encoding | Text (UTF-8) | Binary |
| Traversal | Must parse full string | Length-prefixed — jump to any field |
| Types | String, Number, Boolean, Array, Object, Null | All JSON types + more |
| Extra types | ❌ | Date, ObjectId, Binary, Decimal128, Int32, Int64, Regex, Timestamp |
| Size | Slightly smaller for text | Slightly larger but much faster to process |

**BSON-Specific Types You'll Use:**

| BSON Type | JavaScript / Java Representation | Use |
|-----------|----------------------------------|-----|
| `ObjectId` | `ObjectId("...")` | Default `_id` field — 12-byte unique ID |
| `Date` | `ISODate("2024-01-15T10:30:00Z")` | Timestamps — store as Date, not String |
| `Int32` / `Int64` | `NumberInt(42)` / `NumberLong(9000000)` | Precise integers |
| `Decimal128` | `NumberDecimal("19.99")` | Financial/precise decimal values |
| `Binary` | `BinData(...)` | File content, encrypted data |
| `Regex` | `/pattern/flags` | Regular expression stored in document |
| `Boolean` | `true` / `false` | Boolean — NOT strings "true"/"false" |

**Most Important Rule — Store Dates as Dates, Not Strings:**
```javascript
// ❌ BAD — stored as string, can't sort or range query properly
{ "createdAt": "2024-01-15" }

// ✅ GOOD — stored as BSON Date, enables range queries and proper sorting
{ "createdAt": ISODate("2024-01-15T10:30:00Z") }
```

---

### Slide 7 — Databases, Collections, and Documents — The Hierarchy

**Title:** MongoDB's Three-Level Hierarchy

**Content:**

MongoDB organizes data in three levels: **Database → Collection → Document**. The analogy to relational concepts is approximate — the differences matter.

```
MongoDB                      Relational Equivalent
─────────────────────────    ─────────────────────────
Database                  ≈  Database / Schema
  └── Collection          ≈  Table (approximate)
        └── Document      ≈  Row (very approximate)
              └── Field   ≈  Column (but flexible)
```

**Databases:**
- A MongoDB server can host multiple databases
- Common convention: one database per application (`bookstoredb`, `ecommercedb`)
- System databases: `admin`, `config`, `local` — don't write application data to these
- Created implicitly when you first write a document to a collection within it

**Collections:**
- A collection groups related documents
- Unlike a table, a collection does **not** enforce a fixed schema
- Documents in the same collection can have completely different fields
- Created implicitly when you first insert into them
- Naming convention: **camelCase plural** — `books`, `authors`, `orderHistory`

**Documents:**
- The atomic unit of data
- JSON-like structure (stored as BSON)
- Every document must have a unique `_id` field (auto-generated as ObjectId if not provided)
- Maximum size: 16 MB
- Can nest other documents and arrays to any depth

**mongosh Hierarchy Navigation:**
```javascript
show dbs                    // list all databases
use bookstoredb             // switch to (or create) a database
show collections            // list collections in current database
db.books.countDocuments()   // count docs in the books collection
```

---

### Slide 8 — ObjectId — The Default Unique Identifier

**Title:** ObjectId — MongoDB's Default `_id`

**Content:**

Every MongoDB document **must** have a field called `_id`. If you don't provide one, MongoDB automatically generates a 12-byte `ObjectId`. It is globally unique — two ObjectIds generated anywhere in the world, at any time, will not collide.

**ObjectId Structure (12 bytes total):**
```
┌────────────────────────────────┐
│  4 bytes  │  5 bytes  │ 3 bytes│
│ Unix time │  Random   │ Counter│
│ (seconds) │  value    │        │
└────────────────────────────────┘

Example: 64f2a3b1  c9e2f10012  345678
          ↑           ↑           ↑
      Timestamp    Random      Counter
      (Aug 2023)
```

**What This Gives You:**
- **Roughly sortable by creation time** — higher ObjectId = more recent (to the second)
- **Globally unique** without a coordinating server (unlike auto-increment)
- **Can be generated client-side** — the application can generate an ObjectId before inserting and use it immediately

**Working with ObjectId in mongosh:**
```javascript
// Generate a new ObjectId
const id = new ObjectId()
id                              // ObjectId("64f2a3b1c9e2f10012345678")

// Extract the creation timestamp
id.getTimestamp()               // ISODate("2023-09-02T10:30:09.000Z")

// Query by _id — MUST use ObjectId(), not a plain string
db.books.findOne({ _id: ObjectId("64f2a3b1c9e2f10012345678") })
```

**Custom `_id` Values:**
You can provide your own `_id` value — any BSON type:
```javascript
// Using a meaningful string as _id (if you know it's globally unique)
db.config.insertOne({ _id: "app-settings", theme: "dark", language: "en" })

// Using an integer (for sequential IDs when you control them)
db.countries.insertOne({ _id: 1, name: "United States", code: "US" })
```

**Important:** Once set, `_id` cannot be updated. Design it carefully.

---

### Slide 9 — MongoDB Installation and Setup

**Title:** Installing MongoDB — Local and Docker Options

**Content:**

**Option 1: Docker (Recommended for Development)**

The fastest way to get MongoDB running without installing it on your OS:

```bash
# Pull and run MongoDB in a container
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  -v mongo-data:/data/db \
  mongo:7.0

# Verify it's running
docker ps
```

```yaml
# docker-compose.yml — for use with other services
services:
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
```

**Option 2: Homebrew (macOS)**
```bash
brew tap mongodb/brew
brew install mongodb-community@7.0
brew services start mongodb-community@7.0
```

**Option 3: MongoDB Atlas (Cloud — Free Tier)**
- Create a free account at `cloud.mongodb.com`
- Create a free M0 cluster (512 MB storage)
- No installation — MongoDB runs in the cloud
- Get a connection string: `mongodb+srv://user:pass@cluster.mongodb.net/dbname`
- Perfect for projects when you don't want to manage infrastructure

**Connecting with mongosh:**
```bash
# Install mongosh (if not bundled)
brew install mongosh               # macOS

# Connect to local instance (no auth)
mongosh

# Connect to local instance with auth
mongosh "mongodb://admin:password@localhost:27017/admin"

# Connect to Atlas
mongosh "mongodb+srv://user:pass@cluster.mongodb.net/bookstoredb"
```

---

### Slide 10 — mongosh Basics — Navigation and Help

**Title:** mongosh — The MongoDB Shell

**Content:**

`mongosh` is a Node.js-based JavaScript REPL for interacting with MongoDB. You can write JavaScript code directly in it — variables, loops, functions.

**Essential Navigation Commands:**
```javascript
help                        // show top-level help
db.help()                   // show database-level help
db.books.help()             // show collection-level help

show dbs                    // list all databases (shows only dbs with data)
use bookstoredb             // switch to bookstoredb (creates it on first write)
db                          // print the current database name

show collections            // list collections in current db
db.getCollectionNames()     // same as above, returns array

exit                        // quit mongosh (or Ctrl+D)
```

**The `db` Object:**
`db` is a special variable in mongosh that always refers to the current database. All collection operations go through `db`:
```javascript
db.books                    // reference to the books collection
db.books.find()             // query all documents in books
db["order-history"]         // bracket notation for collection names with hyphens
```

**Variable and JavaScript Usage:**
```javascript
// Assign to variables
const result = db.books.findOne({ title: "Clean Code" })
printjson(result)           // pretty-print a JSON object

// Loop over results
db.books.find().forEach(doc => print(doc.title))

// Count
db.books.countDocuments()

// Check mongosh version
mongosh --version
```

**mongosh vs. Old `mongo` Shell:**
The old `mongo` shell was deprecated in MongoDB 5.0 and removed in 6.0. `mongosh` is the modern replacement. It has better syntax highlighting, autocomplete, and editor support (`edit` command opens a document in `$EDITOR`).

---

### Slide 11 — mongosh — Creating Data and Exploring

**Title:** mongosh — First Inserts and Exploration

**Content:**

Let's create our first database, collection, and documents interactively in mongosh:

```javascript
// Switch to (or create) a new database
use bookstoredb

// Insert a single document — collection "books" is created automatically
db.books.insertOne({
  title: "Clean Code",
  author: "Robert Martin",
  isbn: "978-0132350884",
  price: 39.99,
  tags: ["java", "best-practices", "refactoring"],
  publishedDate: ISODate("2008-08-01"),
  available: true
})
// Response: { acknowledged: true, insertedId: ObjectId("...") }

// Insert multiple documents
db.books.insertMany([
  {
    title: "Refactoring",
    author: "Martin Fowler",
    isbn: "978-0201485677",
    price: 44.99,
    tags: ["refactoring", "design"],
    publishedDate: ISODate("1999-07-08"),
    available: true
  },
  {
    title: "The Pragmatic Programmer",
    author: "David Thomas",
    isbn: "978-0135957059",
    price: 49.99,
    tags: ["career", "best-practices"],
    publishedDate: ISODate("2019-09-13"),
    available: false
  }
])

// Query all documents
db.books.find()

// Pretty-print with formatting
db.books.find().pretty()      // mongosh auto-prettifies; explicit in older tools

// Count documents in the collection
db.books.countDocuments()     // 3

// Look at the collection's stats
db.books.stats()
```

**What Happens If You Insert a Document Without `_id`?**
MongoDB automatically adds `_id: ObjectId("...")`. You can confirm: `db.books.findOne({}, { _id: 1 })` — the `_id` field is always there.

---

### Slide 12 — Embedding vs. Referencing — Schema Design Fundamentals

**Title:** Schema Design: Embedding vs. Referencing

**Content:**

MongoDB gives you a choice that SQL doesn't: should related data live inside a single document (embedding) or in separate collections connected by an ID (referencing)?

**Embedding — Data Nested Inside the Document:**
```json
// Author document with books embedded
{
  "_id": ObjectId("..."),
  "name": "Robert Martin",
  "books": [
    { "title": "Clean Code", "isbn": "978-0132350884" },
    { "title": "The Clean Coder", "isbn": "978-0137081073" }
  ]
}
```
**Pros:** One query fetches everything. Fast reads. Atomic updates on the document.
**Cons:** Document grows unboundedly if the array is large. Data is duplicated if shared across documents. 16 MB limit.

**Referencing — Separate Collections Connected by ID:**
```json
// authors collection
{ "_id": ObjectId("aut1"), "name": "Robert Martin" }

// books collection — author is a reference
{ "_id": ObjectId("bk1"), "title": "Clean Code", "authorId": ObjectId("aut1") }
{ "_id": ObjectId("bk2"), "title": "The Clean Coder", "authorId": ObjectId("aut1") }
```
**Pros:** No data duplication. No 16 MB concern for unbounded arrays. Each entity is independently updatable.
**Cons:** Requires multiple queries or `$lookup` aggregation (similar to SQL JOIN). More complex code.

**The Decision Rules:**

| Embed When... | Reference When... |
|--------------|------------------|
| Data is accessed together and rarely separately | Data is accessed independently |
| The "many" side is bounded and small (e.g., 5–20 items) | The "many" side is large or unbounded |
| Data doesn't need to be updated independently | Data is shared across many documents |
| One-to-few relationship (order → line items) | One-to-many or many-to-many |

**The MongoDB Mantra:** "Data that is accessed together should be stored together."

---

### Slide 13 — Practical Schema Design Examples

**Title:** Schema Design in Practice — Three Common Patterns

**Content:**

**Pattern 1 — E-Commerce Order (Embed line items):**
```json
{
  "_id": ObjectId("..."),
  "customerId": ObjectId("..."),       // reference to customers collection
  "orderDate": ISODate("2024-01-15"),
  "status": "shipped",
  "shippingAddress": {                 // embedded — unique per order
    "street": "123 Main St",
    "city": "Boston",
    "state": "MA",
    "zip": "02101"
  },
  "lineItems": [                       // embedded — bounded, owned by this order
    { "sku": "BOOK-001", "title": "Clean Code", "qty": 1, "price": 39.99 },
    { "sku": "BOOK-007", "title": "Refactoring", "qty": 2, "price": 44.99 }
  ],
  "total": 129.97
}
```

**Pattern 2 — Blog Post with Comments (Embed small comments; reference for large):**
```json
{
  "_id": ObjectId("..."),
  "title": "Understanding MongoDB Schema Design",
  "authorId": ObjectId("..."),         // reference — author exists independently
  "body": "...",
  "tags": ["mongodb", "design"],
  "comments": [                        // embed if comment count is bounded (< 100)
    { "user": "alice", "text": "Great post!", "date": ISODate("2024-01-16") }
  ]
}
```

**Pattern 3 — Separate Collections When Array is Unbounded:**
```json
// books collection
{ "_id": ObjectId("bk1"), "title": "Clean Code", "authorId": ObjectId("aut1") }

// reviews collection — separate, references book by ID
{ "_id": ObjectId("..."), "bookId": ObjectId("bk1"), "rating": 5, "text": "..." }
{ "_id": ObjectId("..."), "bookId": ObjectId("bk1"), "rating": 4, "text": "..." }
// ... potentially thousands of reviews
```

**Rule of Thumb:** If you can say "this document contains AT MOST N things" and N is small (< 100), embed. If it's unbounded, reference.

---

### Slide 14 — mongosh Quick Reference and Part 1 Summary

**Title:** Part 1 Summary — MongoDB Foundations

**Content:**

**mongosh Shell Quick Reference:**
```javascript
// Database
show dbs                           // list databases
use <dbName>                       // switch database
db.dropDatabase()                  // delete current database (destructive!)

// Collections
show collections                   // list collections
db.createCollection("products")    // explicit creation (usually not needed)
db.<collection>.drop()             // delete a collection

// Documents — basics (full CRUD in Part 2)
db.<collection>.insertOne({...})
db.<collection>.insertMany([...])
db.<collection>.find()
db.<collection>.find().pretty()
db.<collection>.countDocuments()
db.<collection>.findOne({ field: value })
```

**Key Concepts Summary:**

| Concept | Key Takeaway |
|---------|-------------|
| NoSQL types | Document, Key-Value, Column-Family, Graph — different problems |
| SQL vs. MongoDB | SQL = normalized tables + JOINs; MongoDB = documents, embed or reference |
| MongoDB architecture | mongod (server) + replica sets + optional sharding |
| Document model | JSON-like, flexible schema, arrays and nested objects first-class |
| BSON | Binary JSON: faster traversal + extra types (ObjectId, Date, Decimal128) |
| Collections | Group related documents; no fixed schema enforced |
| ObjectId | 12-byte globally unique ID: timestamp + random + counter |
| Installation | Docker (`mongo:7.0`) or Homebrew or MongoDB Atlas (cloud) |
| Schema design | Embed (bounded, accessed together) vs. Reference (unbounded, shared) |

**Part 2 Preview:**
- Full CRUD operations in mongosh — `insert`, `find`, `update`, `delete`
- Query operators — `$gt`, `$in`, `$regex`, `$and`, `$elemMatch`
- Aggregation pipeline — `$match`, `$group`, `$project`, `$lookup`
- Indexes — creating, types, using `explain()`
- MongoDB Atlas — cloud setup and free tier
- Spring Data MongoDB — `@Document`, `MongoRepository`, `MongoTemplate`
