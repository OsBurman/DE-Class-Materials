# Day 22 — Part 2 Walkthrough Script
# DML, Indexes, Views, Stored Procedures, Triggers, Transactions & Query Optimization

**Files referenced:**
- `01-dml-insert-update-delete.sql`
- `02-indexes-views-procedures.sql`
- `03-transactions-and-optimization.sql`

**Total time:** ~90 minutes  
**Breakdown:** DML (25 min) → Indexes & Views (25 min) → Procedures/Triggers (20 min) → Transactions & Optimization (20 min)

---

## Segment 1 — Afternoon Opener: From Structure to Data (3 min)

> "This morning we designed and built the structure. Tables, columns, constraints, normalization. The skeleton of the database."

> "This afternoon we fill it with life. DML — inserting, updating, deleting data. Then the advanced machinery: indexes to make it fast, views to simplify it, stored procedures to encapsulate logic, triggers to automate it, and transactions to keep it safe."

> "Let's go. Run the Part 1 DDL file first if you haven't already — we need those tables."

---

## BLOCK A — DML: INSERT, UPDATE, DELETE

*Open `01-dml-insert-update-delete.sql`*

---

## Segment 2 — INSERT (10 min)

*Navigate to Section 2*

> "INSERT adds new rows. The syntax: INSERT INTO table (column list) VALUES (value list)."

*Run Section 2a — individual category inserts*

> "Always specify your column list explicitly. If you skip the column list and just write VALUES, the database expects values in the exact order the table was created. That order can change when you ALTER TABLE. Explicit column lists are self-documenting and safe."

*Navigate to Section 2c — multi-row INSERT*

> "Multi-row INSERT. One INSERT statement, multiple value tuples, separated by commas. This is significantly more efficient than five separate INSERT statements — it's one round trip to the database instead of five."

*Navigate to Section 2e — INSERT RETURNING*

> "RETURNING is PostgreSQL-specific and incredibly useful. After an INSERT with SERIAL, you need the generated ID — you can't know it in advance. RETURNING gives it to you immediately in the same statement. In Spring Boot JPA, this happens automatically. But knowing it exists is useful when writing raw SQL."

*Navigate to Section 2h — INSERT … SELECT*

> "INSERT … SELECT copies data from a query result into a table. The CREATE TABLE AS SELECT version creates the table AND loads data in one step. This is how you create snapshot tables, archive tables, or migration staging tables."

> "Question: if you INSERT a row without specifying `created_at`, what value will it get?"

*(Expected: NOW() — the DEFAULT value defined in CREATE TABLE)*

---

## Segment 3 — UPDATE (8 min)

*Navigate to Section 3*

> "UPDATE modifies existing rows. The most important rule: **always include a WHERE clause**, unless you genuinely mean to update every single row."

> "I want you to form a habit right now. Before you run any UPDATE in production, first run `SELECT * FROM table WHERE <same condition>` to see exactly which rows will be affected. Then replace SELECT * with your UPDATE."

*Run Section 3a — single column update*

> "Basic update. One column, one row, identified by primary key. Clean, surgical."

*Run Section 3c — calculated update*

> "5% price increase across all active electronics. The SET clause can contain any expression — not just literal values. `ROUND(price * 1.05, 2)` runs per row."

*Navigate to Section 3e — UPDATE … RETURNING*

> "UPDATE with RETURNING — same idea as INSERT RETURNING. You can see exactly what changed. Great for debugging and for confirming your WHERE clause matched what you expected."

*Navigate to Section 3f — UPDATE with FROM*

> "UPDATE … FROM is PostgreSQL's version of UPDATE with a JOIN. Here we're updating all order totals by joining orders to a subquery that calculates the real total from order_items. This is a powerful pattern for syncing a denormalized column."

> "**Watch out:** in MySQL, the syntax is `UPDATE orders o JOIN (...) s ON o.order_id = s.order_id SET o.total_amount = s.calculated_total`. Different syntax, same idea."

---

## Segment 4 — DELETE and UPSERT (7 min)

*Navigate to Section 4*

> "DELETE removes rows. Same rules as UPDATE — always use WHERE. And use the same 'SELECT first' habit."

