-- =============================================================================
-- Day 22 — Part 2, File 3: Transactions, ACID, Isolation Levels,
--           Query Optimization, Execution Plans, and Backup Basics
-- Database: PostgreSQL
-- Prerequisite: Run Part-1/01-ddl-create-alter-drop.sql + Part-2/01-dml...sql
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: TRANSACTIONS — BEGIN, COMMIT, ROLLBACK
-- ─────────────────────────────────────────────────────────────────────────────

-- A TRANSACTION is a unit of work that groups multiple SQL statements together.
-- Either ALL statements succeed (COMMIT) or ALL are undone (ROLLBACK).
-- Transactions protect data integrity when a business operation spans
-- multiple SQL statements.
--
-- Classic example: bank transfer
--   1. Debit $500 from Account A  ← must both happen
--   2. Credit $500 to Account B   ← or neither can happen
--
-- If step 1 succeeds and the server crashes before step 2,
-- without transactions, money disappears. With transactions, step 1 is rolled back.

-- 1a. Basic transaction syntax
BEGIN;                          -- Start a transaction

UPDATE orders SET status = 'processing' WHERE order_id = 1;
UPDATE products SET stock_qty = stock_qty - 1 WHERE product_id = 1;

COMMIT;                         -- Permanently save all changes above
-- or ROLLBACK;                 -- Undo all changes back to the BEGIN

-- 1b. Transaction demonstrating ROLLBACK
BEGIN;

  -- Simulate a multi-step operation
  UPDATE customers SET credit_limit = 0 WHERE customer_id = 1;

  -- Intentional check: inspect the state mid-transaction
  SELECT customer_id, credit_limit FROM customers WHERE customer_id = 1;
  -- Shows credit_limit = 0 — but NOT yet visible to other sessions

  ROLLBACK;   -- Oops, that was wrong — undo it

-- Verify the rollback worked
SELECT customer_id, credit_limit FROM customers WHERE customer_id = 1;
-- credit_limit is back to its original value ✅

-- 1c. SAVEPOINT — partial rollback within a transaction
BEGIN;

  INSERT INTO categories (name, display_order) VALUES ('Accessories', 20);
  INSERT INTO categories (name, display_order) VALUES ('Office',      21);

  SAVEPOINT after_categories;  -- mark a point we can roll back to

  INSERT INTO products (sku, name, category_id, price, stock_qty)
  VALUES ('BAD-SKU', '', 1, -999, 0);  -- this will fail the CHECK constraint

  -- Rollback only to the savepoint — categories inserts are still in the transaction
  ROLLBACK TO SAVEPOINT after_categories;

  -- Continue with valid work after partial rollback
  INSERT INTO products (sku, name, category_id, price, stock_qty)
  VALUES ('ACC-001', 'USB Hub', 1, 24.99, 75);

COMMIT;

-- Verify: categories were inserted, bad product was not, USB Hub was inserted
SELECT name FROM categories WHERE name IN ('Accessories', 'Office') ORDER BY name;
SELECT name FROM products WHERE sku IN ('BAD-SKU', 'ACC-001');

-- 1d. RELEASE SAVEPOINT — remove a named savepoint when no longer needed
-- BEGIN;
-- SAVEPOINT sp1;
-- INSERT ...;
-- RELEASE SAVEPOINT sp1;   -- remove the savepoint (the work stays)
-- COMMIT;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: ACID PROPERTIES
-- ─────────────────────────────────────────────────────────────────────────────

-- ACID is the set of properties that guarantee database transactions are
-- processed reliably even in the face of errors, power failures, or concurrent access.
--
-- A — ATOMICITY
--   "All or nothing." Either every statement in a transaction succeeds,
--   or the entire transaction is rolled back. No partial results.
--   Guarantor: ROLLBACK mechanism + Write-Ahead Log (WAL)
--
-- C — CONSISTENCY
--   "Valid state to valid state." A transaction takes the database from one
--   valid state to another. All constraints (PKs, FKs, CHECKs) are enforced.
--   If a transaction violates a constraint, the whole transaction is rejected.
--   Guarantor: constraints + triggers
--
-- I — ISOLATION
--   "Transactions don't interfere with each other." Concurrent transactions
--   behave as if they were executed serially. The database hides partially
--   completed transactions from other sessions.
--   Guarantor: locking, MVCC (Multi-Version Concurrency Control)
--
-- D — DURABILITY
--   "Committed transactions survive crashes." Once COMMIT is acknowledged,
--   the changes are persisted even if the server crashes immediately after.
--   Guarantor: Write-Ahead Log (WAL), data files flushed to disk

