-- =============================================================================
-- Day 22 — Part 1, File 2: Database Normalization & ER Design
-- Topics: Normalization (1NF, 2NF, 3NF), ER Diagrams, Data Modeling Principles
-- Database: PostgreSQL
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: DATA MODELING PRINCIPLES
-- ─────────────────────────────────────────────────────────────────────────────

-- Data modeling is the process of designing how data is structured,
-- stored, and related BEFORE writing any code.
--
-- Three levels of data modeling:
--
--   1. CONCEPTUAL MODEL — "What are the main things we're tracking?"
--      Focus: entities and their relationships. No technical detail.
--      Output: ER diagram with rectangles and connecting lines.
--
--   2. LOGICAL MODEL — "What attributes does each entity have?"
--      Focus: attributes, relationships, primary/foreign keys. No DB-specific syntax.
--      Output: ER diagram with attribute lists and relationship cardinality.
--
--   3. PHYSICAL MODEL — "How will we actually build it in PostgreSQL?"
--      Focus: exact SQL tables, data types, constraints, indexes.
--      Output: the CREATE TABLE statements we write.
--
-- Good data modeling goals:
--   ✅ Eliminate redundant data (normalization)
--   ✅ Ensure data integrity (constraints)
--   ✅ Support required queries efficiently (indexes, relationships)
--   ✅ Allow the schema to evolve without breaking changes

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: ENTITY-RELATIONSHIP (ER) DIAGRAMS
-- ─────────────────────────────────────────────────────────────────────────────

-- An ER diagram visually describes the data model.
-- Key notation elements:
--
--   ENTITY      → a "thing" we track; becomes a table
--                 Drawn as a rectangle: [Customer]
--
--   ATTRIBUTE   → a property of an entity; becomes a column
--                 Drawn as an oval attached to an entity
--                 Underlined attribute = primary key
--
--   RELATIONSHIP → how entities connect; becomes a foreign key (or bridge table)
--                 Drawn as a diamond between two entities
--
-- CARDINALITY — "how many of each side participate in the relationship?"
--
--   ONE-TO-ONE (1:1):
--     One customer has one loyalty card.
--     One side has the FK.
--     customers(loyalty_card_id) → loyalty_cards(card_id)
--
--   ONE-TO-MANY (1:N):
--     One customer places many orders.
--     The "many" side holds the FK.
--     orders(customer_id) → customers(customer_id)
--
--   MANY-TO-MANY (M:N):
--     Many orders contain many products.
--     Requires a BRIDGE TABLE with two FKs.
--     order_items(order_id, product_id)
--
-- ASCII ER Diagram — E-Commerce Platform:
--
--   [Customer] ─────1────< [Order] >────N─────── [OrderItem] >────N─── [Product]
--                                                                           │
--                                                                           1
--                                                                           │
--                                                                      [Category]
--                                                                           │
--                                                                    (self-ref)
--                                                                    parent_id
--
--   [Order] references [Customer] (many orders per customer)
--   [OrderItem] bridges [Order] and [Product] (M:N)
--   [Product] references [Category] (many products per category)
--   [Category] references itself via parent_id (hierarchical categories)
--
-- Tools for drawing ER diagrams:
--   - draw.io (free, web-based)
--   - Lucidchart
--   - dbdiagram.io (SQL-like syntax → visual diagram)
--   - pgAdmin ER diagram view
--   - DBeaver (auto-generates from schema)

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: NORMALIZATION — THE PROBLEM: UNNORMALIZED DATA
-- ─────────────────────────────────────────────────────────────────────────────

