# Day 35 Part 2 — MongoDB: CRUD, Queries, Aggregation, Indexes, Atlas & Spring Data MongoDB
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 35 Part 2: MongoDB — Queries, Aggregation, Indexes & Spring Integration

**Subtitle:** Full CRUD, query operators, aggregation pipeline, indexes, Atlas, and Spring Data MongoDB

**Learning Objectives:**
- Perform full CRUD operations using `insertOne`, `find`, `updateOne`, `deleteOne` and their bulk variants
- Apply query operators for comparisons, logical conditions, array filtering, and text matching
- Use update operators: `$set`, `$unset`, `$inc`, `$push`, `$pull`, `$addToSet`
- Build aggregation pipelines with `$match`, `$group`, `$project`, `$sort`, `$lookup`, `$unwind`
- Create and manage indexes: single-field, compound, text, TTL, unique, sparse
- Use `explain("executionStats")` to diagnose query performance
- Set up and connect to MongoDB Atlas free tier
- Configure Spring Data MongoDB with `@Document`, `MongoRepository`, and `MongoTemplate`
- Write derived query methods and `Criteria`-based queries in Spring Boot

---

### Slide 2 — CRUD — Create: insertOne and insertMany

**Title:** Create — `insertOne` and `insertMany`

**Content:**

**`insertOne(document)` — Insert a Single Document:**
```javascript
db.books.insertOne({
  title: "Clean Code",
  author: "Robert Martin",
  isbn: "978-0132350884",
  price: 39.99,
  tags: ["java", "best-practices", "refactoring"],
  publishedDate: ISODate("2008-08-01"),
  available: true,
  stock: 42
})
// Response:
// { acknowledged: true, insertedId: ObjectId("64f2a3b1c9e2f10012345678") }
```

**`insertMany(array)` — Insert Multiple Documents:**
```javascript
db.books.insertMany([
  {
    title: "Refactoring",
    author: "Martin Fowler",
    isbn: "978-0201485677",
    price: 44.99,
    tags: ["refactoring", "design-patterns"],
    publishedDate: ISODate("1999-07-08"),
    available: true,
    stock: 27
  },
  {
    title: "The Pragmatic Programmer",
    author: "David Thomas",
    isbn: "978-0135957059",
    price: 49.99,
    tags: ["career", "best-practices"],
    publishedDate: ISODate("2019-09-13"),
    available: false,
    stock: 0
  },
  {
    title: "Design Patterns",
    author: "Gang of Four",
    isbn: "978-0201633610",
    price: 54.99,
    tags: ["design-patterns", "oop"],
    publishedDate: ISODate("1994-10-31"),
    available: true,
    stock: 13
  }
])
// Response:
// { acknowledged: true, insertedIds: { '0': ObjectId("..."), '1': ObjectId("..."), ... } }
```

**Key Points:**
- `_id` is automatically added if not provided
- `insertMany` is not fully atomic — if one document fails (e.g., duplicate `_id`), documents before it are inserted; documents after can be skipped depending on options
- Use `{ ordered: false }` option in `insertMany` to continue inserting remaining documents after an error: `db.books.insertMany([...], { ordered: false })`

---

### Slide 3 — CRUD — Read: find and findOne

**Title:** Read — `find`, `findOne`, Projection, Sort, Limit

**Content:**

**Basic Queries:**
```javascript
// Find ALL documents (returns cursor — mongosh auto-iterates first batch)
db.books.find()

// Find ONE document (first match)
db.books.findOne({ author: "Robert Martin" })

// Find with a filter
db.books.find({ available: true })

// Count matching documents
db.books.countDocuments({ available: true })
```

**Projection — Choose Which Fields to Return:**
```javascript
// Include ONLY title, author, price (_id is always included unless excluded)
db.books.find({ available: true }, { title: 1, author: 1, price: 1 })

// Exclude specific fields (return everything EXCEPT tags and stock)
db.books.find({}, { tags: 0, stock: 0 })

// Exclude _id
db.books.find({}, { title: 1, author: 1, _id: 0 })
```
**Rule:** You cannot mix `1` (include) and `0` (exclude) in the same projection — except for `_id`, which can be excluded alongside inclusions.

