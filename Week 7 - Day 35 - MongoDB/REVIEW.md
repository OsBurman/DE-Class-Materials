# Day 35 — MongoDB Quick Reference & Review

---

## 1. The Four NoSQL Categories

| Category | Examples | Best For |
|----------|---------|---------|
| **Document** | MongoDB, CouchDB, Firestore | Hierarchical data, flexible schema, catalogs |
| **Key-Value** | Redis, DynamoDB | Sessions, caches, leaderboards |
| **Column-Family** | Cassandra, HBase | Time-series, IoT, high write throughput |
| **Graph** | Neo4j, Amazon Neptune | Social networks, recommendations, fraud detection |

---

## 2. SQL vs. MongoDB Comparison

| Dimension | SQL (PostgreSQL) | MongoDB |
|-----------|-----------------|---------|
| Data model | Tables, rows, columns | JSON-like documents |
| Schema | Fixed, DB-enforced | Flexible, per-document |
| Relationships | JOINs on FK | Embedding or `$lookup` |
| ACID | Full multi-table | Single-document native; multi-doc since v4.0 |
| Scaling | Vertical (bigger server) | Horizontal (sharding) |
| Best for | Financial, ERP, stable schema | Catalogs, content, evolving schemas |

---

## 3. MongoDB Architecture

- **`mongod`** — the database server daemon; default port `27017`
- **Replica Set** — 1 primary + N secondaries; automatic failover on primary failure
- **`mongos`** — query router for sharded clusters
- **WiredTiger** — default storage engine; document-level locking, compression
- **Minimum production setup** — 3-node replica set (1 primary + 2 secondaries)

---

## 4. Data Hierarchy

```
MongoDB              Relational (approximate)
────────────────     ────────────────────────
Database          ≈  Database / Schema
  └── Collection  ≈  Table (schema-free)
        └── Document ≈  Row (flexible)
              └── Field ≈  Column (variable)
```

---

## 5. BSON Types — The Important Ones

| BSON Type | mongosh / Java | When to Use |
|-----------|---------------|-------------|
| `ObjectId` | `ObjectId("...")` | Default `_id` — 12-byte unique ID |
| `Date` | `ISODate("2024-01-15T...")` | Always store dates as Date, NOT strings |
| `Decimal128` | `NumberDecimal("19.99")` | Financial/precise decimal values |
| `Int32` / `Int64` | `NumberInt(42)` / `NumberLong(...)` | Exact integers |
| `Boolean` | `true` / `false` | NOT the strings "true"/"false" |

---

## 6. ObjectId Structure

```
64f2a3b1    c9e2f10012   345678
   ↑              ↑          ↑
4-byte UTC    5-byte       3-byte
timestamp     random      counter

= 12 bytes total = 24 hex characters
```

- Globally unique without a central coordinator
- Contains a timestamp — can extract with `.getTimestamp()`
- Roughly sortable by creation time
- **Always use `ObjectId("...")` wrapper when querying by `_id` — string != ObjectId**

---

## 7. Installation Quick Reference

```bash
# Docker (recommended for dev)
docker run -d --name mongodb -p 27017:27017 -v mongo-data:/data/db mongo:7.0

# macOS Homebrew
brew tap mongodb/brew
brew install mongodb-community@7.0
brew services start mongodb-community@7.0

# Connect with mongosh
mongosh                                      # localhost:27017
mongosh "mongodb+srv://user:pass@cluster.mongodb.net/dbname"  # Atlas
```

---

## 8. mongosh Navigation Cheat Sheet

```javascript
show dbs                        // list all databases
use <dbName>                    // switch to (or create) database
db                              // print current database name
show collections                // list collections in current db
db.dropDatabase()               // delete entire database ⚠️
db.<col>.drop()                 // delete a collection ⚠️
db.<col>.countDocuments()       // count documents
db.<col>.stats()                // collection statistics
exit                            // quit mongosh
```

---

## 9. CRUD — Full Reference

### Create
```javascript
db.col.insertOne({ field: value, ... })
db.col.insertMany([{...}, {...}], { ordered: false })   // continue on error
```

### Read
```javascript
db.col.find({ filter }, { projection })
db.col.findOne({ filter })
db.col.find({ available: true }).sort({ price: 1 }).skip(20).limit(10)
db.col.countDocuments({ filter })
```

**Projection rules:**
- `{ field: 1 }` — include (cannot mix 1s and 0s except for `_id`)
- `{ field: 0 }` — exclude
- `{ field: 1, _id: 0 }` — include field, exclude `_id` (allowed exception)

### Update
```javascript
db.col.updateOne({ filter }, { $set: { field: value } })
db.col.updateMany({ filter }, { $inc: { qty: -1 } })
db.col.replaceOne({ filter }, { newDoc })   // ⚠️ replaces ALL fields except _id
db.col.findOneAndUpdate({ filter }, { update }, { returnDocument: "after" })
```