> "Notice Section 4c — DELETE RETURNING. You can capture what you deleted. Useful for audit logging without separate triggers."

*Navigate to Section 4d — DELETE with subquery*

> "Delete all order_items for cancelled orders. The subquery identifies cancelled order IDs, the outer DELETE removes their items. Correlated deletes like this are common in cleanup jobs."

*Navigate to Section 5 — UPSERT*

> "UPSERT means 'INSERT if the row doesn't exist, UPDATE if it does.' This solves a real problem: you have data coming in from an external source and you don't know if the record is new or already exists."

*Run Section 5a*

> "ON CONFLICT (sku) DO UPDATE. If the INSERT would violate the UNIQUE constraint on sku, update instead. `EXCLUDED` is a special reference to the row that was about to be inserted."

*Run Section 5b*

> "ON CONFLICT DO NOTHING. If Electronics already exists, silently skip. No error. Great for idempotent seed scripts."

> "Section 6 at the bottom has the safety pattern I mentioned. Please read through it. It's professional practice — BEGIN, SELECT to verify, UPDATE, SELECT again to check, then COMMIT or ROLLBACK."

---

## BLOCK B — Indexes & Views

*Open `02-indexes-views-procedures.sql`*

---

## Segment 5 — Indexes: What They Are (5 min)

*Navigate to Sections 1 and 2*

> "An index is a separate data structure — think of it like a book index — that lets the database jump directly to matching rows instead of reading every row."

> "Without an index on `category_id`, a query `WHERE category_id = 1` reads every product row and checks each one. That's called a Sequential Scan. With 10 rows it's fine. With 10 million rows, it takes seconds."

> "With a B-Tree index, the database uses a sorted tree structure to find matching rows in O(log n) time. A million-row table needs about 20 comparisons instead of 1,000,000."

*Run CREATE INDEX for `idx_products_category_id`*

> "The naming convention `idx_tablename_columnname` is standard. Consistent naming makes it easy to understand what's indexed when you look at the schema."

*Run the composite index creation*

> "Composite index — two columns. Useful when you frequently query by both. Column order matters: put the most selective column first. The database can use the leading column alone, but not the trailing column alone."

---

## Segment 6 — Index Types and Trade-offs (8 min)

*Navigate to Section 3 — index type comparison*

> "Let's go through the index types. You don't need to memorize all of these today — but you need to know B-Tree and GIN."

> "B-Tree: the default. Handles equality, ranges, ORDER BY, LIKE with a trailing wildcard. Use it for almost everything."

> "Hash: only handles equality, not ranges. B-Tree is usually better. Use Hash if you have a very write-heavy table and only need exact matches."

> "GIN — Generalized Inverted Index: used for JSONB columns and full-text search. When a column contains multiple values per row (like an array or JSON object), GIN indexes all of them."

> "BRIN — Block Range Index: very small index for huge tables where data is physically ordered by a timestamp. Log tables, sensor data tables. The index stores min/max for each block of pages, not individual rows."

*Navigate to Section 4*

> "The cost section is important. Indexes are not free."

> "Every INSERT, UPDATE, and DELETE must update every relevant index. If a table has 5 indexes and you insert 1 million rows in a bulk load, you're updating 5 million index entries. That's why bulk data loads sometimes drop indexes first, load the data, then rebuild."

> "Rule of thumb: index columns you frequently filter, join, or order by. Don't index columns with few distinct values, very small tables, or rarely-queried columns."

*Run the partial index and expression index*

> "Partial index: only indexes rows matching `WHERE is_active = TRUE`. If 95% of your queries filter active products, why index the inactive ones? This index is smaller and faster."

> "Expression index: index on `LOWER(email)` enables case-insensitive lookups that use the index. Without it, `WHERE LOWER(email) = 'alice@example.com'` scans every row."

---

## Segment 7 — Views (7 min)

*Navigate to Section 5*

> "A view is a saved SELECT query with a name. It looks like a table, you query it like a table, but it stores no data. Every time you SELECT from a view, the underlying query runs."

*Run the v_product_catalog view creation and query*

> "The view hides the JOIN. Any developer can now write `SELECT * FROM v_product_catalog` without knowing the underlying table structure. This is an API layer over your schema."

