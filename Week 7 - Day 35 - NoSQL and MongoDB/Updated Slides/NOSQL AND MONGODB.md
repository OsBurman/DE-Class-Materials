SLIDE 1: Title Slide
Slide content: "Introduction to NoSQL & MongoDB" | Your name | Date | Course name

Script:
"Good morning everyone, welcome back. Today we're doing a deep dive into NoSQL databases and MongoDB specifically. By the end of this session you'll understand what NoSQL is, why it exists, how MongoDB works under the hood, and you'll have the foundational knowledge to write queries, design schemas, and even wire MongoDB up to a Spring Boot application. We have a lot of ground to cover so let's get right into it."

SLIDE 2: What is NoSQL?
Slide content: Definition of NoSQL | "Not Only SQL" | Brief history (late 2000s, web scale problem) | Key characteristics: schema flexibility, horizontal scaling, distributed by design

Script:
"So what exactly is NoSQL? The term stands for 'Not Only SQL' — and that's an important nuance. It doesn't mean these databases have nothing to do with SQL or relational concepts. It means they go beyond the traditional relational model when the situation calls for it.
NoSQL emerged in the late 2000s largely out of necessity. Companies like Google, Amazon, and Facebook were dealing with data at a scale that traditional relational databases simply weren't designed for. They needed databases that could scale horizontally — meaning you add more machines rather than buying a bigger single machine — and they needed flexibility because their data models were changing constantly.
The core characteristics of NoSQL databases are: they're schema-flexible, meaning you don't have to define your columns upfront; they're built to scale out across many servers; they're optimized for specific data access patterns; and they often sacrifice some of the ACID guarantees of relational databases in exchange for performance and availability."

SLIDE 3: Types of NoSQL Databases
Slide content: Four types with icons/examples — Document (MongoDB, CouchDB) | Key-Value (Redis, DynamoDB) | Column-Family (Cassandra, HBase) | Graph (Neo4j, ArangoDB)

Script:
"NoSQL is not one thing — it's a category that contains four major types of databases, each optimized for a different kind of data.
First, Document stores. This is where MongoDB lives. Data is stored as documents, typically JSON or a similar format. Each document is a self-contained unit that can have nested data. Great for things like user profiles, product catalogs, content management.
Second, Key-Value stores. Think of it like a massive dictionary or HashMap. You have a key and a value, and that's it. Redis is the most popular example. These are incredibly fast and are often used for caching, session management, and real-time leaderboards.
Third, Column-Family stores, like Apache Cassandra. These look a bit like relational tables but are optimized for querying large amounts of data across specific columns rather than rows. They're often used in time-series data, IoT applications, and analytics.
And fourth, Graph databases like Neo4j. These store data as nodes and edges — entities and the relationships between them. They're perfect for social networks, recommendation engines, and fraud detection where relationships are as important as the data itself.
Today we're focused on document stores, and specifically MongoDB, which is the most widely used NoSQL database in the world."

SLIDE 4: SQL vs NoSQL — Side-by-Side Comparison
Slide content: Table comparing — Structure (Tables/rows vs Collections/documents) | Schema (Fixed vs Flexible) | Scaling (Vertical vs Horizontal) | Joins (Yes vs Embedded/lookup) | ACID (Full vs Varies) | Best for (Structured, relational data vs Semi-structured, hierarchical, large-scale data)

