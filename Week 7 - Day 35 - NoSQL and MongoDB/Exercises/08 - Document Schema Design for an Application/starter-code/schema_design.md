# Blog Platform — MongoDB Schema Design
# Complete every TODO section below.

---

## 1. User Document

<!-- TODO: Design the users document schema.
     Required fields: _id (ObjectId), username, email, passwordHash,
     roles (array of strings), profile (embedded object: displayName, bio, avatarUrl),
     createdAt (ISODate).
     Use realistic placeholder values.
-->

```json
{
  "_id": "???",
  "username": "???",
  "email": "???",
  "passwordHash": "???",
  "roles": [],
  "profile": {
    "displayName": "???",
    "bio": "???",
    "avatarUrl": "???"
  },
  "createdAt": "???"
}
```

---

## 2. Post Document

<!-- TODO: Design the posts document schema.
     Required fields: _id, title, body, authorId (ObjectId reference to users._id),
     category, tags (array), createdAt, updatedAt.
-->

```json
{
  "_id": "???",
  "title": "???",
  "body": "???",
  "authorId": { "$oid": "???" },
  "category": "???",
  "tags": [],
  "createdAt": "???",
  "updatedAt": "???"
}
```

### Embed comments or separate collection?

<!-- TODO: Answer in 2–3 sentences.
     Should comments be an array inside the post document, or a separate comments collection?
     Justify your choice using at least one concrete reason.
-->

???

---

## 3. Comment Document

<!-- TODO: Design the comments document schema.
     Required fields: _id, postId (reference), authorId (reference), body, createdAt.
-->

```json
{
  "_id": "???",
  "postId": { "$oid": "???" },
  "authorId": { "$oid": "???" },
  "body": "???",
  "createdAt": "???"
}
```

---

## 4. Embedding vs Referencing — Trade-off Table

<!-- TODO: Fill in every ??? cell. -->

| Dimension          | Embedding                          | Referencing                        |
|--------------------|------------------------------------|------------------------------------|
| Read performance   | ???                                | ???                                |
| Write performance  | ???                                | ???                                |
| Data duplication   | ???                                | ???                                |
| Document size      | ???                                | ???                                |
| Query complexity   | ???                                | ???                                |
| Best for           | ???                                | ???                                |

---

## 5. Access Pattern Analysis

<!-- TODO: For each query, state which collection(s) are hit and whether $lookup is needed. -->

**Query A:** "Get post with title 'MongoDB Basics' and its author's username"
- Collections accessed: ???
- $lookup needed? ???
- Explanation: ???

**Query B:** "Get all comments for a given postId"
- Collections accessed: ???
- $lookup needed? ???
- Explanation: ???

**Query C:** "Get all posts written by user 'alice'"
- Collections accessed: ???
- $lookup needed? ???
- Explanation: ???
