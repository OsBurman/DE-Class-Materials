# Exercise 01: NoSQL vs SQL Comparison and Document Model

## Objective
Understand the fundamental differences between SQL and NoSQL databases and describe the MongoDB document-oriented data model.

## Background
Before writing a single MongoDB command, a developer needs to understand *why* a document database exists and when to choose it over a relational one. MongoDB stores data as BSON documents (Binary JSON) inside collections — there are no tables, rows, or fixed schemas. Every document can have its own shape, and every document is automatically assigned a unique `ObjectId`.

## Requirements

1. In the **Comparison Table** section of your answer file, complete the table by filling in the correct value for every `???` cell. Cover: data model, schema, relationships, query language, scalability, and a typical use-case.

2. In the **NoSQL Types** section, list the four main NoSQL database categories and give one real-world product example for each.

3. In the **Document Model** section, write a sample BSON/JSON document that represents a `user` with the following fields:
   - `_id` (use a realistic ObjectId hex string)
   - `name` (string)
   - `email` (string)
   - `age` (number)
   - `tags` (array of strings)
   - `address` (embedded object with `street`, `city`, `country`)

4. In the **ObjectId Breakdown** section, explain what the 12 bytes of an ObjectId encode (timestamp, machine id, process id, counter) and why that makes it globally unique without a central coordinator.

5. In the **When to Choose MongoDB** section, list **three** scenarios where MongoDB is a better fit than a relational database, and **two** scenarios where a relational database is the better choice.

## Hints
- BSON is a superset of JSON — it supports extra types like `Date`, `Binary`, and `Decimal128` that plain JSON does not.
- An ObjectId looks like `"507f1f77bcf86cd799439011"` — 24 hex characters = 12 bytes.
- MongoDB scales *horizontally* (sharding across nodes); most RDBMS scale *vertically* (bigger server).
- "Schema-on-read" means MongoDB does not validate document shape at write time by default.

## Expected Output

Your completed `answers.md` file should contain a filled-in comparison table similar to:

```
| Feature        | SQL (Relational)        | MongoDB (Document)          |
|----------------|-------------------------|-----------------------------|
| Data model     | Tables, rows, columns   | Collections, documents      |
| Schema         | Fixed (schema-on-write) | Flexible (schema-on-read)   |
| Relationships  | Foreign keys / JOINs    | Embedded docs / $lookup     |
| Query language | SQL                     | MQL (MongoDB Query Language)|
| Scalability    | Vertical (scale-up)     | Horizontal (sharding)       |
| Best for       | Structured, transactional data | Flexible, hierarchical data |
```

And a sample document like:
```json
{
  "_id": { "$oid": "507f1f77bcf86cd799439011" },
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "age": 29,
  "tags": ["admin", "user"],
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "country": "USA"
  }
}
```