-- Demonstrating CONSISTENCY — constraint violation rolls back the entire transaction:
BEGIN;
  INSERT INTO orders (customer_id, shipping_address)
  VALUES (1, '123 Main St');

  INSERT INTO order_items (order_id, product_id, quantity, unit_price)
  VALUES (999, 1, 1, 999.00);   -- order_id 999 does not exist → FK violation!
-- This INSERT fails → entire transaction is in an error state
ROLLBACK;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: ISOLATION LEVELS
-- ─────────────────────────────────────────────────────────────────────────────

-- Isolation levels control HOW MUCH one transaction can "see" of other
-- concurrent transactions that haven't committed yet.
-- There's a trade-off: more isolation = more safety, but also more locking/overhead.
--
-- Phenomena that can occur without proper isolation:
--
--   DIRTY READ:
--     Transaction A reads data written by Transaction B before B commits.
--     If B rolls back, A read data that "never existed."
--
--   NON-REPEATABLE READ:
--     Transaction A reads row X. Transaction B updates and commits row X.
--     Transaction A reads row X again — gets a different value.
--
--   PHANTOM READ:
--     Transaction A queries rows WHERE price > 50. Transaction B inserts a new
--     row with price = 75 and commits. Transaction A repeats the query —
--     now sees a new "phantom" row that appeared.
--
--   SERIALIZATION ANOMALY:
--     Two transactions each read data and write back a result that depends on
--     what they read. The combined result is inconsistent with any serial execution.
--
-- SQL standard isolation levels (most → least restrictive):
--
-- ┌──────────────────────┬────────────┬────────────────────┬──────────────┬──────────────────────┐
-- │ Isolation Level      │ Dirty Read │ Non-Repeatable Read│ Phantom Read │ Serialization Anomaly│
-- ├──────────────────────┼────────────┼────────────────────┼──────────────┼──────────────────────┤
-- │ READ UNCOMMITTED     │ Possible   │ Possible           │ Possible     │ Possible             │
-- │ READ COMMITTED       │ Prevented  │ Possible           │ Possible     │ Possible             │
-- │ REPEATABLE READ      │ Prevented  │ Prevented          │ Possible*    │ Possible             │
-- │ SERIALIZABLE         │ Prevented  │ Prevented          │ Prevented    │ Prevented            │
-- └──────────────────────┴────────────┴────────────────────┴──────────────┴──────────────────────┘
-- * PostgreSQL REPEATABLE READ also prevents phantom reads (stronger than standard)
--
-- PostgreSQL default: READ COMMITTED
-- MySQL default: REPEATABLE READ
-- Most web applications are fine with READ COMMITTED.
-- Banking/financial: use SERIALIZABLE.

-- Set isolation level for a single transaction:
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
  SELECT SUM(credit_limit) FROM customers WHERE status = 'active';
  -- In SERIALIZABLE, this value is guaranteed consistent for the entire transaction
COMMIT;

-- Set default isolation level for the session:
-- SET default_transaction_isolation = 'repeatable read';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: QUERY OPTIMIZATION AND EXECUTION PLANS
-- ─────────────────────────────────────────────────────────────────────────────

-- The PostgreSQL query planner automatically chooses the best execution strategy.
-- EXPLAIN shows you the plan. EXPLAIN ANALYZE actually runs the query and shows
-- real timings.

-- 4a. EXPLAIN — see the query plan without running the query
EXPLAIN
SELECT * FROM products WHERE category_id = 1;
-- Look for: Seq Scan (no index used) vs Index Scan (index used)
-- Look at: cost=X..Y (estimated rows scanned and startup vs total cost)

