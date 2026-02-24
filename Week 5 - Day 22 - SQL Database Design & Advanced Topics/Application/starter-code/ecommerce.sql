-- =============================================================
-- ecommerce_schema.sql
-- Day 22: SQL Database Design & Advanced Topics
-- E-Commerce Database — Part 1: Schema DDL
--
-- Instructions:
--   Complete all TODO sections below.
--   Run this file in order — each section builds on the last.
-- =============================================================

-- ============================================================
-- PART 1 — DDL: CREATE TABLES
-- Complete the CREATE TABLE statements by adding constraints
-- ============================================================

-- TODO Task 1: Add appropriate constraints to the customers table
--   - customer_id: PRIMARY KEY
--   - email: NOT NULL, UNIQUE
--   - phone: optional but must match format if provided
--   - created_at: NOT NULL, default to current timestamp
CREATE TABLE customers (
    customer_id  INTEGER,          -- TODO: add PRIMARY KEY
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    email        VARCHAR(150),     -- TODO: add NOT NULL, UNIQUE
    phone        VARCHAR(20),
    address      TEXT,
    created_at   TIMESTAMP         -- TODO: add NOT NULL, DEFAULT CURRENT_TIMESTAMP
);

-- TODO Task 2: Design the categories table from scratch
-- A category has: category_id (PK), name (unique, not null), description
-- A category can have a parent category (self-referential FK)
CREATE TABLE categories (
    -- TODO: write full CREATE TABLE statement
);

-- TODO Task 3: Add constraints to the products table
--   - product_id: PRIMARY KEY
--   - name: NOT NULL
--   - price: NOT NULL, must be >= 0
--   - stock_quantity: NOT NULL, must be >= 0, default 0
--   - category_id: FOREIGN KEY → categories, SET NULL on delete
CREATE TABLE products (
    product_id      INTEGER,
    name            VARCHAR(200),
    description     TEXT,
    price           DECIMAL(10, 2),
    stock_quantity  INTEGER,
    category_id     INTEGER,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- TODO: add all PRIMARY KEY, NOT NULL, CHECK, FOREIGN KEY constraints
);

-- TODO Task 4: Design the orders table
-- An order belongs to a customer.
-- status must be one of: 'PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'
-- total_amount must be >= 0
CREATE TABLE orders (
    -- TODO: write full CREATE TABLE statement
    -- Required fields: order_id, customer_id (FK), order_date, status, total_amount, shipping_address
);

-- TODO Task 5: Design the order_items junction table
-- Each order can have many products; each product can appear in many orders
-- quantity must be > 0
-- unit_price captures price AT TIME OF ORDER (not current product price)
CREATE TABLE order_items (
    -- TODO: write full CREATE TABLE statement
    -- Required fields: order_item_id (or composite PK), order_id (FK), product_id (FK),
    --                  quantity, unit_price
    -- Add a CHECK: quantity > 0
);

-- TODO Task 6: Design the reviews table
-- A customer can review a product (only once per product)
-- rating must be between 1 and 5
-- review_date defaults to current timestamp
CREATE TABLE reviews (
    -- TODO: write full CREATE TABLE statement
    -- UNIQUE constraint: one review per (customer_id, product_id) pair
);


-- ============================================================
-- PART 2 — INDEXES
-- ============================================================

-- TODO Task 7: Create indexes to optimize common queries
-- Think about which columns are frequently searched or sorted

-- Index 1: Speed up finding orders by customer
-- TODO: CREATE INDEX ...

-- Index 2: Speed up product search by name (for LIKE queries)
-- TODO: CREATE INDEX ...

-- Index 3: Speed up finding reviews by product
-- TODO: CREATE INDEX ...

-- Index 4: Speed up filtering products by category
-- TODO: CREATE INDEX ...


-- ============================================================
-- PART 3 — SAMPLE DATA (DML)
-- ============================================================

-- TODO Task 8: Insert at least:
--   - 3 categories (including 1 subcategory using parent_id)
--   - 5 customers
--   - 8 products (spread across categories)
--   - 4 orders (with varying statuses)
--   - 10 order_items
--   - 6 reviews

-- Example starter:
INSERT INTO customers (customer_id, first_name, last_name, email, phone, address, created_at) VALUES
(1, 'Alice', 'Walker',  'alice@email.com',  '555-1001', '100 Maple St, Austin TX', CURRENT_TIMESTAMP),
(2, 'Bob',   'Sanders', 'bob@email.com',    '555-1002', '200 Oak Ave, Denver CO',  CURRENT_TIMESTAMP);
-- TODO: add 3 more customers

-- TODO: Insert categories, products, orders, order_items, reviews


-- ============================================================
-- PART 4 — VIEWS
-- ============================================================

-- TODO Task 9: Create a view called vw_order_summary
-- Shows: order_id, customer full name, order_date, status, total_amount, number of items
CREATE OR REPLACE VIEW vw_order_summary AS
-- TODO: SELECT with JOINs


-- TODO Task 10: Create a view called vw_product_ratings
-- Shows: product_id, product name, category name, avg_rating (2 decimal places), review_count
-- Only include products that HAVE at least 1 review
CREATE OR REPLACE VIEW vw_product_ratings AS
-- TODO: SELECT with JOINs and aggregation


-- ============================================================
-- PART 5 — STORED PROCEDURE / FUNCTION
-- ============================================================

-- TODO Task 11: Create a stored procedure (or function) called place_order
-- Parameters: p_customer_id, p_product_id, p_quantity
-- Logic:
--   1. Check that the product exists and has enough stock
--   2. If not enough stock, signal/raise an error
--   3. Insert a new order record
--   4. Insert an order_item record
--   5. Decrement the product's stock_quantity
--   6. Return the new order_id

-- PostgreSQL version:
-- CREATE OR REPLACE FUNCTION place_order(p_customer_id INT, p_product_id INT, p_quantity INT)
-- RETURNS INT AS $$
-- DECLARE
--   v_order_id  INT;
--   v_stock     INT;
--   v_price     DECIMAL(10,2);
-- BEGIN
--   -- TODO: implement logic
-- END;
-- $$ LANGUAGE plpgsql;

-- MySQL version:
-- DELIMITER //
-- CREATE PROCEDURE place_order(IN p_customer_id INT, IN p_product_id INT, IN p_quantity INT, OUT p_order_id INT)
-- BEGIN
--   -- TODO: implement logic
-- END //
-- DELIMITER ;


-- ============================================================
-- PART 6 — TRIGGER
-- ============================================================

-- TODO Task 12: Create a trigger called trg_update_order_total
-- Fires: AFTER INSERT on order_items
-- Action: Recalculate and update the total_amount on the parent order
--         by summing (quantity * unit_price) for all items in that order

-- PostgreSQL version:
-- CREATE OR REPLACE FUNCTION fn_update_order_total() RETURNS TRIGGER AS $$
-- BEGIN
--   -- TODO: UPDATE orders SET total_amount = ...
--   RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_update_order_total
--   AFTER INSERT ON order_items
--   FOR EACH ROW
--   EXECUTE FUNCTION fn_update_order_total();


-- ============================================================
-- BONUS — ADVANCED QUERIES
-- ============================================================

-- Bonus 1: Using the vw_order_summary view, find all orders with more than 2 items
-- TODO:

-- Bonus 2: Find customers who have placed orders but never left a review
-- TODO:

-- Bonus 3: Rank products by average rating within each category using a window function
-- RANK() OVER (PARTITION BY category_id ORDER BY avg_rating DESC)
-- TODO:
