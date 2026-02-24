-- =============================================================
-- Exercise 05: Indexes and Query Optimization
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Baseline execution plans (before adding indexes)
-- ---------------------------------------------------------------

-- TODO 1: EXPLAIN a query that searches books by title = '1984'.
--         Note: what scan type does the planner choose?


-- TODO 2: EXPLAIN a query that filters loans by member_id = 1.


-- TODO 3: EXPLAIN ANALYZE a query that joins loans to books and
--         filters by loan_date > '2024-01-01'.
--         Record the cost estimate in a comment.
/*
  Baseline cost notes:
*/

-- ---------------------------------------------------------------
-- Part 2 — Create indexes
-- ---------------------------------------------------------------

-- TODO 4: Create a B-tree index idx_books_title on books(title).


-- TODO 5: Create a unique index idx_authors_email on authors(email).


-- TODO 6: Create a composite index idx_loans_member_book
--         on loans(member_id, book_id).


-- TODO 7: Create a B-tree index idx_loans_loan_date on loans(loan_date).


-- ---------------------------------------------------------------
-- Part 3 — Post-index execution plans
-- ---------------------------------------------------------------

-- TODO 8: Re-run EXPLAIN on the books title search. Did the plan change?
/*
  Observation:
*/

-- TODO 9: Re-run EXPLAIN ANALYZE on the loans+books join from TODO 3.
--         Compare costs with the baseline.
/*
  After-index cost notes:
*/

-- TODO 10: List all indexes on books and loans tables using pg_indexes.


-- ---------------------------------------------------------------
-- Part 4 — When NOT to index
-- ---------------------------------------------------------------

-- TODO 11: Run EXPLAIN on SELECT * FROM books (no WHERE clause).
--          Note the plan type and explain in a comment why an index
--          would not help here.
/*
  Explanation:
*/

-- TODO 12: In a comment, list two scenarios where adding an index
--          would HURT performance.
/*
  Scenario 1:
  Scenario 2:
*/