**Sorting, Limiting, and Skipping:**
```javascript
// Sort by price ascending (1 = ascending, -1 = descending)
db.books.find({ available: true }).sort({ price: 1 })

// Sort by multiple fields
db.books.find().sort({ author: 1, price: -1 })

// Limit results
db.books.find().limit(5)

// Pagination: skip 20, take 10 (page 3 of 10 per page)
db.books.find().sort({ title: 1 }).skip(20).limit(10)

// Cursor iteration in mongosh — use 'it' to get the next batch
db.books.find()
it                             // next batch of results
```

---

### Slide 4 — CRUD — Update Operators

**Title:** Update Operators — Modifying Fields Without Replacing Documents

**Content:**

MongoDB update operations work with **update operators** — directives that tell MongoDB *how* to modify a field. You almost never replace an entire document — you apply targeted operators.

**Core Update Operators:**

| Operator | Purpose | Example |
|----------|---------|---------|
| `$set` | Set or add a field | `{$set: {price: 34.99}}` |
| `$unset` | Remove a field entirely | `{$unset: {discount: ""}}` |
| `$inc` | Increment/decrement a number | `{$inc: {stock: -1}}` |
| `$mul` | Multiply a number | `{$mul: {price: 0.9}}` (10% discount) |
| `$rename` | Rename a field | `{$rename: {isbn: "isbnCode"}}` |
| `$currentDate` | Set to current date | `{$currentDate: {updatedAt: true}}` |
| `$min` | Set field if new value is less | `{$min: {lowestPrice: 29.99}}` |
| `$max` | Set field if new value is greater | `{$max: {highestRating: 5}}` |

**Array Update Operators:**

| Operator | Purpose | Example |
|----------|---------|---------|
| `$push` | Append element to array | `{$push: {tags: "architecture"}}` |
| `$addToSet` | Append only if not already present | `{$addToSet: {tags: "oop"}}` |
| `$pull` | Remove all matching elements | `{$pull: {tags: "outdated"}}` |
| `$pop` | Remove first or last element | `{$pop: {tags: 1}}` (last), `{$pop: {tags: -1}}` (first) |

**Combining Multiple Operators:**
```javascript
db.books.updateOne(
  { title: "Clean Code" },
  {
    $set:         { price: 34.99 },
    $inc:         { stock: -1 },
    $addToSet:    { tags: "clean-architecture" },
    $currentDate: { updatedAt: true }
  }
)
```

---

### Slide 5 — CRUD — updateOne, updateMany, replaceOne, deleteOne, deleteMany

**Title:** Read — Update and Delete Methods

**Content:**

**`updateOne(filter, update)` — Update the First Matching Document:**
```javascript
db.books.updateOne(
  { title: "Clean Code" },                    // filter
  { $set: { price: 34.99, available: true } } // update
)
// Response: { matchedCount: 1, modifiedCount: 1 }
```

**`updateMany(filter, update)` — Update All Matching Documents:**
```javascript
// Make all unavailable books available again
db.books.updateMany(
  { available: false },
  { $set: { available: true }, $currentDate: { restockedAt: true } }
)

// Apply a 10% discount to all books over $40
db.books.updateMany(
  { price: { $gt: 40 } },
  { $mul: { price: 0.90 } }
)
```

**`replaceOne(filter, replacement)` — Replace Entire Document:**
```javascript
// CAUTION: replaceOne replaces EVERYTHING except _id
db.books.replaceOne(
  { title: "Clean Code" },
  { title: "Clean Code", author: "Robert C. Martin", price: 39.99 }
  // ALL other fields (isbn, tags, stock, etc.) are GONE after this
)
```
**Use `$set` for targeted updates; use `replaceOne` only when you intend to replace the entire document.**

**`deleteOne(filter)` and `deleteMany(filter)`:**
```javascript
// Delete the first document matching filter
db.books.deleteOne({ _id: ObjectId("64f2a3b1c9e2f10012345678") })
// Response: { deletedCount: 1 }

// Delete all unavailable books with zero stock
db.books.deleteMany({ available: false, stock: { $lte: 0 } })
// Response: { deletedCount: 2 }

// DANGER: Delete ALL documents in the collection
db.books.deleteMany({})        // empty filter matches everything
```

**`findOneAndUpdate` / `findOneAndDelete` — Atomically Find and Modify:**
```javascript
// Return the document BEFORE the update (default)
const doc = db.books.findOneAndUpdate(
  { title: "Clean Code" },
  { $inc: { stock: -1 } },
  { returnDocument: "after" }   // "after" returns post-update document
)
```

