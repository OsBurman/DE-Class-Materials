# Exercise 03 — Worksheet: RESTful API Design and Resource Naming

---

## Part 1 — REST Architectural Constraints

For each constraint, write one sentence of explanation and one concrete API example.

1. **Uniform Interface**
   - Explanation: TODO
   - Example: TODO

2. **Stateless**
   - Explanation: TODO
   - Example: TODO

3. **Cacheable**
   - Explanation: TODO
   - Example: TODO

4. **Client-Server**
   - Explanation: TODO
   - Example: TODO

5. **Layered System**
   - Explanation: TODO
   - Example: TODO

6. **Code on Demand** (optional constraint)
   - Explanation: TODO
   - Example: TODO

---

## Part 2 — Fix the Bad URLs

For each bad URL below, write the corrected version and identify the violated rule.

| # | Bad URL | Fixed URL | Rule Violated |
|---|---|---|---|
| 1 | `GET /getBooks` | TODO | TODO |
| 2 | `GET /BooksList` | TODO | TODO |
| 3 | `POST /createBook` | TODO | TODO |
| 4 | `GET /books/getById/42` | TODO | TODO |
| 5 | `DELETE /deleteBook/42` | TODO | TODO |
| 6 | `GET /books/42/getAuthor` | TODO | TODO |
| 7 | `POST /books/42/addReview` | TODO | TODO |
| 8 | `GET /books?action=search&q=orwell` | TODO | TODO |
| 9 | `GET /BOOKS` | TODO | TODO |
| 10 | `GET /book` | TODO | TODO |
| 11 | `GET /books/42/reviews/7/comments/3/author` | TODO | TODO |
| 12 | `PUT /api/books/v2update/42` | TODO | TODO |

---

## Part 3 — Design a Complete Resource URL Set

Design endpoints for a Library API (`books`, `authors`, `loans`).

| Method | URL | Description |
|---|---|---|
| TODO | TODO | List all books |
| TODO | TODO | Get a single book by ID |
| TODO | TODO | Create a new book |
| TODO | TODO | Fully replace a book |
| TODO | TODO | Partially update a book |
| TODO | TODO | Delete a book |
| TODO | TODO | Get all books by a specific author |
| --- | --- | --- |
| TODO | TODO | List all authors |
| TODO | TODO | Get a single author by ID |
| TODO | TODO | Create a new author |
| TODO | TODO | Update an author |
| TODO | TODO | Delete an author |
| --- | --- | --- |
| TODO | TODO | List all loans |
| TODO | TODO | Get a single loan by ID |
| TODO | TODO | Create a new loan |
| TODO | TODO | Return a book (close a loan) |

---

## Part 4 — API Versioning Strategies

### Strategy 1: URL Path Versioning
- Example URL: TODO
- Advantage: TODO
- Disadvantage: TODO

### Strategy 2: Query Parameter Versioning
- Example URL: TODO
- Advantage: TODO
- Disadvantage: TODO

### Strategy 3: Header Versioning
- Example header: TODO
- Advantage: TODO
- Disadvantage: TODO

### Recommendation
**Which strategy would you use for a new public API and why?**

TODO (write 2–3 sentences justifying your choice)
