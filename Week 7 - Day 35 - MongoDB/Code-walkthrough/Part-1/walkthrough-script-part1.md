# Day 35 — MongoDB | Walkthrough Script — Part 1
## NoSQL Concepts, MongoDB Architecture, Documents, BSON & mongosh Basics
**Estimated time:** 90 minutes  
**Files:** `01-nosql-and-mongodb-concepts.md`, `02-mongosh-basics.js`

---

## Pre-Class Checklist

- [ ] MongoDB Community Server installed and running (`mongod` process active)
- [ ] `mongosh` installed and can connect (`mongosh` at terminal — should show a prompt)
- [ ] Both files open in the editor
- [ ] Whiteboard or screen available for the architecture diagram

---

## Opening (3 min)

> "We've spent the last several weeks working with relational databases — PostgreSQL, MySQL, SQL. Tables, rows, columns, JOINs. That's been the dominant database paradigm for 50 years, and it's still the right choice for a lot of problems.
>
> But today we're going to look at a fundamentally different approach to storing data. One that was born out of the demands of modern web applications — social media, e-commerce catalogs, real-time feeds, IoT sensor data.
>
> Today is MongoDB day. By the end of Part 1 you'll understand why NoSQL databases exist, how MongoDB thinks about data, and you'll be running commands in the MongoDB shell. Let's start with the big picture."

---

## Section 1 — NoSQL Overview and Types (12 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 1

---

### 1.1 What Is NoSQL? (4 min)

> "Open `01-nosql-and-mongodb-concepts.md`. Let's read through Section 1.
>
> NoSQL — 'Not Only SQL.' Not a rejection of SQL, just an acknowledgment that SQL isn't the only answer.
>
> Here's the context for why NoSQL emerged. When Facebook launched in 2004, they had a few thousand users. By 2008, they had 100 million. The database had to handle billions of status updates, friend connections, and photos. Traditional relational databases are excellent, but they were designed to scale *up* — buy a bigger, more expensive server. NoSQL databases were designed to scale *out* — add more cheaper servers horizontally.
>
> But scale isn't the only driver. The other big one is flexibility. If you're building a product catalog where every product has different attributes — a book has ISBN, an electronics product has wattage, a clothing item has size — a fixed SQL schema makes you add nullable columns for every possible attribute. Documents handle this naturally."

---

### 1.2 The Four Types (8 min)

Point to the types table:

> "Four main categories. Let me walk through each.
>
> **Document stores** — this is MongoDB's category. Data is stored as JSON-like documents with nested structure. Perfect for anything that maps naturally to a JSON object — user profiles, product catalogs, blog posts, orders.
>
> **Key-value stores** — the simplest possible data model. A key maps to a value. Redis is the most famous example. Blazing fast — Redis can do millions of reads per second from memory. You've used Redis for caching in Spring Boot apps.
>
> **Column-family stores** — Apache Cassandra is the big one here. Data is stored by rows, but each row can have different columns. Cassandra powers the backend of Netflix's viewing history and Discord's message storage. Designed for write-heavy workloads at massive scale.
>
> **Graph databases** — Neo4j. Nodes and edges. Perfect when the relationships ARE the data — fraud detection (is this transaction connected to known fraud networks?), recommendation engines (users who liked X also liked Y), social networks."

> **Ask the class:** "If you were building a Twitter-like feed — posts, likes, followers — which database type might be most interesting? And why might graph databases be useful for the follower graph?" *(Let 2-3 students answer)*

> "Today we're focused on MongoDB — document stores. It's the most commonly used NoSQL database in enterprise Java development and the one that integrates most cleanly with Spring Boot."

---

## Section 2 — SQL vs NoSQL Comparison (8 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 2

---

