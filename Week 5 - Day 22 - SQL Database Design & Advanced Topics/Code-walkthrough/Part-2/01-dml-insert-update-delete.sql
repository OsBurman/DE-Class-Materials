-- =============================================================================
-- Day 22 — Part 2, File 1: DML — INSERT, UPDATE, DELETE
-- Data Manipulation Language: writing, modifying, and removing data
-- Database: PostgreSQL
-- Prerequisite: Run Part-1/01-ddl-create-alter-drop.sql first (creates the schema)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: DML OVERVIEW
-- ─────────────────────────────────────────────────────────────────────────────

-- Data Manipulation Language (DML) is the sub-language of SQL used to
-- read and modify DATA within tables. (SELECT is technically DQL but
-- is often grouped with DML.)
--
--   INSERT → add new rows
--   UPDATE → modify existing rows
--   DELETE → remove existing rows
--
-- Unlike DDL, DML changes are NOT auto-committed by default in most
-- database tools. They participate in transactions (BEGIN / COMMIT / ROLLBACK).
-- This means you can undo an accidental UPDATE before committing.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: INSERT
-- ─────────────────────────────────────────────────────────────────────────────

-- 2a. Basic single-row INSERT — explicitly name every column
INSERT INTO categories (name, description, display_order)
VALUES ('Electronics', 'Computers, phones, and gadgets', 1);

INSERT INTO categories (name, description, display_order)
VALUES ('Peripherals', 'Keyboards, mice, monitors, and accessories', 2);

INSERT INTO categories (name, description, display_order)
VALUES ('Displays', 'Monitors and screens', 3);

-- 2b. INSERT with a child category — self-referencing FK
--     parent_id = 1 means "Laptops" is a subcategory of "Electronics"
INSERT INTO categories (name, description, parent_id, display_order)
VALUES ('Laptops', 'Portable computers', 1, 10);

INSERT INTO categories (name, description, parent_id, display_order)
VALUES ('Smartphones', 'Mobile phones and tablets', 1, 11);

-- 2c. INSERT with column list — only specify what you need
--     Columns with DEFAULT or nullable columns can be omitted
INSERT INTO customers (first_name, last_name, email, credit_limit)
VALUES ('Alice', 'Smith', 'alice@example.com', 2500.00);

INSERT INTO customers (first_name, last_name, email)
VALUES
  ('Bob',     'Jones',    'bob@example.com'),
  ('Charlie', 'Brown',    'charlie@example.com'),
  ('Diana',   'Prince',   'diana@example.com'),
  ('Edward',  'Norton',   'edward@example.com');
-- Multi-row INSERT: list multiple value tuples separated by commas
-- Much more efficient than 5 separate INSERT statements

-- 2d. INSERT products
INSERT INTO products (sku, name, description, category_id, price, cost, stock_qty)
VALUES
  ('LPTP-001', 'ProBook 15 Laptop',     '15-inch development laptop',    1, 999.00, 650.00, 50),
  ('LPTP-002', 'UltraBook 13 Laptop',   '13-inch ultra-thin laptop',     1, 1299.00, 850.00, 30),
  ('MOUS-001', 'Wireless Mouse',        'Ergonomic Bluetooth mouse',     2,  29.99,  12.00, 200),
  ('KBRD-001', 'Mechanical Keyboard',   'Full-size mechanical keyboard', 2,  89.99,  45.00, 100),
  ('MON-001',  '27-inch 4K Monitor',    '4K IPS display panel',          3, 449.99, 280.00,  25),
  ('MON-002',  '24-inch FHD Monitor',   'Full HD IPS display',           3, 249.99, 150.00,  40);

-- 2e. INSERT with RETURNING — get the generated primary key back immediately
--     Extremely useful in application code — you need the new order's ID
INSERT INTO orders (customer_id, shipping_address)
VALUES (1, '123 Main St, Springfield, IL 62701')
RETURNING order_id, customer_id, status, order_date;
-- RETURNING is PostgreSQL/PostgreSQL-compatible syntax
-- MySQL equivalent: use LAST_INSERT_ID() immediately after INSERT

