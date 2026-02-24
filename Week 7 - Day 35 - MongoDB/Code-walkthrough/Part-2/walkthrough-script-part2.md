# Day 35 — MongoDB | Walkthrough Script — Part 2
## CRUD, Query Operators, Aggregation Pipeline, Indexes, Atlas & Spring Data MongoDB
**Estimated time:** 90 minutes  
**Files:** `01-crud-and-query-operators.js`, `02-aggregation-pipeline.js`, `03-indexes-and-atlas.js`, `04-spring-data-mongodb.java`

---

## Pre-Class Checklist

- [ ] mongosh connected and `bookstore_db` seeded (run Section 1 of `01-crud-and-query-operators.js`)
- [ ] Spring Boot project ready with `spring-boot-starter-data-mongodb` dependency
- [ ] All four files open
- [ ] Postman or REST client ready for the Spring Boot demo

---

## Opening (2 min)

> "In Part 1 we built the conceptual foundation — NoSQL, document model, BSON, ObjectId, and basic find queries.
>
> Part 2 is where you get productive. We're going to cover the complete MongoDB query toolkit: updates, deletes, operators, the aggregation framework, indexes, MongoDB Atlas, and how to connect all of this to a Spring Boot application.
>
> Let's start at the beginning of the CRUD lifecycle: we've done inserts and reads. Now let's update and delete."

---

## Section 1 — CRUD: Updates (20 min)
**File:** `01-crud-and-query-operators.js` — Sections 2–3

---

### 1.1 updateOne with $set (5 min)

> "Open `01-crud-and-query-operators.js`. If you're starting fresh, run Section 1 first to seed all the books — it's the big `insertMany` at the top.
>
> Scroll to Section 2. The pattern for `updateOne` is two required arguments: a filter document that selects which record to update, and an update document that describes what to change.
>
> The `$set` operator is the most common update operator. It sets specific fields without touching anything else. Compare this to SQL's `UPDATE books SET price = 37.99 WHERE title = 'Clean Code'`."

Run `updateOne` with `$set`:

> "Notice the output: `matchedCount: 1, modifiedCount: 1`. If nothing matches the filter, `matchedCount` is 0 and nothing changes. If the document already has that value, `matchedCount` is 1 but `modifiedCount` is 0."

---

### 1.2 $inc, $unset, $push, $pull (7 min)

> "Scroll to `$inc`. This increments a numeric field in a single atomic operation. The classic use case: someone posts a new review. You don't fetch the document, add 1 in Java, and save — that's a race condition. You use `$inc` which MongoDB applies atomically.
>
> `$unset` removes a field entirely. Run the `pageCount` unset example, then verify with `findOne`. The field is just gone from that document. The other books still have `pageCount`. This is the schema flexibility of MongoDB in action — one document doesn't have a field that others have.
>
> Array operators: `$push` adds an element to an array. `$addToSet` does the same but skips duplicates — it's a 'set insert'. Run both examples — notice how `$addToSet` on 'programming' (already exists) doesn't add a duplicate. `$pull` removes elements matching a value."

---

### 1.3 updateMany and upsert (5 min)

> "`updateMany` is the bulk version. Run the example that sets `isOnSale: false` on all books over $40. Notice the output: `matchedCount: 2` — it found and updated both matching books.
>
> Upsert — this is powerful. Look at the `updateOne` with `{ upsert: true }`. If the filter matches an existing document, it updates. If nothing matches, it *inserts* a new document. The output tells you which happened — if inserted, you get `upsertedId`. If updated, `modifiedCount: 1`.
>
> This is the 'create or update' pattern without needing separate findById + conditional logic."

> **Watch out:** "Students sometimes confuse `updateOne` and `replaceOne`. `updateOne` with `$set` modifies specific fields. `replaceOne` REPLACES the entire document — only `_id` is preserved. Only use `replaceOne` when you intentionally want to replace the whole thing."