---

### Slide 6 — Query Operators — Comparison and Logical

**Title:** Query Operators — Comparison and Logical

**Content:**

MongoDB queries use **operator objects** for anything beyond a simple equality check. The syntax is: `{ field: { $operator: value } }`.

**Comparison Operators:**
```javascript
{ price: { $gt: 30 } }                    // price > 30
{ price: { $gte: 30 } }                   // price >= 30
{ price: { $lt: 50 } }                    // price < 50
{ price: { $lte: 50 } }                   // price <= 50
{ price: { $ne: 39.99 } }                 // price != 39.99

// Range — AND conditions on the same field
{ price: { $gte: 30, $lte: 50 } }         // 30 <= price <= 50

// Set membership
{ author: { $in: ["Robert Martin", "Martin Fowler"] } }   // in set
{ tags: { $nin: ["outdated", "legacy"] } }                 // not in set
```

**Logical Operators:**
```javascript
// $and — explicit (implicit AND when multiple fields in same filter)
{ $and: [{ price: { $gt: 30 } }, { available: true }] }

// Implicit AND — shorthand for above (same field: use explicit $and)
{ price: { $gt: 30 }, available: true }

// $or — at least one condition must be true
{ $or: [{ author: "Robert Martin" }, { tags: "java" }] }

// $nor — none of the conditions are true
{ $nor: [{ available: false }, { stock: 0 }] }

// $not — negates a condition
{ price: { $not: { $gt: 50 } } }          // price NOT greater than 50

// Combining $and with $or — must use explicit $and
{
  $and: [
    { available: true },
    { $or: [{ price: { $lt: 30 } }, { tags: "best-practices" }] }
  ]
}
```

**Element Operators:**
```javascript
{ isbn: { $exists: true } }               // document HAS the isbn field
{ isbn: { $exists: false } }              // document does NOT have isbn
{ price: { $type: "double" } }            // price field is BSON double type
{ price: { $type: ["double", "int"] } }   // price is double OR int
```

---

### Slide 7 — Query Operators — Arrays and Text

**Title:** Query Operators — Arrays and Regular Expressions

**Content:**

**Array Query Operators:**
```javascript
// Simple array query — field contains this value (works for single value too)
db.books.find({ tags: "java" })                         // tags array contains "java"

// $all — array must contain ALL specified values
db.books.find({ tags: { $all: ["java", "best-practices"] } })

// $size — array has exactly N elements
db.books.find({ tags: { $size: 3 } })

// $elemMatch — element in array matches multiple conditions
// (Use when matching multiple conditions against a single array element)
db.orders.find({
  lineItems: {
    $elemMatch: { sku: "BOOK-001", qty: { $gte: 2 } }
  }
})
// Without $elemMatch: { "lineItems.sku": "BOOK-001", "lineItems.qty": {$gte: 2} }
// would match if ANY element has sku "BOOK-001" AND ANY element has qty >= 2
// With $elemMatch: requires the SAME element to satisfy both conditions
```

**Nested Document Queries — Dot Notation:**
```javascript
// Query nested field using dot notation
db.orders.find({ "shippingAddress.city": "Boston" })
db.orders.find({ "shippingAddress.state": "MA", "shippingAddress.zip": "02101" })

// Query an array element at a specific position
db.books.find({ "tags.0": "java" })          // first tag is "java"
```

**Regular Expressions — `$regex`:**
```javascript
// Case-insensitive title search
db.books.find({ title: { $regex: /clean/i } })

// Anchored regex — starts with "The"
db.books.find({ title: { $regex: /^The/ } })

// Ends with "er"
db.books.find({ author: { $regex: /er$/ } })

// LIKE '%pattern%' equivalent
db.books.find({ title: { $regex: "Pragmatic" } })
```

**Text Search — `$text` (requires a text index first):**
```javascript
// Create text index
db.books.createIndex({ title: "text", author: "text" })

// Full-text search
db.books.find({ $text: { $search: "clean code refactoring" } })

// Sort by text relevance score
db.books.find(
  { $text: { $search: "clean code" } },
  { score: { $meta: "textScore" } }
).sort({ score: { $meta: "textScore" } })
```

---