-- 2f. INSERT INTO orders for other customers (without RETURNING for brevity)
INSERT INTO orders (customer_id, shipping_address)
VALUES
  (2, '456 Oak Ave, Chicago, IL 60601'),
  (3, '789 Pine Rd, Austin, TX 78701'),
  (1, '123 Main St, Springfield, IL 62701');  -- Alice places a second order

-- 2g. INSERT order_items — composite FK into the orders + products tables
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES
  (1, 1, 1, 999.00),    -- order 1: 1 ProBook Laptop at $999
  (1, 3, 2,  29.99),    -- order 1: 2 Wireless Mice at $29.99
  (2, 4, 1,  89.99),    -- order 2: 1 Mechanical Keyboard
  (3, 5, 1, 449.99),    -- order 3: 1 27-inch Monitor
  (3, 3, 1,  29.99),    -- order 3: 1 Mouse
  (4, 2, 1, 1299.00),   -- order 4: 1 UltraBook
  (4, 4, 1,  89.99);    -- order 4: 1 Keyboard

-- 2h. INSERT … SELECT — copy data from one table into another
--     Useful for archiving, populating summary tables, or data migrations
DROP TABLE IF EXISTS premium_customers;
CREATE TABLE premium_customers AS
  SELECT customer_id, first_name, last_name, email, created_at
  FROM customers
  WHERE credit_limit >= 2000.00;
-- This CREATE TABLE AS SELECT creates the table AND inserts data in one step.

-- Alternatively, INSERT INTO existing table from a SELECT:
-- INSERT INTO archived_orders (order_id, customer_id, total_amount)
-- SELECT order_id, customer_id, total_amount
-- FROM orders
-- WHERE order_date < NOW() - INTERVAL '2 years';

-- Verify inserts
SELECT * FROM customers ORDER BY customer_id;
SELECT * FROM products  ORDER BY product_id;
SELECT * FROM orders    ORDER BY order_id;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: UPDATE
-- ─────────────────────────────────────────────────────────────────────────────

-- UPDATE modifies existing rows. ALWAYS use a WHERE clause unless you intend
-- to update every single row in the table.
-- WATCH OUT: UPDATE without WHERE updates ALL rows. There is no undo.

-- 3a. Basic UPDATE — update one row by primary key
UPDATE products
SET price = 1099.00
WHERE product_id = 1;

-- 3b. Update multiple columns at once
UPDATE customers
SET
  status       = 'inactive',
  updated_at   = NOW()
WHERE customer_id = 5;

-- 3c. Update using a calculation (not just a literal value)
UPDATE products
SET
  price = ROUND(price * 1.05, 2),  -- 5% price increase
  cost  = ROUND(cost  * 1.03, 2)   -- 3% cost increase
WHERE category_id = 1              -- only Electronics category
  AND is_active = TRUE;

-- 3d. Conditional UPDATE with CASE
UPDATE products
SET stock_qty = CASE
  WHEN product_id = 3 THEN stock_qty + 150   -- Mouse restock
  WHEN product_id = 4 THEN stock_qty + 75    -- Keyboard restock
  ELSE stock_qty                             -- No change for other products
END
WHERE product_id IN (3, 4);

-- 3e. UPDATE … RETURNING — see what changed
UPDATE orders
SET
  status       = 'confirmed',
  total_amount = (
    SELECT ROUND(SUM(quantity * unit_price * (1 - discount_pct / 100)), 2)
    FROM order_items
    WHERE order_id = orders.order_id     -- correlated subquery
  )
WHERE order_id = 1
RETURNING order_id, status, total_amount;

-- 3f. UPDATE with JOIN (UPDATE … FROM in PostgreSQL)
--     Update orders.total_amount for ALL orders at once
UPDATE orders o
SET total_amount = subq.calculated_total
FROM (
  SELECT
    order_id,
    ROUND(SUM(quantity * unit_price), 2) AS calculated_total
  FROM order_items
  GROUP BY order_id
) AS subq
WHERE o.order_id = subq.order_id;

