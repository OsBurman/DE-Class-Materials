# Exercise 03: Normalization and Schema Design

## Objective
Identify normalization violations in a denormalized table and redesign the schema through the three normal forms (1NF → 2NF → 3NF).

## Background
Normalization is the process of structuring a relational database to reduce redundancy and improve data integrity. The three most common normal forms are:

| Normal Form | Rule |
|---|---|
| **1NF** | Every column holds atomic (indivisible) values; no repeating groups; each row is uniquely identifiable. |
| **2NF** | Is in 1NF **and** every non-key column is fully functionally dependent on the **whole** primary key (no partial dependencies — relevant when there is a composite PK). |
| **3NF** | Is in 2NF **and** no non-key column is transitively dependent on the primary key (non-key columns depend only on the PK, not on other non-key columns). |

## Starter Schema — `library_old`

The `setup.sql` creates a single, denormalized table that represents book loans:

```
library_old
-----------
loan_id          INTEGER  (intended primary key)
member_name      VARCHAR
member_email     VARCHAR
member_city      VARCHAR
member_country   VARCHAR
book_title       VARCHAR
book_genres      VARCHAR   ← e.g. "Fiction, Thriller, Mystery" (multi-value!)
author_first     VARCHAR
author_last      VARCHAR
author_email     VARCHAR
publisher_name   VARCHAR
publisher_city   VARCHAR
loan_date        DATE
return_date      DATE
```

## Requirements

**Part 1 — Identify violations**

In a comment block at the top of your queries file, answer each of these questions:

1. Which column(s) violate **1NF** and why?
2. Does this table have a composite primary key? What **partial dependencies** exist (if any)?
3. List at least **two transitive dependencies** that violate **3NF**.

**Part 2 — Design a normalized schema**

Write `CREATE TABLE` statements that bring the design to **3NF**. Your normalized schema must include at least these tables:

- `members` — member identity and location
- `authors` — author details
- `publishers` — publisher details  
- `books` — book details with FK to author and publisher
- `genres` — genre lookup table
- `book_genres` — junction table resolving the many-to-many between books and genres
- `loans` — loan record with FKs to members and books

All tables must have:
- A single-column surrogate primary key (`SERIAL`)
- Appropriate `NOT NULL` constraints
- `FOREIGN KEY` references where applicable

**Part 3 — Populate and verify**

After creating the normalized schema, insert at least:
- 2 members, 2 authors, 1 publisher, 3 books, 3 genres
- Assign at least 2 genres to one book (via `book_genres`)
- 3 loan records

Then write a `SELECT` that joins `loans`, `members`, and `books` to list: member name, book title, loan date.

## Hints
- "Fiction, Thriller" in one cell → needs a separate `genres` table + junction table.
- `author_email` depends on `author_first`/`author_last`, not on `loan_id` → transitive dependency.
- `publisher_city` depends on `publisher_name`, not on `loan_id` → transitive dependency.
- When all transitive dependencies are removed and you have no multi-value columns, you have reached 3NF.
