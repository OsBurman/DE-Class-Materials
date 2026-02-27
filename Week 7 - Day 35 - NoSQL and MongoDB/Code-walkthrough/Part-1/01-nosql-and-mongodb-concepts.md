# Day 35 — MongoDB | Part 1: NoSQL & MongoDB Concepts
## Bookstore Application Context

---

## SECTION 1: NoSQL Database Overview and Types

### What Is NoSQL?

**NoSQL** = "Not Only SQL" — a family of databases that store data in formats other than relational tables.

Born out of the need to handle:
- **Massive scale** (billions of users, petabytes of data)
- **Flexible schemas** (data that evolves rapidly)
- **High velocity** (real-time writes from millions of concurrent users)

Traditional SQL databases were designed in the 1970s when data was structured and predictable. Modern applications — social media feeds, product catalogs, IoT sensor streams — require something different.

---

### The Four Types of NoSQL Databases

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        NoSQL Database Types                             │
├──────────────────┬──────────────────────────────┬──────────────────────┤
│ Type             │ How Data Is Stored            │ Examples             │
├──────────────────┼──────────────────────────────┼──────────────────────┤
│ Document Store   │ JSON/BSON documents           │ MongoDB, CouchDB,    │
│                  │ (nested, flexible structure)  │ Firestore            │
├──────────────────┼──────────────────────────────┼──────────────────────┤
│ Key-Value Store  │ Simple key → value pairs      │ Redis, DynamoDB,     │
│                  │ (fastest lookups)             │ Riak                 │
├──────────────────┼──────────────────────────────┼──────────────────────┤
│ Column-Family    │ Rows + dynamic columns        │ Apache Cassandra,    │
│                  │ (optimized for wide rows)     │ HBase, ScyllaDB      │
├──────────────────┼──────────────────────────────┼──────────────────────┤
│ Graph Database   │ Nodes and edges               │ Neo4j, Amazon        │
│                  │ (relationships first)         │ Neptune, ArangoDB    │
└──────────────────┴──────────────────────────────┴──────────────────────┘
```

**MongoDB** is a **Document Store** — the most popular NoSQL database in the world and the one we use with Spring Boot.

---

## SECTION 2: SQL vs NoSQL Comparison

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SQL vs NoSQL Comparison                             │
├──────────────────────┬──────────────────────────┬──────────────────────────┤
│ Feature              │ SQL (PostgreSQL, MySQL)   │ NoSQL (MongoDB)          │
├──────────────────────┼──────────────────────────┼──────────────────────────┤
│ Data Structure       │ Tables with rows/columns  │ Collections of documents │
│ Schema               │ Fixed schema, must define │ Flexible — each document │
│                      │ columns before inserting  │ can have different fields│
│ Relationships        │ Foreign keys + JOINs      │ Embedded docs or $lookup │
│ Query Language       │ SQL (standardized)        │ MongoDB Query Language   │
│ Scaling              │ Vertical (bigger server)  │ Horizontal (more servers)│
│ ACID Transactions    │ Full ACID out of the box  │ Single-doc ACID; multi-  │
│                      │                          │ doc since MongoDB 4.0    │
│ Best For             │ Financial data, ERP,      │ Catalogs, user profiles, │
│                      │ complex reporting         │ content, IoT, real-time  │
│ Schema Changes       │ ALTER TABLE migrations    │ Just add a field — done  │
└──────────────────────┴──────────────────────────┴──────────────────────────┘
```

### When to Choose MongoDB Over SQL

✅ Use MongoDB when:
- Data structure varies per record (e.g., each product has different attributes)
- You're storing hierarchical/nested data (orders with line items)
- You need to scale horizontally across many servers
- Schema evolves frequently (startup/prototyping phase)
- You're building a catalog, CMS, user profile system, or real-time feed

✅ Stick with SQL when:
- Data is highly relational with complex multi-table queries
- You need strict ACID guarantees (banking, accounting)
- Your team already has deep SQL expertise
- You're doing complex aggregate reporting

**The reality:** Most modern applications use BOTH — SQL for transactional data, MongoDB for flexible/document data.

---

## SECTION 3: MongoDB Architecture and Concepts