Script:
"Let's directly compare SQL and NoSQL because this is something you'll be asked to explain in your careers constantly.
In a relational database like MySQL or PostgreSQL, data lives in tables with rows and columns, and the schema — the structure of those tables — must be defined before you insert any data. If you need to add a column, you have to run an ALTER TABLE statement, which can be painful on a large table.
In MongoDB, data lives in collections of documents. There is no enforced schema by default. Two documents in the same collection can have completely different fields. That sounds chaotic, but it's actually very powerful when you're building applications where the data model is still evolving.
Scaling is a big one. Relational databases scale vertically — you make the one server bigger and more powerful. That gets expensive fast. MongoDB is designed to scale horizontally — you shard your data across many commodity servers.
Joins are another key difference. In SQL, joins are how you connect related data across tables. MongoDB avoids the need for joins by encouraging you to embed related data inside a single document. When you do need to reference data across collections, MongoDB has the aggregation pipeline's $lookup stage, which we'll cover shortly.
ACID compliance — this refers to Atomicity, Consistency, Isolation, and Durability. Traditional relational databases are fully ACID compliant. MongoDB has made significant strides here and supports multi-document ACID transactions since version 4.0, but historically NoSQL databases traded some of these guarantees for speed and scale.
Neither is better than the other — they're tools. You use SQL when you have highly relational, structured data with complex transactions, like banking. You use MongoDB when you have flexible, hierarchical data that needs to scale quickly, like a content platform or e-commerce catalog."

SLIDE 5: MongoDB Architecture Overview
Slide content: Diagram — Database → Collections → Documents | Replica Sets | Sharding concept | mongod process

Script:
"Let's get into MongoDB's architecture. At the top level you have a database — this is just a namespace that holds a group of related collections. You can have many databases on a single MongoDB server.
Inside a database you have collections, which are analogous to tables in SQL. A collection holds a group of documents.
A document is the fundamental unit of data in MongoDB. It's a JSON-like object with field-value pairs. We'll look at the actual format in a moment.
At the infrastructure level, MongoDB uses Replica Sets for high availability. A replica set is a group of MongoDB servers that hold the same data — there's a primary node that handles all writes, and one or more secondary nodes that replicate the data from the primary. If the primary goes down, the secondaries automatically elect a new primary. This is how MongoDB achieves fault tolerance.
For scaling horizontally, MongoDB uses sharding. Sharding distributes data across multiple machines based on a shard key. Each shard is itself a replica set. A mongos router sits in front and directs queries to the right shard.
The actual MongoDB server process is called mongod — that's the daemon that you run on your server. When you install MongoDB and start it, you're starting mongod."

SLIDE 6: The Document Data Model
Slide content: Sample JSON document for a user | Nested object example | Array example | Field types listed

Script:
"The document model is really the heart of what makes MongoDB different. Let me show you what a document looks like.
json{
  "_id": ObjectId("64ab12cd..."),
  "name": "Sarah Johnson",
  "email": "sarah@example.com",
  "age": 29,
  "address": {
    "street": "123 Main St",
    "city": "Austin",
    "state": "TX"
  },
  "hobbies": ["reading", "hiking", "coding"],
  "createdAt": ISODate("2024-01-15")
}
Notice a few things here. This document has fields of different types — a string, a number, an embedded object for the address, an array of strings for hobbies, and a date. All of this lives in a single document. In a relational database, the address might be in a separate addresses table, and hobbies might be in a separate hobbies table with a foreign key relationship. Here, everything related to Sarah is in one place.
This is called embedding, and it's one of MongoDB's most powerful schema design patterns. When you embed related data, you can retrieve everything about Sarah in a single read operation — no joins required.
The trade-off is data duplication. If 'Austin, TX' appears in 10,000 user documents and the city changes its name, you have to update 10,000 documents. That's the kind of trade-off you think about when designing schemas in MongoDB.
Documents can be up to 16 megabytes in size. That's usually more than enough, but it's worth knowing."

SLIDE 7: BSON — Binary JSON
Slide content: BSON logo/concept | JSON vs BSON comparison | Additional BSON types: ObjectId, Date, Binary, Int32, Int64, Decimal128, Timestamp

Script:
"Now, when you work with MongoDB you write and read data in JSON format. But internally, MongoDB actually stores and transmits data in BSON, which stands for Binary JSON.
BSON serves two purposes. First, it's more efficient to traverse than plain JSON because it encodes length information, so MongoDB can skip over fields it doesn't need quickly. Second, BSON extends JSON with additional data types that JSON doesn't support natively.
The most important extra types you'll encounter are: ObjectId — the default type for document IDs; Date — a proper 64-bit timestamp, not just a string; Int32 and Int64 — because JSON has only one number type but BSON distinguishes between integers and doubles; Decimal128 — for precise decimal arithmetic, important for financial data; and Binary — for storing raw byte data.
As a developer you rarely interact with BSON directly. Your MongoDB driver handles the conversion between your application's objects and BSON automatically. But it's important to understand it exists because it explains why MongoDB has specific date and number types and why an ObjectId isn't just a plain string."

