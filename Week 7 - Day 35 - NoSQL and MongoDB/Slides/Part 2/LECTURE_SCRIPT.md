# Day 35 Part 2 — MongoDB: CRUD, Queries, Aggregation, Indexes, Atlas & Spring Data MongoDB
## Lecture Script

---

**[00:00–01:30] — Welcome Back and Part 2 Overview**

Alright, welcome back everyone. Part 1 gave you the conceptual foundation — NoSQL categories, why MongoDB exists, the document model, BSON, ObjectId, collections, and basic mongosh navigation. Part 2 is where we get operational. We're going to write real queries, do full CRUD, use every major query operator, build aggregation pipelines, create indexes, set up MongoDB Atlas, and wire everything up to Spring Boot.

This is the session where MongoDB starts to feel powerful. Let's get right into it.

---

**[01:30–10:00] — CRUD: Create — insertOne and insertMany**

Slide two. Let's start with creating data. In MongoDB you have two methods: `insertOne` for a single document and `insertMany` for a batch.

`insertOne` takes one document object. Let me type this into mongosh with you right now — make sure you're connected, and make sure you're on the `bookstoredb` database:

```javascript
use bookstoredb

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
```

When you run this, MongoDB responds with `{ acknowledged: true, insertedId: ObjectId("...") }`. Two important parts: `acknowledged: true` means the write was confirmed received and persisted to disk. The `insertedId` is the ObjectId that was auto-generated for `_id`. Copy that ObjectId — we'll query by it in a minute.

Now let's bulk insert our sample dataset:

```javascript
db.books.insertMany([
  { title: "Refactoring", author: "Martin Fowler", isbn: "978-0201485677",
    price: 44.99, tags: ["refactoring", "design-patterns"],
    publishedDate: ISODate("1999-07-08"), available: true, stock: 27 },
  { title: "The Pragmatic Programmer", author: "David Thomas", isbn: "978-0135957059",
    price: 49.99, tags: ["career", "best-practices"],
    publishedDate: ISODate("2019-09-13"), available: false, stock: 0 },
  { title: "Design Patterns", author: "Gang of Four", isbn: "978-0201633610",
    price: 54.99, tags: ["design-patterns", "oop"],
    publishedDate: ISODate("1994-10-31"), available: true, stock: 13 }
])
```

`insertMany` responds with an object containing an array of all inserted IDs.

One thing to know about `insertMany`: by default, it stops at the first error. If document 2 out of 5 has a duplicate `_id` and would fail, documents 3, 4, and 5 are NOT inserted. To continue despite errors — perhaps in a bulk import where some duplicates are expected — pass `{ ordered: false }` as a second argument. MongoDB will attempt all inserts and collect the errors, but keep going.

Now run `db.books.countDocuments()` — you should have 4. Good.

---

**[10:00–19:00] — CRUD: Read — find, findOne, Projection, Sort, Limit**

Slide three. Reading data. `find()` returns a cursor — a pointer to the result set. In mongosh, the shell automatically fetches the first 20 documents and displays them. Type `it` to iterate to the next batch.

Let's run some basic queries:

```javascript
// Get all books
db.books.find()

// Get just one book — first match
db.books.findOne({ author: "Robert Martin" })

// Get all available books
db.books.find({ available: true })

// Count matching documents
db.books.countDocuments({ available: true })
```

Now — **projection.** The second argument to `find()` controls which fields are returned. This is important for performance: if you only need the title and price, don't fetch the entire document.

Projection has two modes: inclusion and exclusion. You cannot mix them — except for `_id`, which can always be explicitly excluded.

```javascript
// Include only title, author, price — _id is included by default
db.books.find({ available: true }, { title: 1, author: 1, price: 1 })

// Include title and author, but explicitly exclude _id
db.books.find({ available: true }, { title: 1, author: 1, _id: 0 })

// Exclude tags and stock — return everything else
db.books.find({}, { tags: 0, stock: 0 })
```

One common mistake: mixing 1s and 0s in the same projection. `{ title: 1, tags: 0 }` will throw an error because you can't include some and exclude others. Pick one mode.

