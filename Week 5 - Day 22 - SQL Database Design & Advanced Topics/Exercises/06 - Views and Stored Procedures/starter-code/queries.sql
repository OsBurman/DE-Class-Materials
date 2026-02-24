-- =============================================================
-- Exercise 06: Views and Stored Procedures
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Views
-- ---------------------------------------------------------------

-- TODO 1: Create view v_available_books
-- Columns: book_id, title, author_name (first || ' ' || last), stock, price
-- Filter:  stock > 0


-- TODO 2: Create view v_active_loans
-- Columns: loan_id, member_name (first || ' ' || last), title, loan_date
-- Filter:  return_date IS NULL


-- TODO 3: Create view v_member_loan_count
-- Columns: member_id, member_name (first || ' ' || last), loan_count


-- TODO 4: Query v_available_books for books priced under $13.00,
--         ordered by price ascending.


-- TODO 5: Use CREATE OR REPLACE VIEW to add published_year to v_available_books.


-- TODO 6: Drop the view v_member_loan_count.


-- ---------------------------------------------------------------
-- Part 2 — Stored Functions
-- ---------------------------------------------------------------

-- TODO 7: Create function get_member_loans(p_member_id INTEGER)
-- Returns TABLE(loan_id INTEGER, book_title VARCHAR, loan_date DATE, return_date DATE)
-- Call: SELECT * FROM get_member_loans(1);


-- TODO 8: Create function count_loans_by_member(p_member_id INTEGER)
-- Returns INTEGER — total loans for that member.
-- Call: SELECT count_loans_by_member(2);


-- TODO 9: Create function is_book_available(p_book_id INTEGER)
-- Returns BOOLEAN — TRUE if stock > 0, else FALSE.
-- Test: SELECT is_book_available(1); → true
--       SELECT is_book_available(7); → false (stock = 0)


-- ---------------------------------------------------------------
-- Part 3 — Stored Procedure (DML)
-- ---------------------------------------------------------------

-- TODO 10: Create procedure checkout_book(p_member_id INTEGER, p_book_id INTEGER)
-- Steps:
--   1. Check stock; if 0 RAISE EXCEPTION 'Book is out of stock'
--   2. INSERT into loans (member_id, book_id, loan_date) VALUES (...)
--   3. UPDATE books SET stock = stock - 1 WHERE book_id = p_book_id
-- Call with CALL checkout_book(1, 2);
-- Then test the out-of-stock case: CALL checkout_book(1, 7);

