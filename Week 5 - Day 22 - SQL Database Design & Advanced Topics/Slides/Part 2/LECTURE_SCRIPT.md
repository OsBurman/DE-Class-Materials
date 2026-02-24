# Day 22 Part 2 — DML, Indexes, Views, Stored Procedures, Triggers, Transactions & Optimization
## Lecture Script — 60 Minutes

---

**Before class:** Have a SQL client open with the library schema from Part 1 already created. Have the bank transfer transaction example ready to type live. Pre-create an `accounts` table with two rows if possible — it makes the transaction demo dramatically clearer.

---

## [00:00–02:00] Opening — From Defining Structure to Working with Data

Welcome to Part 2. In Part 1 we designed the schema — we built the tables, applied constraints, normalized the structure, and drew the blueprint. Now we fill it with data and talk about everything that makes databases production-worthy: inserting and modifying records, making queries fast with indexes, reusing logic with views and stored procedures, automating behavior with triggers, protecting data integrity with transactions, and understanding how to read a query execution plan.

This is the half of the day that's closest to what you'll do every day as a backend developer. Let's get into it.

---

## [02:00–14:00] Slides 2–4: DML — INSERT, UPDATE, DELETE

### INSERT [Slide 2]

Let's put some data in our tables. `INSERT INTO customers (full_name, email) VALUES ('Alice Smith', 'alice@example.com')`. Run it.

First rule: always specify the column list. `INSERT INTO customers VALUES (...)` works — but if someone later adds or reorders columns in the table, your insert will put values in the wrong columns or fail with a type mismatch. When you name the columns, the insert is self-documenting and order-independent.

Second rule: batch your inserts when loading multiple rows. `INSERT INTO genres (genre_id, name) VALUES (1, 'Fiction'), (2, 'Non-Fiction'), (3, 'Science Fiction')` — one round trip to the database instead of three. For large data loads, this can be dramatically faster.

The `INSERT ... SELECT` pattern is extremely useful. Want to archive old orders to a history table? `INSERT INTO archived_orders SELECT * FROM orders WHERE order_date < '2023-01-01'`. The source is a SELECT — you can filter, join, and transform before inserting.

The UPSERT pattern — insert if the row doesn't exist, update if it does. MySQL uses `ON DUPLICATE KEY UPDATE`. PostgreSQL uses `ON CONFLICT DO UPDATE`. You'll use this constantly in data pipelines and APIs that receive the same record multiple times.

### UPDATE [Slide 3]

`UPDATE customers SET email = 'alice.new@example.com' WHERE customer_id = 1`. Simple.

The most important thing I will say about UPDATE: **always include a WHERE clause unless you explicitly intend to update every row**. I've seen developers run `UPDATE customers SET status = 'inactive'` — no WHERE clause — in a production database and immediately regret it. The database does exactly what you told it to do and asks no questions.

Best practice before any destructive UPDATE: run the equivalent SELECT first. `SELECT * FROM customers WHERE customer_id = 1` — confirm it returns exactly what you expect — then change SELECT to UPDATE. Same WHERE clause. This habit will save you at some point in your career.

You can update multiple columns in a single statement — `SET email = '...', updated_at = CURRENT_TIMESTAMP`. You can use arithmetic — `SET price = price * 0.9` applies a 10% discount to every matched row. You can UPDATE using a JOIN when you need to filter on a related table.

### DELETE [Slide 4]

`DELETE FROM customers WHERE customer_id = 42`. Same WHERE clause discipline as UPDATE.

`DELETE FROM orders WHERE status = 'cancelled' AND order_date < '2022-01-01'` — removes all old cancelled orders in one statement.

DELETE is safe in the sense that it's logged and can be rolled back if you're inside a transaction. TRUNCATE is faster but cannot be rolled back. DROP removes the structure. Use the right tool for the situation.

The soft delete pattern deserves a mention here. In production systems, you often don't want to physically delete records. Orders have legal and financial history. Users want to recover accidentally deleted items. Setting `is_deleted = TRUE` and `deleted_at = CURRENT_TIMESTAMP` instead of running DELETE gives you a safety net. The tradeoff: every query that should show active data needs `WHERE is_deleted = FALSE`. Some teams use a database view to handle this — we'll get to views shortly.

Foreign key constraints protect you from orphaned data. If `orders` has a FK to `customers`, you cannot delete a customer who still has orders. The database will reject it. This is exactly what you want — no orphaned records floating around.