> "Let's look at the comparison table. I want to walk through the most important rows.
>
> **Schema** — in SQL, you define your table structure *before* you insert any data. `CREATE TABLE` first, then `INSERT`. In MongoDB, you insert your first document and the collection is created automatically. The document defines its own structure. No migration needed to add a field — just include it in the next insert.
>
> **Relationships** — SQL has foreign keys and JOINs. MongoDB's approach is different: either embed related data inside the document (so one query returns everything), or use `$lookup` (MongoDB's version of a JOIN) when you need to reference separate collections.
>
> **Scaling** — this is a fundamental architectural difference. SQL databases scale *vertically* by default — buy a better server. MongoDB is designed from the ground up for *horizontal* scaling — spread data across multiple servers via sharding.
>
> **ACID transactions** — historically this was MongoDB's weakness. Modern MongoDB (4.0+) supports multi-document ACID transactions, but SQL still has the edge for transaction-heavy workloads like banking."

> "Read 'When to Choose MongoDB'. The key insight: most modern applications use BOTH. You might use PostgreSQL for your order processing and financial records (strict ACID, complex queries) and MongoDB for your product catalog and user profiles (flexible schema, fast reads)."

> **Watch out:** "A very common mistake is trying to use MongoDB for everything or SQL for everything. The right answer is always 'it depends on the use case.' Learn to articulate when you'd choose each one — this comes up in every technical interview."

---

## Section 3 — MongoDB Architecture and Concepts (10 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 3

---

> "Now let's understand MongoDB's architecture. Look at the diagram in Section 3.
>
> At the top level: `mongod` is the server process. Think of it like the PostgreSQL server — the thing that actually manages the data on disk. You start it as a service, and it listens on port 27017 by default.
>
> Inside mongod, you have databases. Our bookstore app has `bookstore_db`. Each database contains collections. Collections are roughly analogous to SQL tables — but without a schema.
>
> Inside each collection are documents. Each document is a JSON object with an `_id` field plus whatever fields you add.
>
> `mongosh` is the shell — the CLI client you use to talk to mongod. Think of it like `psql` for PostgreSQL. You'll use it for exploration, debugging, and today's exercises.
>
> The key table is the component list. Let's read it together."

Read through the component table, elaborating:
> "Compass is the GUI version of mongosh — useful for visualizing document structure. Drivers are how your Java/Spring Boot application connects. In Part 2 we'll use the Spring Data MongoDB driver."

---

## Section 4 — Document-Oriented Data Model (15 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 4

---

### 4.1 SQL Row vs MongoDB Document (8 min)

> "This is the most important conceptual shift of the day. Look at Section 4 — the SQL vs MongoDB example.
>
> In SQL, we define a flat `books` table. Every book has exactly these columns. If we want to add `author_nationality`, we run an ALTER TABLE on potentially millions of rows.
>
> Now look at the MongoDB document. The same book looks very different. The author is a nested object with both `name` and `nationality`. `genres` is an array — a book can have multiple genres in a single field. `editions` is an array of objects — we've embedded the edition history directly inside the book document.
>
> In SQL, to get this data you'd need a `books` table, an `authors` table, a `genres` table, a `book_genres` junction table, and an `editions` table. Five tables, four JOINs, just to read one book. In MongoDB: one document, one query."

> **Ask the class:** "Can you think of any data that would be very awkward to store in SQL because it's variable or nested?" *(Good answers: user settings/preferences, product attributes that vary by category, event logs with variable payloads)*

---

### 4.2 Embedded vs Referenced Documents (7 min)

Point to the diagram:

> "One of the key design decisions in MongoDB schema design: do you embed related data, or reference it?
>
> **Embedded:** The order document contains the items array right inside it. One query gets everything. Updates are atomic. The downside: if book prices change, you've captured the price at time of order — which is actually *correct* for an order! You want order history to reflect what the customer paid.
>
> **Referenced:** The order stores a `bookId` that points to the books collection — just like a foreign key. No data duplication. But to show the order with book details, you need a `$lookup` — MongoDB's equivalent of a JOIN.
>
> The rule of thumb: embed data you always read together, reference data that's shared or grows unboundedly. A customer can have many addresses — embed them. An order references books — those books exist independently across many orders."

> **Watch out:** "Students often try to model MongoDB just like SQL — every reference becomes a `$lookup`. Resist that instinct. The power of MongoDB is denormalization — embed data to avoid lookups. Design for your read patterns, not for theoretical normalization."

