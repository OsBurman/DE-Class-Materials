-- =============================================================
-- Exercise 07: Triggers and Sequences
-- File: starter-code/queries.sql
-- Instructions: Run setup.sql first, then complete each TODO below.
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Sequences
-- ---------------------------------------------------------------

-- TODO 1: Create sequence seq_loan_ref starting at 10000, increment 1.


-- TODO 2: Insert a loan using nextval('seq_loan_ref') as loan_id.
--         member_id=1, book_id=2, loan_date=CURRENT_DATE.


-- TODO 3: Insert a second loan using nextval('seq_loan_ref').
--         member_id=1, book_id=3, loan_date=CURRENT_DATE.
--         Then SELECT loan_id FROM loans ORDER BY loan_id to verify.


-- TODO 4: SELECT currval('seq_loan_ref') to confirm the current value.


-- TODO 5: Create sequence seq_member_code starting at 5000, increment 10.
--         Retrieve the next three values with three SELECT nextval(...) calls.


-- ---------------------------------------------------------------
-- Part 2 — Audit trigger (AFTER INSERT on loans)
-- ---------------------------------------------------------------

-- TODO 6: Create the audit_log table.
--   Columns: log_id SERIAL PK, table_name VARCHAR(50),
--            action VARCHAR(10), record_id INTEGER,
--            changed_at TIMESTAMP DEFAULT NOW()


-- TODO 7: Create trigger function trg_fn_log_loan_insert()
--         Inserts into audit_log after each loan insert.
--         Use: table_name='loans', action='INSERT', record_id=NEW.loan_id


-- TODO 8: Create AFTER INSERT trigger trg_log_loan_insert on loans.


-- TODO 9: Insert a loan (member_id=2, book_id=3) and verify audit_log.


-- ---------------------------------------------------------------
-- Part 3 — Business-rule trigger (BEFORE INSERT on loans)
-- ---------------------------------------------------------------

-- TODO 10: Create trigger function trg_fn_check_stock()
--          Raises EXCEPTION if books.stock = 0 for NEW.book_id.


-- TODO 11: Create BEFORE INSERT trigger trg_check_stock on loans.


-- TODO 12: Test the trigger.
--   a) Try to insert a loan for book_id=7 (stock=0) → expect ERROR
--   b) Insert a loan for book_id=1 (stock=10) → expect SUCCESS


-- ---------------------------------------------------------------
-- Part 4 — Update trigger (AFTER UPDATE on loans)
-- ---------------------------------------------------------------

-- TODO 13: Create trigger function trg_fn_log_loan_return()
--          Inserts into audit_log ONLY when OLD.return_date IS NULL
--          AND NEW.return_date IS NOT NULL.
--          Use action='RETURN', record_id=NEW.loan_id.


-- TODO 14: Create AFTER UPDATE trigger trg_log_loan_return on loans.
--          Then UPDATE a loan to set return_date=CURRENT_DATE and
--          verify the audit_log entry appears.