### Slide 8 — Aggregation Pipeline — Core Concept

**Title:** The Aggregation Pipeline — Processing Data in Stages

**Content:**

The aggregation pipeline is MongoDB's mechanism for transforming and analyzing data. Documents enter the pipeline, flow through a series of **stages** — each stage transforms the stream — and the final output is the result.

**Pipeline Concept:**
```
Input Documents
      ↓
  [ $match  ]   ← filter (like WHERE)
      ↓
  [ $group  ]   ← group and accumulate (like GROUP BY)
      ↓
  [ $sort   ]   ← sort the grouped results
      ↓
  [ $limit  ]   ← take top N
      ↓
  [ $project ]  ← reshape the output
      ↓
Output Documents
```

**Aggregation Syntax:**
```javascript
db.books.aggregate([
  { $match: { available: true } },               // Stage 1
  { $group: {
      _id: "$author",
      bookCount: { $sum: 1 },
      avgPrice:  { $avg: "$price" },
      minPrice:  { $min: "$price" },
      allTitles: { $push: "$title" }
  }},                                            // Stage 2
  { $sort: { bookCount: -1 } },                 // Stage 3
  { $limit: 5 },                                // Stage 4
  { $project: {
      _id: 0,
      author:    "$_id",
      bookCount: 1,
      avgPrice:  { $round: ["$avgPrice", 2] }
  }}                                            // Stage 5
])
```

**`$group` Accumulator Operators:**

| Accumulator | Purpose |
|-------------|---------|
| `$sum: 1` | Count documents (or `$sum: "$qty"` to sum a field) |
| `$avg: "$price"` | Average of a field |
| `$min` / `$max` | Minimum/maximum value |
| `$push: "$title"` | Collect values into an array |
| `$addToSet: "$tag"` | Collect unique values into an array |
| `$first` / `$last` | First/last value (requires preceding `$sort`) |

**Performance Tip:** Always put `$match` and `$sort` on indexed fields **at the beginning** of the pipeline to reduce the number of documents processed by subsequent stages.

---

### Slide 9 — Aggregation Pipeline — Advanced Stages

**Title:** Aggregation Stages — `$lookup`, `$unwind`, `$addFields`, and More

**Content:**

**`$project` — Reshape Documents:**
```javascript
{ $project: {
    _id:        0,                                  // exclude _id
    title:      1,                                  // include title
    author:     1,                                  // include author
    discount:   { $multiply: ["$price", 0.9] },    // computed field
    titleUpper: { $toUpper: "$title" },             // expression
    ageYears: {
      $divide: [
        { $subtract: [new Date(), "$publishedDate"] },
        1000 * 60 * 60 * 24 * 365
      ]
    }
}}
```

**`$lookup` — Left Outer Join Between Collections:**
```javascript
db.orders.aggregate([
  {
    $lookup: {
      from:         "books",           // the other collection
      localField:   "bookId",          // field in orders
      foreignField: "_id",             // field in books
      as:           "bookDetails"      // output array field name
    }
  },
  {
    $unwind: "$bookDetails"            // flatten the bookDetails array
  },
  {
    $project: {
      orderId: "$_id",
      bookTitle: "$bookDetails.title",
      bookPrice: "$bookDetails.price",
      quantity: 1
    }
  }
])
```

**`$unwind` — Deconstruct Array into Separate Documents:**
```javascript
// Each book's tags array becomes one document per tag
db.books.aggregate([
  { $unwind: "$tags" },
  { $group: {
      _id: "$tags",
      count: { $sum: 1 }
  }},
  { $sort: { count: -1 } }
])
// Result: { _id: "java", count: 3 }, { _id: "design-patterns", count: 2 }, ...
```

**`$addFields` — Add/Compute Fields Without Removing Existing Ones:**
```javascript
// Unlike $project, $addFields preserves ALL existing fields
{ $addFields: {
    discountedPrice: { $multiply: ["$price", 0.85] },
    tagsCount: { $size: "$tags" }
}}
```

**`$count` — Output a Count Document:**
```javascript
db.books.aggregate([
  { $match: { available: true } },
  { $count: "availableBookCount" }
])
// Result: { availableBookCount: 3 }
```

---

### Slide 10 — Indexes — Types and Creation

**Title:** Indexes — Making Queries Fast

**Content:**

