# Day 35 Exercise 01 — NoSQL vs SQL Comparison and Document Model
# Complete every section marked with TODO below.

---

## 1. Comparison Table

Fill in every `???` cell.

| Feature        | SQL (Relational) | MongoDB (Document) |
|----------------|------------------|--------------------|
| Data model     | ???              | ???                |
| Schema         | ???              | ???                |
| Relationships  | ???              | ???                |
| Query language | SQL              | ???                |
| Scalability    | ???              | ???                |
| Best for       | ???              | ???                |

---

## 2. NoSQL Types

<!-- TODO: List the four main NoSQL database categories.
     For each, provide:
       - Category name
       - One sentence describing what it stores
       - One real-world product example
     Example format:
       **Key-Value** — Stores simple key→value pairs. Example: Redis
-->

1. ???
2. ???
3. ???
4. ???

---

## 3. Document Model — Sample BSON Document

<!-- TODO: Write a JSON/BSON document representing a `user`.
     Required fields: _id (ObjectId), name, email, age, tags (array), address (embedded object).
     Use realistic values.
-->

```json
{
  "_id": "???",
  "name": "???",
  "email": "???",
  "age": ???,
  "tags": [],
  "address": {
    "street": "???",
    "city": "???",
    "country": "???"
  }
}
```

---

## 4. ObjectId Breakdown

<!-- TODO: Explain the four components encoded in MongoDB's 12-byte ObjectId.
     For each component state: what it encodes, how many bytes, and why it contributes to global uniqueness.
-->

| Bytes | Component | What it encodes |
|-------|-----------|-----------------|
| 1–4   | ???       | ???             |
| 5–7   | ???       | ???             |
| 8–9   | ???       | ???             |
| 10–12 | ???       | ???             |

Why does this make ObjectId globally unique without a central coordinator?
<!-- TODO: Write 1–2 sentences explaining the uniqueness guarantee. -->

---

## 5. When to Choose MongoDB vs SQL

<!-- TODO: List THREE scenarios where MongoDB is the better choice, and TWO where SQL wins.
     Be specific — generic answers like "when data is flexible" are too vague.
     Example: "MongoDB: storing user activity events where each event type has different fields"
-->

**Choose MongoDB when:**
1. ???
2. ???
3. ???

**Choose SQL when:**
1. ???
2. ???