---

## [14:00–26:00] Slides 5–6: Indexes

### How Indexes Work [Slide 5]

Let me ask you a question. You have a customers table with a million rows. You run `SELECT * FROM customers WHERE email = 'alice@example.com'`. Without any index on the email column, how does the database find Alice?

It reads every single row. Row one — not Alice. Row two — not Alice. All the way through one million rows. That's called a full table scan and it's marked as the worst-case scenario in any database performance discussion.

An index is a separate, ordered data structure that the database builds and maintains alongside your table. The most common type is a B-tree — a balanced tree structure where each comparison eliminates half of the remaining possibilities. Finding one value in a million rows takes roughly twenty comparisons instead of a million. The difference between O(n) and O(log n) is the difference between "slow" and "instant" at production scale.

The cost of indexes: every INSERT, UPDATE, and DELETE has to update not just the table, but every index on that table. Indexes also consume disk space. This is why you don't index every column — you index strategically.

### Index Types and Usage [Slide 6]

The syntax: `CREATE INDEX idx_customers_email ON customers(email)`. You choose a name — I recommend the pattern `idx_tablename_columnname` — the table, and the column.

Unique index: `CREATE UNIQUE INDEX idx_products_isbn ON products(isbn)`. This does double duty — it enforces uniqueness and provides fast lookups. It's equivalent to adding a UNIQUE constraint, but explicit as an index.

Composite index: `CREATE INDEX idx_customers_name ON customers(last_name, first_name)`. This covers queries that filter on `last_name` alone or on `last_name` AND `first_name` together. It does NOT help a query that filters only on `first_name` — because the tree is sorted by last_name first. The leftmost column rule: a composite index is only usable if the query filters on the leftmost column.

When should you index? FK columns — always. Databases don't automatically index FK columns in MySQL, which means every JOIN on that FK without an index becomes a full scan of the child table. Index them. Columns in frequent WHERE clauses on large tables — yes. Columns used in ORDER BY for large result sets — often yes, because the index is already sorted.

When not to index: small tables — the optimizer often skips the index anyway because a full scan is faster with only a few rows. Columns with very low cardinality — if a boolean column has only two values, an index doesn't help much because half the table matches either value. Columns rarely used in queries — the write overhead isn't worth it.

---

## [26:00–34:00] Slide 7: Views

### Views [Slide 7]

A view is a saved SELECT statement with a name. You create it once and then treat it like a table. It contains no data — when you query a view, the database executes the underlying SELECT at that moment.

`CREATE VIEW active_orders_detail AS SELECT o.order_id, o.order_date, c.full_name, c.email, o.total_amount FROM orders o JOIN customers c ON o.customer_id = c.customer_id WHERE o.status = 'active'`. Now instead of writing that JOIN every time, every part of the application can just do `SELECT * FROM active_orders_detail WHERE total_amount > 100`.

Four use cases worth knowing. First: simplifying complex queries. If your application has a six-table join that marketing uses to generate a report every day, put it in a view. Everyone on the team can use it without understanding the join logic.

Second: security. You can grant a user permission to SELECT from a view but not from the underlying tables. The view exposes only the columns you want — no salary data, no payment info.

Third: consistent business logic. What does "active customer" mean? Orders in the last 90 days? Not marked as deleted? If that definition lives in one place — a view — then a business rule change means updating one view definition instead of hunting down twenty queries.

Fourth: backward compatibility. If you rename a table, you can create a view with the old name that points to the new table. Old queries keep working without modification.

One note on materialized views — available in PostgreSQL and Oracle. A regular view re-executes the query every time you select from it. A materialized view stores the result physically. It's fast to read but must be manually refreshed when the underlying data changes. Use it for expensive aggregation queries that are read-heavy and don't need real-time freshness.

---

## [34:00–44:00] Slides 8–9: Stored Procedures, Triggers, Sequences

### Stored Procedures [Slide 8]

A stored procedure is like a function that lives in the database — a named, reusable block of SQL plus procedural logic (variables, loops, conditionals) that you call by name.

Look at the `place_order` procedure on the slide. It takes a customer ID, product ID, and quantity. Inside, it looks up the current price, creates the order, captures the new order ID, and adds the order line item — all in one operation. Application code calls `CALL place_order(1, 42, 2, @new_order_id)` and the entire business operation happens in one round trip.

