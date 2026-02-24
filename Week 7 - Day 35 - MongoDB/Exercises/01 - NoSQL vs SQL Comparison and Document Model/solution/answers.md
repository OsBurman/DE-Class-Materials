# Day 35 Exercise 01 — NoSQL vs SQL Comparison and Document Model (SOLUTION)

---

## 1. Comparison Table

| Feature        | SQL (Relational)                      | MongoDB (Document)                     |
|----------------|---------------------------------------|----------------------------------------|
| Data model     | Tables, rows, columns                 | Collections, documents (JSON/BSON)     |
| Schema         | Fixed schema (schema-on-write)        | Flexible schema (schema-on-read)       |
| Relationships  | Foreign keys and JOINs                | Embedded documents or `$lookup`        |
| Query language | SQL (Structured Query Language)       | MQL (MongoDB Query Language)           |
| Scalability    | Vertical (bigger server)              | Horizontal (sharding across nodes)     |
| Best for       | Structured, transactional, relational data | Hierarchical, variable-shape, high-volume data |

---

## 2. NoSQL Types

1. **Key-Value** — Stores data as simple key→value pairs; extremely fast lookups. Example: **Redis**
2. **Document** — Stores semi-structured JSON/BSON documents; supports nested objects and arrays. Example: **MongoDB**
3. **Column-Family (Wide-Column)** — Stores data in columns grouped into families; optimised for large-scale analytical reads. Example: **Apache Cassandra**
4. **Graph** — Stores nodes and edges to represent and traverse relationships efficiently. Example: **Neo4j**

---

## 3. Document Model — Sample BSON Document

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

Key observations:
- `_id` holds an **ObjectId** — MongoDB's default primary key type.
- `tags` is an array — MongoDB natively stores lists inside a document.
- `address` is an **embedded document** — no JOIN required to access city or country.

---

## 4. ObjectId Breakdown

| Bytes | Component        | What it encodes                                   |
|-------|------------------|---------------------------------------------------|
| 1–4   | Timestamp        | Unix epoch seconds at creation time               |
| 5–7   | Machine identifier | First 3 bytes of the MD5 hash of the hostname   |
| 8–9   | Process ID       | PID of the process that created the ObjectId      |
| 10–12 | Random counter   | Auto-incrementing 3-byte counter, random start    |

**Why globally unique without a central coordinator:**
The combination of the exact creation second, the specific machine, the specific process on that machine, and a counter that increments within that second means two ObjectIds generated at the same millisecond on different machines or processes will still differ because the machine/process bytes differ. No server needs to coordinate ID assignment.

---

## 5. When to Choose MongoDB vs SQL

**Choose MongoDB when:**
1. Your application stores user-generated content where each record has a different set of optional fields (e.g., a product catalogue with thousands of different attribute combinations).
2. You need to store hierarchical or nested data (e.g., a blog post with embedded comments and tags) and want to avoid multiple JOINs.
3. You expect extremely high write throughput and need to shard data horizontally across many nodes (e.g., IoT sensor events, activity logs).

**Choose SQL when:**
1. Your data has strict relational integrity requirements — e.g., a banking system where account, transaction, and ledger tables must be kept perfectly consistent with foreign key constraints.
2. You rely heavily on complex multi-table JOINs and aggregate queries that are well-suited to the relational algebra SQL optimisers are built for (e.g., business intelligence reporting on normalised ERP data).
