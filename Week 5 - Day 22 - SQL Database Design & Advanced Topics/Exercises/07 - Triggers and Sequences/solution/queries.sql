-- =============================================================
-- Exercise 07: Triggers and Sequences
-- File: solution/queries.sql
-- =============================================================

-- ---------------------------------------------------------------
-- Part 1 — Sequences
-- ---------------------------------------------------------------

-- TODO 1: Create seq_loan_ref
CREATE SEQUENCE seq_loan_ref
    START WITH  10000
    INCREMENT BY 1
    MINVALUE    10000
    NO MAXVALUE;

-- TODO 2: Insert first loan using nextval
INSERT INTO loans (loan_id, member_id, book_id, loan_date)
VALUES (nextval('seq_loan_ref'), 1, 2, CURRENT_DATE);

-- TODO 3: Insert second loan and verify IDs
INSERT INTO loans (loan_id, member_id, book_id, loan_date)
VALUES (nextval('seq_loan_ref'), 1, 3, CURRENT_DATE);

SELECT loan_id FROM loans ORDER BY loan_id;
-- Expected: 10000, 10001

-- TODO 4: Check current sequence value
SELECT currval('seq_loan_ref');  -- → 10001

-- TODO 5: seq_member_code starting at 5000, increment 10
CREATE SEQUENCE seq_member_code
    START WITH  5000
    INCREMENT BY 10
    MINVALUE    5000;

SELECT nextval('seq_member_code');  -- 5000
SELECT nextval('seq_member_code');  -- 5010
SELECT nextval('seq_member_code');  -- 5020

-- ---------------------------------------------------------------
-- Part 2 — Audit trigger (AFTER INSERT on loans)
-- ---------------------------------------------------------------

-- TODO 6: Create audit_log table
CREATE TABLE audit_log (
    log_id     SERIAL       PRIMARY KEY,
    table_name VARCHAR(50)  NOT NULL,
    action     VARCHAR(10)  NOT NULL,
    record_id  INTEGER,
    changed_at TIMESTAMP    DEFAULT NOW()
);

-- TODO 7: Trigger function for loan inserts
CREATE OR REPLACE FUNCTION trg_fn_log_loan_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO audit_log (table_name, action, record_id)
    VALUES ('loans', 'INSERT', NEW.loan_id);
    RETURN NULL;  -- AFTER trigger; return value ignored
END;
$$;

-- TODO 8: Attach AFTER INSERT trigger
CREATE TRIGGER trg_log_loan_insert
AFTER INSERT ON loans
FOR EACH ROW EXECUTE FUNCTION trg_fn_log_loan_insert();

-- TODO 9: Insert a loan and verify audit_log
INSERT INTO loans (loan_id, member_id, book_id, loan_date)
VALUES (nextval('seq_loan_ref'), 2, 3, CURRENT_DATE);

SELECT * FROM audit_log ORDER BY log_id;
-- Expected: 1 row with table_name='loans', action='INSERT', record_id=10002

-- ---------------------------------------------------------------
-- Part 3 — Business-rule trigger (BEFORE INSERT on loans)
-- ---------------------------------------------------------------

-- TODO 10: Trigger function to check stock
CREATE OR REPLACE FUNCTION trg_fn_check_stock()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_stock INTEGER;
BEGIN
    SELECT stock INTO v_stock FROM books WHERE book_id = NEW.book_id;

    IF v_stock = 0 THEN
        RAISE EXCEPTION 'Cannot create loan: book (id=%) is out of stock', NEW.book_id;
    END IF;

    RETURN NEW;  -- allow the INSERT to proceed
END;
$$;

-- TODO 11: Attach BEFORE INSERT trigger
CREATE TRIGGER trg_check_stock
BEFORE INSERT ON loans
FOR EACH ROW EXECUTE FUNCTION trg_fn_check_stock();

-- TODO 12: Test the trigger
-- a) Out-of-stock — expect ERROR
-- INSERT INTO loans (loan_id, member_id, book_id, loan_date)
-- VALUES (nextval('seq_loan_ref'), 1, 7, CURRENT_DATE);
-- → ERROR: Cannot create loan: book (id=7) is out of stock

-- b) In-stock — expect SUCCESS
INSERT INTO loans (loan_id, member_id, book_id, loan_date)
VALUES (nextval('seq_loan_ref'), 1, 1, CURRENT_DATE);

SELECT loan_id, member_id, book_id FROM loans ORDER BY loan_id DESC LIMIT 1;

-- ---------------------------------------------------------------
-- Part 4 — Update trigger (AFTER UPDATE on loans)
-- ---------------------------------------------------------------

-- TODO 13: Trigger function to log a book return
CREATE OR REPLACE FUNCTION trg_fn_log_loan_return()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Only fire when return_date transitions from NULL → non-NULL
    IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN
        INSERT INTO audit_log (table_name, action, record_id)
        VALUES ('loans', 'RETURN', NEW.loan_id);
    END IF;
    RETURN NULL;
END;
$$;

-- TODO 14: Attach AFTER UPDATE trigger, then test
CREATE TRIGGER trg_log_loan_return
AFTER UPDATE ON loans
FOR EACH ROW EXECUTE FUNCTION trg_fn_log_loan_return();

-- Test: mark the first loan as returned
UPDATE loans
SET    return_date = CURRENT_DATE
WHERE  loan_id = 10000;

-- Verify audit_log now contains a RETURN entry
SELECT * FROM audit_log ORDER BY log_id;
-- Expected: original INSERT row(s) AND a new RETURN row for loan_id=10000