SLIDE 8: ObjectId and Document Identifiers
Slide content: ObjectId structure diagram — 4 bytes timestamp | 5 bytes random | 3 bytes incrementing counter | Total 12 bytes = 24 hex characters

Script:
"Every document in MongoDB must have an _id field — this is the primary key equivalent. If you don't provide one when inserting a document, MongoDB automatically generates an ObjectId for you.
An ObjectId is a 12-byte value encoded as a 24-character hexadecimal string. It's cleverly structured: the first 4 bytes are a Unix timestamp of when the ObjectId was created, the next 5 bytes are a random value unique to the machine and process, and the last 3 bytes are an incrementing counter.
This design means ObjectIds are globally unique without requiring a central authority to assign them — your application can generate them client-side and you don't need to worry about collisions. It also means you can extract the creation time from an ObjectId, which is a useful trick.
You don't have to use ObjectId as your _id. You can use any value — a string, a number, a UUID — as long as it's unique within the collection. But ObjectId is the default for a reason: it's efficient, sortable, and practically guaranteed to be unique."

SLIDE 9: MongoDB Installation and Setup
Slide content: Installation options — Community Edition (local), Atlas (cloud) | Steps for local install on Mac/Windows/Linux | Starting mongod | Connecting with mongosh

Script:
"Let's talk about getting MongoDB running. You have two main options.
Option 1: Local installation. You download MongoDB Community Edition from mongodb.com. On Mac you can use Homebrew: brew tap mongodb/brew then brew install mongodb-community. On Windows you use the MSI installer. On Ubuntu/Debian you add the MongoDB repo and use apt. After installation, you start the server with brew services start mongodb-community on Mac, or mongod --config /etc/mongod.conf on Linux.
Option 2: MongoDB Atlas, which is the cloud-hosted version. You go to cloud.mongodb.com, create a free account, and spin up a free M0 cluster in minutes. No local installation required. We'll cover Atlas in more depth shortly.
Once MongoDB is running, you connect to it using mongosh, the MongoDB Shell. You just type mongosh in your terminal and it connects to localhost on the default port 27017. If you need to connect to a remote server or Atlas, you pass a connection string: mongosh 'mongodb+srv://username:password@cluster.mongodb.net/mydb'
From the mongosh prompt you can run all your database operations interactively. Let's look at the basics."

SLIDE 10: mongosh Basics
Slide content: Common shell commands — show dbs | use dbname | show collections | db.stats() | db.collectionName.find() | exit

Script:
"Once you're in the mongosh shell, here are the commands you'll use constantly.
show dbs — lists all databases on the server.
use myDatabase — switches to a database. If it doesn't exist yet, MongoDB creates it lazily when you first insert data. This is important — MongoDB doesn't actually create a database or collection until you put data in it.
show collections — lists all collections in the current database.
db — shows you which database you're currently using.
db.stats() — gives you statistics about the current database.
db.users.find() — queries the users collection. We'll get deep into queries in a moment.
db.users.drop() — drops a collection. Be careful with this one.
db.dropDatabase() — drops the entire current database. Very careful with this one.
exit or Ctrl+D — exits the shell.
One thing to note — in mongosh, db is a reference to the current database, and you access collections as properties of db. So db.users refers to the users collection, db.products refers to products, and so on. This makes the shell feel almost like working with a JavaScript object, which makes sense because mongosh is a JavaScript REPL."

SLIDE 11: CRUD — Create (Insert)
Slide content: insertOne() syntax | insertMany() syntax | Code examples | Returned result structure with insertedId

