# Exercise 07: Creating and Using Indexes

## Objective
Create single-field and compound indexes in MongoDB, verify their use with `explain()`, and understand when indexes improve query performance.

## Background
Without indexes, MongoDB performs a **collection scan** — it reads every document to find matches. On large collections this is slow. An index stores a sorted copy of one or more fields, letting MongoDB jump directly to matching documents. The `explain("executionStats")` command shows whether a query used an index (`IXSCAN`) or scanned everything (`COLLSCAN`).

## Requirements

Use the `books` collection from Exercise 05 (run the setup block if needed).

1. Run `db.books.find({ genre: "Technology" }).explain("executionStats")` **before** creating any index. Record the `winningPlan.stage` value (it should be `COLLSCAN`) and the `totalDocsExamined` count.

2. Create a **single-field index** on the `genre` field.

3. Run the same `find({ genre: "Technology" })` query with `explain("executionStats")` again. Confirm the `winningPlan.stage` is now `IXSCAN` and `totalDocsExamined` is lower.

4. Create a **compound index** on `{ genre: 1, year: -1 }` (genre ascending, year descending).

5. Write a query that benefits from the compound index: find books where `genre` is `"Technology"` sorted by `year` descending. Run it with `explain("executionStats")` and confirm `IXSCAN` is used.

6. Use `db.books.getIndexes()` to list all indexes on the collection. Confirm at least 3 exist: the default `_id` index plus the two you created.

7. Drop the single-field `genre` index using `dropIndex`.

8. Run `db.books.getIndexes()` again to confirm only 2 indexes remain.

## Hints
- `createIndex` syntax: `db.collection.createIndex({ field: 1 })` — `1` = ascending, `-1` = descending.
- `explain("executionStats")` returns a large object; focus on `executionStats.executionStages.stage` or `queryPlanner.winningPlan.stage`.
- Compound indexes support queries on a **prefix** of the index fields — e.g., `{ genre: 1, year: -1 }` also accelerates queries on `genre` alone.
- `dropIndex({ genre: 1 })` drops an index by its key specification.

## Expected Output

```js
// Before index — COLLSCAN
winningPlan: { stage: 'COLLSCAN' }
totalDocsExamined: 10

// After single-field index — IXSCAN
winningPlan: { stage: 'IXSCAN', ... }
totalDocsExamined: 2   // only matched docs examined

// getIndexes() after creating both indexes
[
  { key: { _id: 1 }, name: '_id_' },
  { key: { genre: 1 }, name: 'genre_1' },
  { key: { genre: 1, year: -1 }, name: 'genre_1_year_-1' }
]

// After dropIndex — only 2 indexes remain
[
  { key: { _id: 1 }, name: '_id_' },
  { key: { genre: 1, year: -1 }, name: 'genre_1_year_-1' }
]
```