-- 4b. EXPLAIN ANALYZE — run the query AND show actual vs estimated stats
EXPLAIN ANALYZE
SELECT * FROM products WHERE category_id = 1;
-- Now shows: actual rows, actual time, loops
-- Compare "estimated rows" to "actual rows" — large differences indicate stale statistics

-- 4c. EXPLAIN ANALYZE with a JOIN
EXPLAIN ANALYZE
SELECT
  c.first_name,
  c.last_name,
  o.order_id,
  o.total_amount
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
WHERE c.status = 'active'
ORDER BY o.total_amount DESC;

-- Output keywords to understand:
--   Seq Scan        → reading all rows (no usable index)
--   Index Scan      → using a B-tree index
--   Index Only Scan → all needed columns are in the index (no table access)
--   Bitmap Heap Scan→ collecting index hits then fetching from heap
--   Hash Join       → building a hash table from the smaller table, probing with larger
--   Nested Loop     → for each row in outer, scan inner (efficient for small tables)
--   Merge Join      → both inputs sorted on join key (efficient for large sorted sets)
--   Sort            → explicit sort operation (can be eliminated by an index)
--   Filter          → applied after scanning (should be part of index if possible)
--   rows=N          → estimated row count (accuracy depends on ANALYZE statistics)

-- 4d. Update statistics so the planner has accurate row counts
ANALYZE customers;
ANALYZE products;
ANALYZE orders;
-- ANALYZE collects statistics about data distribution (how many distinct values,
-- most common values, histogram of value ranges). Run after bulk data loads.
-- VACUUM ANALYZE does both VACUUM (reclaim dead rows) and ANALYZE in one step.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: QUERY OPTIMIZATION TECHNIQUES
-- ─────────────────────────────────────────────────────────────────────────────

-- 5a. ❌ SLOW: using a function on a WHERE column breaks index usage
EXPLAIN ANALYZE
SELECT * FROM customers
WHERE UPPER(last_name) = 'SMITH';
-- The index on last_name cannot be used because UPPER() transforms the value.
-- The planner must evaluate UPPER(last_name) for every row — sequential scan.

-- ✅ FIX 1: store data in a consistent case and match it
-- WHERE last_name = 'Smith'

-- ✅ FIX 2: create an expression index matching the function
CREATE INDEX idx_customers_last_name_upper ON customers(UPPER(last_name));

EXPLAIN ANALYZE
SELECT * FROM customers WHERE UPPER(last_name) = 'SMITH';
-- Now uses the expression index ✅

-- 5b. ❌ SLOW: leading wildcard prevents index use
EXPLAIN ANALYZE
SELECT * FROM products WHERE name LIKE '%laptop%';
-- The leading % means the index cannot narrow down a starting point.
-- Must scan all rows.

-- ✅ FIX: trailing wildcard CAN use index
EXPLAIN ANALYZE
SELECT * FROM products WHERE name LIKE 'Pro%';
-- Uses the B-tree index on name (if one exists) ✅

-- ✅ BETTER FIX for text search: full-text search indexes (GIN/tsvector)
-- CREATE INDEX idx_products_name_fts ON products USING GIN(to_tsvector('english', name));
-- WHERE to_tsvector('english', name) @@ to_tsquery('laptop')

-- 5c. ❌ SLOW: SELECT * fetches every column including large ones
EXPLAIN ANALYZE
SELECT * FROM products;

-- ✅ FIX: select only the columns you need
EXPLAIN ANALYZE
SELECT product_id, sku, name, price FROM products;
-- Less data transferred from database to application layer