---

### 1.4 Delete Operations (3 min)

> "Section 3 — deletes. `deleteOne` deletes the first match. `deleteMany` deletes all matches.
>
> `findOneAndDelete` returns the deleted document AND removes it in one atomic operation. This is useful for task queues — 'claim the next unprocessed task and remove it from the queue atomically'.
>
> ⚠️ Warning: `db.books.deleteMany({})` with an empty filter deletes EVERYTHING in the collection. MongoDB doesn't prompt for confirmation. Always double-check your filter before running a deleteMany."

---

## Section 2 — Query Operators (18 min)
**File:** `01-crud-and-query-operators.js` — Sections 4–8

---

### 2.1 Comparison Operators (5 min)

> "Section 4 — comparison operators. These all follow the same pattern: `{ field: { $operator: value } }`.
>
> `$gt`, `$gte`, `$lt`, `$lte` — greater than, greater than or equal, less than, less than or equal. These are identical to SQL's `>`, `>=`, `<`, `<=`.
>
> `$ne` — not equal. Use it to exclude specific values.
>
> The last example shows combining conditions: `{ price: { $gte: 15, $lte: 40 } }` — price is between 15 and 40. Multiple operators on the same field are implicitly ANDed."

Run a few examples live:

> "Run `db.books.find({ price: { $gt: 40 } })` — you should get 'Designing Data-Intensive Applications' at $55. Run `db.books.find({ price: { $gte: 15, $lte: 40 } })` — you should get the middle-priced books."

---

### 2.2 Array Operators (5 min)

> "Section 5 — array operators. Arrays are first-class citizens in MongoDB.
>
> `$in` with an array field like `genres` asks: 'does this book's genre array contain any of these values?' Run the genres `$in` example. Think of it as 'OR membership in any of these values'.
>
> `$all` is the opposite: 'the array must contain ALL of these values'. So `{ genres: { $all: ['fiction', 'philosophy'] } }` only returns books tagged as BOTH fiction AND philosophy.
>
> `$elemMatch` is for arrays of objects — when you need to match multiple conditions on the SAME array element. For example, find editions where `year >= 2010` AND `format == 'paperback'`. Without `$elemMatch`, MongoDB might match a document where one element has year 2015 and a *different* element has format 'paperback'. `$elemMatch` ensures both conditions apply to the same element."

---

### 2.3 Logical Operators (4 min)

> "Section 6 — `$and`, `$or`, `$nor`, `$not`.
>
> Implicit AND is the default. `{ inStock: true, price: { $lt: 30 } }` is already an AND. You only need explicit `$and` when you need two operators on the same field — like checking if `genres` contains 'programming' AND also contains 'career'.
>
> `$or` — at least one condition must be true. Books that are cheap OR highly rated.
>
> `$nor` — none of the conditions are true. This is a strong exclusion filter.
>
> `$not` negates a single condition. It's less common — usually `$ne` or the inverse operator is cleaner — but useful with `$regex`."

---

### 2.4 $exists, $type, $regex (4 min)

> "Section 7 — element operators. `$exists` checks whether a field is present in the document at all. Remember how we removed `pageCount` from Clean Code with `$unset`? `{ pageCount: { $exists: false } }` finds that document. This is unique to MongoDB — you can't query for 'missing columns' in SQL because every row has every column.
>
> Section 8 — `$regex`. MongoDB supports full regular expressions on string fields. The `/code/i` syntax is a JavaScript regex literal — `i` means case-insensitive. You can also use a string: `{ $regex: 'code', $options: 'i' }`.
>
> The last example combines multiple operators in one realistic query: in-stock programming books under $40 with rating above 4.5. This is what production queries look like."

---

## Section 3 — Aggregation Pipeline (22 min)
**File:** `02-aggregation-pipeline.js`

---

### 3.1 What Is the Pipeline? (4 min)

