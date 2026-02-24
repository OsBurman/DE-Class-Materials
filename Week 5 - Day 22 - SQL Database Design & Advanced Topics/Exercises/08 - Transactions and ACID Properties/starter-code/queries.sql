-- =============================================================
-- Exercise 08: Transactions and ACID Properties
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Basic Commit and Rollback
-- ---------------------------------------------------------------

-- TODO 1: BEGIN a transaction. INSERT Tom Brady. COMMIT.
--         Then SELECT to verify he exists.


-- TODO 2: BEGIN a transaction. UPDATE Tom Brady's membership_type to 'student'.
--         ROLLBACK. Then SELECT to verify the update did NOT persist.


-- TODO 3: BEGIN a transaction. DELETE Tom Brady. ROLLBACK.
--         Verify he still exists.


-- ---------------------------------------------------------------
-- Part 2 — Multi-statement Atomic Transaction
-- ---------------------------------------------------------------

-- TODO 4: BEGIN a transaction. INSERT a loan (member 1 borrows book 2, today).
--         UPDATE books SET stock = stock - 1 WHERE book_id = 2.
--         COMMIT.
--         Verify: loan exists AND book 2 stock decreased by 1.


-- TODO 5: BEGIN a transaction. INSERT a loan for book_id=7.
--         UPDATE books SET stock = stock - 1 WHERE book_id = 7.
--         (This will fail due to CHECK constraint stock >= 0)
--         ROLLBACK.
--         Verify: no loan for book 7 exists, stock is still 0.


-- ---------------------------------------------------------------
-- Part 3 — Savepoints
-- ---------------------------------------------------------------

-- TODO 6: Transaction with savepoints and a failed duplicate insert.
-- BEGIN;
--   INSERT genre 'Biography';         SAVEPOINT sp_genre;
--   INSERT genre 'Self-Help';         SAVEPOINT sp_genre2;
--   INSERT genre 'Biography'; -- fails (duplicate)
--   ROLLBACK TO SAVEPOINT sp_genre2;
--   INSERT genre 'Science';           -- succeeds
-- COMMIT;
-- Verify all three genres exist.


-- TODO 7: Transaction — member insert then loan rollback via savepoint.
-- BEGIN;
--   INSERT new member;     SAVEPOINT sp_member;
--   INSERT loan for member;
--   ROLLBACK TO SAVEPOINT sp_member;
-- COMMIT;
-- Verify: member exists, loan does NOT exist.


-- ---------------------------------------------------------------
-- Part 4 — Isolation Levels (theory + demonstration)
-- ---------------------------------------------------------------

-- TODO 8: In the comment block below, explain dirty reads,
--         non-repeatable reads, and phantom reads, and state which
--         PostgreSQL isolation level prevents each.
/*
  Dirty Read:

  Non-Repeatable Read:

  Phantom Read:

  PostgreSQL isolation levels and what they prevent:
*/

-- TODO 9: Write a SERIALIZABLE transaction that transfers stock:
--         subtract 2 from book 1, add 2 to book 2.
--         Use: BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;

