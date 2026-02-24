-- =============================================================
-- Exercise 02: ALTER and DROP Statements
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part A — Add and modify columns
-- ---------------------------------------------------------------

-- TODO 1: Add a nullable TEXT column called `biography` to the authors table.


-- TODO 2: Add a NOT NULL INTEGER column `available_stock` with DEFAULT 0 to books.


-- TODO 3: Add a nullable VARCHAR(20) column `phone` to members.


-- TODO 4: Rename the `phone` column in members to `phone_number`.


-- TODO 5: Change authors.nationality from VARCHAR(50) to VARCHAR(80).


-- ---------------------------------------------------------------
-- Part B — Add constraints after table creation
-- ---------------------------------------------------------------

-- TODO 6: Add a UNIQUE constraint named `uq_books_isbn` on books(isbn).
--         (The column was defined UNIQUE in CREATE TABLE; in practice you
--          can add a named constraint for documentation purposes. If a
--          conflict arises, drop the system-generated one first.)


-- TODO 7: Add a CHECK constraint named `chk_members_phone` on members
--         ensuring phone_number IS NULL OR length(phone_number) >= 7.


-- TODO 8: Add a NOT NULL constraint to books.price.


-- ---------------------------------------------------------------
-- Part C — Drop column and tables
-- ---------------------------------------------------------------

-- TODO 9: Drop the `biography` column from authors.


-- TODO 10: Create a temporary table `temp_audit_log` with:
--          log_id    SERIAL PRIMARY KEY
--          action    TEXT
--          logged_at TIMESTAMP DEFAULT NOW()


-- TODO 11: Drop the `temp_audit_log` table.