> "Switch to `02-aggregation-pipeline.js`. Read Section 1.
>
> The aggregation framework is MongoDB's answer to SQL's GROUP BY, JOIN, computed columns, and window functions. It processes documents through a sequence of stages — each stage transforms the documents and passes them to the next.
>
> The SQL-to-MongoDB mental map in Section 1 is key. `$match` = WHERE. `$group` = GROUP BY with aggregations. `$project` = SELECT. `$sort` = ORDER BY. `$lookup` = JOIN. `$unwind` = explode an array into multiple rows.
>
> The pipeline concept is very similar to Java Streams: `stream().filter().map().sorted().collect()`. Each operation returns a new stream. MongoDB's pipeline is the same idea but for database documents."

---

### 3.2 $match (2 min)

> "`$match` is always the best first stage. Filter early, reduce the number of documents flowing through subsequent stages, and leverage indexes. Put `$match` before `$group` whenever possible — it's the equivalent of adding a WHERE clause before a GROUP BY."

---

### 3.3 $group — The Power Stage (6 min)

> "Section 3 — `$group`. This is the most powerful aggregation stage.
>
> The `_id` field in `$group` is what you're grouping BY. `_id: null` means the entire collection is one group — useful for getting totals and averages across everything.
>
> Run the first group example: `{ $group: { _id: null, totalBooks: { $sum: 1 }, ... } }`. The `$sum: 1` counts documents — each doc contributes 1 to the sum. `$avg: '$price'` averages the price field. The `$` prefix on field names means 'the value of this field from the document'.
>
> Now run the group by `inStock`. You get two groups: true and false, each with their own count and average price. And the `$push: '$title'` accumulates all titles into an array — you get to see exactly which books are in each group.
>
> The dollar sign before field names is critical. Without `$`: it's a string literal. With `$`: it means 'the value of this field'. `_id: '$inStock'` groups by the value of the `inStock` field. `_id: 'inStock'` would make one group with the literal string 'inStock' as the key."

> **Watch out:** "The most common error when writing `$group` is forgetting the `$` prefix on field references. `{ $avg: 'price' }` returns null — MongoDB thinks 'price' is a string literal. `{ $avg: '$price' }` returns the average of the price field."

---

### 3.4 $project and Computed Fields (3 min)

> "`$project` reshapes the output documents — like SQL's SELECT clause. 1 includes a field, 0 excludes it.
>
> The real power is computed fields. Run the discounted price example: `discountedPrice: { $multiply: ['$price', 0.9] }`. MongoDB computes 10% off for every document. `$divide` works similarly — the `pricePerPage` calculation shows cost efficiency. These are exactly the kinds of derived columns you'd compute in SQL's SELECT with expressions."

---

### 3.5 $unwind (4 min)

> "Section 6 — `$unwind`. This is unique to document databases because SQL tables don't have array fields.
>
> Look at the 'Before $unwind' and 'After $unwind' comments. Clean Code has `genres: ['programming', 'software-engineering']`. After `$unwind: '$genres'`, you get TWO documents — one with `genres: 'programming'` and one with `genres: 'software-engineering'`.
>
> Why is this useful? Because after unwinding, you can `$group` by the single genre value. Run the full example: unwind genres, then group by genre to count how many books belong to each."

> **Ask the class:** "Without `$unwind`, if you try to `$group` by the genres array field, what do you get?" *(Each unique array combination becomes a group key — `['programming', 'career']` is one key, `['programming', 'software-engineering']` is another. You can't count per-genre. $unwind is what makes per-element aggregation possible.)*

---

### 3.6 $lookup — The Join (3 min)

> "Section 7 — `$lookup`. This is MongoDB's JOIN. You need the `orders` collection with customer references for this.
>
> Run the customer inserts and orders inserts in Section 7. Then run the `$lookup` aggregate.
>
> The four fields: `from` is the other collection, `localField` is the field in your current document, `foreignField` is the matching field in the other collection, `as` is the name of the new array field that's added.
>
> The result: each order document gets a `customerInfo` array containing the matching customer. It's an array because MongoDB supports one-to-many lookups. When you know it's one-to-one, add `$unwind` right after to flatten the array into a single object."

---

## Section 4 — Indexes (12 min)
**File:** `03-indexes-and-atlas.js`

---

### 4.1 Why Indexes Matter (4 min)

> "Switch to `03-indexes-and-atlas.js`. Read Section 1.
>
> Without an index, every query triggers a collection scan — MongoDB reads every document and evaluates your filter against each one. For 7 books: instant. For 10 million products: seconds or worse.
>
> The B-tree analogy: imagine you need to find all books with price > $30 in a sorted list of prices. With an index, you binary-search to the $30 mark and read forward. Without an index, you scan every item.
>
> The tradeoff is important: indexes make reads fast but writes slightly slower because MongoDB must update the index on every insert, update, and delete. Don't over-index. Create indexes for your most common query patterns."

---

### 4.2 explain() — See the Query Plan (4 min)

> "Before creating an index, let's see the query without one. Run `db.books.find({ title: 'Clean Code' }).explain('executionStats')`.
>
> Look for `'stage': 'COLLSCAN'` — collection scan. And `totalDocsExamined: 7` — it scanned all 7 documents to find the one match.
>
> Now run `db.books.createIndex({ title: 1 })` and run the same explain again. Now you should see `'stage': 'IXSCAN'` and `totalDocsExamined: 1`. MongoDB went straight to the right document via the index."

---

### 4.3 Index Types (4 min)

> "Run through the index creation examples. `unique: true` on isbn creates a uniqueness constraint — MongoDB throws a duplicate key error if you try to insert two books with the same ISBN. Same concept as `UNIQUE` in SQL.
>
> Dot notation for nested fields: `'author.name': 1` — works exactly as expected.
>
> Compound indexes: `{ inStock: 1, price: 1 }` optimizes the query 'find in-stock books sorted by price'. One index covers both the filter AND the sort. No collection scan needed.
>
> Text index for full-text search. `{ title: 'text', genres: 'text' }` enables `$text: { $search: '...' }` queries. MongoDB tokenizes the text, ignores stop words (the, a, and), and matches semantically. Run the examples — notice it's case-insensitive and multi-word search returns all documents containing any of the words."

---

## Section 5 — MongoDB Atlas (3 min)
**File:** `03-indexes-and-atlas.js` — Section 8

---

> "Section 8 — Atlas. This is the cloud-managed MongoDB service. Read through it.
>
> The short version: Atlas handles all the operational work — replication, backups, patching, scaling. Free tier (M0) gets you 512 MB of storage — more than enough to build and deploy a real app.
>
> The key thing for Spring Boot: the Atlas connection string format — `mongodb+srv://...`. Drop that into `spring.data.mongodb.uri` and your app connects to Atlas exactly the same as local MongoDB. The driver handles the SRV DNS lookup and TLS automatically.
>
> Many production Spring Boot applications run against Atlas. You'll use it in your capstone project if you want a cloud-hosted MongoDB."

---

## Section 6 — Spring Data MongoDB (13 min)
**File:** `04-spring-data-mongodb.java`

---

### 6.1 Setup and @Document (3 min)

> "Switch to `04-spring-data-mongodb.java`. Section 1 — dependencies and config. One dependency: `spring-boot-starter-data-mongodb`. Connection via `spring.data.mongodb.uri`. That's it — Spring Boot auto-configures the MongoDB connection and all the repositories.
>
> Section 2 — `@Document`. This is Spring Data MongoDB's equivalent of JPA's `@Entity`. `collection = 'books'` maps to the MongoDB collection. `@Id` maps to the `_id` field. Spring Data uses `String` for the ID type — it automatically converts between Java `String` and MongoDB `ObjectId`.
>
> The `Author` class has no `@Document` — it's not a top-level collection. It's an embedded object stored inside book documents."

---

### 6.2 MongoRepository (5 min)

> "Section 3 — `BookRepository extends MongoRepository<Book, String>`. This should look familiar from Spring Data JPA. Same derived query method naming convention — Spring Data generates the implementation based on the method name.
>
> Look at `findByGenresContaining` — this generates a query to find documents where the `genres` array contains the given value. MongoDB arrays get first-class support in Spring Data.
>
> `findByAuthorName` uses nested field access — Spring Data translates this to a dot-notation query on `author.name`.
>
> The `@Query` annotation lets you write raw MongoDB queries when the derived method name can't express what you need. `?0` refers to the first parameter. The `fields` attribute on the second `@Query` is a MongoDB projection — return only title, price, and rating."

> **Ask the class:** "What's the main difference between Spring Data MongoDB's MongoRepository and JPA's JpaRepository in terms of query generation?" *(The method names follow the same convention, but the generated queries are MongoDB queries — not SQL. JPA generates JPQL/SQL, Spring Data MongoDB generates MongoDB filter documents.)*

---

### 6.3 MongoTemplate (5 min)

> "Section 4 — `MongoTemplate`. This gives you direct, fine-grained access to MongoDB operations — like JPA's EntityManager or JDBC's JdbcTemplate.
>
> `incrementReviewCount` — the key advantage here is not fetching the full document. `mongoTemplate.updateFirst()` sends a single `{ $inc: { reviewCount: 1 } }` operation to MongoDB. Compare this to: fetch document → increment in Java → save document. The MongoTemplate approach is one network call. The fetch-and-save approach is two calls with a potential race condition.
>
> `findPremiumBooks` shows the `Criteria` builder — a fluent Java API for building MongoDB filter documents. `Criteria.where('rating').gte(minRating)` reads almost like English.
>
> The aggregation method at the end shows the Spring Data aggregation DSL — you build the pipeline using Java objects: `Aggregation.unwind()`, `Aggregation.group()`, `Aggregation.project()`. It's more verbose than writing the pipeline in mongosh but fully type-safe."

---

### 6.4 REST Endpoints Demo (2 min)

> "Section 5 — the REST controller. If your Spring Boot app is running, try these requests in Postman:
>
> - `POST /api/books` with a JSON body — creates a new book, MongoDB assigns an ObjectId
> - `GET /api/books/search?keyword=code` — case-insensitive title search
> - `GET /api/books/genre/programming` — books in a genre
> - `GET /api/books/genre-stats` — the aggregation pipeline result
>
> The response from `POST` will include the `id` field — that's the MongoDB ObjectId as a string. Use it in `GET /api/books/{id}` to fetch that specific book."

---

## Wrap-Up (5 min)

> "Let's review the full MongoDB stack we covered today."

```
MongoDB Day 35 — Complete Coverage
────────────────────────────────────────────────────
Part 1:
  ✅ NoSQL types — document, key-value, column-family, graph
  ✅ SQL vs NoSQL — when to use each
  ✅ MongoDB architecture — mongod, mongosh, collections, documents
  ✅ Document model — embedded vs referenced
  ✅ BSON — binary encoding, extra types
  ✅ ObjectId — structure, timestamp, uniqueness
  ✅ mongosh basics — connect, create, insert, find

Part 2:
  ✅ Full CRUD — updateOne, updateMany, deleteOne, upsert
  ✅ Update operators — $set, $inc, $unset, $push, $pull, $addToSet
  ✅ Query operators — $eq, $gt, $lt, $in, $nin, $all, $elemMatch,
                      $and, $or, $nor, $exists, $type, $regex
  ✅ Aggregation — $match, $group, $project, $sort, $unwind, $lookup
  ✅ Indexes — single-field, compound, text, unique, explain()
  ✅ Atlas — cloud MongoDB overview, connection string
  ✅ Spring Data MongoDB — @Document, MongoRepository, @Query,
                           MongoTemplate, aggregation pipeline
```

---

## Interview Questions

1. **"What is the difference between SQL and NoSQL? When would you choose MongoDB over PostgreSQL?"**  
   *(SQL: fixed schema, relational, ACID; NoSQL: flexible schema, document/key-value/etc., horizontal scale. Choose MongoDB for variable schemas, nested data, horizontal scaling needs. Choose PostgreSQL for strict ACID, complex multi-table joins, financial data.)*

2. **"Explain the MongoDB aggregation pipeline. What stages have you used?"**  
   *($match to filter, $group to aggregate with accumulators like $sum/$avg, $project to shape output, $unwind to flatten arrays, $lookup to join collections, $sort and $limit for ordering and pagination)*

3. **"When would you embed a document vs reference another collection in MongoDB?"**  
   *(Embed: data always read together, belongs to one parent, bounded size. Reference: data shared across many documents, or grows unboundedly. e.g., embed order items in an order; reference customers from orders)*

4. **"What is an index in MongoDB and what tradeoff does it introduce?"**  
   *(B-tree structure on field(s) that allows fast lookup instead of collection scan. Tradeoff: reads are faster, but writes are slightly slower because the index must be updated. Also consumes memory and disk.)*

5. **"How does Spring Data MongoDB's derived query method naming work? Give an example."**  
   *(Spring Data reads the method name and generates the MongoDB query. `findByGenresContaining(String genre)` generates `{ genres: genre }` — an array membership query. `findByPriceLessThan(double max)` generates `{ price: { $lt: max } }`.)*

---

## Cheat Card — MongoDB Day 35

```
SHELL BASICS
  use bookstore_db                              // switch/create db
  db.books.insertOne({ title: "..." })          // insert
  db.books.find({ inStock: true })              // read
  db.books.find({}, { title: 1, _id: 0 })       // with projection
  db.books.countDocuments({ inStock: true })    // count

UPDATE OPERATORS
  $set    — { $set: { price: 39.99 } }
  $inc    — { $inc: { reviewCount: 1 } }
  $unset  — { $unset: { pageCount: "" } }
  $push   — { $push: { genres: "classic" } }
  $pull   — { $pull: { genres: "classic" } }
  $addToSet — no-duplicate push

QUERY OPERATORS
  $gt/$gte/$lt/$lte  — { price: { $gt: 20 } }
  $in / $nin         — { genres: { $in: ["fiction"] } }
  $all               — { genres: { $all: ["a","b"] } }
  $exists            — { pageCount: { $exists: false } }
  $regex             — { title: { $regex: /code/i } }
  $or                — { $or: [{ price: {$lt:15} }, { rating: {$gte:4.9} }] }

AGGREGATION PIPELINE
  [
    { $match:   { inStock: true } },
    { $unwind:  "$genres" },
    { $group:   { _id: "$genres", count: { $sum: 1 }, avg: { $avg: "$price" } } },
    { $project: { genre: "$_id", count: 1, avg: 1, _id: 0 } },
    { $sort:    { count: -1 } }
  ]

INDEXES
  db.books.createIndex({ title: 1 })                    // single
  db.books.createIndex({ inStock: 1, price: 1 })        // compound
  db.books.createIndex({ title: "text" })               // text search
  db.books.createIndex({ isbn: 1 }, { unique: true })   // unique
  db.books.find({}).explain("executionStats")           // check plan

SPRING DATA MONGODB
  @Document(collection = "books")   // map class to collection
  @Id String id;                    // maps to _id
  extends MongoRepository<Book, String>
  findByGenresContaining(String genre)          // derived query
  @Query("{ 'inStock': true, 'price': { $lte: ?0 } }")  // custom query
  mongoTemplate.updateFirst(query, update, Book.class)  // MongoTemplate

ATLAS
  spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/db
```