Without an index, MongoDB must scan every document in a collection to find matching documents — called a **Collection Scan (COLLSCAN)**. With an index, MongoDB jumps directly to the matching documents — called an **Index Scan (IXSCAN)**. For large collections, indexes are the difference between millisecond queries and seconds-long queries.

**The Default Index:**
Every collection automatically has a unique index on `_id`. This cannot be dropped.

**Creating Indexes:**
```javascript
// Single-field index — ascending (1) or descending (-1)
db.books.createIndex({ author: 1 })

// Compound index — multiple fields (order matters for query patterns)
db.books.createIndex({ author: 1, price: -1 })

// Text index — for $text search
db.books.createIndex({ title: "text", author: "text" })

// Unique index — enforce uniqueness like a SQL UNIQUE constraint
db.books.createIndex({ isbn: 1 }, { unique: true })

// Sparse index — only indexes documents that HAVE the field
// (skips documents where the field is missing)
db.books.createIndex({ discount: 1 }, { sparse: true })

// TTL (Time-To-Live) index — documents auto-deleted after N seconds
// Useful for sessions, temporary data, cache expiry
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })

// Named index — easier to manage and drop
db.books.createIndex({ author: 1 }, { name: "idx_author_asc" })
```

**Managing Indexes:**
```javascript
db.books.getIndexes()              // list all indexes on the collection
db.books.dropIndex({ author: 1 }) // drop by key pattern
db.books.dropIndex("idx_author_asc")  // drop by name
```

**Compound Index Field Order Matters:**
A compound index on `{ author: 1, price: -1 }` supports:
- Queries filtering on `author` alone ✅
- Queries filtering on `author` AND `price` ✅
- Queries filtering on `price` alone ❌ (not supported — use leftmost prefix)

---

### Slide 11 — Indexes — Query Performance with explain()

**Title:** Diagnosing Query Performance with `explain()`

**Content:**

`explain("executionStats")` runs a query and returns detailed statistics about how MongoDB executed it — including whether it used an index.

**Running explain():**
```javascript
// Append explain() to any find or aggregate
db.books.find({ author: "Robert Martin" }).explain("executionStats")
```

**Key Fields to Look For:**
```javascript
{
  "queryPlanner": {
    "winningPlan": {
      "stage": "IXSCAN",              // ✅ Index Scan — good
      // OR
      "stage": "COLLSCAN",            // ❌ Collection Scan — bad for large collections
      "inputStage": {
        "indexName": "author_1"       // which index was used
      }
    }
  },
  "executionStats": {
    "nReturned": 1,                   // documents returned
    "totalKeysExamined": 1,           // index keys examined — should ≈ nReturned
    "totalDocsExamined": 1,           // documents examined — should ≈ nReturned
    "executionTimeMillis": 0          // query execution time
  }
}
```

**Signs of a Problem:**
- `"stage": "COLLSCAN"` on a large collection
- `totalDocsExamined` >> `nReturned` (many examined, few returned = inefficient)
- `executionTimeMillis` is high

**Example — Before and After an Index:**
```javascript
// BEFORE index on price — results in COLLSCAN
db.books.find({ price: { $gt: 40 } }).explain("executionStats")
// totalDocsExamined: 10000, executionTimeMillis: 45

// Create the index
db.books.createIndex({ price: 1 })

// AFTER index — results in IXSCAN
db.books.find({ price: { $gt: 40 } }).explain("executionStats")
// totalDocsExamined: 127, executionTimeMillis: 1
```

**When NOT to Over-Index:**
- Indexes consume disk space
- Every write must update all relevant indexes
- Rule of thumb: index fields you filter on frequently, sort on, or use in joins (`$lookup`)

---

### Slide 12 — MongoDB Atlas — Cloud-Hosted MongoDB

**Title:** MongoDB Atlas — Free Tier Cloud Hosting

**Content:**

MongoDB Atlas is MongoDB's fully managed cloud database service, available on AWS, Azure, and GCP. The **free M0 tier** gives you 512MB of shared storage with no credit card required — ideal for development, demos, and small projects.

**Setting Up an Atlas Free Cluster:**
1. Go to `cloud.mongodb.com` → Create a free account
2. Create a new project → Build a Database → Choose **M0 Free**
3. Choose a cloud provider and region (closest to you)
4. Set a **database username and password** (not your Atlas account password)
5. Under **Network Access** → Add IP Address → "Allow Access from Anywhere" (for dev: `0.0.0.0/0`)
6. Under **Database** → Connect → **Drivers** → Copy connection string

