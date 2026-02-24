-- =============================================================================
-- Day 22 — Part 2, File 2: Indexes, Views, Stored Procedures, Functions,
--           Triggers, and Sequences
-- Database: PostgreSQL
-- Prerequisite: Run Part-1/01-ddl-create-alter-drop.sql + Part-2/01-dml...sql first
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: INDEXES — WHAT THEY ARE AND WHY THEY MATTER
-- ─────────────────────────────────────────────────────────────────────────────

-- An INDEX is a separate data structure that allows the database to find rows
-- matching a condition WITHOUT scanning every row in the table.
--
-- Analogy: a book's index vs. reading the entire book to find a word.
--
-- Without an index: SEQUENTIAL SCAN (seq scan)
--   → read every row, check condition, return matches
--   → O(n): doubles the time if you double the rows
--
-- With an index: INDEX SCAN (or index-only scan)
--   → use B-tree structure to jump directly to matching rows
--   → O(log n): much faster for selective queries
--
-- PostgreSQL automatically creates an index for:
--   - PRIMARY KEY constraints
--   - UNIQUE constraints
--
-- You manually create indexes for:
--   - Columns used frequently in WHERE, JOIN ON, and ORDER BY clauses
--   - Foreign key columns (PostgreSQL does NOT auto-index FKs — MySQL does)

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: CREATING INDEXES
-- ─────────────────────────────────────────────────────────────────────────────

-- 2a. Basic B-Tree index — most common type, handles =, <, >, BETWEEN, ORDER BY
CREATE INDEX idx_products_category_id
  ON products(category_id);
-- This column is a FK — queries like JOIN ON p.category_id = c.category_id
-- and WHERE p.category_id = 1 benefit from this index

CREATE INDEX idx_products_price
  ON products(price);
-- Useful for: WHERE price BETWEEN 50 AND 100, ORDER BY price

CREATE INDEX idx_orders_customer_id
  ON orders(customer_id);
-- FK column — speeds up JOIN orders ON customer_id and WHERE customer_id = ?

CREATE INDEX idx_orders_status_date
  ON orders(status, order_date DESC);
-- COMPOSITE index: covers WHERE status = 'pending' AND order_date > ...
-- Also covers ORDER BY order_date DESC for pending orders
-- Column order matters: put the most selective filter column first

-- 2b. UNIQUE index (creates a constraint + index in one step)
CREATE UNIQUE INDEX idx_customers_email_unique
  ON customers(email);
-- Same effect as the UNIQUE constraint in CREATE TABLE, but can be added later

-- 2c. PARTIAL index — only indexes rows matching a condition
--     Smaller and faster than a full-table index
CREATE INDEX idx_products_active_only
  ON products(category_id, price)
  WHERE is_active = TRUE;
-- This index only exists for active products.
-- If most of your queries filter WHERE is_active = TRUE,
-- this is much smaller and faster than indexing all products.

-- 2d. Expression index — index on a computed value
CREATE INDEX idx_customers_email_lower
  ON customers(LOWER(email));
-- Speeds up case-insensitive email lookups:
-- WHERE LOWER(email) = LOWER('Alice@example.com')
-- Without this, every email must be lowercased at query time for comparison.

-- 2e. View existing indexes on a table
SELECT
  indexname,
  indexdef
FROM pg_indexes
WHERE tablename = 'products'
ORDER BY indexname;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: INDEX TYPES AND WHEN TO USE THEM
-- ─────────────────────────────────────────────────────────────────────────────

-- INDEX TYPE COMPARISON:
--
-- B-TREE (default):
--   - Best for: =, <, >, <=, >=, BETWEEN, LIKE 'prefix%', ORDER BY
--   - Not good for: LIKE '%suffix', full-text search
--   - Use for: most columns
--
-- HASH:
--   - Best for: exact equality only (=)
--   - Not good for: range queries, ORDER BY
--   - Use for: high-cardinality equality-only columns (rare — B-tree usually better)
--
-- GiST (Generalized Search Tree):
--   - Best for: geometric data, full-text search (tsvector), range types
--   - Use for: PostGIS spatial queries, text search
--
-- GIN (Generalized Inverted Index):
--   - Best for: arrays, JSONB, full-text search (multiple keys per row)
--   - Use for: JSONB columns, PostgreSQL full-text search
--
-- BRIN (Block Range Index):
--   - Best for: very large tables where values correlate with physical order
--   - Tiny size: useful for time-series tables ordered by timestamp
--   - Use for: log tables, sensor data, append-only time-series