```
┌─────────────────────────────────────────────────────────────┐
│                    MongoDB Architecture                      │
│                                                             │
│  mongod (server process)                                    │
│  ├── bookstore_db          ← Database                       │
│  │   ├── books             ← Collection (like a SQL table)  │
│  │   │   ├── { _id: ..., title: "Clean Code", ... }        │
│  │   │   ├── { _id: ..., title: "Pragmatic Programmer" }   │
│  │   │   └── { _id: ..., title: "Design Patterns", ... }   │
│  │   ├── customers                                          │
│  │   │   └── { _id: ..., name: "Alice", orders: [...] }    │
│  │   └── orders                                             │
│  │       └── { _id: ..., customerId: ..., items: [...] }   │
│  └── admin_db                                               │
│                                                             │
│  mongosh  ← Shell client (connects to mongod)               │
│  Compass  ← GUI client                                      │
│  Drivers  ← Java, Node, Python, etc. connect your app       │
└─────────────────────────────────────────────────────────────┘
```

### Key Architecture Components

| Component | Description |
|-----------|-------------|
| **mongod** | The MongoDB server daemon — this is the database process |
| **mongosh** | Interactive MongoDB shell — the CLI you use to interact with mongod |
| **Database** | A named container holding collections (like a SQL schema/database) |
| **Collection** | A group of documents (like a SQL table, but schema-free) |
| **Document** | A single JSON/BSON record (like a SQL row, but nested and flexible) |
| **Driver** | Language-specific library to connect your app to MongoDB |
| **Atlas** | MongoDB's managed cloud hosting service |

---

## SECTION 4: Document-Oriented Data Model

### SQL Row vs MongoDB Document

**In SQL (PostgreSQL):**
```sql
-- books table — rigid, flat
CREATE TABLE books (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255),
    author      VARCHAR(255),
    genre       VARCHAR(100),
    price       DECIMAL(10,2),
    in_stock    BOOLEAN
);
-- Every book must have exactly these columns. No more, no less.
```

**In MongoDB:**
```json
// books collection — flexible, nested
{
  "_id": ObjectId("64a1f2e3b4c5d6e7f8091234"),
  "title": "Clean Code",
  "author": {
    "name": "Robert C. Martin",
    "nationality": "American"
  },
  "genres": ["programming", "software-engineering"],
  "price": 35.99,
  "inStock": true,
  "editions": [
    { "year": 2008, "format": "hardcover", "isbn": "978-0132350884" },
    { "year": 2009, "format": "paperback", "isbn": "978-0132350885" }
  ],
  "tags": ["clean-code", "java", "best-practices"],
  "rating": 4.7,
  "reviewCount": 12483
}
```

### The Power of the Document Model

1. **Nested objects** — `author` is an embedded object, not a separate table
2. **Arrays** — `genres`, `tags`, `editions` are arrays right inside the document
3. **No JOIN needed** — all book data lives in one document; one query retrieves everything
4. **Variable structure** — a digital ebook might have `downloadUrl` field; a physical book has `weight`. Both can live in the same collection
5. **Schema-free** — add a `discountPrice` field to some books without an `ALTER TABLE`

### Embedded vs Referenced Documents

```
Embedded (denormalized) — all data in one document:
┌──────────────────────────────────┐
│ order document                   │
│ ├── _id                          │
│ ├── customerId                   │
│ └── items: [                     │
│     { title: "Clean Code",       │
│       price: 35.99, qty: 1 }     │
│     { title: "Pragmatic...",     │
│       price: 42.00, qty: 2 }     │
│   ]                              │
└──────────────────────────────────┘
✅ One query to read everything
✅ Atomic updates on the order
⚠️  Data duplication (book title copied)

Referenced (normalized) — like SQL foreign key:
┌──────────────────────────────────┐
│ order document                   │
│ ├── _id                          │
│ ├── customerId: ObjectId(...)    │
│ └── items: [                     │
│     { bookId: ObjectId(...),     │
│       price: 35.99, qty: 1 }     │
│   ]                              │
└──────────────────────────────────┘
✅ No data duplication
⚠️  Requires $lookup (like a JOIN) to get book details
```

**Rule of thumb:** Embed when you always read the data together. Reference when data is shared across many documents or grows unboundedly.

---

## SECTION 5: BSON — Binary JSON

**JSON** = JavaScript Object Notation — human-readable text format
**BSON** = Binary JSON — what MongoDB actually stores on disk

```
JSON (what you write):            BSON (what MongoDB stores on disk):
{                                 ┌────────────────────────────────────┐
  "title": "Clean Code",          │ Binary encoded, compressed         │
  "price": 35.99,                 │ Supports additional types:         │
  "inStock": true                 │  • ObjectId (12-byte unique ID)    │
}                                 │  • Date (real date type)           │
                                  │  • Int32, Int64, Decimal128        │
                                  │  • BinData (binary blobs)          │
                                  │  • Regular expressions             │
                                  └────────────────────────────────────┘
```