**Atlas Connection String Format:**
```
mongodb+srv://username:password@cluster0.abc12.mongodb.net/dbname?retryWrites=true&w=majority
```

**Connecting mongosh to Atlas:**
```bash
mongosh "mongodb+srv://username:password@cluster0.abc12.mongodb.net/bookstoredb"
```

**What You Get with M0:**
| Feature | M0 Free |
|---------|---------|
| Storage | 512 MB |
| RAM | Shared |
| vCPUs | Shared |
| Connections | 500 max |
| Atlas Search | ✅ Included |
| Atlas Vector Search | ✅ Included (relevant from Day 34!) |
| Backups | Not included |
| SLA | Not included |

**Atlas-Specific Features (beyond raw MongoDB):**
- **Atlas Search** — Lucene-based full-text search layered over your MongoDB data
- **Atlas Vector Search** — vector similarity search (used in RAG pipelines — connects to Spring AI from Day 34)
- **Atlas Charts** — built-in data visualization dashboard
- **MongoDB Compass** — free GUI desktop client for browsing data visually
- **Performance Advisor** — recommends indexes based on your query patterns

---

### Slide 13 — Spring Data MongoDB — Setup and Entity Mapping

**Title:** Spring Data MongoDB — Dependencies, Configuration, and `@Document`

**Content:**

**Dependency:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

**Configuration:**
```yaml
# application.yml — Local MongoDB
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/bookstoredb

# application.yml — MongoDB Atlas
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/bookstoredb?retryWrites=true&w=majority
```

**Entity Mapping with `@Document`:**
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "books")      // maps this class to the "books" collection
public class Book {

    @Id
    private String id;               // maps to MongoDB's _id; ObjectId stored as String

    private String title;            // field name in BSON defaults to the Java field name

    private String author;

    @Field("isbn_code")              // custom BSON field name (overrides Java name)
    private String isbn;

    private double price;

    private boolean available;

    private List<String> tags;

    private LocalDateTime publishedDate;

    private LocalDateTime createdAt;

    // Constructors, getters, setters — or use @Data from Lombok
}
```

**The `@Id` Annotation:**
- Maps to MongoDB's `_id` field
- If the Java type is `String`, Spring Data auto-converts between BSON `ObjectId` and `String`
- You can also use `ObjectId` directly as the Java type for explicit control

**`@Indexed` — Create Indexes via Code:**
```java
@Indexed(unique = true)
private String isbn;