The advantages: complex business logic lives in one place. If the order-creation logic changes, you update the procedure — not every application that calls it. Stored procedures also run inside the database process, so there's no network round trip between each SQL statement. For complex operations with many small queries, this matters.

The disadvantages: stored procedures are harder to version control, test, and debug than application code. They can become a "hidden layer" of business logic that new developers don't know exists. Modern practice in many teams is to keep business logic in the application layer (your Java/Spring service layer) and use the database for data storage and constraints. Stored procedures are still common in enterprise environments and data pipelines, so you need to know how to read and write them.

The difference between a procedure and a function: a function returns a single value and can be used inline in a SQL expression. A procedure uses `OUT` parameters for outputs and is called with `CALL`. You can't use a procedure in a WHERE clause the way you can a function.

### Triggers [Slide 9]

A trigger is code that fires automatically when something happens to a table — a BEFORE or AFTER INSERT, UPDATE, or DELETE.

The most common use: automatically updating `updated_at`. Instead of remembering to set `updated_at = CURRENT_TIMESTAMP` in every UPDATE statement, one trigger handles it. It runs before every UPDATE on the products table and sets `NEW.updated_at` — `NEW` is a reference to the row being written.

The second example is an audit log — recording every deletion from the orders table. The trigger inserts a row into `orders_audit` after each DELETE, capturing the deleted data via `OLD`. `OLD` references the row that existed before the change. Now even after a row is deleted, you have a permanent record of it.

When to use triggers: automatic timestamp updates, audit logging, enforcing cross-table business rules that are too complex for a CHECK constraint. When not to use them: complex business logic — triggers that silently modify data or call other procedures are hard to debug and can cause surprising behavior. Keep triggers simple.

Sequences and AUTO_INCREMENT are what generate your primary key values automatically. In MySQL, you declare `INT AUTO_INCREMENT PRIMARY KEY` and every insert gets the next integer. PostgreSQL uses `SERIAL` or the newer `GENERATED ALWAYS AS IDENTITY` syntax. Under the hood, these use a sequence object. You can create explicit sequences in PostgreSQL when you need shared counters or non-consecutive IDs.

---

## [44:00–55:00] Slides 10–13: Transactions, ACID, Isolation

### Transactions [Slide 10]

A transaction is a group of SQL statements that succeed together or fail together. Either the whole thing commits, or none of it does.

The classic example is a bank transfer. Let me type this live. `START TRANSACTION`. `UPDATE accounts SET balance = balance - 500 WHERE account_id = 101`. Now — what if the database crashes right here? Account 101 lost $500 and account 202 never got it. Money vanished. That's exactly what transactions prevent.

We continue: `UPDATE accounts SET balance = balance + 500 WHERE account_id = 202`. Both updates have run. Everything looks good. `COMMIT`. Both changes are now permanent.

If anything goes wrong — a constraint violation, an error, the server crashes — we call `ROLLBACK` and both updates are undone. The accounts return to exactly the state they were in before the transaction started.

The e-commerce example is more realistic for what you'll actually build. Inside a transaction: create the order record, get the new order ID, add the order line, deduct inventory. If the inventory check shows we'd go negative, we `ROLLBACK` — everything is undone. The customer never sees a half-completed order. If everything succeeds, `COMMIT`.

`SAVEPOINT` is a named checkpoint inside a transaction. You can roll back to a savepoint without rolling back the entire transaction. Useful when you have multiple independent operations and you only want to undo the later ones if they fail.

### ACID Properties [Slide 11]

ACID is the four-property guarantee that a transactional database makes. You will be asked about this in every database interview, so let's nail it.

**Atomicity:** The transaction is atomic — all or nothing. Either every operation in the transaction completes, or none of them do. No half-completed transactions.

**Consistency:** A transaction takes the database from one valid state to another valid state. All constraints — PRIMARY KEY, FOREIGN KEY, CHECK, NOT NULL — are satisfied before and after. Business rules are maintained.

**Isolation:** Concurrent transactions don't see each other's in-progress changes. If two users are both modifying the same data simultaneously, each one sees a consistent view as if the other doesn't exist.

**Durability:** Once a transaction commits, it's permanent. Committed data survives crashes, power failures, and restarts. The database achieves this through Write-Ahead Logging — it writes to a durable log before touching actual data pages, so it can always replay committed transactions after a crash.