Now **sorting, limiting, and pagination:**

```javascript
// Sort by price ascending (1 = ascending, -1 = descending)
db.books.find({ available: true }).sort({ price: 1 })

// Sort by author ascending, then price descending within author
db.books.find().sort({ author: 1, price: -1 })

// Get the top 2 most expensive books
db.books.find().sort({ price: -1 }).limit(2)

// Pagination: page 1 (skip 0, limit 2), page 2 (skip 2, limit 2)
db.books.find().sort({ title: 1 }).skip(0).limit(2)
db.books.find().sort({ title: 1 }).skip(2).limit(2)
```

The order of chaining `.sort()`, `.skip()`, `.limit()` doesn't matter — MongoDB always applies sort first, then skip, then limit, regardless of how you chain them. But for readability, it's good practice to write them in that order.

---

**[19:00–28:30] — CRUD: Update Operators, updateOne, updateMany**

Slides four and five. Updating documents in MongoDB works differently from SQL. In SQL, you do `UPDATE books SET price = 34.99 WHERE title = 'Clean Code'`. In MongoDB, you use **update operators** — directives that tell MongoDB specifically how to modify a field.

The most important operator is `$set`. Let's use it:

```javascript
db.books.updateOne(
  { title: "Clean Code" },              // filter — which document to update
  { $set: { price: 34.99 } }           // update — what to change
)
// Response: { matchedCount: 1, modifiedCount: 1 }
```

`matchedCount` tells you how many documents matched the filter. `modifiedCount` tells you how many were actually modified (if the value was already 34.99, modifiedCount would be 0 even if matchedCount is 1).

Now let me walk through the key operators. On the slide you can see the full table — let me highlight the ones you'll use most.

`$set` — sets a field. If the field doesn't exist, it creates it. This is your most-used operator.

`$unset` — removes a field from a document entirely. The value you pass doesn't matter — convention is `""` or `1`:
```javascript
db.books.updateOne({ title: "Clean Code" }, { $unset: { discount: "" } })
```

`$inc` — increments a numeric field. Negative value decrements. Incredibly useful for counters:
```javascript
// Decrement stock by 1 when a book is sold
db.books.updateOne({ title: "Clean Code" }, { $inc: { stock: -1 } })
```

`$mul` — multiplies. Great for bulk price changes:
```javascript
// Apply 10% discount to all books over $40
db.books.updateMany({ price: { $gt: 40 } }, { $mul: { price: 0.90 } })
```

`$currentDate` — sets a field to the current server timestamp:
```javascript
db.books.updateOne({ title: "Clean Code" }, { $currentDate: { updatedAt: true } })
```

The array operators are critically important for document model development.

`$push` appends an element to an array. `$addToSet` does the same but only if the element isn't already in the array. `$pull` removes all elements that match a condition.

```javascript
// Add a new tag only if it doesn't already exist
db.books.updateOne({ title: "Clean Code" }, { $addToSet: { tags: "clean-architecture" } })

// Remove a specific tag
db.books.updateOne({ title: "Clean Code" }, { $pull: { tags: "outdated" } })
```

You can combine multiple operators in a single update — MongoDB executes them all atomically on that one document:

```javascript
db.books.updateOne(
  { title: "Clean Code" },
  {
    $set:         { price: 34.99, available: true },
    $inc:         { stock: -1 },
    $addToSet:    { tags: "clean-architecture" },
    $currentDate: { updatedAt: true }
  }
)
```

`updateMany` applies the update to ALL matching documents:
```javascript
// Restock all unavailable books
db.books.updateMany({ available: false }, { $set: { available: true, stock: 10 } })
```

And `replaceOne` — I want you to be careful with this one. `replaceOne` replaces the ENTIRE document — not just the specified fields. Every field that isn't in your replacement object is gone. Use `$set` for targeted updates. `replaceOne` is only for when you genuinely want to replace the whole document.

For delete — `deleteOne` deletes the first matching document, `deleteMany` deletes all matches. If you pass an empty filter `{}` to `deleteMany`, you delete EVERY document in the collection. I have definitely done that in development. Be careful.