-- NORMALIZATION is the process of organizing a database to reduce data
-- redundancy and improve data integrity.
--
-- The three main normal forms are: 1NF, 2NF, 3NF.
-- There are higher forms (BCNF, 4NF, 5NF) but 3NF is sufficient for most apps.
--
-- Before normalization — a FLAT (unnormalized) orders table:
-- This is what you might store in a spreadsheet:
--
-- ┌──────────┬───────────────────────┬──────────────────────────────────────────────────────────┐
-- │ order_id │ customer_info         │ items                                                    │
-- ├──────────┼───────────────────────┼──────────────────────────────────────────────────────────┤
-- │ 1001     │ Alice Smith, alice@x  │ Laptop:$999.00:2, Mouse:$29.99:1                         │
-- │ 1002     │ Alice Smith, alice@x  │ Monitor:$399.00:1                                        │
-- │ 1003     │ Bob Jones,  bob@y     │ Laptop:$999.00:1, Keyboard:$49.99:1, Mouse:$29.99:2      │
-- └──────────┴───────────────────────┴──────────────────────────────────────────────────────────┘
--
-- PROBLEMS with this flat structure:
-- 1. Repeating groups (multiple items in one cell — hard to query)
-- 2. Update anomaly: if Alice changes her email, we must update EVERY row
-- 3. Insert anomaly: can't store a product until someone orders it
-- 4. Delete anomaly: deleting order 1001 might delete the only record of Alice
-- 5. Redundant data: "Laptop:$999.00" appears in multiple rows

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: FIRST NORMAL FORM (1NF)
-- ─────────────────────────────────────────────────────────────────────────────

-- RULE: A table is in 1NF if:
--   1. All values are ATOMIC (no multi-valued cells, no arrays of values)
--   2. Each row is UNIQUE (has a primary key)
--   3. No REPEATING GROUPS (no "item1, item2, item3" column pattern)
--
-- Fix: Explode each order item into its own row.
-- The flat table above becomes:

-- VIOLATES 1NF (multiple values in one column — do NOT do this):
DROP TABLE IF EXISTS orders_unnormalized;
CREATE TABLE orders_unnormalized (
  order_id        INTEGER,
  customer_name   VARCHAR(100),    -- 1NF violation: should be separate first/last name
  customer_email  VARCHAR(255),    -- 1NF violation: will cause update anomalies
  product_names   TEXT,            -- 1NF violation: "Laptop, Mouse, Monitor" in one cell
  quantities      TEXT             -- 1NF violation: "2, 1, 1" — repeating group
);

-- SATISFIES 1NF (each row has one product, each column has one value):
DROP TABLE IF EXISTS orders_1nf;
CREATE TABLE orders_1nf (
  order_id        INTEGER     NOT NULL,
  customer_name   VARCHAR(100) NOT NULL,   -- still has redundancy (not yet 2NF/3NF)
  customer_email  VARCHAR(255) NOT NULL,
  product_name    VARCHAR(200) NOT NULL,   -- one product per row ✅
  product_price   NUMERIC(10,2) NOT NULL,
  quantity        INTEGER     NOT NULL,
  PRIMARY KEY (order_id, product_name)     -- composite PK ensures uniqueness ✅
);

INSERT INTO orders_1nf VALUES
  (1001, 'Alice Smith', 'alice@example.com', 'Laptop',   999.00, 2),
  (1001, 'Alice Smith', 'alice@example.com', 'Mouse',     29.99, 1),
  (1002, 'Alice Smith', 'alice@example.com', 'Monitor',  399.00, 1),
  (1003, 'Bob Jones',   'bob@example.com',   'Laptop',   999.00, 1),
  (1003, 'Bob Jones',   'bob@example.com',   'Keyboard',  49.99, 1),
  (1003, 'Bob Jones',   'bob@example.com',   'Mouse',     29.99, 2);

-- Verify 1NF data:
SELECT * FROM orders_1nf ORDER BY order_id, product_name;

-- REMAINING PROBLEM in 1NF:
-- Alice's name and email appear 3 times. If she changes her email,
-- we must update 3 rows — that's still an UPDATE ANOMALY.
-- Also: product_price depends only on product_name, not on the order.
-- We need 2NF to fix this.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: SECOND NORMAL FORM (2NF)
-- ─────────────────────────────────────────────────────────────────────────────

-- RULE: A table is in 2NF if:
--   1. It is already in 1NF
--   2. Every non-key column is FULLY FUNCTIONALLY DEPENDENT on the ENTIRE primary key
--      (no PARTIAL DEPENDENCIES — where a column depends on only PART of a composite PK)
--
-- In orders_1nf, the primary key is (order_id, product_name).
-- But: customer_name and customer_email depend only on order_id (partial dependency).
-- And: product_price depends only on product_name (partial dependency).
--
-- Fix: Decompose into separate tables — one for each partial dependency.

DROP TABLE IF EXISTS customers_2nf;
DROP TABLE IF EXISTS products_2nf;
DROP TABLE IF EXISTS order_headers_2nf;
DROP TABLE IF EXISTS order_items_2nf;