-- GIN index on JSONB column (example — requires a jsonb column):
-- ALTER TABLE products ADD COLUMN metadata JSONB;
-- CREATE INDEX idx_products_metadata_gin ON products USING GIN(metadata);
-- Query: WHERE metadata @> '{"warranty_years": 2}'

-- BRIN index example (for a large time-series table):
-- CREATE INDEX idx_events_created_brin ON events USING BRIN(created_at);

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: INDEX TRADE-OFFS
-- ─────────────────────────────────────────────────────────────────────────────

-- BENEFITS of indexes:
--   ✅ Dramatically faster SELECT for matching columns
--   ✅ Required for efficient JOINs on large tables
--   ✅ Can eliminate sort operations if index matches ORDER BY
--
-- COSTS of indexes:
--   ❌ Storage space (indexes can be as large as the table itself)
--   ❌ Slower INSERT, UPDATE, DELETE (every write must update all indexes)
--   ❌ Maintenance overhead during VACUUM and ANALYZE
--
-- WHEN NOT TO INDEX:
--   - Very small tables (< ~1,000 rows) — seq scan is fine, index overhead hurts
--   - Low-cardinality columns (gender, boolean) — index on is_active is rarely useful
--     because it selects 50% of rows — the database might ignore it
--   - Columns rarely used in WHERE, JOIN, or ORDER BY
--   - Write-heavy tables where INSERT speed matters most