```javascript
db.books.deleteOne({ _id: ObjectId("...") })
db.books.deleteMany({ available: false, stock: { $lte: 0 } })
```

---

**[28:30–38:00] — Query Operators — Comparison, Logical, Array, and Regex**

Slides six and seven. Now let's talk about the full query language. The basic equality query — `{ available: true }` or `{ author: "Robert Martin" }` — you've seen. For anything more complex, you use operators.

The syntax is always: `{ field: { $operator: value } }`.

**Comparison operators** — let me walk through these quickly because they map directly to SQL comparison operators:

```javascript
db.books.find({ price: { $gt: 40 } })          // price > 40
db.books.find({ price: { $gte: 40, $lte: 55 } })  // 40 <= price <= 55
db.books.find({ available: { $ne: false } })     // available != false
```

For `$in` and `$nin` — checking against a set of values:
```javascript
// Author is either Robert Martin OR Martin Fowler
db.books.find({ author: { $in: ["Robert Martin", "Martin Fowler"] } })
```

**Logical operators** — these let you combine conditions:

```javascript
// AND — implicit: just put multiple fields in the same object
db.books.find({ available: true, price: { $lt: 50 } })

// AND — explicit (required when you have two conditions on the same field)
db.books.find({ $and: [{ price: { $gt: 30 } }, { price: { $lt: 50 } }] })

// OR — at least one condition
db.books.find({ $or: [{ author: "Robert Martin" }, { tags: "java" }] })

// NOT — negates a sub-expression
db.books.find({ price: { $not: { $gt: 50 } } })
```

An important subtlety: if you have two conditions on the same field and write `{ price: { $gt: 30 }, price: { $lt: 50 } }` — JavaScript/MongoDB actually treats this as only the second condition. The first key is overwritten. Use `$and: [...]` explicitly when you have two conditions on the same field.

**Array operators** — this is where MongoDB's document model really shines. Most real-world data has arrays, and MongoDB gives you powerful tools for querying them.

When you query an array field for a value — like `{ tags: "java" }` — MongoDB checks if the array *contains* that value. It doesn't require the entire array to equal `["java"]`. So `db.books.find({ tags: "java" })` finds all books where `tags` contains the string "java" anywhere in the array.

`$all` requires the array to contain ALL specified values:
```javascript
db.books.find({ tags: { $all: ["java", "best-practices"] } })
```

`$size` matches documents where the array has exactly that many elements:
```javascript
db.books.find({ tags: { $size: 3 } })  // exactly 3 tags
```

`$elemMatch` — this one is subtle but important. It requires a SINGLE array element to satisfy multiple conditions simultaneously. Without `$elemMatch`, conditions can be satisfied by different array elements:

```javascript
// WITHOUT $elemMatch — any element matching sku "BOOK-001" AND any element with qty >= 2
db.orders.find({ "lineItems.sku": "BOOK-001", "lineItems.qty": { $gte: 2 } })

// WITH $elemMatch — the SAME element must match both
db.orders.find({ lineItems: { $elemMatch: { sku: "BOOK-001", qty: { $gte: 2 } } } })
```

The difference only matters when your array contains objects. For arrays of primitives, the distinction doesn't apply.

**Regex** — for pattern matching:
```javascript
// Case-insensitive search for "clean" anywhere in title
db.books.find({ title: { $regex: /clean/i } })

// Starts with "The"
db.books.find({ title: { $regex: /^The/ } })

// SQL LIKE '%Programmer%' equivalent
db.books.find({ title: { $regex: "Programmer" } })
```

**Important performance note on regex**: A regex query that starts with a caret `^` — an anchored prefix — can use an index on that field. A regex like `/clean/i` that can match anywhere in the string cannot use an index and will always do a collection scan. For full-text search, use a text index with `$text` instead of `/pattern/i` regex on large collections.

---

**[38:00–47:00] — Aggregation Pipeline**

