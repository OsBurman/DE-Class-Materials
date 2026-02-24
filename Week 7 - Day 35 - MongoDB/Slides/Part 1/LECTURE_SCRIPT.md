# Day 35 Part 1 — MongoDB: NoSQL Fundamentals, Document Model, and mongosh Basics
## Lecture Script

---

**[00:00–01:30] — Welcome and Setup Check**

Good morning, everyone. Today we start Week 7's MongoDB content, and this is a big one — MongoDB is the most widely used NoSQL database in the world and it's extremely common in Spring Boot applications. By end of day you'll have a complete mental model of what MongoDB is, how it works under the hood, how to interact with it from the shell, and tomorrow we'll write full Spring Boot applications on top of it.

Quick housekeeping before we dive in — if you have Docker installed, I want you to run one command right now so we have a MongoDB instance ready. Open a terminal and run: `docker run -d --name mongodb -p 27017:27017 mongo:7.0` — that pulls the official MongoDB image and starts it on the default port 27017. While that downloads, we're going to cover the concepts. Don't worry if you hit issues — I'll walk through a few installation options.

Alright, slide one — our learning objectives for Part 1. Today is conceptual and exploratory. Part 2 is where we write real queries, aggregations, and Spring Boot code. Part 1 builds the foundation that makes Part 2 make sense. These are the nine things I need you to walk away understanding. Let's move.

---

**[01:30–08:30] — What Is NoSQL? The Four Categories**

Slide two. "NoSQL" — let's immediately clarify what this word means, because the name is kind of confusing. NoSQL does NOT mean "we don't use SQL syntax." It means "not only SQL." It's a catch-all term for database systems that depart from the relational model — meaning they don't organize data into tables with fixed columns and foreign key relationships.

NoSQL databases emerged around 2007 to 2010, and they emerged for a specific reason: companies like Amazon, Google, and Facebook were scaling to hundreds of millions of users, and they were hitting walls with traditional relational databases. Not because relational databases are bad — they're excellent — but because the relational model optimizes for a specific set of trade-offs. When you're storing a trillion rows of sensor data, or you need to represent a social graph of a billion people, or you need to cache session tokens for ten million concurrent users — a relational database isn't the best tool.

So different companies built different systems, and each one was optimized for a different problem shape. Over time, these systems fell into four major categories.

Category one: **Document databases.** MongoDB is in this category. So is CouchDB and Google Firestore. These databases store data as JSON-like documents — rich objects that can contain nested objects, arrays, arrays of objects. Think of a product catalog where each product has different attributes, or a user profile where different users have different preferences. Document databases are excellent for hierarchical, semi-structured data where the schema varies from record to record.

Category two: **Key-Value stores.** Redis is the classic example. DynamoDB from AWS is another. The model is dead simple: you have a key, and you have a value. The value can be a blob of anything — a string, a JSON object, a binary file. The reason to use a key-value store is *speed*. Redis can handle millions of operations per second. You use it for session data, caches, leaderboards, shopping carts. The trade-off: you can only look up by key. No complex queries.

Category three: **Column-Family databases.** Apache Cassandra is the dominant example. HBase is another. This model is optimized for very high write throughput and time-series data. Think of sensor readings from millions of IoT devices, or a social media feed where millions of posts are written per second. Cassandra distributes data across many nodes and can handle enormous write loads. The trade-off is that it's harder to query flexibly.

Category four: **Graph databases.** Neo4j is the main one. Amazon Neptune is another. These databases store data as nodes (things) and edges (relationships between things). The killer use case is when the relationships between entities ARE the data — fraud detection networks, social connection traversal, recommendation engines. "Find all my friends-of-friends who bought this product" — that's a graph problem.

Now — take a look at the table on the slide. What you'll notice is that each category matches a problem shape. There's no single winner. In fact, most large production systems use multiple database types. Your main application data might live in PostgreSQL. Your user sessions live in Redis. Your recommendation engine uses Neo4j. Your activity feed is in Cassandra.

Today we focus on MongoDB — the document database. It's the most relevant for the type of full-stack Spring Boot development you'll be doing, and it's the most flexible of the four categories for general-purpose application development.

Any questions so far before we compare MongoDB directly to relational databases? Good, let's keep moving.

---

**[08:30–17:00] — SQL vs. NoSQL — When to Use Which**

Slide three. Now I want to compare SQL and MongoDB head-to-head across the dimensions that actually matter in practice. Because the question you'll be asked in technical interviews and on the job is: "Why would you choose MongoDB over PostgreSQL for this feature?" You need a real answer, not "because it's trendy."

First dimension: **data model.** SQL stores data in tables — rows and columns. Every row has the exact same columns, and those columns are defined upfront. MongoDB stores data in documents — JSON objects. Documents can have different fields. That's the fundamental difference.