The memory device on the slide: bank wire transfer. It either fully arrives (Atomicity). Balances stay valid (Consistency). Other users don't see the money in transit (Isolation). Once confirmed, it's permanently there (Durability).

### Isolation Levels [Slide 12]

Isolation has degrees — and higher isolation has a performance cost because the database has to do more work preventing concurrent conflicts.

Three problems that can occur when transactions overlap:

**Dirty read:** Transaction A reads data written by Transaction B before B commits. If B rolls back, A was reading data that never officially existed. Most isolation levels prevent this.

**Non-repeatable read:** A reads a row, B updates that row and commits, A reads the same row again and gets a different value within the same transaction. The row "changed" between two reads.

**Phantom read:** A runs a query that returns five rows, B inserts a new row that matches A's query, A re-runs the query and gets six rows. New rows "appeared" within A's transaction.

Four isolation levels from lowest to highest protection. `READ UNCOMMITTED` — dirty reads allowed, nothing is protected, fastest. Nobody should use this in a real application.

`READ COMMITTED` — no dirty reads. You only see committed data. But non-repeatable reads and phantoms are still possible. This is the default in PostgreSQL and SQL Server, and it's the right choice for most applications.

`REPEATABLE READ` — no dirty reads, no non-repeatable reads. Once you read a row in a transaction, that row won't change if you read it again. MySQL's default. Phantoms are still theoretically possible (MySQL's implementation actually prevents them with gap locks).

`SERIALIZABLE` — complete isolation. Transactions behave as if they ran one after another, not concurrently. Zero concurrency anomalies. But it's the slowest because transactions can block each other. Use it only when absolute read consistency is critical — financial reconciliation, ticket inventory where overselling is catastrophic.

For most web applications: `READ COMMITTED` is the right default.

---

## [55:00–60:00] Slides 13–17: Query Optimization and Summary

### Query Optimization [Slides 13–14]

`EXPLAIN` is your most powerful diagnostic tool for slow queries. Put `EXPLAIN` in front of any SELECT and the database tells you its execution plan — which indexes it's using, how many rows it's estimating to examine, and what operations it's performing.

The key column to watch in EXPLAIN output is `type`. `const` and `eq_ref` are the best — the database is doing a single PK or unique index lookup. `ref` and `range` are good — using an index, reading a range. `ALL` is the red flag — full table scan.

Four quick optimization rules. One: don't apply functions to indexed columns in WHERE. `WHERE YEAR(order_date) = 2024` defeats the index on `order_date`. Use a range instead: `WHERE order_date >= '2024-01-01' AND order_date < '2025-01-01'`.

Two: avoid leading wildcards. `WHERE name LIKE '%book%'` can't use an index. If you need full-text search, use a FULLTEXT index.

Three: don't `SELECT *` in production queries. It returns more data than you need, uses more memory, and is fragile — if the schema changes, you might get unexpected columns.

Four: replace correlated subqueries with JOINs. A correlated subquery runs once per outer row. A JOIN with GROUP BY runs once total. On large tables, this is a massive difference.

### Backup and Summary [Slides 15–17]

One minute on backups because it often doesn't get enough attention until something goes wrong. `mysqldump -u root -p library > backup.sql` — this outputs a file of SQL statements that recreate the database from scratch. You can restore it with `mysql < backup.sql`. PostgreSQL uses `pg_dump` and `pg_restore`.

The non-negotiable rule: test your restores. A backup you've never verified restoring from is not a backup — it's a hope. Automate backups, store them off-site, and periodically run a restore drill.

Here's the full Day 22 summary. Part 1: DDL — CREATE, ALTER, DROP. Six constraints. Normalization. ER diagrams. Part 2: DML — INSERT, UPDATE, DELETE always with WHERE. Indexes make reads fast at the cost of write overhead. Views save reusable queries. Stored procedures encapsulate complex operations. Triggers automate reactions. Transactions are all-or-nothing units of work. ACID — Atomicity, Consistency, Isolation, Durability. Isolation levels trade performance for correctness. EXPLAIN reveals the execution plan. Optimize by using indexes correctly and avoiding anti-patterns.

Tomorrow is Day 23 — REST and API Tools. We take everything we've built in the last two days and think about how a web application exposes and consumes data through HTTP APIs. See you then.

---

*End of Part 2 — 60 minutes*