Slides eight and nine. The aggregation pipeline is one of MongoDB's most powerful features. Think of it as a data processing pipeline — documents flow in from a collection, pass through a series of stages, and the final output is the transformed result.

The best analogy is a Unix pipe: `cat file | grep "error" | sort | head -5`. Each command transforms the output and passes it to the next. MongoDB's aggregation pipeline works the same way.

The syntax is `db.collection.aggregate([stage1, stage2, stage3, ...])`. Each stage is an object with a single key — the stage name — like `$match`, `$group`, `$sort`.

Let me build a pipeline progressively. The goal: find the top 5 authors by number of available books, with their average book price.

```javascript
db.books.aggregate([
  // Stage 1: Filter — only look at available books
  { $match: { available: true } },

  // Stage 2: Group — group by author, count books, calculate average price
  { $group: {
      _id: "$author",                    // group by the author field
      bookCount: { $sum: 1 },            // count one per document
      avgPrice:  { $avg: "$price" },     // average of the price field
      titles:    { $push: "$title" }     // collect all titles into an array
  }},

  // Stage 3: Sort — most books first
  { $sort: { bookCount: -1 } },

  // Stage 4: Limit — top 5 only
  { $limit: 5 },

  // Stage 5: Project — reshape the output
  { $project: {
      _id: 0,                            // exclude _id
      author: "$_id",                    // rename _id to author
      bookCount: 1,                      // include bookCount
      avgPrice: { $round: ["$avgPrice", 2] },  // round to 2 decimal places
      titles: 1
  }}
])
```

Let me walk through each stage. In `$match`, notice it's just a regular query filter — same syntax as `find()`. Always put `$match` first to reduce the number of documents the subsequent stages have to process.

In `$group`, the `_id` is required and defines what you're grouping by. `"$author"` — the dollar sign means "use the value of the author field from the incoming document." Then each accumulator defines what to calculate per group. `$sum: 1` counts documents. `$avg: "$price"` averages the price field. `$push: "$title"` collects all matching book titles into an array.

In `$project`, I'm using computed expressions: `"$_id"` to rename the grouping key to "author", and `$round` to round the average price to 2 decimal places.

Now let me show you `$lookup` — MongoDB's equivalent of a SQL JOIN:

```javascript
db.orders.aggregate([
  { $lookup: {
      from:         "books",          // the other collection
      localField:   "bookId",         // field in the current collection (orders)
      foreignField: "_id",            // matching field in the other collection (books)
      as:           "bookDetails"     // name for the output array
  }},
  { $unwind: "$bookDetails" }         // flatten the array since lookup returns an array
])
```

`$lookup` always produces an array — even if there's only one match. `$unwind` deconstructs that array so each resulting document has a single embedded object instead of a single-element array.

`$unwind` has another use case: exploding an array into multiple documents. If a book has `tags: ["java", "oop", "design"]`, after `$unwind: "$tags"` you get three documents — one per tag. This is useful for analytics:

```javascript
// Count how many books have each tag
db.books.aggregate([
  { $unwind: "$tags" },
  { $group: { _id: "$tags", count: { $sum: 1 } } },
  { $sort: { count: -1 } }
])
```

**Performance tip for aggregation:** Put `$match` and `$sort` with indexed fields at the very beginning of the pipeline. MongoDB can use indexes for the first `$match` and `$sort` stages — once documents are in the pipeline, indexes no longer help.

---

**[47:00–53:30] — Indexes and explain()**

Slides ten and eleven. Indexes are what make MongoDB fast at scale. Without an index, every query does a full collection scan — reading every document to find matches. With an index, MongoDB jumps directly to the relevant documents.

The default index — `_id` — is always there. For everything else, you create indexes explicitly.

```javascript
// Index on author — speeds up find({author: "..."})
db.books.createIndex({ author: 1 })   // 1 = ascending, -1 = descending

// Compound index — for queries filtering on both author AND price
db.books.createIndex({ author: 1, price: -1 })

// Text index — for $text search
db.books.createIndex({ title: "text", author: "text" })

// Unique — enforce uniqueness (like a SQL UNIQUE constraint)
db.books.createIndex({ isbn: 1 }, { unique: true })

// TTL — automatically delete documents after 1 hour
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })
```