-- Customer info depends only on customer identity — separate table
CREATE TABLE customers_2nf (
  customer_id   SERIAL          PRIMARY KEY,
  customer_name VARCHAR(100)    NOT NULL,
  email         VARCHAR(255)    NOT NULL UNIQUE
);

-- Product info depends only on product identity — separate table
CREATE TABLE products_2nf (
  product_id    SERIAL          PRIMARY KEY,
  product_name  VARCHAR(200)    NOT NULL UNIQUE,
  price         NUMERIC(10,2)   NOT NULL
);

-- Order header depends only on order_id
CREATE TABLE order_headers_2nf (
  order_id      SERIAL          PRIMARY KEY,
  customer_id   INTEGER         NOT NULL REFERENCES customers_2nf(customer_id),
  order_date    DATE            NOT NULL DEFAULT CURRENT_DATE
);

-- Order items — now just the junction between orders and products
CREATE TABLE order_items_2nf (
  order_id      INTEGER         NOT NULL REFERENCES order_headers_2nf(order_id),
  product_id    INTEGER         NOT NULL REFERENCES products_2nf(product_id),
  quantity      INTEGER         NOT NULL CHECK (quantity > 0),
  PRIMARY KEY (order_id, product_id)
);

-- Seed data for 2NF schema
INSERT INTO customers_2nf (customer_name, email) VALUES
  ('Alice Smith', 'alice@example.com'),
  ('Bob Jones',   'bob@example.com');

INSERT INTO products_2nf (product_name, price) VALUES
  ('Laptop',   999.00),
  ('Mouse',     29.99),
  ('Monitor',  399.00),
  ('Keyboard',  49.99);

INSERT INTO order_headers_2nf (customer_id, order_date) VALUES
  (1, '2024-03-01'),
  (1, '2024-03-15'),
  (2, '2024-03-20');

INSERT INTO order_items_2nf VALUES
  (1, 1, 2),  -- order 1: 2 laptops
  (1, 2, 1),  -- order 1: 1 mouse
  (2, 3, 1),  -- order 2: 1 monitor
  (3, 1, 1),  -- order 3: 1 laptop
  (3, 4, 1),  -- order 3: 1 keyboard
  (3, 2, 2);  -- order 3: 2 mice

-- Verify: query across all four tables
SELECT
  c.customer_name,
  o.order_date,
  p.product_name,
  p.price,
  oi.quantity,
  ROUND(p.price * oi.quantity, 2) AS line_total
FROM order_headers_2nf o
JOIN customers_2nf  c  ON o.customer_id  = c.customer_id
JOIN order_items_2nf oi ON o.order_id    = oi.order_id
JOIN products_2nf   p  ON oi.product_id  = p.product_id
ORDER BY o.order_date, c.customer_name;

-- REMAINING PROBLEM in 2NF:
-- products_2nf has no partial dependency issues, but if we added
-- a "category_name" column to products, that might depend on a
-- category_id rather than the product itself — that's a TRANSITIVE DEPENDENCY.
-- We need 3NF to eliminate transitive dependencies.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: THIRD NORMAL FORM (3NF)
-- ─────────────────────────────────────────────────────────────────────────────

-- RULE: A table is in 3NF if:
--   1. It is already in 2NF
--   2. Every non-key column depends DIRECTLY on the primary key
--      (no TRANSITIVE DEPENDENCIES — where A→B→C, but B is not a key)
--
-- Example of a transitive dependency:
-- If products had: product_id → category_id → category_name
-- Then category_name depends on category_id, not directly on product_id.
-- category_name is TRANSITIVELY dependent on product_id via category_id.
--
-- Fix: Move the transitively dependent column to its own table.

DROP TABLE IF EXISTS order_items_3nf   CASCADE;
DROP TABLE IF EXISTS order_headers_3nf CASCADE;
DROP TABLE IF EXISTS products_3nf      CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;

-- Create categories table (breaks the transitive dependency)
CREATE TABLE product_categories (
  category_id     SERIAL          PRIMARY KEY,
  category_name   VARCHAR(100)    NOT NULL UNIQUE,
  tax_rate_pct    NUMERIC(5, 2)   NOT NULL DEFAULT 0.00
  -- tax_rate_pct depends on category, not on individual products
  -- If it were stored in products, it would be: product_id → category_id → tax_rate_pct
  -- That's a transitive dependency — putting tax_rate in category fixes it.
);