### Why BSON?

| Feature | JSON | BSON |
|---------|------|------|
| Human readable | ✅ Yes | ❌ No (binary) |
| Parse speed | Slower | Faster |
| Extra data types | ❌ No Date, Int types | ✅ ObjectId, Date, Int32/64 |
| Storage size | Larger text | Smaller binary |

When you work with MongoDB — in the shell, in your Java code, or via Compass — you always work in JSON syntax. MongoDB silently converts it to/from BSON storage internally.

---

## SECTION 6: Collections and Documents

```
SQL Concept      →    MongoDB Equivalent
────────────────────────────────────────
Database         →    Database
Table            →    Collection
Row              →    Document
Column           →    Field
Primary Key      →    _id field (ObjectId)
Index            →    Index
JOIN             →    $lookup aggregation stage
```

### Document Rules
- Every document **must** have an `_id` field (MongoDB auto-creates it if you don't provide one)
- Documents in the same collection can have **different fields** — no schema enforcement by default
- Maximum document size: **16 MB** (use GridFS for larger files like PDFs/images)
- Fields can contain strings, numbers, booleans, arrays, nested objects, dates, null, and binary

---

## SECTION 7: ObjectId and Document Identifiers

```
ObjectId("64a1f2e3b4c5d6e7f8091234")
         │        │        │    │
         └─4 byte └─5 byte └─3b └─ hex string (24 chars)
           Unix     random  increment
           timestamp machine
```

**ObjectId** is a 12-byte value that MongoDB generates automatically for `_id`:
- **4 bytes** — Unix timestamp (seconds since epoch) — means ObjectIds sort chronologically!
- **5 bytes** — Random machine/process identifier
- **3 bytes** — Auto-incrementing counter

**Why not use an auto-increment integer like SQL?**
- Auto-increment requires a central counter — doesn't work when scaling across many servers
- ObjectId is generated *locally* by each MongoDB driver/node without coordination
- Still globally unique across all servers in a cluster

### Custom _id Values
You can use your own `_id` if you prefer:
```json
{ "_id": "isbn-978-0132350884", "title": "Clean Code" }    // string ID
{ "_id": 42, "title": "Clean Code" }                       // integer ID
```
MongoDB does not require ObjectId — it just defaults to it when you don't specify `_id`.

---

## SECTION 8: Installation and Setup Reference

```bash
# ─── macOS (Homebrew) ──────────────────────────────────────────────────
brew tap mongodb/brew
brew install mongodb-community@7.0
brew services start mongodb-community@7.0

# ─── Ubuntu/Debian ────────────────────────────────────────────────────
# Import MongoDB GPG key
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor
# Add repo and install
sudo apt-get install -y mongodb-org
sudo systemctl start mongod
sudo systemctl enable mongod

# ─── Windows ──────────────────────────────────────────────────────────
# Download the .msi installer from mongodb.com/try/download/community
# Run installer → choose "Complete" → MongoDB installs as a Windows Service

# ─── Verify installation ──────────────────────────────────────────────
mongod --version        # check server version
mongosh --version       # check shell version

# ─── Connect with mongosh ─────────────────────────────────────────────
mongosh                             # connects to localhost:27017 (default)
mongosh "mongodb://localhost:27017" # explicit connection string
```

---

## SECTION 9: MongoDB Shell (mongosh) — Key Commands Reference

```javascript
// ─── Database management ────────────────────────────────────────────
show dbs                    // list all databases
use bookstore_db            // switch to (or create) a database
db                          // show current database name
db.dropDatabase()           // delete current database ⚠️

// ─── Collection management ──────────────────────────────────────────
show collections            // list collections in current db
db.createCollection("books")           // explicit collection creation
db.books.drop()                        // delete a collection ⚠️
db.getCollectionNames()               // programmatic collection list

// ─── Quick inserts to create a collection ──────────────────────────
// MongoDB creates a collection automatically on first insert
db.books.insertOne({ title: "Clean Code", author: "Robert C. Martin" })

// ─── Basic find ─────────────────────────────────────────────────────
db.books.find()             // all documents (returns cursor)
db.books.find().pretty()    // formatted output (mongosh formats by default)
db.books.countDocuments()   // count all docs in collection

// ─── Help ────────────────────────────────────────────────────────────
help                        // general help
db.help()                   // database-level methods
db.books.help()             // collection-level methods
```