### Delete
```javascript
db.col.deleteOne({ filter })
db.col.deleteMany({ filter })        // {} deletes ALL documents ⚠️
```

---

## 10. Update Operators

| Operator | Purpose |
|----------|---------|
| `$set` | Set/add a field value |
| `$unset` | Remove a field entirely |
| `$inc` | Increment numeric (negative = decrement) |
| `$mul` | Multiply numeric |
| `$rename` | Rename a field |
| `$currentDate` | Set to current server timestamp |
| `$push` | Append to array |
| `$addToSet` | Append to array only if unique |
| `$pull` | Remove all matching elements from array |
| `$pop` | Remove first (`-1`) or last (`1`) array element |

---

## 11. Query Operators

### Comparison
```javascript
{ price: { $gt: 30, $lte: 50 } }            // range
{ author: { $ne: "Unknown" } }              // not equal
{ author: { $in: ["A", "B"] } }            // in set
{ tags: { $nin: ["outdated"] } }            // not in set
```

### Logical
```javascript
{ $and: [{ price: {$gt: 20} }, { available: true }] }
{ $or: [{ author: "Martin" }, { tags: "java" }] }
{ $nor: [{ available: false }, { stock: 0 }] }
{ price: { $not: { $gt: 50 } } }
```

### Element
```javascript
{ isbn: { $exists: true } }                 // field exists
{ price: { $type: "double" } }             // BSON type check
```

### Array
```javascript
{ tags: "java" }                                          // array contains value
{ tags: { $all: ["java", "best-practices"] } }            // contains ALL
{ tags: { $size: 3 } }                                    // exactly 3 elements
{ lineItems: { $elemMatch: { sku: "X", qty: {$gte: 2} } } } // single element matches all
```

### Text / Regex
```javascript
{ title: { $regex: /clean/i } }             // case-insensitive (can't use index)
{ title: { $regex: /^Clean/ } }             // anchored prefix (can use index)
{ $text: { $search: "clean code" } }        // full-text (requires text index)
```

---

## 12. Aggregation Pipeline

**Syntax:**
```javascript
db.collection.aggregate([stage1, stage2, ...])
```

**Stage Reference:**

| Stage | Purpose | SQL Equivalent |
|-------|---------|---------------|
| `$match` | Filter documents | `WHERE` |
| `$group` | Group and accumulate | `GROUP BY` |
| `$project` | Reshape fields | `SELECT ...` |
| `$sort` | Sort results | `ORDER BY` |
| `$limit` | Take first N | `LIMIT` |
| `$skip` | Skip N documents | `OFFSET` |
| `$lookup` | Join another collection | `LEFT JOIN` |
| `$unwind` | Deconstruct array → one doc per element | (no direct equivalent) |
| `$addFields` | Add computed fields (preserves existing) | `SELECT *, computed AS name` |
| `$count` | Output document count | `COUNT(*)` |

**`$group` Accumulators:** `$sum`, `$avg`, `$min`, `$max`, `$push`, `$addToSet`, `$first`, `$last`, `$count`

**Example Pipeline:**
```javascript
db.books.aggregate([
  { $match: { available: true } },
  { $group: { _id: "$author", count: { $sum: 1 }, avg: { $avg: "$price" } } },
  { $sort: { count: -1 } },
  { $limit: 5 },
  { $project: { _id: 0, author: "$_id", count: 1, avg: { $round: ["$avg", 2] } } }
])
```

**Performance:** Put `$match` and `$sort` on indexed fields **first** to reduce pipeline input size.

---

## 13. Indexes

```javascript
db.col.createIndex({ field: 1 })                       // single-field ascending
db.col.createIndex({ field1: 1, field2: -1 })          // compound
db.col.createIndex({ title: "text", body: "text" })    // text search
db.col.createIndex({ isbn: 1 }, { unique: true })      // unique constraint
db.col.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })  // TTL
db.col.createIndex({ field: 1 }, { sparse: true })     // skip docs missing field

db.col.getIndexes()                    // list all indexes
db.col.dropIndex({ field: 1 })         // drop by key pattern
```

**Compound Index Leftmost Prefix Rule:**
Index `{ a: 1, b: 1, c: 1 }` supports queries on `a`, `a+b`, or `a+b+c` — but NOT `b` or `c` alone.

**Diagnose with explain():**
```javascript
db.col.find({ author: "X" }).explain("executionStats")
// Look for: "stage": "IXSCAN" ✅  or  "stage": "COLLSCAN" ❌
// Check: totalDocsExamined ≈ nReturned (efficient) vs. >> (inefficient)
```

---

## 14. Schema Design — Embedding vs. Referencing

| Embed When | Reference When |
|-----------|---------------|
| Data is accessed together always | Data is accessed independently |
| One-to-few relationship (bounded) | One-to-many or many-to-many (unbounded) |
| Child data owned by the parent | Data is shared across multiple documents |
| Atomic updates needed | Document size would exceed 16 MB |