-- Verify the totals were updated
SELECT order_id, customer_id, status, total_amount FROM orders ORDER BY order_id;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: DELETE
-- ─────────────────────────────────────────────────────────────────────────────

-- DELETE removes rows from a table. Always use WHERE unless you mean to delete all.
-- Unlike TRUNCATE, DELETE fires triggers and generates WAL (write-ahead log) entries.

-- 4a. Delete one specific row
DELETE FROM customers
WHERE customer_id = 5;
-- If orders have ON DELETE CASCADE, Edward's orders are deleted too.
-- If orders have ON DELETE RESTRICT, this fails until orders are deleted first.

-- 4b. Delete rows matching a condition
DELETE FROM order_items
WHERE discount_pct > 0;   -- remove all discounted items (hypothetical cleanup)

-- 4c. DELETE with RETURNING — see what was deleted
DELETE FROM premium_customers
WHERE created_at < NOW() - INTERVAL '90 days'
RETURNING customer_id, email;

-- 4d. Delete using a subquery
DELETE FROM order_items
WHERE order_id IN (
  SELECT order_id
  FROM orders
  WHERE status = 'cancelled'
);

-- 4e. DELETE … USING (PostgreSQL) — delete with a join condition
-- DELETE FROM order_items oi
-- USING orders o
-- WHERE oi.order_id = o.order_id
--   AND o.status = 'cancelled';
-- This is equivalent to 4d but more readable for complex join conditions

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: UPSERT — INSERT OR UPDATE
-- ─────────────────────────────────────────────────────────────────────────────

-- UPSERT: if the row exists, update it; if not, insert it.
-- PostgreSQL uses INSERT … ON CONFLICT DO UPDATE.
-- MySQL uses INSERT … ON DUPLICATE KEY UPDATE.

-- 5a. Upsert a product — update price if SKU already exists
INSERT INTO products (sku, name, category_id, price, cost, stock_qty)
VALUES ('MOUS-001', 'Wireless Mouse Pro', 2, 34.99, 14.00, 200)
ON CONFLICT (sku) DO UPDATE
  SET
    name      = EXCLUDED.name,      -- EXCLUDED = the row that was rejected
    price     = EXCLUDED.price,
    cost      = EXCLUDED.cost;
-- The row is identified by SKU (which has a UNIQUE constraint).
-- If 'MOUS-001' already exists, update name and price instead of failing.

-- 5b. Upsert — do nothing if row already exists
INSERT INTO categories (name, display_order)
VALUES ('Electronics', 99)
ON CONFLICT (name) DO NOTHING;
-- Electronics already exists — this INSERT is silently skipped. No error.

-- Verify the mouse was updated
SELECT product_id, sku, name, price, stock_qty FROM products WHERE sku = 'MOUS-001';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: COMMON DML PITFALLS AND BEST PRACTICES
-- ─────────────────────────────────────────────────────────────────────────────

-- ❌ NEVER DO THIS IN PRODUCTION:
-- UPDATE products SET price = 0;          -- Updates ALL products!
-- DELETE FROM orders;                     -- Deletes ALL orders!
--
-- ✅ ALWAYS:
--   1. Start with a SELECT using the same WHERE clause to verify which rows match
--   2. Wrap in a transaction (BEGIN) so you can ROLLBACK if something looks wrong
--   3. Use RETURNING to confirm what was changed
--   4. Run UPDATE/DELETE on staging/dev before production
--
-- Safe pattern for a dangerous UPDATE:
--   BEGIN;
--   SELECT * FROM products WHERE category_id = 1;  -- see what you're about to change
--   UPDATE products SET price = ROUND(price * 0.9, 2) WHERE category_id = 1;
--   SELECT * FROM products WHERE category_id = 1;  -- verify the change looks right
--   COMMIT;  -- or ROLLBACK; if something is wrong

-- Drop the demo table used in section 2h
DROP TABLE IF EXISTS premium_customers;