-- DROP an index (won't affect data, just removes the optimization)
-- DROP INDEX IF EXISTS idx_products_price;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: VIEWS
-- ─────────────────────────────────────────────────────────────────────────────

-- A VIEW is a saved SELECT query with a name.
-- It behaves like a table — you can SELECT from it — but stores no data itself.
-- The query runs every time you SELECT from the view.
--
-- Use cases:
--   - Simplify complex JOINs into a reusable "virtual table"
--   - Hide sensitive columns from specific users
--   - Provide a stable API layer over a changing schema
--   - Implement row-level access control

-- 5a. Simple view — product catalog with category name
CREATE OR REPLACE VIEW v_product_catalog AS
SELECT
  p.product_id,
  p.sku,
  p.name                              AS product_name,
  p.price,
  p.stock_qty,
  c.name                              AS category_name,
  p.is_active
FROM products p
JOIN categories c ON p.category_id = c.category_id
WHERE p.is_active = TRUE;

-- Use the view exactly like a table:
SELECT * FROM v_product_catalog ORDER BY category_name, product_name;

-- Query the view with additional filters:
SELECT * FROM v_product_catalog
WHERE price < 100
ORDER BY price;

-- 5b. View for order summary (complex JOIN simplified)
CREATE OR REPLACE VIEW v_order_summary AS
SELECT
  o.order_id,
  c.first_name || ' ' || c.last_name  AS customer_name,
  c.email,
  o.status,
  o.total_amount,
  o.order_date,
  COUNT(oi.product_id)                AS item_count,
  SUM(oi.quantity)                    AS total_units
FROM orders o
JOIN customers   c  ON o.customer_id = c.customer_id
JOIN order_items oi ON o.order_id    = oi.order_id
GROUP BY o.order_id, c.first_name, c.last_name, c.email, o.status, o.total_amount, o.order_date;

-- Query the view:
SELECT * FROM v_order_summary ORDER BY order_date DESC;

SELECT * FROM v_order_summary
WHERE status = 'pending'
ORDER BY total_amount DESC;

-- 5c. Updatable view — views on a single table with no aggregates can be updated
--     (Simple views only — complex JOINs/aggregates are not updatable)
CREATE OR REPLACE VIEW v_active_customers AS
SELECT customer_id, first_name, last_name, email, status
FROM customers
WHERE is_active = TRUE;   -- Oops: customers doesn't have is_active — using status

-- Actually let's use our real columns:
DROP VIEW IF EXISTS v_active_customers;
CREATE OR REPLACE VIEW v_active_customers AS
SELECT customer_id, first_name, last_name, email, credit_limit, status
FROM customers
WHERE status = 'active';

-- This view is updatable — INSERT/UPDATE/DELETE flow through to customers:
-- UPDATE v_active_customers SET credit_limit = 3000.00 WHERE customer_id = 1;

-- 5d. Drop a view (does not affect underlying data)
-- DROP VIEW IF EXISTS v_product_catalog;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: STORED PROCEDURES AND USER-DEFINED FUNCTIONS
-- ─────────────────────────────────────────────────────────────────────────────

-- FUNCTION: returns a value, can be used in SELECT/WHERE, cannot commit transactions
-- PROCEDURE: does not return a value (returns nothing or via OUT params),
--            can manage transactions (COMMIT/ROLLBACK inside procedure)
-- PostgreSQL added PROCEDURE in version 11.
-- In older PostgreSQL and MySQL, functions were used for everything.

-- 6a. Simple scalar function — returns a single calculated value
CREATE OR REPLACE FUNCTION calculate_order_total(p_order_id INTEGER)
RETURNS NUMERIC(12, 2)
LANGUAGE plpgsql
AS $$
DECLARE
  v_total NUMERIC(12, 2);
BEGIN
  SELECT ROUND(SUM(quantity * unit_price * (1 - discount_pct / 100)), 2)
  INTO v_total
  FROM order_items
  WHERE order_id = p_order_id;

  RETURN COALESCE(v_total, 0.00);
END;
$$;

-- Use the function in a query (just like a built-in function)
SELECT
  order_id,
  calculate_order_total(order_id) AS calculated_total
FROM orders;

-- 6b. Function that returns a table — a "table-valued function"
CREATE OR REPLACE FUNCTION get_customer_orders(p_customer_id INTEGER)
RETURNS TABLE (
  order_id      INTEGER,
  order_date    TIMESTAMPTZ,
  status        VARCHAR,
  total_amount  NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN QUERY
  SELECT o.order_id, o.order_date, o.status, o.total_amount
  FROM orders o
  WHERE o.customer_id = p_customer_id
  ORDER BY o.order_date DESC;
END;
$$;

-- Call the table function (use it like a table in FROM)
SELECT * FROM get_customer_orders(1);

-- 6c. Stored procedure — process a new order (demonstrates transactions inside)
CREATE OR REPLACE PROCEDURE place_order(
  p_customer_id       INTEGER,
  p_shipping_address  TEXT,
  p_product_id        INTEGER,
  p_quantity          INTEGER,
  OUT p_new_order_id  INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
  v_price     NUMERIC(10, 2);
  v_stock     INTEGER;
BEGIN
  -- Look up the current price and stock
  SELECT price, stock_qty INTO v_price, v_stock
  FROM products
  WHERE product_id = p_product_id
    AND is_active = TRUE;

  -- Validate stock availability
  IF v_stock IS NULL THEN
    RAISE EXCEPTION 'Product % not found or inactive', p_product_id;
  END IF;

  IF v_stock < p_quantity THEN
    RAISE EXCEPTION 'Insufficient stock: requested %, available %', p_quantity, v_stock;
  END IF;

  -- Insert the order header
  INSERT INTO orders (customer_id, shipping_address)
  VALUES (p_customer_id, p_shipping_address)
  RETURNING order_id INTO p_new_order_id;

  -- Insert the order item
  INSERT INTO order_items (order_id, product_id, quantity, unit_price)
  VALUES (p_new_order_id, p_product_id, p_quantity, v_price);

  -- Update the order total
  UPDATE orders
  SET total_amount = v_price * p_quantity
  WHERE order_id = p_new_order_id;

  -- Decrement stock
  UPDATE products
  SET stock_qty = stock_qty - p_quantity
  WHERE product_id = p_product_id;

  -- Procedure commits automatically at the end (no explicit COMMIT needed here)
END;
$$;

-- Call the stored procedure
DO $$
DECLARE
  new_id INTEGER;
BEGIN
  CALL place_order(2, '456 Oak Ave, Chicago', 1, 2, new_id);
  RAISE NOTICE 'New order ID: %', new_id;
END;
$$;

-- Verify the order was created and stock was decremented
SELECT * FROM orders ORDER BY order_id DESC LIMIT 1;
SELECT product_id, stock_qty FROM products WHERE product_id = 1;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: SEQUENCES
-- ─────────────────────────────────────────────────────────────────────────────

-- A SEQUENCE is a database object that generates a series of unique integers.
-- SERIAL columns use sequences internally.
-- You can also create and use sequences directly.

-- 7a. Create a sequence explicitly
CREATE SEQUENCE IF NOT EXISTS invoice_number_seq
  START WITH 10000
  INCREMENT BY 1
  NO MAXVALUE
  NO CYCLE;           -- stops at max, does not wrap around

-- Use the sequence in an INSERT
SELECT NEXTVAL('invoice_number_seq');   -- first call returns 10000
SELECT NEXTVAL('invoice_number_seq');   -- returns 10001
SELECT CURRVAL('invoice_number_seq');   -- returns current value without advancing
SELECT LASTVAL();                       -- returns the last value returned by NEXTVAL in this session

-- 7b. Sequence in a table
CREATE TABLE invoices (
  invoice_id      BIGINT          PRIMARY KEY DEFAULT NEXTVAL('invoice_number_seq'),
  order_id        INTEGER         NOT NULL REFERENCES orders(order_id),
  invoice_number  TEXT            GENERATED ALWAYS AS ('INV-' || invoice_id) STORED,
  -- GENERATED ALWAYS AS STORED: a computed column stored physically (PostgreSQL 12+)
  issued_date     DATE            NOT NULL DEFAULT CURRENT_DATE,
  amount          NUMERIC(12, 2)  NOT NULL
);

INSERT INTO invoices (order_id, amount)
VALUES (1, 1058.98)
RETURNING invoice_id, invoice_number;

-- 7c. Reset a sequence (useful after data migration)
-- SELECT SETVAL('invoice_number_seq', 20000);  -- next call returns 20001

-- 7d. View all sequences
SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 8: TRIGGERS
-- ─────────────────────────────────────────────────────────────────────────────

-- A TRIGGER automatically executes a function BEFORE or AFTER a DML event.
-- Events: INSERT, UPDATE, DELETE, TRUNCATE
-- Timing: BEFORE (can modify the row), AFTER (row already written), INSTEAD OF (for views)
--
-- Common use cases:
--   - Automatically update `updated_at` timestamps
--   - Maintain audit logs
--   - Enforce complex business rules
--   - Cascade custom logic beyond what FK ON DELETE supports

-- 8a. Trigger: auto-update the updated_at timestamp
CREATE OR REPLACE FUNCTION fn_update_timestamp()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  -- NEW is a special record in triggers: the row being inserted/updated
  NEW.updated_at = NOW();
  RETURN NEW;   -- RETURN NEW to apply the (possibly modified) row
END;
$$;

-- Attach the trigger to the customers table
CREATE OR REPLACE TRIGGER trg_customers_updated_at
  BEFORE UPDATE ON customers
  FOR EACH ROW
  EXECUTE FUNCTION fn_update_timestamp();

-- Test the trigger: update a customer and check updated_at
UPDATE customers
SET first_name = 'Alicia'
WHERE customer_id = 1;

SELECT customer_id, first_name, updated_at FROM customers WHERE customer_id = 1;

-- 8b. Trigger: audit log — record every change to orders
CREATE TABLE IF NOT EXISTS orders_audit (
  audit_id      SERIAL          PRIMARY KEY,
  order_id      INTEGER         NOT NULL,
  changed_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  changed_by    TEXT            NOT NULL DEFAULT current_user,
  operation     TEXT            NOT NULL,   -- INSERT, UPDATE, DELETE
  old_status    VARCHAR(30),
  new_status    VARCHAR(30),
  old_total     NUMERIC(12, 2),
  new_total     NUMERIC(12, 2)
);

CREATE OR REPLACE FUNCTION fn_audit_orders()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    INSERT INTO orders_audit (order_id, operation, new_status, new_total)
    VALUES (NEW.order_id, 'INSERT', NEW.status, NEW.total_amount);

  ELSIF TG_OP = 'UPDATE' THEN
    INSERT INTO orders_audit (order_id, operation, old_status, new_status, old_total, new_total)
    VALUES (NEW.order_id, 'UPDATE', OLD.status, NEW.status, OLD.total_amount, NEW.total_amount);

  ELSIF TG_OP = 'DELETE' THEN
    INSERT INTO orders_audit (order_id, operation, old_status, old_total)
    VALUES (OLD.order_id, 'DELETE', OLD.status, OLD.total_amount);
  END IF;

  RETURN NULL;  -- RETURN NULL for AFTER triggers (row already written)
END;
$$;

CREATE OR REPLACE TRIGGER trg_orders_audit
  AFTER INSERT OR UPDATE OR DELETE ON orders
  FOR EACH ROW
  EXECUTE FUNCTION fn_audit_orders();

-- Test the audit trigger
UPDATE orders SET status = 'confirmed' WHERE order_id = 2;
UPDATE orders SET status = 'shipped'   WHERE order_id = 2;

-- View the audit trail
SELECT * FROM orders_audit ORDER BY audit_id;

-- Drop objects when done
DROP TABLE IF EXISTS invoices;
DROP SEQUENCE IF EXISTS invoice_number_seq;
