# Blog Platform — MongoDB Schema Design (SOLUTION)

---

## 1. User Document

```json
{
  "_id": { "$oid": "64a1f3c2b45e2a001f3d8c01" },
  "username": "alice_dev",
  "email": "alice@example.com",
  "passwordHash": "$2b$12$KIXNs7F...",
  "roles": ["author", "user"],
  "profile": {
    "displayName": "Alice Dev",
    "bio": "Backend engineer and occasional blogger.",
    "avatarUrl": "https://cdn.example.com/avatars/alice.png"
  },
  "createdAt": { "$date": "2024-01-15T09:00:00Z" }
}
```

Design notes:
- `roles` as an array of strings allows multiple roles without a separate join table.
- `profile` is embedded because it is always read with the user and rarely changes independently.
- `passwordHash` stores a bcrypt hash — never store plain text passwords.

---

## 2. Post Document

```json
{
  "_id": { "$oid": "64a1f3c2b45e2a001f3d8c10" },
  "title": "Getting Started with MongoDB",
  "body": "MongoDB is a document-oriented NoSQL database...",
  "authorId": { "$oid": "64a1f3c2b45e2a001f3d8c01" },
  "category": "Databases",
  "tags": ["mongodb", "nosql", "backend"],
  "createdAt": { "$date": "2024-02-01T10:30:00Z" },
  "updatedAt": { "$date": "2024-02-03T14:00:00Z" }
}
```

### Embed comments or separate collection?

**Decision: separate `comments` collection.**

A popular post can accumulate hundreds or thousands of comments over time. Embedding them all inside the post document would cause it to grow unboundedly, eventually hitting MongoDB's 16 MB document limit and making every post fetch unnecessarily large. A separate collection lets us query comments independently (e.g., "show all comments by user Alice"), paginate them efficiently, and index them on `postId` for fast retrieval without loading the full post body.

---

## 3. Comment Document

```json
{
  "_id": { "$oid": "64a1f3c2b45e2a001f3d8c20" },
  "postId":   { "$oid": "64a1f3c2b45e2a001f3d8c10" },
  "authorId": { "$oid": "64a1f3c2b45e2a001f3d8c01" },
  "body": "Great article! Really helped me understand aggregation pipelines.",
  "createdAt": { "$date": "2024-02-02T08:15:00Z" }
}
```

Design notes:
- `postId` is a reference — create an index on it for fast comment lookup per post.
- `authorId` is a reference — use `$lookup` when you need to display the commenter's username.

---

## 4. Embedding vs Referencing — Trade-off Table

| Dimension          | Embedding                                       | Referencing                                        |
|--------------------|-------------------------------------------------|----------------------------------------------------|
| Read performance   | Fast — one query retrieves parent + child       | Slower — requires $lookup or second query          |
| Write performance  | Updates to parent doc may rewrite large payload | Targeted writes to child collection are cheaper    |
| Data duplication   | May duplicate data (e.g., author name in posts) | Single source of truth; no duplication             |
| Document size      | Grows over time if child list is unbounded      | Parent stays small regardless of child count       |
| Query complexity   | Simple — no joins needed                        | More complex — $lookup pipelines required          |
| Best for           | One-to-few, tightly coupled, co-read data       | One-to-many, independently queried, large sub-sets |

---

## 5. Access Pattern Analysis

**Query A:** "Get post with title 'MongoDB Basics' and its author's username"
- Collections accessed: `posts` then `users`
- $lookup needed? **Yes** — the post stores `authorId` (an ObjectId reference); to get `username` you must join to `users`.
- Explanation: `db.posts.aggregate([{ $match: { title: "MongoDB Basics" } }, { $lookup: { from: "users", localField: "authorId", foreignField: "_id", as: "author" } }, { $unwind: "$author" }, { $project: { title: 1, "author.username": 1 } }])`

**Query B:** "Get all comments for a given postId"
- Collections accessed: `comments` only
- $lookup needed? **No** — `comments` already stores `postId`. A simple `find({ postId: ObjectId("...") })` suffices (add an index on `postId` for speed).
- Explanation: The separate `comments` collection is queried directly by `postId` — no join required.

**Query C:** "Get all posts written by user 'alice'"
- Collections accessed: `users` (to resolve username → `_id`), then `posts`
- $lookup needed? **No** — if you already know Alice's `_id`, `db.posts.find({ authorId: aliceId })` is a single-collection query. You only need `$lookup` if you start from the username string and must resolve the ObjectId first.
- Explanation: Store `authorId` on posts enables efficient querying by author without a join, provided you have an index on `posts.authorId`.