Second: **schema.** In SQL, the schema is enforced at the database level. You can't insert a row that has a column the table doesn't define, and you can't leave out a NOT NULL column. In MongoDB, the schema is flexible by default. You can insert a document with any fields you want into a collection, and different documents in the same collection can have completely different shapes. Now — that doesn't mean you should put random data everywhere. But it means schema changes don't require ALTER TABLE migrations. You just start writing documents with the new field.

Third: **relationships.** SQL handles relationships through normalization and JOINs. You store a customer's ID in an orders table, and at query time you JOIN the tables together. MongoDB's primary mechanism is *embedding* — you store the related data inside the document. An order document contains its own line items, its own shipping address. You can also store IDs as references and do a `$lookup` aggregation that functions like a JOIN — but the document-centric, embedded approach is the first choice in MongoDB design.

Fourth: **transactions.** This is where relational databases historically had a clear advantage. SQL databases offer full ACID transactions — Atomicity, Consistency, Isolation, Durability — across multiple tables. MongoDB offers ACID guarantees at the *single document* level natively — a single document write is always atomic. Multi-document transactions were added in MongoDB version 4.0, so they exist, but the document model is designed to minimize the need for them by co-locating related data.

Fifth: **scaling.** Relational databases scale primarily *vertically* — you get a bigger server with more RAM and CPU. Horizontal scaling is possible but architecturally complex. MongoDB was designed for *horizontal scaling* — you add more servers (shards) and data distributes across them. This is why the big tech companies built document databases — they needed to scale out, not up.

Sixth: **use cases.** Where does SQL win? Financial systems. Payment processing. ERP software. Systems where correctness of relationships is critical. Where does MongoDB win? Product catalogs where different products have different attributes. Content management systems. User-generated data. Applications where the schema is evolving rapidly and you don't want to run ALTER TABLE migrations.

And here's the real-world truth: most mature architectures use both. A financial application might use PostgreSQL for the payments and accounting ledger — where the schema is stable and ACID is critical — and MongoDB for the product catalog, user activity feed, and session data. These are not competing technologies. They're complementary tools.

Now, I want to call out something important for this course. You already know SQL deeply — you covered it in Week 5. You already know Spring Data JPA, which sits on top of relational databases. Today we're adding MongoDB to your toolkit. The goal isn't to replace one with the other. The goal is to know when each is the right choice.

---

**[17:00–26:00] — MongoDB Architecture: mongod, Replica Sets, and Sharding**

Slide four. Let's talk about what's happening under the hood when MongoDB runs.

The core server process is called `mongod` — "mongo dee." That's the MongoDB daemon — the process that runs continuously, listens for connections on port 27017, accepts queries, manages data on disk, and handles all the database operations. When you run `docker run mongo:7.0`, you're starting a `mongod` process inside a container.

