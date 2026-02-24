# Exercise 02: mongosh Basics and Collection Exploration

## Objective
Navigate the MongoDB shell (`mongosh`), switch databases, inspect collections, and insert your first document.

## Background
`mongosh` is the official MongoDB interactive shell. You type JavaScript-style commands directly against a running MongoDB instance. Before you can query data you need to know how to navigate to the right database, list what exists, and confirm documents are stored correctly.

## Requirements

1. Start `mongosh` and run the command to **list all databases** on the server.
2. Switch to a database named `bookstore`. (MongoDB creates it automatically when you first write data.)
3. Run the command to **show all collections** in the `bookstore` database. At this point the output will be empty — that is expected.
4. Insert **one document** into a collection called `books` using `insertOne`. The document must have the fields: `title` (string), `author` (string), `genre` (string), `year` (number), `available` (boolean).
5. Run `show collections` again and confirm `books` now appears.
6. Run `db.books.findOne()` to retrieve and display the document you just inserted.
7. Run `db.stats()` and note the `collections` and `dataSize` fields in the output.

## Hints
- `show dbs` lists all databases; `use <name>` switches to one.
- A database only shows up in `show dbs` once it contains at least one document.
- `db` always refers to the currently selected database — you can type `db` alone to confirm which one is active.
- `insertOne({ ... })` returns a result object containing `acknowledged: true` and the new `_id`.

## Expected Output

```
// show dbs — before inserting
admin   40.00 KiB
config  60.00 KiB
local   40.00 KiB

// use bookstore
switched to db bookstore

// show collections (empty)
(no output)

// insertOne result
{
  acknowledged: true,
  insertedId: ObjectId('...')
}

// show collections (after insert)
books

// findOne result
{
  _id: ObjectId('...'),
  title: 'The Pragmatic Programmer',
  author: 'David Thomas',
  genre: 'Technology',
  year: 1999,
  available: true
}
```
