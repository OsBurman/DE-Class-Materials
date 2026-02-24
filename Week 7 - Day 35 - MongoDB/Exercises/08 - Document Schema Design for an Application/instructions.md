# Exercise 08: Document Schema Design for an Application

## Objective
Design MongoDB document schemas for a real application, choosing between embedded documents and document references based on data access patterns.

## Background
Unlike relational databases where normalisation rules dictate structure, MongoDB schema design is driven by *how the application reads data*. The two core patterns are **embedding** (putting related data inside the same document) and **referencing** (storing an ObjectId that points to another document, similar to a foreign key). Choosing wrong leads to expensive queries or bloated documents.

## Requirements

You are designing a MongoDB schema for a simple **blog platform**. The platform has:
- **Users** — account information, profile, list of roles
- **Posts** — blog entries written by a user, with a category and tags array
- **Comments** — short replies on a post, written by a user

Answer the following in `schema_design.md`:

1. **User document:** Design a `users` document schema. Include: `_id`, `username`, `email`, `passwordHash`, `roles` (array), `profile` (embedded object with `displayName`, `bio`, `avatarUrl`), `createdAt`. Write it as a JSON example.

2. **Post document (embed or reference comments?):** Design a `posts` document schema with: `_id`, `title`, `body`, `authorId` (reference to `users._id`), `category`, `tags` (array), `createdAt`, `updatedAt`. Write it as a JSON example. Then answer: should comments be **embedded inside the post** or stored in a **separate `comments` collection**? Explain your reasoning in 2–3 sentences.

3. **Comment document:** Design a `comments` document with: `_id`, `postId` (reference to `posts._id`), `authorId` (reference to `users._id`), `body`, `createdAt`. Write it as a JSON example.

4. **Embedding vs Referencing decision table:** Complete the table with the trade-offs for each approach.

5. **Access pattern analysis:** For each of the following queries, state which collection(s) are accessed and whether any `$lookup` is needed:
   - "Get post with title 'MongoDB Basics' and its author's username"
   - "Get all comments for a given post"
   - "Get all posts written by user 'alice'"

## Hints
- Embed when the child data is always read with the parent and will not grow unboundedly (e.g., a user's address).
- Reference when the child data is queried independently or can grow very large (e.g., a post's comments over years).
- An `authorId: ObjectId("...")` reference keeps the post document small; you pay a `$lookup` only when you need the author's name.
- MongoDB has a 16 MB document size limit — embedding hundreds of comments in a single post document will eventually break.

## Expected Output

Your completed `schema_design.md` should contain:
- Three JSON document examples (user, post, comment) with realistic placeholder values
- A completed trade-off table
- Access pattern answers that correctly identify collections and whether `$lookup` is needed