Script:
"Now let's get into the most important thing you'll do in MongoDB — CRUD operations. That's Create, Read, Update, Delete.
Starting with Create. You have two methods.
db.users.insertOne() inserts a single document:
javascriptdb.users.insertOne({
  name: "Alex Rivera",
  email: "alex@example.com",
  age: 34,
  role: "admin"
})
MongoDB returns a result object containing acknowledged: true and the insertedId of the new document.
db.users.insertMany() inserts multiple documents at once:
javascriptdb.users.insertMany([
  { name: "Maria Chen", age: 28, role: "user" },
  { name: "James Patel", age: 45, role: "moderator" }
])
This returns an insertedIds object mapping indexes to their generated ObjectIds.
A few important things to know: First, if you provide an _id field yourself, MongoDB uses it. If you don't, it auto-generates an ObjectId. Second, if you try to insert a document with a duplicate _id, MongoDB throws a duplicate key error. Third, insertMany by default stops on the first error — you can pass { ordered: false } as a second argument to tell it to continue inserting the rest even if some fail."

SLIDE 12: CRUD — Read (Find)
Slide content: find() vs findOne() | Filter syntax | Projection syntax | Cursor methods: sort(), limit(), skip()

Script:
"Reading data uses find() and findOne().
db.users.find() with no arguments returns all documents in the collection — use this carefully on large collections.
To filter, you pass a query document as the first argument:
javascript// Find all users with role "admin"
db.users.find({ role: "admin" })

// Find a single user by email
db.users.findOne({ email: "alex@example.com" })
The second argument to find() is a projection — it lets you control which fields are returned. A value of 1 includes a field, 0 excludes it:
javascript// Return only name and email, exclude _id
db.users.find({}, { name: 1, email: 1, _id: 0 })
find() returns a cursor — a pointer to the result set. You can chain methods on the cursor:
javascript// Sort by age descending, return first 5
db.users.find().sort({ age: -1 }).limit(5)

// Skip first 10, return next 10 — classic pagination
db.users.find().skip(10).limit(10)
findOne() returns the first matching document directly, not a cursor. Use it when you expect exactly one result, like looking up by a unique identifier."

SLIDE 13: Query Operators
Slide content: Comparison: $eq, $ne, $gt, $gte, $lt, $lte, $in, $nin | Logical: $and, $or, $not, $nor | Element: $exists, $type | Evaluation: $regex, $where

Script:
"Simple equality filters will only get you so far. MongoDB has a rich set of query operators for more complex conditions. Operators in MongoDB always start with a dollar sign.
Comparison operators:
javascript// Greater than: age > 30
db.users.find({ age: { $gt: 30 } })

// Less than or equal: age <= 25
db.users.find({ age: { $lte: 25 } })

// Not equal
db.users.find({ role: { $ne: "admin" } })

// In a list of values
db.users.find({ role: { $in: ["admin", "moderator"] } })

// Not in a list
db.users.find({ role: { $nin: ["banned", "deleted"] } })
Logical operators:
javascript// AND — both conditions must be true
db.users.find({ $and: [{ age: { $gte: 18 } }, { role: "user" }] })
// Note: you can also just chain fields in one object for implicit AND
db.users.find({ age: { $gte: 18 }, role: "user" })

// OR — at least one condition must be true
db.users.find({ $or: [{ role: "admin" }, { age: { $gt: 40 } }] })
Element operators:
javascript// Field exists
db.users.find({ email: { $exists: true } })

// Field is of a specific BSON type
db.users.find({ age: { $type: "int" } })
The $regex operator — very useful for text searching:
javascript// Find users whose name starts with 'A', case insensitive
db.users.find({ name: { $regex: /^A/i } })
Be cautious with $regex on large collections — without an index it does a full collection scan. We'll talk about indexes in a few minutes."

SLIDE 14: CRUD — Update
Slide content: updateOne() | updateMany() | replaceOne() | Update operators: $set, $unset, $inc, $push, $pull, $addToSet | upsert option

Script:
"Now for updating documents. The key thing to understand about MongoDB updates is that you don't just pass the new document — you use update operators to specify what should change. This is important because if you accidentally pass a raw document without operators, MongoDB will replace the entire document.
javascript// Update one document — set the age field
db.users.updateOne(
  { email: "alex@example.com" },  // filter
  { $set: { age: 35, role: "superadmin" } }  // update
)