*Run the v_order_summary view*

> "Complex JOIN + GROUP BY, hidden behind a simple name. Your Spring Boot repository can now run `SELECT * FROM v_order_summary WHERE status = 'pending'` instead of embedding a 15-line query."

> "**Watch out:** views that use JOINs or aggregates are read-only. You can't INSERT or UPDATE through them. Simple single-table views without aggregates CAN be updated — the change flows through to the underlying table."

> "Use cases: security (hide sensitive columns), simplification (hide complexity), stability (if the underlying schema changes, update the view definition — application queries stay the same)."

---

## Segment 8 — Stored Procedures and Functions (10 min)

*Navigate to Section 6*

> "Stored procedures and functions are reusable SQL programs stored in the database. They encapsulate multi-step logic."

> "The difference: functions return a value and can be called in SELECT. Procedures don't return a value (they use OUT parameters instead) and can manage their own transactions."

*Walk through Section 6a — calculate_order_total function*

> "PL/pgSQL is PostgreSQL's procedural language. The dollar-sign dollar-sign syntax `$$` wraps the function body. DECLARE declares local variables. SELECT … INTO assigns a query result to a variable."

*Run the function and show it in a query*

> "We call it exactly like a built-in function: `calculate_order_total(order_id)`. It runs the summing logic internally. Clean."

*Navigate to Section 6c — place_order procedure*

> "The stored procedure is where it gets interesting. This encapsulates an entire business operation: validate stock, insert order, insert order_items, update total, decrement stock — all in one call."

> "Look at the validation: if the product doesn't exist or stock is insufficient, RAISE EXCEPTION — this will cause the entire transaction to fail and roll back. The procedure ensures the database either processes the order completely or not at all."

*Run the DO block to call the procedure*

> "DO runs an anonymous code block. We CALL the procedure and get back the new order ID via the OUT parameter."

> "In Spring Boot, you'd call stored procedures using `@Procedure` annotation or `SimpleJdbcCall`. But understanding what a stored procedure IS helps you work with legacy systems and DBA-provided procedures."

---

## Segment 9 — Sequences and Triggers (8 min)

*Navigate to Section 7 — Sequences*

> "Sequences are the mechanism behind SERIAL and BIGSERIAL. You can create and use them directly for custom ID patterns — like invoice numbers starting at 10000."

*Run the NEXTVAL calls*

> "Each call to NEXTVAL advances the counter. CURRVAL returns the current value. Sequences are guaranteed unique even with multiple concurrent sessions — the database atomically increments them."

*Navigate to Section 8 — Triggers*

> "Triggers fire automatically when something happens to a table. You can't forget to run them — the database enforces them unconditionally."

*Walk through the updated_at trigger*

> "The trigger function returns TRIGGER — a special return type. `NEW` is the row being inserted or updated. We set `NEW.updated_at = NOW()` and RETURN NEW. Every UPDATE to customers automatically refreshes updated_at. No application code needed."

*Run the UPDATE and check updated_at*

> "Update Alice's name, check the timestamp. It changed without us doing anything explicit."

*Walk through the audit trigger*

> "This is the real power of triggers: audit logs. Every INSERT, UPDATE, or DELETE on the orders table writes a row to orders_audit automatically. This is immutable history — even if someone updates orders directly in psql, the audit log captures it."

*Run the two UPDATE statements and SELECT from orders_audit*

> "Order 2 went from pending to confirmed to shipped. Two rows in the audit log — one for each change. The old and new status and total are both recorded."

> "**Watch out:** triggers add overhead to every DML operation. On write-heavy tables, too many triggers can hurt performance. Use them for important auditing and automation, not for everything."

---

## BLOCK C — Transactions, ACID, Optimization & Backup

*Open `03-transactions-and-optimization.sql`*

---

## Segment 10 — Transactions: BEGIN, COMMIT, ROLLBACK (8 min)

*Navigate to Section 1*

> "Let's talk about transactions. A transaction groups multiple SQL statements into a single unit. Either ALL statements succeed, or NONE do."

> "The bank transfer problem: debit Account A, credit Account B. If the server crashes between those two statements without transactions, Account A loses money but Account B never gains it. With transactions, the partial debit is rolled back automatically."

