# Exercise 06: Aggregation Pipeline Stages

## Objective
Build multi-stage MongoDB aggregation pipelines using `$match`, `$group`, `$project`, `$sort`, `$lookup`, and `$unwind`.

## Background
The bookstore wants analytics: total revenue by genre, average book price, and a joined view of orders with their book details. Simple `find()` queries can't aggregate across documents — that's what the **aggregation framework** is for. You chain pipeline stages; each stage transforms the documents and passes results to the next.

## Requirements

The setup block creates two collections: `books` (10 documents) and `orders` (5 documents). Run it first.

1. **$match + $group — Count by genre:** Write a pipeline that filters books with `available: true` and then groups them by `genre`, counting how many books are in each genre. Sort the result by count descending.

2. **$group — Average price:** Write a pipeline that groups **all** books (no filter) and computes the average `price` across the entire collection. Round to 2 decimal places using `$round`.

3. **$match + $group + $project — Genre revenue report:** Write a pipeline that:
   - Matches only available books
   - Groups by `genre`, summing `price` as `totalRevenue` and counting as `bookCount`
   - Projects a clean output with `genre`, `totalRevenue`, `bookCount`
   - Sorts by `totalRevenue` descending

4. **$unwind + $group — Tags count (orders):** The `orders` collection has an `items` array (array of book titles). Unwind the `items` array, then group by `item` to count how many orders include each title.

5. **$lookup — Join orders with books:** Write a pipeline on `orders` that uses `$lookup` to join with the `books` collection on `title` (orders have a `bookTitle` field; books have a `title` field). Project the order `customerId`, `bookTitle`, and the joined book's `price`.

## Hints
- Aggregation syntax: `db.collection.aggregate([ stage1, stage2, ... ])`.
- `$group` requires a `_id` field: `{ $group: { _id: "$genre", count: { $sum: 1 } } }`.
- `$project` with computed fields: `{ $project: { genre: "$_id", total: 1, _id: 0 } }`.
- `$lookup` syntax: `{ $lookup: { from: "books", localField: "bookTitle", foreignField: "title", as: "bookDetails" } }` — the result is an array named `bookDetails`.
- `$unwind: "$items"` flattens an array field so each element becomes its own document.

## Expected Output

```js
// Pipeline 1 — books per genre (available only)
[
  { _id: 'Technology', count: 2 },
  { _id: 'Psychology', count: 2 },
  ...
]

// Pipeline 2 — average price
[ { avgPrice: 22.67 } ]

// Pipeline 3 — genre revenue report
[
  { genre: 'Technology', totalRevenue: 74.98, bookCount: 2 },
  ...
]

// Pipeline 4 — items ordered count
[
  { _id: 'Clean Code', orderedCount: 2 },
  ...
]

// Pipeline 5 — orders joined with book price
[
  { customerId: 'C001', bookTitle: 'Clean Code', price: 34.99 },
  ...
]
```