// Update many documents at once
db.users.updateMany(
  { role: "user" },
  { $set: { verified: false } }
)
The most important update operators are:
$set — sets the value of a field. If the field doesn't exist, it creates it.
$unset — removes a field from the document entirely.
$inc — increments a numeric field by a given amount. Perfect for counters:
javascriptdb.products.updateOne({ _id: id }, { $inc: { viewCount: 1 } })
$push — appends a value to an array field.
$pull — removes a value from an array field.
$addToSet — like $push but only adds if the value isn't already in the array.
You can also pass { upsert: true } as a third options argument. This tells MongoDB: if no document matches the filter, insert a new one. It's a very clean way to handle 'create or update' logic."

SLIDE 15: CRUD — Delete
Slide content: deleteOne() | deleteMany() | Syntax examples | Warning about deleteMany with empty filter

Script:
"Deleting documents is straightforward.
javascript// Delete one document
db.users.deleteOne({ email: "alex@example.com" })

// Delete all users with role "banned"
db.users.deleteMany({ role: "banned" })
Both methods take a filter document. deleteOne deletes the first matching document. deleteMany deletes all matching documents.
A critical warning: db.users.deleteMany({}) — with an empty filter — deletes every single document in the collection. MongoDB won't ask for confirmation. It'll just do it. This is one of those commands where you want to make absolutely sure you have the right database selected and the right filter before you run it. In production environments, access controls should prevent most users from running deleteMany with an empty filter, but you should still be careful.
There's also db.users.drop() which deletes the entire collection including all its indexes, which is different from deleting all documents."

SLIDE 16: Aggregation Framework — Concepts
Slide content: Pipeline concept diagram — Collection → Stage 1 → Stage 2 → Stage 3 → Result | Analogy: assembly line or UNIX pipes

Script:
"We've covered basic queries. Now let's talk about the aggregation framework, which is one of MongoDB's most powerful features and honestly where MongoDB really shines for data analysis.
The aggregation framework works on the concept of a pipeline. Think of it like an assembly line, or if you're familiar with UNIX, like piping commands together. You start with your collection, pass documents through a series of stages, and each stage transforms the data before passing it to the next stage.
javascriptdb.orders.aggregate([
  { $match: ... },    // Stage 1: filter
  { $group: ... },    // Stage 2: aggregate
  { $project: ... },  // Stage 3: reshape
  { $sort: ... }      // Stage 4: sort
])
Each stage takes the documents output by the previous stage as its input. The stages don't have to be in any particular order — you compose them to get the result you need. Let's go through the most important stages."

SLIDE 17: Aggregation — $match, $group, $project
Slide content: match syntax | $group syntax with accumulators (
sum, $avg, $count, $min, $max, $push) | $project syntax


Script:
"$match is your filter stage. It works just like a find() query:
javascript{ $match: { status: "completed", total: { $gt: 100 } } }
Always put $match as early in the pipeline as possible — it reduces the number of documents flowing through subsequent stages, which makes your pipeline much faster.
$group is where you do aggregation — grouping documents together and computing totals, averages, counts, etc.:
javascript{ $group: {
    _id: "$category",          // group by this field
    totalRevenue: { $sum: "$total" },
    averageOrder: { $avg: "$total" },
    orderCount: { $sum: 1 }
}}
The _id field in $group is the grouping key. The dollar sign before a field name ($category) means 'the value of the category field'. Important accumulators are $sum, $avg, $min, $max, $count, and $push which collects values into an array.
$project reshapes documents — include, exclude, or compute new fields:
javascript{ $project: {
    name: 1,
    email: 1,
    fullName: { $concat: ["$firstName", " ", "$lastName"] },
    _id: 0
}}
$project is like a powerful SELECT clause — you can compute new fields, rename fields, and transform values."

SLIDE 18: Aggregation — $lookup and $unwind
Slide content: $lookup syntax | SQL JOIN equivalent diagram | $unwind explanation with before/after example

