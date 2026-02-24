-- =============================================================
-- Exercise 08: Transactions and ACID Properties
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Basic Commit and Rollback
-- ---------------------------------------------------------------

-- TODO 1: Committed INSERT
BEGIN;
INSERT INTO members (first_name, last_name, email, city, country, membership_type)
VALUES ('Tom', 'Brady', 'tbrady@email.com', 'Boston', 'USA', 'premium');
COMMIT;

-- Verify
SELECT first_name, last_name, membership_type
FROM   members
WHERE  email = 'tbrady@email.com';
-- → Tom | Brady | premium

-- TODO 2: Rolled-back UPDATE
BEGIN;
UPDATE members SET membership_type = 'student' WHERE email = 'tbrady@email.com';
ROLLBACK;

-- Verify (still 'premium' — update was rolled back)
SELECT membership_type FROM members WHERE email = 'tbrady@email.com';
-- → premium

-- TODO 3: Rolled-back DELETE
BEGIN;
DELETE FROM members WHERE email = 'tbrady@email.com';
ROLLBACK;

-- Verify (Tom Brady still exists)
SELECT COUNT(*) FROM members WHERE email = 'tbrady@email.com';
-- → 1

-- ---------------------------------------------------------------
-- Part 2 — Multi-statement Atomic Transaction
-- ---------------------------------------------------------------

-- TODO 4: Successful checkout (atomic loan + stock decrement)
BEGIN;
INSERT INTO loans (member_id, book_id, loan_date)
VALUES (1, 2, CURRENT_DATE);

UPDATE books SET stock = stock - 1 WHERE book_id = 2;
COMMIT;

-- Verify
SELECT title, stock FROM books WHERE book_id = 2;
-- → Animal Farm | 4  (was 5)
SELECT * FROM loans WHERE member_id = 1 AND book_id = 2 ORDER BY loan_id DESC LIMIT 1;

-- TODO 5: Failed checkout — constraint violation triggers rollback
BEGIN;
INSERT INTO loans (member_id, book_id, loan_date)
VALUES (1, 7, CURRENT_DATE);

UPDATE books SET stock = stock - 1 WHERE book_id = 7;
-- ↑ Fails: CHECK (stock >= 0) violated because stock is already 0
-- PostgreSQL marks the transaction as aborted automatically.
ROLLBACK;

-- Verify: no loan for book 7 from member 1 today, stock still 0
SELECT COUNT(*) FROM loans WHERE book_id = 7;  -- → 0 (only seed loans count)
SELECT stock    FROM books  WHERE book_id = 7;  -- → 0

-- ---------------------------------------------------------------
-- Part 3 — Savepoints
-- ---------------------------------------------------------------

-- TODO 6: Savepoint — recover from duplicate genre insert
BEGIN;
INSERT INTO genres (genre_name) VALUES ('Biography');
SAVEPOINT sp_genre;

INSERT INTO genres (genre_name) VALUES ('Self-Help');
SAVEPOINT sp_genre2;

-- This will fail (duplicate 'Biography'), aborting to the last savepoint:
-- INSERT INTO genres (genre_name) VALUES ('Biography');
-- After the failure PostgreSQL is in error state within the transaction.
-- Roll back to sp_genre2 (before the failed statement's effect):
ROLLBACK TO SAVEPOINT sp_genre2;

INSERT INTO genres (genre_name) VALUES ('Science');
COMMIT;

-- Verify all three new genres exist
SELECT genre_name FROM genres WHERE genre_name IN ('Biography', 'Self-Help', 'Science');
-- → Biography, Self-Help, Science

-- TODO 7: Savepoint — insert member, rollback loan, keep member
BEGIN;
INSERT INTO members (first_name, last_name, email, city, country)
VALUES ('Zara', 'Ali', 'zali@email.com', 'Dubai', 'UAE');

SAVEPOINT sp_member;

INSERT INTO loans (member_id, book_id, loan_date)
VALUES (
    (SELECT member_id FROM members WHERE email = 'zali@email.com'),
    1,
    CURRENT_DATE
);

ROLLBACK TO SAVEPOINT sp_member;  -- undo the loan insert only
COMMIT;                            -- commit the member insert

-- Verify
SELECT COUNT(*) FROM members WHERE email = 'zali@email.com';
-- → 1  (member exists)

SELECT COUNT(*) FROM loans
WHERE  member_id = (SELECT member_id FROM members WHERE email = 'zali@email.com');
-- → 0  (loan was rolled back)

-- ---------------------------------------------------------------
-- Part 4 — Isolation Levels
-- ---------------------------------------------------------------

-- TODO 8: Isolation anomalies explained
/*
  Dirty Read:
    Transaction A reads data written by Transaction B that has NOT yet
    committed. If B later rolls back, A has read invalid ("dirty") data.
    PostgreSQL's READ COMMITTED level prevents dirty reads (it is the
    default isolation level). READ UNCOMMITTED theoretically allows them,
    but PostgreSQL treats it the same as READ COMMITTED.

  Non-Repeatable Read:
    Transaction A reads a row, then Transaction B updates and commits
    that row. When A reads the same row again within its transaction,
    it gets a different value. Prevented by REPEATABLE READ and above.

  Phantom Read:
    Transaction A queries a range of rows (e.g., WHERE price < 15).
    Transaction B inserts a new row that falls in that range and commits.
    When A re-executes the same range query, it sees a new ("phantom") row.
    Prevented only by SERIALIZABLE isolation level.

  PostgreSQL isolation levels summary:
  ┌──────────────────────┬─────────────┬────────────────────┬──────────────┐
  │ Level                │ Dirty Read  │ Non-Repeatable Read│ Phantom Read │
  ├──────────────────────┼─────────────┼────────────────────┼──────────────┤
  │ READ UNCOMMITTED     │ Not possible│ Possible           │ Possible     │
  │ READ COMMITTED (def) │ Not possible│ Possible           │ Possible     │
  │ REPEATABLE READ      │ Not possible│ Not possible       │ Not possible*│
  │ SERIALIZABLE         │ Not possible│ Not possible       │ Not possible │
  └──────────────────────┴─────────────┴────────────────────┴──────────────┘
  * PostgreSQL's REPEATABLE READ also prevents phantom reads in practice
    due to its MVCC implementation.
*/

-- TODO 9: SERIALIZABLE stock transfer transaction
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Transfer 2 units of stock from book 1 to book 2
UPDATE books SET stock = stock - 2 WHERE book_id = 1;
UPDATE books SET stock = stock + 2 WHERE book_id = 2;

COMMIT;

-- Verify
SELECT book_id, title, stock FROM books WHERE book_id IN (1, 2);
-- book 1: stock decreased by 2  (10 → 8)
-- book 2: stock increased by 2  (4  → 6)