CREATE TABLE products_3nf (
  product_id      SERIAL          PRIMARY KEY,
  product_name    VARCHAR(200)    NOT NULL UNIQUE,
  price           NUMERIC(10,2)   NOT NULL,
  -- category_id is the FK — stores the relationship, not the category name/tax
  category_id     INTEGER         NOT NULL REFERENCES product_categories(category_id)
  -- category_name is NOT stored here — that would be a transitive dependency
);

-- Seed 3NF data
INSERT INTO product_categories (category_name, tax_rate_pct) VALUES
  ('Electronics', 8.00),
  ('Peripherals',  8.00),
  ('Displays',     8.00);

INSERT INTO products_3nf (product_name, price, category_id) VALUES
  ('Laptop',    999.00, 1),
  ('Mouse',      29.99, 2),
  ('Monitor',   399.00, 3),
  ('Keyboard',   49.99, 2);

-- Verify 3NF query — still get category name, but it's not duplicated
SELECT
  p.product_name,
  p.price,
  c.category_name,
  c.tax_rate_pct,
  ROUND(p.price * (1 + c.tax_rate_pct / 100), 2) AS price_with_tax
FROM products_3nf p
JOIN product_categories c ON p.category_id = c.category_id
ORDER BY c.category_name, p.product_name;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: NORMALIZATION SUMMARY
-- ─────────────────────────────────────────────────────────────────────────────

-- ┌──────┬────────────────────────────────────────────────────────────────────┐
-- │ Form │ Requirement                                                        │
-- ├──────┼────────────────────────────────────────────────────────────────────┤
-- │ 1NF  │ Atomic values, unique rows, no repeating groups                    │
-- │ 2NF  │ 1NF + no partial dependencies on composite PK                     │
-- │ 3NF  │ 2NF + no transitive dependencies (non-key depends on non-key)     │
-- │ BCNF │ 3NF + every determinant is a candidate key (advanced, rarely needed)│
-- └──────┴────────────────────────────────────────────────────────────────────┘
--
-- When to DENORMALIZE (intentionally break 3NF):
--   - For reporting/analytics tables (data warehouses) where reads >> writes
--   - For performance-critical queries where JOINs are too expensive
--   - For caching computed aggregates (total_order_amount stored on orders)
--   Always document WHY you denormalized.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 8: FULLY NORMALIZED FINAL SCHEMA (3NF)
-- ─────────────────────────────────────────────────────────────────────────────

-- The final schema from 01-ddl-create-alter-drop.sql is already in 3NF:
--
--   customers(customer_id PK, first_name, last_name, email, ...)
--       No partial deps (single-column PK)
--       No transitive deps (all columns depend directly on customer_id)
--
--   categories(category_id PK, name, description, parent_id FK, ...)
--       Self-referencing FK for hierarchy — not a 3NF violation
--
--   products(product_id PK, sku, name, price, category_id FK, ...)
--       category_id is just the FK — category_name lives in categories table ✅
--
--   orders(order_id PK, customer_id FK, status, total_amount, ...)
--       customer info NOT duplicated here ✅
--       total_amount is a COMPUTED denormalization — acceptable with documentation
--
--   order_items(order_id FK + product_id FK = composite PK, quantity, unit_price)
--       unit_price stored here (not FK to products.price) — intentional:
--       if product price changes later, historical order price must not change ✅

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 9: CLEANUP
-- ─────────────────────────────────────────────────────────────────────────────

-- Drop the normalization demo tables (keep the production schema from file 01)
DROP TABLE IF EXISTS order_items_2nf    CASCADE;
DROP TABLE IF EXISTS order_headers_2nf  CASCADE;
DROP TABLE IF EXISTS products_2nf       CASCADE;
DROP TABLE IF EXISTS customers_2nf      CASCADE;
DROP TABLE IF EXISTS order_items_3nf    CASCADE;
DROP TABLE IF EXISTS order_headers_3nf  CASCADE;
DROP TABLE IF EXISTS products_3nf       CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;
DROP TABLE IF EXISTS orders_1nf         CASCADE;
DROP TABLE IF EXISTS orders_unnormalized CASCADE;