Script:
"$lookup is MongoDB's version of a JOIN. It lets you pull in documents from another collection:
javascript{ $lookup: {
    from: "products",        // the other collection
    localField: "productId", // field in current collection
    foreignField: "_id",     // field in the other collection
    as: "productDetails"     // name for the joined data
}}
This adds a productDetails array field to each order document, containing the matching product documents. It's similar to a LEFT OUTER JOIN in SQL.
$unwind deconstructs an array field. When you have a document with an array, $unwind creates one document per array element:
Before $unwind:
json{ "_id": 1, "tags": ["mongodb", "database", "nosql"] }
After { $unwind: "$tags" }:
json{ "_id": 1, "tags": "mongodb" }
{ "_id": 1, "tags": "database" }
{ "_id": 1, "tags": "nosql" }
You often use $unwind after $lookup because $lookup produces an array, and you might want to work with the individual joined documents as separate records. You can also use $unwind to analyze array contents — for example, to count how many documents use each tag."

SLIDE 19: Indexes in MongoDB
Slide content: Why indexes matter (query performance) | Default _id index | createIndex() syntax | Index types: single, compound, multikey, text, TTL | explain() for query analysis

Script:
"Indexes are one of the most important performance topics in MongoDB. Without the right indexes, your queries do a collection scan — MongoDB reads every single document to find matches. On a collection with millions of documents, that's extremely slow.
An index is a data structure that MongoDB maintains alongside your collection that makes it fast to find documents matching certain fields. MongoDB always creates an index on _id automatically.
You create indexes with createIndex():
javascript// Single field index
db.users.createIndex({ email: 1 })  // 1 = ascending, -1 = descending

// Compound index — covers queries on both fields
db.users.createIndex({ lastName: 1, firstName: 1 })

// Unique index — enforces uniqueness
db.users.createIndex({ email: 1 }, { unique: true })

// TTL index — automatically deletes documents after a time period
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })

// Text index — enables full-text search
db.articles.createIndex({ content: "text", title: "text" })
The TTL index is particularly cool for things like session management — documents automatically expire without you writing any cleanup code.
To understand how a query is executing, use .explain():
javascriptdb.users.find({ email: "alex@example.com" }).explain("executionStats")
```

This shows you whether MongoDB used an index (`IXSCAN`) or did a full scan (`COLLSCAN`), and how many documents it examined. You want to see IXSCAN and a low 'docsExamined' count.

The general rule: create indexes on fields you frequently query, sort, or use in aggregation pipelines. But don't over-index — every index takes up space and slows down writes because MongoDB has to update all indexes on every insert and update."

---

## SLIDE 20: MongoDB Atlas
**Slide content:** Atlas logo | What it is (DBaaS) | Free M0 cluster | Features: Visual UI, Monitoring, Atlas Search, Atlas Data API, Backups | Connection string format

---

**Script:**

"MongoDB Atlas is MongoDB's fully managed cloud database service. Instead of managing your own MongoDB servers — handling backups, upgrades, scaling, security patches — you let MongoDB do all of that for you.

Atlas runs on AWS, Google Cloud, or Azure, and you pick your region. There's a **free M0 tier** with 512MB of storage, which is perfect for learning and small projects.

Beyond just hosting MongoDB, Atlas has a rich ecosystem of additional features. The **Atlas UI** gives you a visual interface to browse collections, run queries, and manage indexes without using the shell. **Atlas Charts** lets you build visualizations from your data. **Atlas Search** provides full-text search powered by Lucene. **Atlas Data API** lets you query your database over HTTP without a driver.

To connect your application to Atlas, you use a connection string in this format:
```
mongodb+srv://username:password@cluster0.abc12.mongodb.net/myDatabase
The +srv part means it's using DNS SRV records, which allows Atlas to handle connection routing and failover automatically.
For this course, I'd encourage you to set up a free Atlas cluster if you haven't already. It lets you practice without needing MongoDB installed locally and it's what you'd realistically use in a real project."

SLIDE 21: Schema Design Principles
Slide content: Embed vs Reference decision tree | One-to-few: embed | One-to-many: embed with consideration | Many-to-many: reference | Rule of thumb: model data the way your application uses it

