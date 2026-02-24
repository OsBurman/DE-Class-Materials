-- =============================================================
-- Exercise 06: Views and Stored Procedures
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Views
-- ---------------------------------------------------------------

-- TODO 1: v_available_books
CREATE VIEW v_available_books AS
SELECT b.book_id,
       b.title,
       a.first_name || ' ' || a.last_name AS author_name,
       b.stock,
       b.price
FROM   books   b
JOIN   authors a ON a.author_id = b.author_id
WHERE  b.stock > 0;

-- TODO 2: v_active_loans
CREATE VIEW v_active_loans AS
SELECT l.loan_id,
       m.first_name || ' ' || m.last_name AS member_name,
       b.title,
       l.loan_date
FROM   loans   l
JOIN   members m ON m.member_id = l.member_id
JOIN   books   b ON b.book_id   = l.book_id
WHERE  l.return_date IS NULL;

-- TODO 3: v_member_loan_count
CREATE VIEW v_member_loan_count AS
SELECT m.member_id,
       m.first_name || ' ' || m.last_name AS member_name,
       COUNT(l.loan_id)                   AS loan_count
FROM   members m
LEFT JOIN loans l ON l.member_id = m.member_id
GROUP  BY m.member_id, m.first_name, m.last_name;

-- TODO 4: Query v_available_books — books under $13
SELECT * FROM v_available_books
WHERE  price < 13.00
ORDER  BY price;
-- Expected:
-- book_id | title              | author_name      | stock | price
-- --------+--------------------+------------------+-------+-------
--       6 | Pride & Prejudice  | Jane Austen      |    12 |  9.99
--       2 | Animal Farm        | George Orwell    |     5 |  8.99
--       5 | The Great Gatsby   | F. Scott Fitzger.|     8 | 11.99
--       1 | 1984               | George Orwell    |    10 | 12.99

-- TODO 5: Recreate v_available_books adding published_year
CREATE OR REPLACE VIEW v_available_books AS
SELECT b.book_id,
       b.title,
       a.first_name || ' ' || a.last_name AS author_name,
       b.published_year,
       b.stock,
       b.price
FROM   books   b
JOIN   authors a ON a.author_id = b.author_id
WHERE  b.stock > 0;

-- TODO 6: Drop v_member_loan_count
DROP VIEW v_member_loan_count;

-- ---------------------------------------------------------------
-- Part 2 — Stored Functions
-- ---------------------------------------------------------------

-- TODO 7: get_member_loans
CREATE OR REPLACE FUNCTION get_member_loans(p_member_id INTEGER)
RETURNS TABLE (
    loan_id     INTEGER,
    book_title  VARCHAR,
    loan_date   DATE,
    return_date DATE
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT l.loan_id,
           b.title::VARCHAR,
           l.loan_date,
           l.return_date
    FROM   loans l
    JOIN   books b ON b.book_id = l.book_id
    WHERE  l.member_id = p_member_id
    ORDER  BY l.loan_date;
END;
$$;

-- Test
SELECT * FROM get_member_loans(1);

-- TODO 8: count_loans_by_member
CREATE OR REPLACE FUNCTION count_loans_by_member(p_member_id INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM   loans
    WHERE  member_id = p_member_id;

    RETURN v_count;
END;
$$;

-- Test
SELECT count_loans_by_member(2);  -- Expected: 2

-- TODO 9: is_book_available
CREATE OR REPLACE FUNCTION is_book_available(p_book_id INTEGER)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
    v_stock INTEGER;
BEGIN
    SELECT stock INTO v_stock FROM books WHERE book_id = p_book_id;
    RETURN v_stock > 0;
END;
$$;

-- Test
SELECT is_book_available(1);  -- true  (stock = 10)
SELECT is_book_available(7);  -- false (stock = 0)

-- ---------------------------------------------------------------
-- Part 3 — Stored Procedure
-- ---------------------------------------------------------------

-- TODO 10: checkout_book procedure
CREATE OR REPLACE PROCEDURE checkout_book(
    p_member_id INTEGER,
    p_book_id   INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_stock INTEGER;
BEGIN
    -- Step 1: Check stock
    SELECT stock INTO v_stock FROM books WHERE book_id = p_book_id;

    IF v_stock = 0 THEN
        RAISE EXCEPTION 'Book (id=%) is out of stock', p_book_id;
    END IF;

    -- Step 2: Record loan
    INSERT INTO loans (member_id, book_id, loan_date)
    VALUES (p_member_id, p_book_id, CURRENT_DATE);

    -- Step 3: Decrement stock
    UPDATE books
    SET    stock = stock - 1
    WHERE  book_id = p_book_id;
END;
$$;

-- Test successful checkout
CALL checkout_book(1, 2);
SELECT title, stock FROM books WHERE book_id = 2;  -- stock should decrease by 1

-- Test out-of-stock error (book 7 has stock = 0)
-- CALL checkout_book(1, 7);
-- → ERROR: Book (id=7) is out of stock
