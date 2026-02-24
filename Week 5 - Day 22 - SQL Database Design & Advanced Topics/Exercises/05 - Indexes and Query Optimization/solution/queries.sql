-- =============================================================
-- Exercise 05: Indexes and Query Optimization
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Baseline execution plans (before indexes)
-- ---------------------------------------------------------------

-- TODO 1: Baseline — search books by title
EXPLAIN SELECT * FROM books WHERE title = '1984';
-- With only 7 rows the planner will use a Seq Scan (sequential scan).
-- The planner determined a full-table scan is cheaper than an index
-- lookup for this small table size.

-- TODO 2: Baseline — filter loans by member_id
EXPLAIN SELECT * FROM loans WHERE member_id = 1;

-- TODO 3: Baseline — join loans to books filtered by loan_date
EXPLAIN ANALYZE
SELECT l.loan_id, b.title, l.loan_date
FROM   loans l
JOIN   books b ON b.book_id = l.book_id
WHERE  l.loan_date > '2024-01-01';
/*
  Baseline cost notes:
  Expect Seq Scans on both loans and books for this small dataset.
  With large tables, cost estimates here would be high due to no index on loan_date.
*/

-- ---------------------------------------------------------------
-- Part 2 — Create indexes
-- ---------------------------------------------------------------

-- TODO 4: B-tree index on books(title)
CREATE INDEX idx_books_title ON books (title);

-- TODO 5: Unique index on authors(email)
-- Note: a unique constraint already exists from the column definition;
-- this creates a named index explicitly.
CREATE UNIQUE INDEX idx_authors_email ON authors (email);

-- TODO 6: Composite index on loans(member_id, book_id)
CREATE INDEX idx_loans_member_book ON loans (member_id, book_id);

-- TODO 7: B-tree index on loans(loan_date)
CREATE INDEX idx_loans_loan_date ON loans (loan_date);

-- ---------------------------------------------------------------
-- Part 3 — Post-index execution plans
-- ---------------------------------------------------------------

-- TODO 8: Re-run EXPLAIN after creating idx_books_title
EXPLAIN SELECT * FROM books WHERE title = '1984';
/*
  Observation:
  On very small tables (< ~100 rows) PostgreSQL's planner will still
  choose a Seq Scan because the cost of an index lookup + heap fetch
  exceeds the cost of a full scan. This is expected and correct.
  On a large table (100k+ rows), the planner would switch to an
  Index Scan, dramatically reducing cost.
*/

-- TODO 9: Re-run EXPLAIN ANALYZE on the join after adding indexes
EXPLAIN ANALYZE
SELECT l.loan_id, b.title, l.loan_date
FROM   loans l
JOIN   books b ON b.book_id = l.book_id
WHERE  l.loan_date > '2024-01-01';
/*
  After-index cost notes:
  For a small dataset the plan may not change. On large tables,
  idx_loans_loan_date would enable an Index Scan on loans and
  significantly reduce estimated startup cost and actual execution time.
*/

-- TODO 10: List indexes on books and loans
SELECT tablename, indexname, indexdef
FROM   pg_indexes
WHERE  tablename IN ('books', 'loans')
ORDER  BY tablename, indexname;

-- Expected indexes visible:
--  books:  books_pkey, idx_books_title
--  loans:  loans_pkey, idx_loans_loan_date, idx_loans_member_book

-- ---------------------------------------------------------------
-- Part 4 — When NOT to index
-- ---------------------------------------------------------------

-- TODO 11: Full table scan — no WHERE clause
EXPLAIN SELECT * FROM books;
/*
  Explanation:
  The planner uses a Seq Scan because ALL rows must be returned.
  An index cannot reduce the I/O cost when no rows are filtered out.
  Indexes help only when a WHERE clause selects a small fraction
  of rows (good selectivity). Returning every row means reading
  every page anyway, making the index an unnecessary extra step.
*/

-- TODO 12: When indexes hurt performance
/*
  Scenario 1 — High-write, low-read tables (e.g., logging/audit tables):
    Every INSERT, UPDATE, or DELETE must also update the index
    data structure. On a table with thousands of writes per second
    and rare reads, the index maintenance overhead outweighs any
    read benefit.

  Scenario 2 — Low-cardinality columns (e.g., a `status` column with
    only 3 possible values: 'active', 'inactive', 'pending'):
    An index on such a column has poor selectivity — each value
    matches a large fraction of rows. The planner will often ignore
    the index and do a Seq Scan anyway. The index wastes space and
    write overhead without providing a meaningful speedup.
*/