One important concept for compound indexes: the **leftmost prefix rule**. A compound index on `{ author: 1, price: -1 }` can be used by:
- Queries filtering on `author` alone ✅
- Queries filtering on `author` AND `price` ✅
- Queries filtering on `price` alone ❌

Think of it like a phone book sorted by last name, then first name. You can look up by last name, or by last name and first name. But you can't look up by first name alone — you'd still have to scan everything.

To see if your query is using an index, use `explain()`:

```javascript
db.books.find({ author: "Robert Martin" }).explain("executionStats")
```

Look for the `stage` field in the output. `IXSCAN` means an index was used. `COLLSCAN` means a full collection scan was performed — and for large collections, that's a performance problem.

Also look at `totalDocsExamined` vs `nReturned`. If MongoDB examined 10,000 documents to return 1, your query isn't selective and needs a better index. The ideal is `totalDocsExamined ≈ nReturned`.

One warning: don't over-index. Every index consumes disk space and must be updated on every write. A collection with 10 indexes is slower to write to than one with 3. Index the fields you actually filter and sort on — not everything.

---

**[53:30–57:00] — MongoDB Atlas**

Slide twelve. MongoDB Atlas is the cloud-hosted version of MongoDB. Free M0 tier gives you 512 MB and zero cost — no credit card required. For this course, local Docker is fine, but Atlas is what you'd use when deploying to production or sharing a dev database with a team.

Setup takes about 5 minutes: create an account at cloud.mongodb.com, create a free M0 cluster, set a database username and password — not your Atlas account password, separate credentials — and add your IP address under Network Access.

The connection string looks like: `mongodb+srv://username:password@cluster0.abc12.mongodb.net/bookstoredb`

The `+srv` part means it's using a DNS SRV record lookup to find the cluster nodes. This is Atlas-specific. For local MongoDB, you use `mongodb://localhost:27017/bookstoredb`.

I'll have you set up an Atlas account as part of today's lab. It's worth doing because Atlas is what you'll use in real projects.

Quick note on Atlas Search and Atlas Vector Search — if you remember from Day 34 when we talked about RAG and vector databases, MongoDB Atlas Vector Search is one of the supported vector store backends in Spring AI. So there's a direct connection between what we built in Day 34 and what you're learning now.

---

**[57:00–60:00] — Spring Data MongoDB and Closing**

Slides thirteen through sixteen. Let me walk through the Spring Data MongoDB integration. It follows the same patterns you know from Spring Data JPA — `@Document` instead of `@Entity`, `MongoRepository` instead of `JpaRepository`.

The dependency is `spring-boot-starter-data-mongodb`. Connection is set in `application.yml` with `spring.data.mongodb.uri`.

Your entity class uses `@Document(collection = "books")` to map to a collection. `@Id` marks the identifier field — if it's a Java `String`, Spring Data automatically converts between the Java String and MongoDB's BSON ObjectId type. You don't have to manage that conversion yourself.

`MongoRepository` gives you all the standard CRUD methods plus derived query methods — just like JPA. `findByAuthor`, `findByPriceLessThan`, `findByTagsContaining` — these all work by the method name parsing convention you already know.

For complex queries, `MongoTemplate` with `Criteria` gives you full programmatic control. `Criteria.where("available").is(true).and("price").lte(maxPrice)` — it reads like English and maps directly to MongoDB's query language.

The update API — `new Update().set("price", 34.99).inc("stock", -1).currentDate("updatedAt")` — maps directly to MongoDB's update operators. Same concepts, Java API.

Alright — that's Day 35. You now have a complete MongoDB toolkit from the shell to Spring Boot. Tonight's lab: build a Spring Boot application with a `Book` collection, a `BookRepository`, and a few endpoints — find by tag, update price, delete by ISBN. Use Atlas or local Docker.

Tomorrow is Docker and Kubernetes in Week 8. See you then.

---

*[End of Part 2 Script — approximately 60 minutes]*