*Run Section 1a*

> "BEGIN starts the transaction. COMMIT permanently saves it. Any error between BEGIN and COMMIT causes the transaction to abort."

*Run Section 1b — ROLLBACK demo*

> "We set Alice's credit limit to 0 — then decide that's wrong and ROLLBACK. The SELECT after ROLLBACK shows the original value is restored."

*Navigate to Section 1c — SAVEPOINT*

> "SAVEPOINTs let you create checkpoints inside a transaction. If something fails, you can roll back to the savepoint without losing everything that came before it."

*Run the SAVEPOINT block*

> "We insert categories, set a savepoint, then try to insert a bad product — it fails. We roll back to the savepoint. The categories inserts survived. We insert a valid product and commit."

> "Run the verification queries. Categories are there, bad product is not, USB Hub is there."

---

## Segment 11 — ACID Properties (5 min)

*Navigate to Section 2*

> "ACID is the guarantee that every relational database provides. It's what separates a production database from a flat file."

> "Atomicity: all or nothing. The transaction either fully commits or fully rolls back."

> "Consistency: the database moves from one valid state to another valid state. Constraints are enforced. If an INSERT violates a FK, the entire transaction fails."

> "Isolation: concurrent transactions don't see each other's partial work. If two people are both checking out from your e-commerce site at the same time, they don't see each other's half-written orders."

> "Durability: once you receive COMMIT, the data is on disk. Even if the server crashes at that exact moment, it's preserved in the Write-Ahead Log."

*Run the consistency demo — FK violation*

> "Try to insert an order_item for order_id 999 which doesn't exist. FK violation. The entire transaction fails. The orders INSERT before it is also rolled back. Consistency enforced."

---

## Segment 12 — Isolation Levels (5 min)

*Navigate to Section 3 — the table*

> "Different isolation levels offer different trade-offs between correctness and performance. Higher isolation = fewer anomalies but more locking and potential deadlocks."

> "READ UNCOMMITTED: the most permissive — you can read dirty (uncommitted) data from other transactions. Almost never used."

> "READ COMMITTED: the PostgreSQL default. You only see committed data. If Transaction B commits a change while Transaction A is running, Transaction A's second read will see the new value. This is called a non-repeatable read."

> "REPEATABLE READ: guarantees that if you read a row twice within the same transaction, you get the same value both times. Useful for reports that read the same data multiple times."

> "SERIALIZABLE: the strictest level. The result is as if transactions were executed one at a time — no concurrency anomalies at all. Use this for financial calculations, inventory management, or anything where correctness is critical."

> "The default READ COMMITTED is the right choice for most web applications. Switch to SERIALIZABLE only when you have a specific correctness requirement."

---

## Segment 13 — EXPLAIN and Query Optimization (8 min)

*Navigate to Section 4*

*Run EXPLAIN on the products WHERE query*

> "EXPLAIN shows you what the database PLANS to do — without running the query. Look at the node type: Seq Scan means reading every row. If you see Seq Scan on a large table in a WHERE clause, you probably need an index."

*Run EXPLAIN ANALYZE*

> "EXPLAIN ANALYZE actually runs the query. Now you see real numbers: actual rows returned, actual time. Compare 'rows=X (estimated)' to 'actual rows=Y'. If these differ widely, your statistics are stale — run ANALYZE."

*Navigate to Section 5 — optimization techniques*

> "Let me show you the most common performance killers."

*Run Section 5a — function on WHERE column*

> "Using a function on a WHERE column breaks index usage. `WHERE UPPER(last_name) = 'SMITH'` applies UPPER() to every row — sequential scan. Fix: create an expression index on `UPPER(last_name)`."

*Create the expression index, then re-run EXPLAIN*

> "With the expression index, the same query now uses an index scan."

*Navigate to Section 5b — leading wildcard*

> "`LIKE '%laptop%'` — the leading percent means 'anything before laptop.' The index doesn't know where to start. Sequential scan. If you need full-text search, use PostgreSQL's tsvector and GIN index."

*Navigate to Section 5d — correlated subquery vs JOIN*

