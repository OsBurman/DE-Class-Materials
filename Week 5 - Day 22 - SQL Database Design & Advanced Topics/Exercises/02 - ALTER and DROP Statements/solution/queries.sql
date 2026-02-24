-- =============================================================
-- Exercise 02: ALTER and DROP Statements
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part A — Add and modify columns
-- ---------------------------------------------------------------

-- TODO 1: Add biography column to authors
ALTER TABLE authors ADD COLUMN biography TEXT;

-- TODO 2: Add available_stock to books
ALTER TABLE books ADD COLUMN available_stock INTEGER NOT NULL DEFAULT 0;

-- TODO 3: Add phone column to members
ALTER TABLE members ADD COLUMN phone VARCHAR(20);

-- TODO 4: Rename phone → phone_number
ALTER TABLE members RENAME COLUMN phone TO phone_number;

-- TODO 5: Widen nationality to VARCHAR(80)
ALTER TABLE authors ALTER COLUMN nationality TYPE VARCHAR(80);

-- ---------------------------------------------------------------
-- Part B — Add constraints after table creation
-- ---------------------------------------------------------------

-- TODO 6: Add a named UNIQUE constraint on books(isbn)
-- The UNIQUE was declared inline; add an explicit named one:
ALTER TABLE books ADD CONSTRAINT uq_books_isbn UNIQUE (isbn);
-- (If this fails with "already exists", drop the system-generated constraint first:
--  ALTER TABLE books DROP CONSTRAINT books_isbn_key;
--  then retry the ADD CONSTRAINT above.)

-- TODO 7: Add CHECK constraint on members.phone_number
ALTER TABLE members
    ADD CONSTRAINT chk_members_phone
    CHECK (phone_number IS NULL OR LENGTH(phone_number) >= 7);

-- TODO 8: Add NOT NULL to books.price
ALTER TABLE books ALTER COLUMN price SET NOT NULL;

-- ---------------------------------------------------------------
-- Part C — Drop column and tables
-- ---------------------------------------------------------------

-- TODO 9: Drop biography from authors
ALTER TABLE authors DROP COLUMN biography;

-- TODO 10: Create temp_audit_log
CREATE TABLE temp_audit_log (
    log_id    SERIAL    PRIMARY KEY,
    action    TEXT,
    logged_at TIMESTAMP DEFAULT NOW()
);

-- TODO 11: Drop temp_audit_log
DROP TABLE temp_audit_log;

-- ---------------------------------------------------------------
-- Verify final state of books table
-- ---------------------------------------------------------------
SELECT column_name, data_type, is_nullable, column_default
FROM   information_schema.columns
WHERE  table_name = 'books'
ORDER  BY ordinal_position;