How does `mongod` store data? The default storage engine is called **WiredTiger**, which has been the default since MongoDB 3.2. WiredTiger provides document-level locking (which means two writes to different documents in the same collection don't block each other), transparent compression to reduce disk usage, and journal-based durability. You generally don't interact with the storage engine directly — it runs under the hood.

Now, a single `mongod` process is fine for development. But in production, you almost never run a standalone `mongod`. You run a **replica set**.

A replica set is a group of `mongod` processes — typically three — that all maintain the same data. Here's how it works: there's a **primary** node and one or more **secondary** nodes. All writes go to the primary. The primary records every write to an operation log — called the **oplog**. The secondaries continuously read the primary's oplog and replay those operations on their own copy of the data. This is called replication.

If the primary goes down — whether due to hardware failure, network partition, or planned maintenance — the secondary nodes hold an automatic election. They vote among themselves, and the node that gets a majority of votes becomes the new primary. This process typically takes 10 to 30 seconds. Your application reconnects and continues. This is MongoDB's **high availability** story.

A three-node replica set is the minimum recommended production configuration: one primary and two secondaries. Why three? Because you need a majority to elect a new primary. With two nodes, if the primary goes down, the secondary can't reach a majority alone and won't promote itself — you'd have a split-brain scenario.

Now, **sharding** is the horizontal scaling mechanism. Replica sets solve the high-availability problem, but they don't solve the "my data is too large to fit on one server" problem. Sharding does. In a sharded cluster, data is partitioned across multiple shards — where each shard is itself a replica set. A special process called `mongos` acts as a router. When your application sends a query to `mongos`, it figures out which shard or shards have the data and routes the query appropriately.

You don't need to understand sharding deeply right now — it's an advanced operations topic. But I want you to know it exists, because it explains why MongoDB is used at truly massive scale.

For this class, we're running a single `mongod` in Docker. That's all you need for application development.

---

**[26:00–34:00] — The Document Model and Flexible Schema**

Slide five. This is the slide that explains MongoDB's core design philosophy. Let me start with a concrete problem in relational databases and then show how MongoDB approaches it differently.

In an e-commerce application, you have products. Some products are books — they have an author, an ISBN, a number of pages, a genre. Some products are electronics — they have a brand, a model number, voltage, and compatible accessories. Some products are clothing — they have sizes, colors, materials, and gender.

In a relational database, the typical solutions are ugly. Option A: one gigantic products table with every possible column, where most columns are NULL for most rows. Option B: a base products table with common fields, and separate tables for each category — books, electronics, clothing — joined back to products. Both approaches are verbose, fragile, and painful to extend when you add a new product category.

In MongoDB, this is natural. Each product document contains exactly the fields that product needs:
```
Book: { title: "...", author: "...", isbn: "...", pages: 464, genre: "..." }
Electronics: { brand: "...", model: "...", voltage: 120, weight: 2.4 }
Clothing: { material: "cotton", sizes: ["S","M","L"], colors: ["red","blue"] }
```

All three live in the same `products` collection. No NULL columns. No complex JOINs. The schema is flexible because it's defined at the document level, not the collection level.

Let me show you what a full document looks like — the clean code example from the slide. Notice a few things about this document.

First, the `_id` field at the top — that's the unique identifier, auto-generated as an ObjectId. Second, the `books` field is an array containing two nested objects — not a foreign key to a separate table, just the books embedded directly. Third, each book contains a `reviews` array — another level of nesting. This entire structure — an author with books with reviews — is ONE document. Retrieving it requires ONE query with NO JOINs.

That's the core power of the document model: you structure your data around how you query it, not around how you normalize it. You co-locate data that's always read together.

The trade-off: if you need to find a specific review by a specific user across all authors and all books, you have to search across all author documents and their nested arrays. There's no separate `reviews` table to query directly. This is why schema design in MongoDB is driven by your query patterns — you design documents around how you access data, not just how it's structured logically.

Maximum document size is 16 MB. That's a generous limit for most use cases, but it's worth keeping in mind if you have arrays that could grow very large — like "all reviews ever written for a bestselling book."

---

**[34:00–40:00] — BSON — The Storage Format**

Slide six. I want to spend a few minutes on BSON because it explains some behaviors you'll see when working with MongoDB.

BSON stands for Binary JSON. When you write a query using JSON-like syntax — `{ title: "Clean Code" }` — MongoDB doesn't actually store that as a JSON text string. It converts it to BSON, a binary encoding. The wire protocol, the storage format, everything inside MongoDB uses BSON.

Why binary instead of text? Three reasons. First, traversal speed — BSON uses length-prefixed fields, so MongoDB can skip to any field in a document without parsing the whole thing. Text JSON requires reading from the start. Second, type precision — plain JSON only has a generic "number" type. BSON distinguishes Int32, Int64, Double, and Decimal128. That matters for financial calculations and large integer IDs. Third, additional types — JSON has no native concept of a date, a binary blob, a UUID, or a regular expression. BSON adds these as first-class types.

The BSON types you'll actually use are on the slide. The most important ones:

**ObjectId** — the default `_id` type. 12 bytes, globally unique. You'll interact with this constantly.

**Date** — stored as a 64-bit integer representing milliseconds since the Unix epoch. This is a critical one: always store timestamps as BSON Dates, not as strings. If you store a date as the string "2024-01-15", you can't do range queries on it — MongoDB doesn't know it's a date. Store it as `ISODate("2024-01-15T00:00:00Z")` and you get full date range query support, proper sorting, and timezone handling.

**Decimal128** — for financial values. JavaScript's native number is a 64-bit float, which has precision issues for decimal arithmetic. `Decimal128` stores exact decimal values. When you need to store prices, use this.

**Boolean** — `true` or `false`. Not the strings "true" or "false". A common mistake from beginners coming from APIs that serialize everything as strings.

The practical takeaway: when you write queries and insert documents, think about the BSON type of your data. `ISODate()` for dates. `NumberDecimal()` for precise decimals. `ObjectId()` for ID lookups.

---

**[40:00–46:00] — ObjectId — Structure and Usage**

Slide seven. Let me dig into ObjectId because you'll see it everywhere and it's worth understanding its structure.

An ObjectId is 12 bytes, represented as 24 hexadecimal characters. The structure is: four bytes for a Unix timestamp in seconds, five bytes for a random value seeded per process, and three bytes for an incrementing counter. This structure guarantees global uniqueness without a central coordinator.

Think about what that means. In a SQL database with an auto-increment primary key, to generate a new ID you have to ask the database. You can't generate an ID client-side because you don't know what the next available integer is. With ObjectId, any client — your Spring Boot application, your mobile app, your microservice — can generate an ObjectId independently and it will not collide with any ObjectId ever generated by any other client.

The timestamp component means ObjectIds are roughly sortable by creation time. You can do `ObjectId("507f1f77bcf86cd799439011").getTimestamp()` and get back `ISODate("2012-10-15T21:26:47.000Z")`. You can estimate when a document was created from its `_id` alone.

The practical thing I want you to remember: when you query by `_id`, you must wrap the ID in `ObjectId()`. This is a common bug — you have the 24-character string and you do `db.books.findOne({ _id: "64f2a3b1c9e2f10012345678" })` and you get null, because MongoDB is comparing an ObjectId type to a String type, and they're not equal even if the characters match. The correct query is `db.books.findOne({ _id: ObjectId("64f2a3b1c9e2f10012345678") })`.

When you're using Spring Data MongoDB, the `@Id` annotation on a `String` field handles this conversion automatically — it maps between the Java String representation and the BSON ObjectId type transparently.

---

**[46:00–53:00] — Installation, mongosh Navigation, and First Inserts**

Slides eight through eleven — let me work through these practically. 

For installation — Docker is my recommendation for development. The slide has the command. If you ran it at the start of class, your container should be up. Let's verify: `docker ps` and you should see a mongo:7.0 container on port 27017.

Now let's connect. Run: `mongosh` — if you have it installed, it auto-connects to localhost:27017. If not: `docker exec -it mongodb mongosh`. You should see the mongosh prompt.

Let's run a few navigation commands together. Type `show dbs` — you'll see `admin`, `config`, and `local`. Those are system databases. Don't write your application data there.

Type `use bookstoredb` — you've switched to bookstoredb. It doesn't exist yet on disk — MongoDB creates a database when you first write data to it. Type `db` — it prints `bookstoredb`. Type `show collections` — empty for now.

Let's insert our first document. I want everyone to type this with me:

```javascript
db.books.insertOne({
  title: "Clean Code",
  author: "Robert Martin",
  price: 39.99,
  tags: ["java", "best-practices"],
  publishedDate: ISODate("2008-08-01"),
  available: true
})
```

You get back: `{ acknowledged: true, insertedId: ObjectId("...") }`. The `acknowledged: true` means MongoDB confirmed the write was received and persisted. The `insertedId` is the auto-generated ObjectId.

Now `show collections` — you'll see `books` appeared. Now `show dbs` — you'll see `bookstoredb` in the list. It materialized the moment you wrote data.

Run `db.books.find()` — you get your document back, with `_id` added automatically. Run `db.books.countDocuments()` — returns `1`. Good. This is the basic interaction pattern we'll expand in Part 2.

---

**[53:00–60:00] — Schema Design Fundamentals and Part 1 Summary**

Slides twelve through fourteen — schema design and our summary.

Embedding versus referencing. This is the most important design decision in MongoDB. Let me give you the simple mental model: embed data that belongs to one thing and is always needed with that thing. Reference data that is shared, large, or independently useful.

An order and its line items — embed. The line items belong to that order, they're always loaded with the order, and they're bounded (a typical order has five to twenty items). An author and their reviews — probably reference. Reviews are unbounded — a popular book might have ten thousand reviews. Embedding them all in the author document would eventually exceed the 16 MB limit.

The guiding principle MongoDB uses: "store data that is accessed together." If every query for an order always needs the line items, embed them. If you rarely need all reviews for an author in one shot — usually you page through them — keep them in a separate collection.

The practical implication for schema design: design your documents around your query patterns, not around theoretical normalization. This is the opposite of what you learned in relational database design. In SQL, you normalize first and add query optimization later. In MongoDB, you think about queries first.

Let me do a quick summary of what we covered today.

NoSQL is four main categories. Document databases like MongoDB store JSON-like documents with flexible schemas. MongoDB is the leading document database. The architecture is mongod as the server process, replica sets for high availability, and optional sharding for horizontal scale. Documents are rich JSON-like objects stored as BSON — they can contain nested objects and arrays. Collections are like tables but schema-free, created implicitly. Every document has a `_id` — ObjectId by default, which is a 12-byte globally unique ID with a timestamp component. Installation is easiest via Docker. mongosh is the JavaScript REPL for interacting with MongoDB.

In Part 2, we go from conceptual understanding to operational skill. Full CRUD with every operator. Aggregation pipelines. Indexes and query performance. MongoDB Atlas for cloud hosting. And Spring Data MongoDB to connect everything to Spring Boot.

Take ten minutes. Grab coffee. When you come back we're writing queries.

---

*[End of Part 1 Script — approximately 60 minutes]*