-- 5d. ❌ SLOW: correlated subquery runs once per row
EXPLAIN ANALYZE
SELECT
  o.order_id,
  (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count
FROM orders o;

-- ✅ FIX: use a JOIN + GROUP BY instead
EXPLAIN ANALYZE
SELECT
  o.order_id,
  COUNT(oi.product_id) AS item_count
FROM orders o
LEFT JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY o.order_id;

-- 5e. ❌ SLOW: DISTINCT on large result sets (requires sorting all rows)
-- ✅ FIX: use GROUP BY or EXISTS instead when semantically equivalent

-- 5f. Pagination optimization — using keyset pagination instead of OFFSET
-- ❌ SLOW for large offsets: offset must skip N rows before returning
EXPLAIN ANALYZE
SELECT product_id, name, price FROM products ORDER BY product_id LIMIT 10 OFFSET 1000;

-- ✅ FAST: keyset pagination — use WHERE to filter past the last seen ID
EXPLAIN ANALYZE
SELECT product_id, name, price
FROM products
WHERE product_id > 1000     -- last product_id seen on previous page
ORDER BY product_id
LIMIT 10;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: DATABASE BACKUP AND RECOVERY BASICS
-- ─────────────────────────────────────────────────────────────────────────────

-- Backup and recovery are critical production concerns.
-- SQL itself doesn't cover backups directly — these are CLI/admin tools.
-- Below are the commands with explanations (shown as comments — run in terminal).

-- LOGICAL BACKUPS (pg_dump):
--   Creates a SQL script or custom-format dump of one database.
--   Portable — can restore to a different server or PostgreSQL version.
--
-- Backup a single database to SQL format:
--   pg_dump ecommerce > ecommerce_backup.sql
--
-- Backup in custom compressed format (recommended — supports selective restore):
--   pg_dump -Fc ecommerce > ecommerce.dump
--
-- Restore from SQL format:
--   psql -d ecommerce_new -f ecommerce_backup.sql
--
-- Restore from custom format:
--   pg_restore -d ecommerce_new ecommerce.dump
--
-- Restore only specific tables:
--   pg_restore -d ecommerce_new -t orders ecommerce.dump
--
-- Backup ALL databases (including roles and tablespaces):
--   pg_dumpall > full_cluster_backup.sql

-- PHYSICAL BACKUPS:
--   Copies the raw data files.
--   Faster for large databases.
--   Requires the same PostgreSQL version.
--   Tools: pg_basebackup, Barman, pgBackRest

-- POINT-IN-TIME RECOVERY (PITR):
--   Uses Write-Ahead Log (WAL) archiving + a base backup.
--   Can restore the database to any point in time (e.g. "restore to 14:35 yesterday").
--   Required setup: wal_level = replica, archive_mode = on in postgresql.conf.

-- BACKUP BEST PRACTICES:
--   1. Automate backups on a schedule (cron or managed service)
--   2. Store backups in a DIFFERENT location than the server (S3, GCS, etc.)
--   3. TEST YOUR RESTORES — a backup you've never restored is not a backup
--   4. Monitor backup job success/failure with alerting
--   5. Define and document RPO (Recovery Point Objective) and RTO (Recovery Time Objective)
--      RPO: maximum acceptable data loss (e.g. "we can lose up to 1 hour of data")
--      RTO: maximum acceptable downtime (e.g. "we must be back online within 4 hours")
--   6. Use managed database services (AWS RDS, Google Cloud SQL, Supabase) for
--      automated backups, PITR, and failover in production

-- AWS RDS backup example (for awareness — not SQL):
--   - Automated daily snapshots (retention: 1–35 days)
--   - Manual snapshots (retained until deleted)
--   - PITR within the retention window
--   - Multi-AZ deployment for high availability

-- MONITORING QUERIES — check database size and table sizes:
SELECT
  pg_database.datname,
  pg_size_pretty(pg_database_size(pg_database.datname)) AS size
FROM pg_database
ORDER BY pg_database_size(pg_database.datname) DESC;

SELECT
  tablename,
  pg_size_pretty(pg_total_relation_size(schemaname || '.' || tablename)) AS total_size,
  pg_size_pretty(pg_relation_size(schemaname || '.' || tablename))       AS table_size,
  pg_size_pretty(pg_indexes_size(schemaname || '.' || tablename))        AS index_size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname || '.' || tablename) DESC;