---

## Section 5 — BSON Format (7 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 5

---

> "Quick but important: BSON. You write JSON — MongoDB stores BSON.
>
> BSON is a binary-encoded version of JSON. It's faster to parse than text JSON and adds data types that JSON doesn't have: ObjectId, proper Date types, 32-bit and 64-bit integers, binary data.
>
> Look at the comparison table. The practical implication: when you write `new Date()` in mongosh or `new Date("2024-01-15")` in your app, MongoDB stores it as a proper date type, not a string. That means you can query 'find all books published after January 2024' and MongoDB compares real dates, not alphabetical string comparison.
>
> You'll also see `NumberInt(5)` and `NumberLong(12345)` in mongosh — these explicitly create int32 and int64 types rather than JavaScript's default float64. Usually you don't need to worry about this — Spring Data MongoDB handles type mapping automatically."

---

## Section 6 — Collections and Documents (5 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Sections 6 & 7

---

> "The SQL-to-MongoDB vocabulary mapping. Point to the table.
>
> Database → Database. Table → Collection. Row → Document. Column → Field. Primary Key → `_id`. INDEX → Index. JOIN → `$lookup`.
>
> The three document rules worth highlighting: every document must have `_id`, documents in the same collection can have different fields, and max size is 16 MB. For large files like book cover images or PDFs — use GridFS, MongoDB's file storage system."

---

## Section 7 — ObjectId Deep Dive (8 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 7

---

> "ObjectId is worth understanding deeply because it's everywhere in MongoDB. Look at the breakdown.
>
> 12 bytes total. The first 4 bytes are a Unix timestamp — seconds since January 1, 1970. Then 5 bytes of random machine/process identifier. Then 3 bytes of an auto-incrementing counter.
>
> What does that give us? Two things: global uniqueness without a central counter, and chronological sortability.
>
> **Why not auto-increment like SQL?** SQL's auto-increment needs a central sequence generator. Every INSERT asks 'what's the next number?' That's fine for a single database server. But MongoDB is designed for clusters of servers. If ten servers are all inserting documents simultaneously, there's no single counter to ask. ObjectId is generated *locally* by the driver without any network call — it's still globally unique because of the machine identifier bytes."

> **Ask the class:** "If you have two ObjectIds and you want to know which document was inserted first, how would you find out?" *(Answer: ObjectId with the smaller hex value (earlier bytes) was inserted first — ObjectId has a timestamp baked in, so you can call `.getTimestamp()` on it)*

> "You can also use custom `_id` values if you prefer — the ISBN string, or an integer. MongoDB doesn't require ObjectId. But ObjectId is the safest default for scalable applications."

---

## Section 8 — Installation Reference (3 min)
**File:** `01-nosql-and-mongodb-concepts.md` — Section 8

---

> "The installation commands are in Section 8 — this is a reference. We've already confirmed MongoDB is running, but for the interview or for setting up a new project: Homebrew on Mac, apt on Ubuntu, installer on Windows. MongoDB runs on port 27017 by default.
>
> Check: `mongod --version` for the server, `mongosh --version` for the shell. Both should return a version number. If `mongosh` connects and shows a prompt, you're good."

---

## Section 9 — mongosh Hands-On Walkthrough (20 min)
**File:** `02-mongosh-basics.js`

---

> "Switch to `02-mongosh-basics.js`. We're going to run every command in this file. Open your terminal and connect to MongoDB."

Run in your terminal:
```
mongosh
```

> "You should see the mongosh prompt. Type `db` and press enter."

---

### 9.1 Navigation (3 min)

> "Section 1 of the file. Let's run `show dbs`. You'll see admin, config, local — MongoDB's internal databases. Don't touch those.
>
> Now `use bookstore_db`. Notice the output: 'switched to db bookstore_db'. Now type `db` — it confirms we're in `bookstore_db`. MongoDB doesn't create this database yet — it will create it the moment we insert our first document."

---

### 9.2 Inserting Documents (8 min)

