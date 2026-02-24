-- =============================================================================
-- Day 22 — Part 1, File 1: Data Definition Language (DDL)
-- DDL: CREATE, ALTER, DROP, SQL Data Types, and Constraints
-- Database: PostgreSQL (compatibility notes for MySQL/SQL Server included)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: DDL OVERVIEW
-- ─────────────────────────────────────────────────────────────────────────────

-- Data Definition Language (DDL) is the sub-language of SQL used to
-- DEFINE and MODIFY the structure (schema) of a database.
--
-- DDL commands:
--   CREATE  → create a new database object (database, table, index, view, etc.)
--   ALTER   → modify an existing database object
--   DROP    → permanently delete a database object
--   TRUNCATE → remove all rows from a table (faster than DELETE, resets sequences)
--   RENAME  → rename an object
--
-- CRITICAL: DDL is AUTO-COMMITTED in most databases.
-- There is NO ROLLBACK for DROP TABLE in MySQL, SQL Server, or Oracle.
-- PostgreSQL is the exception — DDL can be wrapped in a transaction.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: CREATING A DATABASE
-- ─────────────────────────────────────────────────────────────────────────────

-- Create a new database (run outside of psql as superuser, or via pgAdmin)
-- CREATE DATABASE ecommerce
--   WITH ENCODING = 'UTF8'
--        LC_COLLATE = 'en_US.UTF-8'
--        LC_CTYPE   = 'en_US.UTF-8';

-- Connect to the database (psql command, not SQL)
-- \c ecommerce

-- For this walkthrough we'll use the current database and clean up our demo tables:
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders      CASCADE;
DROP TABLE IF EXISTS products    CASCADE;
DROP TABLE IF EXISTS categories  CASCADE;
DROP TABLE IF EXISTS customers   CASCADE;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: SQL DATA TYPES
-- ─────────────────────────────────────────────────────────────────────────────

-- PostgreSQL data types (grouped by category):
--
-- NUMERIC:
--   SMALLINT              → -32,768 to 32,767 (2 bytes)
--   INTEGER / INT         → -2.1B to 2.1B (4 bytes) — most common for IDs
--   BIGINT                → ±9.2 quintillion (8 bytes) — for very large counts
--   NUMERIC(precision, scale) → exact decimal, e.g. NUMERIC(10,2) for $9,999,999.99
--   DECIMAL               → alias for NUMERIC
--   REAL                  → 4-byte floating point (approximate)
--   DOUBLE PRECISION      → 8-byte floating point (approximate)
--   SERIAL / BIGSERIAL    → auto-increment integer (PostgreSQL shorthand)
--
-- TEXT:
--   CHAR(n)               → fixed-length, padded with spaces
--   VARCHAR(n)            → variable-length, up to n characters
--   TEXT                  → unlimited length (PostgreSQL extension)
--
-- DATE/TIME:
--   DATE                  → calendar date only (2025-03-15)
--   TIME                  → time only (14:30:00)
--   TIMESTAMP             → date + time, no timezone
--   TIMESTAMPTZ           → date + time WITH timezone (preferred for production)
--   INTERVAL              → duration (e.g. '3 days', '2 hours 30 minutes')
--
-- BOOLEAN:
--   BOOLEAN               → TRUE / FALSE / NULL
--
-- BINARY / SPECIAL:
--   UUID                  → universally unique identifier (128-bit)
--   JSON / JSONB          → JSON data (JSONB is binary, indexed, preferred)
--   ARRAY                 → array of any type, e.g. INTEGER[], TEXT[]
--
-- MySQL equivalents to know:
--   TEXT     → same concept, with size variants (TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT)
--   DATETIME → similar to TIMESTAMP (no timezone awareness)
--   TINYINT(1) → commonly used as boolean
--   AUTO_INCREMENT → MySQL's equivalent of SERIAL

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: CREATE TABLE WITH CONSTRAINTS
-- ─────────────────────────────────────────────────────────────────────────────

-- Constraints enforce data integrity rules at the database level.
-- They run BEFORE any INSERT or UPDATE is committed.
-- Types: PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT

-- 4a. customers table — demonstrates all constraint types
CREATE TABLE customers (
  -- PRIMARY KEY: uniquely identifies each row, implies NOT NULL + UNIQUE
  -- SERIAL auto-generates the next integer value (1, 2, 3, ...)
  customer_id   SERIAL          PRIMARY KEY,

  -- NOT NULL: this column MUST have a value — empty string is allowed but NULL is not
  first_name    VARCHAR(50)     NOT NULL,
  last_name     VARCHAR(50)     NOT NULL,

  -- UNIQUE: no two rows can share the same email address
  email         VARCHAR(255)    NOT NULL UNIQUE,

  phone         VARCHAR(20),                        -- nullable (no NOT NULL = optional)

  -- CHECK: custom validation rule — status must be one of these values
  status        VARCHAR(20)     NOT NULL
                DEFAULT 'active'                    -- DEFAULT: value when not provided
                CHECK (status IN ('active', 'inactive', 'suspended')),

  -- NUMERIC with precision for financial data
  credit_limit  NUMERIC(10, 2)  NOT NULL DEFAULT 1000.00
                CHECK (credit_limit >= 0),

  -- TIMESTAMPTZ: production best practice — store with timezone
  created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  customers            IS 'Registered customers of the e-commerce platform';
COMMENT ON COLUMN customers.status     IS 'Customer account status: active, inactive, or suspended';
COMMENT ON COLUMN customers.credit_limit IS 'Maximum credit extended to this customer in USD';

-- 4b. categories table — simple lookup table
CREATE TABLE categories (
  category_id   SERIAL          PRIMARY KEY,
  name          VARCHAR(100)    NOT NULL UNIQUE,
  description   TEXT,
  parent_id     INTEGER         REFERENCES categories(category_id) ON DELETE SET NULL,
  -- parent_id is a SELF-REFERENCING FK for hierarchical categories (Electronics > Laptops)
  display_order SMALLINT        NOT NULL DEFAULT 0,
  is_active     BOOLEAN         NOT NULL DEFAULT TRUE
);

-- 4c. products table — demonstrates FOREIGN KEY and multiple constraints
CREATE TABLE products (
  product_id    SERIAL          PRIMARY KEY,
  sku           VARCHAR(50)     NOT NULL UNIQUE,       -- Stock Keeping Unit — internal code
  name          VARCHAR(200)    NOT NULL,
  description   TEXT,

  -- FOREIGN KEY: category_id must exist in categories.category_id
  -- ON DELETE RESTRICT: prevents deleting a category that still has products
  category_id   INTEGER         NOT NULL
                REFERENCES categories(category_id) ON DELETE RESTRICT,

  price         NUMERIC(10, 2)  NOT NULL CHECK (price >= 0),
  cost          NUMERIC(10, 2)  CHECK (cost >= 0),     -- nullable — cost may not be set
  stock_qty     INTEGER         NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
  weight_kg     NUMERIC(8, 3),
  is_active     BOOLEAN         NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 4d. orders table — demonstrates named constraints (TABLE-LEVEL constraints)
CREATE TABLE orders (
  order_id      SERIAL          PRIMARY KEY,
  customer_id   INTEGER         NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
  -- ON DELETE CASCADE: if a customer is deleted, their orders are deleted too

  -- TABLE-LEVEL named constraint: easier to identify in error messages
  status        VARCHAR(30)     NOT NULL DEFAULT 'pending',
  total_amount  NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
  shipping_address TEXT         NOT NULL,
  order_date    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  shipped_date  TIMESTAMPTZ,       -- null until actually shipped

  -- Named table-level CHECK constraint
  CONSTRAINT chk_orders_status
    CHECK (status IN ('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled')),

  CONSTRAINT chk_orders_total_positive
    CHECK (total_amount >= 0),

  -- Named CHECK spanning multiple columns
  CONSTRAINT chk_orders_shipped_after_ordered
    CHECK (shipped_date IS NULL OR shipped_date >= order_date)
);

-- 4e. order_items — junction / bridge table with COMPOSITE PRIMARY KEY
CREATE TABLE order_items (
  order_id      INTEGER         NOT NULL REFERENCES orders(order_id)   ON DELETE CASCADE,
  product_id    INTEGER         NOT NULL REFERENCES products(product_id) ON DELETE RESTRICT,
  quantity      INTEGER         NOT NULL CHECK (quantity > 0),
  unit_price    NUMERIC(10, 2)  NOT NULL CHECK (unit_price >= 0),
  discount_pct  NUMERIC(5, 2)   NOT NULL DEFAULT 0.00
                CHECK (discount_pct BETWEEN 0 AND 100),

  -- COMPOSITE PRIMARY KEY: the combination of order_id + product_id must be unique
  -- A product can appear in many orders, and an order can have many products,
  -- but a product can only appear ONCE per order (that's what this PK enforces)
  PRIMARY KEY (order_id, product_id)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: ALTER TABLE — MODIFYING EXISTING TABLES
-- ─────────────────────────────────────────────────────────────────────────────

-- ALTER TABLE lets you modify the schema of an existing table.
-- Common operations: add column, drop column, rename column, change data type,
-- add/drop constraints, rename table.

-- 5a. Add a new column
ALTER TABLE customers
  ADD COLUMN date_of_birth DATE;

ALTER TABLE customers
  ADD COLUMN marketing_opt_in BOOLEAN NOT NULL DEFAULT FALSE;

-- 5b. Add a column with a constraint in one step
ALTER TABLE products
  ADD COLUMN barcode VARCHAR(100) UNIQUE;

-- 5c. Set a default on an existing column
ALTER TABLE products
  ALTER COLUMN is_active SET DEFAULT TRUE;

-- 5d. Remove the default from a column
ALTER TABLE products
  ALTER COLUMN is_active DROP DEFAULT;

-- 5e. Make a column NOT NULL (data must already have no NULLs, or set a default first)
ALTER TABLE products
  ALTER COLUMN is_active SET NOT NULL;

-- 5f. Rename a column
ALTER TABLE customers
  RENAME COLUMN phone TO phone_number;

-- 5g. Change a column's data type (USING clause handles conversion)
-- ALTER TABLE products
--   ALTER COLUMN weight_kg TYPE NUMERIC(10, 4) USING weight_kg::NUMERIC(10, 4);
-- (Commented out — changing precision on existing data may cause precision loss)

-- 5h. Add a named constraint after table creation
ALTER TABLE orders
  ADD CONSTRAINT uq_orders_customer_date UNIQUE (customer_id, order_date);

-- 5i. Drop a constraint by name
ALTER TABLE orders
  DROP CONSTRAINT uq_orders_customer_date;

-- 5j. Add a foreign key constraint to an existing column
ALTER TABLE products
  ADD CONSTRAINT fk_products_category
    FOREIGN KEY (category_id) REFERENCES categories(category_id);
-- This fails if the FK already exists — shown for demonstration of ALTER syntax

-- 5k. Rename a table
-- ALTER TABLE customers RENAME TO clients;
-- ALTER TABLE clients  RENAME TO customers;  -- rename it back

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: DROP — REMOVING DATABASE OBJECTS
-- ─────────────────────────────────────────────────────────────────────────────

-- DROP permanently removes a database object. There is no "Undo" (outside a transaction).

-- 6a. Drop a column (and all data in it)
ALTER TABLE customers
  DROP COLUMN marketing_opt_in;

-- 6b. DROP TABLE — removes table structure and all data
-- DROP TABLE IF EXISTS products;
-- IF EXISTS prevents an error if the table doesn't exist

-- 6c. CASCADE — also drops dependent objects (views, foreign keys that reference this table)
-- DROP TABLE IF EXISTS categories CASCADE;
-- USE WITH EXTREME CAUTION in production

-- 6d. TRUNCATE — removes all rows but keeps the table structure
--     Much faster than DELETE FROM table — skips row-by-row processing
--     Resets SERIAL sequences (auto-increment counters)
-- TRUNCATE TABLE order_items;
-- TRUNCATE TABLE orders, order_items;         -- truncate multiple tables at once
-- TRUNCATE TABLE orders RESTART IDENTITY;     -- reset serial counter back to 1

-- 6e. DROP DATABASE (must be connected to a DIFFERENT database first)
-- DROP DATABASE IF EXISTS ecommerce;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: VERIFY THE SCHEMA
-- ─────────────────────────────────────────────────────────────────────────────

-- View all tables in the current schema
SELECT table_name, table_type
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- View all columns and their definitions for the customers table
SELECT
  column_name,
  data_type,
  character_maximum_length,
  is_nullable,
  column_default
FROM information_schema.columns
WHERE table_name = 'customers'
ORDER BY ordinal_position;

-- View all constraints on the orders table
SELECT
  constraint_name,
  constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'orders';