Script:
"Before we get to Spring Boot integration, let's talk about schema design because this is where MongoDB gets nuanced and where developers from a SQL background often make mistakes.
In SQL, you normalize your data — you split it into separate tables and use foreign keys to link them. This avoids data duplication. In MongoDB, you have a choice: embed related data inside a document, or reference it with an ID that points to another collection.
The guiding principle is: model your data the way your application accesses it.
Embed when: the related data is always accessed together with the parent document. For example, a blog post and its comments — if you always load them together, embed the comments in the post document. Also embed when the related data is specific to one parent and won't be shared.
Reference when: the related data is large and you don't always need it. Or when the same entity is shared across many parents — for example, a product's manufacturer is shared by many products, so you reference a manufacturers collection. Or when the embedded array would grow unboundedly — MongoDB has a 16MB document limit, so if comments could number in the thousands, consider referencing.
There's no single right answer — it depends on your query patterns. Think about what your most common and most critical queries are, and design your schema to make those fast."

SLIDE 22: MongoDB with Spring Boot — Setup
Slide content: Dependencies (spring-boot-starter-data-mongodb) | application.properties configuration | MongoClient auto-configuration

Script:
"Now let's talk about using MongoDB in a Spring Boot application with Spring Data MongoDB.
First, your dependencies. In your pom.xml add:
xml<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
Then configure your connection in application.properties:
propertiesspring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/mydb
spring.data.mongodb.database=mydb
Spring Boot's autoconfiguration handles everything else — it creates a MongoClient bean, a MongoTemplate bean, and sets up Spring Data repositories automatically. You don't need to write any configuration class unless you need to customize behavior."

SLIDE 23: Spring Data MongoDB — Document Mapping
Slide content: @Document annotation | @Id annotation | @Field annotation | @Indexed annotation | Sample entity class

Script:
"Spring Data MongoDB uses annotations to map your Java classes to MongoDB documents.
java@Document(collection = "users")
public class User {

    @Id
    private String id;  // Maps to _id in MongoDB

    @Field("full_name")
    private String name;  // Stored as full_name in MongoDB

    private String email;
    private int age;

    @Indexed
    private String role;

    // constructors, getters, setters
}
@Document marks this class as a MongoDB document and specifies the collection name. If you omit the collection name, Spring uses the class name in lowercase.
@Id maps the field to the _id field in MongoDB. If you declare it as a String, Spring Data automatically converts between String and ObjectId.
@Field lets you specify a different field name in the database. Useful when you want your Java field names to follow Java naming conventions but your database fields to follow a different convention.
@Indexed creates an index on that field when the application starts. You can also add unique = true to it."

SLIDE 24: Spring Data MongoDB — Repositories
Slide content: MongoRepository interface | Built-in methods | Derived query methods | @Query annotation example

Script:
"Spring Data MongoDB's most powerful feature for day-to-day development is the Repository pattern. You create an interface that extends MongoRepository and Spring generates the implementation at runtime.
javapublic interface UserRepository extends MongoRepository<User, String> {

    // Spring generates the query from the method name
    List<User> findByRole(String role);

    Optional<User> findByEmail(String email);

    List<User> findByAgeGreaterThan(int age);

    List<User> findByRoleAndAgeGreaterThan(String role, int age);

    // Custom query with @Query annotation
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<User> findByEmailContaining(String pattern);
}
MongoRepository<User, String> takes the entity type and the ID type. It gives you built-in methods like save(), findById(), findAll(), deleteById(), count(), and existsById().
Derived query methods are magic — Spring parses the method name and generates the MongoDB query automatically. findByRole becomes { role: value }. findByAgeGreaterThan becomes { age: { $gt: value } }. You just name your method following the convention and Spring handles everything.
For complex queries that can't be expressed as method names, use @Query with the MongoDB query JSON as a string. The ?0, ?1 placeholders are positional parameters."

SLIDE 25: Spring Data MongoDB — MongoTemplate
Slide content: MongoTemplate vs Repository | Query and Criteria classes | Aggregation with MongoTemplate