> "Section 2. First, explicit collection creation with `db.createCollection('books')`. You'll get `{ ok: 1 }`. But honest answer — most developers skip this and let the collection auto-create on first insert.
>
> Try inserting into a collection that doesn't exist yet — `customers`. Copy and run the `insertOne` for Alice Johnson."

Run the customer insert.

> "Look at the output: `insertedId: ObjectId('...')`. MongoDB generated that `_id` automatically. We didn't provide one. Copy that ObjectId — we'll use it in a moment.
>
> Now let's insert one book with `insertOne`. Look at how rich this document is: nested `author` object, `genres` array, `editions` array of objects, multiple scalar fields. This is the document model in action."

Run the book `insertOne`.

> "Now `insertMany` — run the bulk insert of 4 more books. The response includes `insertedIds` — one ObjectId per inserted document."

Run `insertMany`.

---

### 9.3 ObjectId Exploration (4 min)

> "Section 3. Let's explore ObjectId interactively. Type: `let id = new ObjectId()`"

Run `new ObjectId()`, then `id.getTimestamp()`:

> "See that? The ObjectId contains the time it was created. If you ever need to find documents inserted in a date range without a dedicated `createdAt` field, you can query by ObjectId range. That's a neat trick.
>
> Now — when you need to query a document by its `_id`, you must wrap the hex string in `ObjectId()`. This trips up almost every beginner. `db.books.findOne({ _id: '64a1...' })` will return null even if that document exists, because the stored type is ObjectId, not string. You need `ObjectId('64a1...')`."

---

### 9.4 Reading Documents (5 min)

> "Section 4 — reading data. Run `db.books.find()`. This returns all documents. In mongosh the cursor auto-iterates — in older versions you'd type `it` to page through results.
>
> Now projection: `db.books.find({}, { title: 1, price: 1, _id: 0 })`. The `{}` first argument is the filter — empty means all documents. The second argument is the projection: 1 means include, 0 means exclude. We're explicitly excluding `_id` with `_id: 0`.
>
> Dot notation: `db.books.find({ 'author.name': 'Stephen Hawking' })`. The single quotes around `author.name` are required — it's a string key with a dot in it."

Run sort, limit, and skip examples:

> "`.sort({ price: -1 })` — minus one means descending. `.limit(3)` — first three. `.skip(2).limit(3)` — this is pagination. Page 1 is skip 0 limit 3, page 2 is skip 3 limit 3, etc. The pattern at the bottom of Section 4g shows how to compute skip from a page number."

---

### Section 9.5 Wrap-Up (2 min)

> "Run `show collections` — you should see both `books` and `customers`. Run `db.books.countDocuments()` to confirm all your inserts landed.
>
> We've covered the conceptual foundation and the basic shell operations. In Part 2 we'll go deeper — full CRUD with all the update and delete operations, complex query operators, the aggregation pipeline, indexes, Atlas, and how to connect all of this to Spring Boot."

---

## Quick Check (5 min — end of Part 1)

Ask the class these questions before break:

1. **"What's the MongoDB equivalent of a SQL table?"** *(Collection)*

2. **"If I insert a document without specifying `_id`, what happens?"** *(MongoDB auto-generates an ObjectId)*

3. **"You have a SQL query: `SELECT title, price FROM books WHERE inStock = true`. Write the equivalent MongoDB query."** *(db.books.find({ inStock: true }, { title: 1, price: 1 }))* 

4. **"In MongoDB's document model, would you embed book editions inside the book document, or create a separate `editions` collection? Justify your answer."** *(Embed — editions are always read with the book, they belong to one book, they don't grow unboundedly)*

5. **"Why can't MongoDB use a simple auto-incrementing integer for `_id` like SQL does?"** *(Auto-increment requires a central counter; MongoDB is designed for distributed clusters where multiple servers insert simultaneously without coordination)*

---

## Transition to Part 2

> "Great questions. Take a 10-minute break. When we come back, we're going hands-on with the full MongoDB query language — updates, deletes, operators, aggregation pipelines — and then we'll wire MongoDB up to a Spring Boot application."
