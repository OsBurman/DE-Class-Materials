-- =============================================================
-- Exercise 04: INSERT, UPDATE, and DELETE
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part A — INSERT
-- ---------------------------------------------------------------

-- TODO 1: Insert a new author: F. Scott Fitzgerald, fscott@lib.com


-- TODO 2: Insert a new publisher: Scribner, New York


-- TODO 3: Insert a new book: 'The Great Gatsby', published 1925, price 11.99,
--         stock 8. Use subqueries to look up author_id and publisher_id
--         by email/name rather than hard-coding IDs.


-- TODO 4: Insert two new members:
--   Maria Garcia | mgarcia@email.com | Madrid | Spain  | premium
--   Liam Chen    | lchen@email.com   | Toronto| Canada | student


-- TODO 5: Insert a loan for Maria Garcia borrowing 'The Great Gatsby'
--         with loan_date = CURRENT_DATE.


-- ---------------------------------------------------------------
-- Part B — UPDATE
-- ---------------------------------------------------------------

-- TODO 6: Update Maria Garcia's membership_type to 'standard'.


-- TODO 7: Update the price of '1984' to 15.99.


-- TODO 8: Increase stock by 5 for every book published before 2000.


-- TODO 9: Set the return_date of Maria Garcia's loan (from TODO 5)
--         to CURRENT_DATE + INTERVAL '14 days'.


-- ---------------------------------------------------------------
-- Part C — DELETE
-- ---------------------------------------------------------------

-- TODO 10: Insert a loan for Liam Chen for 'Animal Farm', then
--          delete it. Confirm with SELECT before and after.


-- TODO 11: First run a SELECT to check for books with stock = 0.
--          If none exist, update one book to stock = 0.
--          Then DELETE all books with stock = 0.


-- TODO 12: Try to delete an author who has books. Observe the FK error.
--          In a comment, explain what ON DELETE CASCADE would do vs
--          ON DELETE RESTRICT.
/*
  YOUR ANSWER HERE
*/