Script:
"While repositories are great for standard CRUD and simple queries, sometimes you need more control. That's when you reach for MongoTemplate, which is a lower-level API that gives you direct access to MongoDB operations.
java@Autowired
private MongoTemplate mongoTemplate;

// Find with criteria
Query query = new Query();
query.addCriteria(Criteria.where("role").is("admin")
    .and("age").gt(30));
query.with(Sort.by(Sort.Direction.DESC, "name"));
query.limit(10);

List<User> users = mongoTemplate.find(query, User.class);

// Aggregation
Aggregation agg = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("status").is("active")),
    Aggregation.group("role").count().as("count"),
    Aggregation.sort(Sort.Direction.DESC, "count")
);

AggregationResults<Document> results = 
    mongoTemplate.aggregate(agg, "users", Document.class);
MongoTemplate gives you the full power of MongoDB's query language through a fluent Java API. Use repositories for the common cases and MongoTemplate when you need to do something more complex like aggregations or bulk operations."

SLIDE 26: Putting It Together — Use Cases
Slide content: When to choose MongoDB — Content management, E-commerce catalogs, Real-time analytics, User profiles, Event logging, IoT data | When NOT to — Complex multi-entity transactions, Highly normalized relational data

Script:
"Before we wrap up, let's put everything together with a practical lens. When should you actually reach for MongoDB?
MongoDB is an excellent choice for content management systems — articles, blog posts, pages with varying structures. It's great for e-commerce product catalogs where different product types have completely different attributes — a shirt has size and color, a laptop has RAM and storage, and you don't want to have 200 nullable columns in a single table. It's great for user profiles that need to store varying preferences and social data. It's widely used for event logging and activity streams where you're writing high volumes of time-series data. And it's very popular for real-time analytics because the aggregation framework is powerful.
Where would you prefer a relational database? When you have complex multi-entity transactions — like banking, where transferring money between accounts must be fully atomic and consistent. When your data is naturally very relational and normalized — like a complex HR system with many interdependent entities. When your team has deep SQL expertise and your queries are complex JOIN-heavy analytics queries, a traditional data warehouse might serve better.
The best engineers know multiple tools and choose the right one for the problem. MongoDB and SQL are not enemies — in many modern architectures, you use both."

SLIDE 27: Summary & Key Takeaways
Slide content: Bullet recap — NoSQL types | SQL vs NoSQL trade-offs | MongoDB: documents, collections, BSON, ObjectId | CRUD operations | Query operators | Aggregation pipeline | Indexes | Atlas | Spring Data MongoDB

Script:
"Let's quickly recap everything we covered today.
We started with the NoSQL landscape — four types of databases each suited to different problems. We compared SQL and NoSQL on schema, scaling, joins, and ACID compliance, emphasizing that both have their place.
We dove into MongoDB's architecture — replica sets for high availability, sharding for horizontal scale. We explored the document model with embedding and referencing, and understood BSON as the binary format MongoDB uses internally, with its extended type system including ObjectId and Date.
We got hands-on with mongosh and walked through all four CRUD operations, and we covered MongoDB's rich query operator vocabulary including comparison, logical, and regex operators.
We explored the aggregation framework and its pipeline stages — $match, $group, $project, $lookup, and $unwind — which give you powerful data transformation capabilities.
We covered indexes and why they're critical for performance, including TTL and text index types.
We briefly covered MongoDB Atlas as the cloud-hosted option, and finally we looked at integrating MongoDB into a Spring Boot application using Spring Data MongoDB — @Document mapping, repositories with derived query methods, and MongoTemplate for complex operations.
That's a lot to absorb. For next class, I'd like you to set up either a local MongoDB instance or a free Atlas cluster, and practice inserting documents and running queries from the shell. We'll build on this in our next session.
Any questions?"

SLIDE 28: Next Steps / Homework
Slide content: Set up MongoDB locally or on Atlas | Practice CRUD in mongosh | Create a simple Spring Boot app with MongoRepository | Recommended reading: MongoDB documentation at docs.mongodb.com

---