**Rule of thumb:** "Data that is accessed together should be stored together."
**16 MB document limit** — unbounded arrays will eventually hit it. Reference them instead.

---

## 15. MongoDB Atlas — Free Tier Setup

1. Create account at `cloud.mongodb.com`
2. Create M0 Free cluster (512MB storage, no credit card)
3. Create a **Database User** (username/password — separate from Atlas login)
4. Add IP Address under **Network Access** (`0.0.0.0/0` for dev)
5. Click **Connect** → **Drivers** → copy connection string

**Connection string:** `mongodb+srv://username:password@cluster0.abc12.mongodb.net/dbname`

---

## 16. Spring Data MongoDB — Quick Reference

### Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Configuration
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bookstoredb
      # Atlas: mongodb+srv://user:pass@cluster.mongodb.net/bookstoredb
```

### Entity
```java
@Document(collection = "books")    // maps to "books" collection
public class Book {
    @Id
    private String id;             // _id → auto ObjectId conversion

    private String title;

    @Field("isbn_code")            // custom BSON field name
    private String isbn;

    @Indexed(unique = true)        // creates unique index
    private String email;

    private List<String> tags;     // stored as BSON array
    private LocalDateTime createdAt;
}
```

### Repository — Derived Queries
```java
public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByAuthor(String author);
    List<Book> findByAvailableTrue();
    List<Book> findByPriceLessThanEqual(double maxPrice);
    List<Book> findByTagsContaining(String tag);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAuthorAndAvailableTrueOrderByPriceAsc(String author);
    long countByAvailableTrue();
    void deleteByAvailableFalseAndStockLessThanEqual(int stock);
}
```

### MongoTemplate — Programmatic Queries
```java
@Autowired
private MongoTemplate mongoTemplate;

// Query
Query query = new Query(Criteria.where("available").is(true).and("price").lte(maxPrice));
query.with(Sort.by(Sort.Direction.ASC, "price")).limit(10);
List<Book> books = mongoTemplate.find(query, Book.class);

// Update
Query q = Query.query(Criteria.where("author").is(author));
Update u = new Update().set("available", true).inc("stock", 10).currentDate("updatedAt");
mongoTemplate.updateMulti(q, u, Book.class);

// Aggregation
Aggregation agg = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("available").is(true)),
    Aggregation.group("author").count().as("count"),
    Aggregation.sort(Sort.by(Sort.Direction.DESC, "count"))
);
mongoTemplate.aggregate(agg, "books", Document.class).getMappedResults();
```

---

## 17. Common Gotchas

| Gotcha | Correct Approach |
|--------|-----------------|
| Querying `_id` with a plain string | Use `ObjectId("...")` wrapper |
| Storing dates as strings | Use `ISODate(...)` / `new Date()` |
| Mixing include/exclude in projection | Pick one mode (except `_id` can always be 0) |
| `replaceOne` removing unexpected fields | Use `updateOne` + `$set` for targeted updates |
| `deleteMany({})` with empty filter | Deletes ALL documents — always include a filter |
| Regex `/pattern/i` without index | Use text index + `$text` for large collections |
| Two conditions on same field: `{ price: {$gt:1}, price: {$lt:10} }` | Use `$and: [...]` explicitly |
| `insertMany` stops on first duplicate | Use `{ ordered: false }` to continue on error |

---

## 18. Day 35 — Key Concepts Checklist

- [ ] Four NoSQL categories and when each is appropriate
- [ ] SQL vs. MongoDB trade-offs across schema, scaling, relationships, ACID
- [ ] MongoDB architecture: mongod, replica sets, sharding, mongos
- [ ] Document model: flexible schema, nested objects, arrays
- [ ] BSON type system: ObjectId, Date, Decimal128
- [ ] ObjectId structure: timestamp + random + counter = globally unique
- [ ] mongosh navigation: show dbs, use, show collections, find, insertOne
- [ ] Full CRUD: insertOne/Many, find/findOne with projection, updateOne/Many, deleteOne/Many
- [ ] Update operators: $set, $unset, $inc, $push, $pull, $addToSet
- [ ] Query operators: comparison, logical, element, array, regex
- [ ] Aggregation pipeline: $match → $group → $sort → $limit → $project
- [ ] $lookup and $unwind for join-like operations
- [ ] Index types: single, compound, text, TTL, unique, sparse
- [ ] Compound index leftmost prefix rule
- [ ] explain("executionStats"): IXSCAN vs. COLLSCAN
- [ ] Schema design: embedding vs. referencing, 16MB limit, unbounded arrays
- [ ] MongoDB Atlas: free M0 tier, connection string format
- [ ] Spring Data MongoDB: @Document, @Id, MongoRepository, MongoTemplate
- [ ] Derived query method conventions in MongoRepository
- [ ] Criteria and Update API in MongoTemplate
