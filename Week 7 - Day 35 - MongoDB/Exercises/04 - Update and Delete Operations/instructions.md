# Exercise 04: Update and Delete Operations

## Objective
Modify and remove documents in a MongoDB collection using `updateOne`, `updateMany`, `deleteOne`, and `deleteMany`.

## Background
The bookstore's inventory changes constantly — prices are updated, books go out of stock, and discontinued titles need to be removed. You will use MongoDB's update and delete commands to keep the `books` collection current.

## Requirements

Assume the `books` collection from Exercise 03 is populated with 5 documents (re-run Exercise 03 first if needed).

1. Use `updateOne` with the `$set` operator to change the `price` of `"The Great Gatsby"` to `12.99`.
2. Use `updateOne` with the `$set` operator to set `available: false` on `"Sapiens"`.
3. Use `updateMany` with the `$set` operator to add a new field `onSale: true` to **all** books with a `price` less than `20`.
4. Use `updateOne` with the `$unset` operator to **remove** the `onSale` field from `"The Great Gatsby"`.
5. Use `deleteOne` to delete the document where `title` is `"Clean Code"`.
6. Use `deleteMany` to delete **all** books where `available` is `false`.
7. Use `find()` to confirm the final state of the collection and verify the changes.

## Hints
- `$set` modifies specified fields without touching the rest of the document: `{ $set: { field: value } }`.
- `$unset` removes a field: `{ $unset: { fieldName: "" } }` — the value `""` is conventional, any value works.
- `updateOne` modifies only the **first** matching document; `updateMany` modifies **all** matches.
- After `deleteMany({ available: false })`, the collection should have fewer documents — use `countDocuments` to verify.

## Expected Output

```js
// After updateOne — price of Great Gatsby
{ title: 'The Great Gatsby', price: 12.99, ... }

// After updateMany — onSale field added to cheap books
// Dune (14.99), Sapiens (19.99), Great Gatsby (12.99) should have onSale: true

// After $unset — Great Gatsby should no longer have onSale field

// After deleteOne — Clean Code gone
// After deleteMany — Sapiens (available: false) gone

// Final find() — should show remaining books
[
  { title: 'The Pragmatic Programmer', price: 39.99, available: true },
  { title: 'Dune', price: 14.99, available: true, onSale: true },
  { title: 'The Great Gatsby', price: 12.99, available: true }
]
```
