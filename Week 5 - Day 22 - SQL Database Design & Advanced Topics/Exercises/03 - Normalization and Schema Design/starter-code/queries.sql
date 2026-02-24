-- =============================================================
-- Exercise 03: Normalization and Schema Design
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Identify violations (answer in comment blocks)
-- ---------------------------------------------------------------

-- TODO 1a: Which column(s) in library_old violate 1NF, and why?
/*
  YOUR ANSWER HERE
*/

-- TODO 1b: Does library_old have a composite PK? What partial
--          dependencies exist?
/*
  YOUR ANSWER HERE
*/

-- TODO 1c: List at least TWO transitive dependencies in library_old.
/*
  YOUR ANSWER HERE
*/

-- ---------------------------------------------------------------
-- Part 2 — Normalized schema (3NF)
-- Drop tables if they exist first (in dependency order)
-- ---------------------------------------------------------------

-- TODO 2: DROP existing normalized tables (if any) in correct order.
--         Then write CREATE TABLE for:
--           members, authors, publishers, books,
--           genres, book_genres, loans


-- ---------------------------------------------------------------
-- Part 3 — Populate and verify
-- ---------------------------------------------------------------

-- TODO 3a: Insert at least 2 members, 2 authors, 1 publisher,
--          3 books, 3 genres, genre assignments, and 3 loans.


-- TODO 3b: Write a SELECT joining loans → members and books
--          to display: member full name, book title, loan_date.