> "Correlated subquery: runs once per row in the outer query. With 100,000 orders, it executes 100,000 sub-queries. The equivalent JOIN + GROUP BY executes once. Run both EXPLAIN ANALYZE and look at the time difference on a larger dataset."

*Navigate to Section 5f — pagination*

> "Keyset pagination vs OFFSET. With OFFSET 1000, the database still reads the first 1000 rows to skip them. With `WHERE product_id > 1000`, it uses the PK index to jump directly to row 1001. For deep pages, keyset is dramatically faster."

---

## Segment 14 — Backup and Recovery Basics (4 min)

*Navigate to Section 6*

> "The last topic today is one students often overlook until it's too late: backups."

> "Read through the pg_dump commands with me. pg_dump creates a logical backup — a SQL script or compressed dump file of your database. You can restore it with pg_restore or psql."

> "The `-Fc` flag creates a custom compressed format. I recommend this over plain SQL because: it's smaller, it restores faster, and you can selectively restore individual tables."

> "Physical backups copy the raw data files. Faster for large databases but requires the same PostgreSQL version."

> "PITR — Point-in-Time Recovery. With WAL archiving enabled, you can restore the database to the exact state it was in at any timestamp. If someone runs `DELETE FROM orders` at 2:47pm, you can restore to 2:46pm."

> "Two concepts to know: RPO and RTO. RPO is how much data you can afford to lose — 'we backup every hour so we can lose up to 1 hour of data.' RTO is how long you can be down — 'we must recover within 4 hours.'"

*Run the database size query*

> "This query shows the size of every table and its indexes. Useful for capacity planning. If order_items is 10 GB and has 3 indexes, your backup file will be large — plan accordingly."

---

## Segment 15 — Day 22 Wrap-Up (3 min)

> "Let's do a final recap of everything we covered today."

> "Part 1: DDL — CREATE TABLE with all six constraint types. ALTER TABLE to evolve schema. DROP/TRUNCATE safely. SQL data types — use NUMERIC for money, TIMESTAMPTZ for timestamps, VARCHAR for strings. Normalization from a flat spreadsheet through 1NF, 2NF, and 3NF. ER diagrams as a design tool."

> "Part 2: DML — INSERT (single, multi-row, RETURNING, SELECT), UPDATE (calculated, with RETURNING, FROM), DELETE (row-targeted, subquery), UPSERT (ON CONFLICT). Indexes — B-Tree, partial, expression, and when NOT to index. Views as reusable query abstractions. Stored procedures and functions for encapsulated logic. Triggers for automated auditing and timestamps. Transactions and ACID. Isolation levels. EXPLAIN ANALYZE for reading query plans. Optimization techniques. Backup with pg_dump."

> "Tomorrow we connect this database knowledge to the backend: REST APIs and the tools that test them — Postman and Swagger."

---

## Instructor Q&A Prompts

1. **"What's the difference between a FUNCTION and a STORED PROCEDURE in PostgreSQL?"**  
   *(Expected: functions return a value and can be used in SELECT; procedures use OUT parameters and can manage transactions. Functions cannot COMMIT/ROLLBACK; procedures can.)*

2. **"Why does LIKE '%keyword%' prevent index usage but LIKE 'keyword%' can use it?"**  
   *(Expected: a leading % means 'anything before keyword' — the index can't narrow down a starting point. A trailing % means 'starts with keyword' — the B-tree can jump to the 'keyword' entry and scan forward.)*

3. **"If you set isolation level to SERIALIZABLE for all transactions, what's the downside?"**  
   *(Expected: more locking, more contention between concurrent transactions, higher chance of serialization failures that must be retried, lower throughput under high concurrency.)*

4. **"An e-commerce app is running slow on the order history page. What steps would you take to investigate?"**  
   *(Expected: run EXPLAIN ANALYZE on the query, look for Seq Scans on large tables, check if FK columns are indexed, look for correlated subqueries or SELECT *, check if statistics are up to date with ANALYZE.)*

5. **"Why is it important to test a database restore, not just the backup?"**  
   *(Expected: a corrupt backup file, a version mismatch, or a misconfigured restore procedure won't be discovered until you actually need it in a disaster — by which point it's too late. Regular restore drills catch these problems before they become crises.)*
