# Exercise 05: Indexes and Query Optimization

## Objective
Create and analyze different types of indexes (single-column, unique, composite) and use `EXPLAIN` / `EXPLAIN ANALYZE` to understand how PostgreSQL chooses query execution plans.

## Background
An **index** is a data structure that allows the database engine to find rows faster without scanning the entire table. The trade-off is write overhead and storage cost.

| Index type | When to use |
|---|---|
| **B-tree** (default) | Equality and range lookups on one or more columns |
| **Unique** | Enforce uniqueness AND accelerate lookups |
| **Composite** | Queries that filter on two or more columns together |

**`EXPLAIN`** shows the query plan without executing it.  
**`EXPLAIN ANALYZE`** executes the query and shows actual run-time statistics (rows, loops, timing).

Run `setup.sql` first — it creates and seeds the library schema with enough rows to make index comparisons meaningful.

## Requirements

**Part 1 — Baseline execution plans**

1. Run `EXPLAIN` on a query that searches `books` by `title`. Note the plan node type (Seq Scan vs Index Scan).
2. Run `EXPLAIN` on a query that filters `loans` by `member_id`. Note the plan.
3. Run `EXPLAIN ANALYZE` on a query that joins `loans` to `books` and filters by `loan_date > '2024-01-01'`.

**Part 2 — Create indexes**

4. Create a **B-tree index** named `idx_books_title` on `books(title)`.
5. Create a **unique index** named `idx_authors_email` on `authors(email)`.
6. Create a **composite index** named `idx_loans_member_book` on `loans(member_id, book_id)`.
7. Create a **B-tree index** named `idx_loans_loan_date` on `loans(loan_date)`.

**Part 3 — Post-index execution plans**

8. Re-run `EXPLAIN` on the `books` title search from step 1. Has the plan changed?
9. Re-run `EXPLAIN ANALYZE` on the join from step 3. Compare cost and timing with the baseline.
10. List all indexes on the `books` and `loans` tables by querying `pg_indexes`.

**Part 4 — When NOT to index**

11. Write a query that returns **all rows** from `books` (no `WHERE` clause). Run `EXPLAIN`. Note that the planner still uses a Seq Scan — explain in a comment why.
12. In a comment, list two scenarios where adding an index would **hurt** performance rather than help it.

## Hints
- `CREATE INDEX idx_name ON table_name (column_name);`
- `CREATE UNIQUE INDEX idx_name ON table_name (column_name);`
- `CREATE INDEX idx_name ON table_name (col1, col2);`
- `EXPLAIN SELECT ...;`
- `EXPLAIN ANALYZE SELECT ...;`
- `SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'books';`
- PostgreSQL may not switch to an index scan on very small tables — the planner determines it's faster to scan. This is normal behaviour; the plans become more interesting with many rows.

## Expected Output
```sql
SELECT indexname FROM pg_indexes WHERE tablename IN ('books', 'loans') ORDER BY indexname;
-- idx_books_title
-- idx_loans_loan_date
-- idx_loans_member_book
-- (plus system-generated indexes for PKs and UNIQUE constraints)
```