@Indexed(expireAfterSeconds = 3600)
private LocalDateTime sessionCreatedAt;
```

---

### Slide 14 — Spring Data MongoDB — MongoRepository

**Title:** `MongoRepository` — Repository Pattern with Derived Queries

**Content:**

`MongoRepository<T, ID>` extends Spring Data's `CrudRepository` and `PagingAndSortingRepository`. It provides standard CRUD methods and supports **derived query method names** — query methods generated automatically from the method name.

```java
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    // --- Derived Query Methods ---

    // Find all books by author
    List<Book> findByAuthor(String author);

    // Find all available books
    List<Book> findByAvailableTrue();

    // Find books with price > specified value
    List<Book> findByPriceGreaterThan(double price);

    // Find books with price between min and max
    List<Book> findByPriceBetween(double min, double max);

    // Find by exact ISBN (should be unique)
    Optional<Book> findByIsbn(String isbn);

    // Find books whose tags array contains the given tag
    List<Book> findByTagsContaining(String tag);

    // Find by author, sorted by price ascending
    List<Book> findByAuthorOrderByPriceAsc(String author);

    // Count available books by author
    long countByAuthorAndAvailableTrue(String author);

    // Find available books with price <= maxPrice, sorted by price
    List<Book> findByAvailableTrueAndPriceLessThanEqualOrderByPriceAsc(double maxPrice);

    // Delete all out-of-stock unavailable books
    void deleteByAvailableFalseAndStockLessThanEqual(int stock);
}
```

**Service Using the Repository:**
```java
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        return bookRepository.save(book);          // save() = insert or update
    }

    public List<Book> findAffordableBooks(double maxPrice) {
        return bookRepository.findByAvailableTrueAndPriceLessThanEqualOrderByPriceAsc(maxPrice);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
```

---

### Slide 15 — Spring Data MongoDB — MongoTemplate

**Title:** `MongoTemplate` — Programmatic Queries with `Query` and `Criteria`

**Content:**

`MongoTemplate` provides a lower-level, programmatic API for querying MongoDB. Use it when derived query methods don't support the complexity you need — dynamic filters, complex `$and`/`$or` logic, update operations, or aggregation pipelines.

**Dependency:** `MongoTemplate` is auto-configured when you add `spring-boot-starter-data-mongodb`. Inject it directly.

```java
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;

@Service
@RequiredArgsConstructor
public class AdvancedBookService {

    private final MongoTemplate mongoTemplate;

    // Complex query with Criteria
    public List<Book> findBooksInPriceRange(double min, double max, String tag) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("available").is(true)
                .and("price").gte(min).lte(max)
                .and("tags").in(tag)
        );
        query.with(Sort.by(Sort.Direction.ASC, "price"));
        query.limit(20);
        return mongoTemplate.find(query, Book.class);
    }

    // Update with $set and $inc
    public UpdateResult applyDiscount(String author, double discountPercent) {
        Query query = Query.query(Criteria.where("author").is(author).and("available").is(true));
        Update update = new Update()
            .multiply("price", (100 - discountPercent) / 100.0)
            .currentDate("updatedAt");
        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Check if a document exists
    public boolean isbnExists(String isbn) {
        Query query = Query.query(Criteria.where("isbn").is(isbn));
        return mongoTemplate.exists(query, Book.class);
    }

    // Count with criteria
    public long countAvailableByAuthor(String author) {
        Query query = Query.query(
            Criteria.where("author").is(author).and("available").is(true)
        );
        return mongoTemplate.count(query, Book.class);
    }

    // Aggregation pipeline via MongoTemplate
    public List<Document> groupByAuthor() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("available").is(true)),
            Aggregation.group("author")
                .count().as("bookCount")
                .avg("price").as("avgPrice"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "bookCount")),
            Aggregation.limit(10)
        );
        return mongoTemplate.aggregate(agg, "books", Document.class).getMappedResults();
    }
}
```

---

### Slide 16 — Part 2 Summary and Full-Day Review

**Title:** Part 2 Summary — Full MongoDB Toolkit

**Content:**

**CRUD Quick Reference:**
```javascript
db.col.insertOne({...}) / insertMany([...])
db.col.find({filter}, {projection}).sort({}).skip(n).limit(n)
db.col.updateOne({filter}, {$set:{...}}) / updateMany(...)
db.col.deleteOne({filter}) / deleteMany({filter})
```

**Key Query Operators:**

| Category | Operators |
|----------|-----------|
| Comparison | `$eq`, `$ne`, `$gt`, `$gte`, `$lt`, `$lte`, `$in`, `$nin` |
| Logical | `$and`, `$or`, `$nor`, `$not` |
| Element | `$exists`, `$type` |
| Array | `$all`, `$elemMatch`, `$size` |
| Text/Regex | `$regex`, `$text` |

**Key Update Operators:** `$set`, `$unset`, `$inc`, `$mul`, `$push`, `$pull`, `$addToSet`, `$currentDate`

**Aggregation Stages:** `$match` → `$group` → `$sort` → `$limit` → `$project` → `$lookup` → `$unwind` → `$addFields` → `$count`

**Index Types:** Single-field, Compound (order matters), Text, TTL, Unique, Sparse

**Spring Data MongoDB:**
- `@Document(collection = "name")` — entity mapping
- `@Id` — maps to `_id`; String ↔ ObjectId auto-conversion
- `MongoRepository<T, ID>` — derived query methods from method names
- `MongoTemplate` + `Criteria` + `Update` — programmatic queries and updates
- Connection: `spring.data.mongodb.uri` in `application.yml`

**Day 35 Complete. Key Topics for Review:**
- When to choose MongoDB over SQL
- ObjectId structure and why it's globally unique
- Embedding vs. referencing — the core schema design decision
- Aggregation pipeline stage order (match early, sort before group when possible)
- `explain("executionStats")` — IXSCAN vs COLLSCAN
- Spring Data MongoDB: Repository pattern vs. MongoTemplate
