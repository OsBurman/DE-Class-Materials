# Day 21 Part 2 — Aggregates, JOINs, Subqueries & CASE
## Lecture Script — 60 Minutes

---

**Before class:** Have the reference schema on the board or first slide. Confirm students can see their SQL tool from Part 1. Have the four-table query from Slide 10 ready to demo live.

---

## [00:00–03:00] Opening — Why Queries Get Interesting Here

Welcome back. Take a breath — Part 1 was a lot of fundamentals and you survived it. Now we get to the part that actually makes SQL powerful as a tool in software engineering.

In Part 1, we learned to retrieve and filter data from a single table. That covers maybe thirty percent of what you'll do with SQL in a real project. The other seventy percent lives in what we're covering right now: aggregating data, joining multiple tables together, and building logic into your queries.

Here's what I want you to hold in your head as a mental model for Part 2. Most real-world data is spread across multiple tables. A customer is in one table. Their orders are in another. The products on those orders are in a third. If you can only query one table at a time, you can answer simple questions. But if you can join those tables together, you can answer questions like "show me our top ten customers by total revenue, but only customers who signed up in the last six months, along with every product category they've bought from." That is an actual business question someone will ask you to answer. We're going to have the tools to do that by the end of this session.

The topics today build on each other: aggregates lead to GROUP BY, GROUP BY leads to HAVING, then we do JOINs which are the centerpiece of the session, then subqueries, and we finish with CASE statements for conditional logic. Stick with me through the JOIN section — it's the most important and also where people hit the most confusion. I'll walk it carefully.

Let's go.

---

## [03:00–14:00] Slides 2–4: Aggregate Functions + GROUP BY + HAVING

### Aggregate Functions [Slide 2]

Everything in Part 1 was about selecting and filtering rows. Aggregate functions change the game — instead of returning one row per matching record, they collapse multiple rows into a summary.

The core five are: `COUNT`, `SUM`, `AVG`, `MIN`, and `MAX`.

Let me show you the most important distinctions right away. Slide up.

`COUNT(*)` — with the asterisk — counts all rows, including rows where columns are NULL. `COUNT(column_name)` counts only rows where that specific column is not NULL. These will give you different numbers on the same table if any values are missing.

`COUNT(DISTINCT column_name)` — this is the one students forget about — counts how many unique values appear in that column. "How many different countries do our customers come from?" — that's a `COUNT(DISTINCT country)` question.

`SUM`, `AVG`, `MIN`, `MAX` — all of these ignore NULLs. If you have ten employees and two have NULL salaries, `AVG(salary)` calculates the average of the eight non-null values. That might not be what you want. Always check your NULLs when working with aggregates.

Let me run through the practical queries on the slide. Look at the revenue summary query — notice I'm doing all five aggregates in one SELECT statement and giving each one an alias. One query, five numbers. That's efficient. You're not running five separate queries; you're doing one pass over the table and getting all your summary stats at once.

Go ahead and try `SELECT COUNT(*), SUM(total_amount), AVG(total_amount) FROM orders` in your SQL tool. You should get a single row back with three values. Notice it's just one row — all those orders collapsed into a single summary row.

### GROUP BY [Slide 3]

Now here's where it gets more interesting. `COUNT(*)` on the whole table gives you one number. But what if you want the count broken out by country? That's `GROUP BY`.

`GROUP BY` says: instead of collapsing all rows into one, collapse rows into groups — one per unique value of the column I specify — and then apply my aggregate to each group separately.

Look at the first query: `SELECT country, COUNT(*) FROM customers GROUP BY country ORDER BY customer_count DESC`. The result isn't one row — it's one row per country. Each row shows that country's name and how many customers belong to it.

Here's the rule you need to tattoo somewhere: **every column in your SELECT that is NOT inside an aggregate function must appear in your GROUP BY clause.** If you select `country` and `COUNT(*)`, you GROUP BY `country`. If you select `country, city, COUNT(*)`, you GROUP BY `country, city`.

Why? Because if you have ten customers in New York and you're grouping by country, you collapse those ten rows into one "USA" row. The `COUNT(*)` aggregate makes sense — it tells you how many got collapsed. But `city` doesn't make sense at the group level unless you also group by it, because which city would SQL show for the USA group? New York? Los Angeles? It can't know. Most databases will give you an error. MySQL historically was more permissive about this — it would pick an arbitrary value — which is arguably worse because it fails silently.

Take a minute and try: `SELECT status, COUNT(*), SUM(total_amount) FROM orders GROUP BY status`. You should see one row per status value. Good.

### HAVING [Slide 4]

`GROUP BY` by itself gives you groups for everything. What if you only want to see countries that have more than a hundred customers? You might think to use `WHERE` — and that's the wrong instinct but it's a natural one.

`WHERE` runs before `GROUP BY`. It filters rows before they get grouped. At the time `WHERE` runs, there are no groups yet — there are no counts yet. So you can't filter on `COUNT(*)` inside a WHERE clause. You'll get a syntax error saying the aggregate function isn't allowed there.

`HAVING` is the solution. It runs after GROUP BY, after the aggregate functions have been computed, and it filters groups. Think of `WHERE` as "row filter before grouping" and `HAVING` as "group filter after grouping."

Look at the slide: `HAVING COUNT(*) > 100`. This says "after counting rows per country, keep only the groups where the count exceeded one hundred."

The trick query at the bottom of the slide combines both. `WHERE salary > 50000` filters individual employee rows before grouping. Then `HAVING COUNT(*) >= 3 AND AVG(salary) > 75000` filters the resulting department groups. Both clauses together. That's a clean, efficient query.

Common exam question, common interview question: "What's the difference between WHERE and HAVING?" WHERE filters rows before grouping. HAVING filters groups after grouping. Say it to yourself once. Good.

---

## [14:00–23:00] Slides 5–6: Table Relationships + INNER JOIN

### Table Relationships [Slide 5]

Before we dive into joins, we need thirty seconds on table relationships, because a join is just the mechanism for navigating a relationship that already exists in your schema.

We have three relationship types. One-to-many is by far the most common. One customer can have many orders. One category can contain many products. The "many" side holds the foreign key. So `orders.customer_id` points to `customers.customer_id`. Every order record knows which customer it belongs to.

Many-to-many relationships need a junction table — or bridge table. One order can contain many products, and one product can appear in many orders. You can't represent that directly. So you have `order_items` in the middle, with a foreign key to both `orders` and `products`. Each row in `order_items` is one product on one order.

One-to-one is less common — used when you want to split a wide table or isolate sensitive data like payment information.

The reason this matters for joins: when you join two tables, you join them on the foreign key column. The ON clause of your JOIN is always the foreign key relationship. Every JOIN you write in this session is connecting two columns that have this FK-to-PK relationship.

### INNER JOIN [Slide 6]

Let's write our first join. An INNER JOIN returns rows that have a matching record on both sides.

Imagine placing two tables side by side. `orders` on the left, `customers` on the right. Each order has a `customer_id`. You're asking: for each order, find the customer with that ID. If a match exists on both sides, include it. If not — an order with a customer ID that doesn't exist in the customers table, or a customer with no orders — it's excluded.

Here's the syntax: `FROM orders o JOIN customers c ON o.customer_id = c.customer_id`. Let me point out the pieces. After FROM, you list one table. Then you say JOIN followed by the second table. Then ON, followed by the join condition — which columns are you matching.

The short aliases `o` and `c` are important here. With one table you can use the full table name. With two or more tables, you'll be typing those names constantly in your column list, and aliases save you from repetitive typing. Use them.

Notice I wrote just `JOIN`, not `INNER JOIN`. The keyword INNER is optional — plain `JOIN` is always an INNER JOIN. You'll see both in the wild.

Try this right now: `SELECT o.order_id, o.total_amount, c.first_name, c.last_name FROM orders o JOIN customers c ON o.customer_id = c.customer_id ORDER BY o.order_date DESC LIMIT 10`. Run it. You should see orders with customer names next to them — data that lives in two separate tables, pulled together in one result.

This is the pattern you'll use more than anything else in your SQL career. Learn it well.

---

## [23:00–34:00] Slides 7–9: LEFT JOIN, FULL OUTER JOIN, CROSS & Self JOIN

### LEFT JOIN [Slide 7]

INNER JOIN only returns matched rows. But sometimes you want all rows from one table, and the matching rows from the other table if they exist — and NULLs if they don't.

That's a LEFT JOIN. "Give me everything from the left table. For each row, look in the right table. If a match exists, fill in those columns. If no match exists, fill those columns with NULL."

The most important pattern to know: **finding rows that have no match**. Look at the query: customers LEFT JOIN orders, then `WHERE o.order_id IS NULL`. The logic is: after the LEFT JOIN, customers with no orders will have NULL in all the order columns. We then filter for those NULLs. Result: a list of customers who have never placed an order. Marketing would love this list.

Same idea: `products LEFT JOIN order_items WHERE oi.item_id IS NULL` gives you products that have never been ordered. Maybe it's time to discount them or pull them from inventory.

RIGHT JOIN is the mirror image — all rows from the right table, matching rows from the left. In practice, almost no one uses RIGHT JOIN. The same result is always achievable by swapping your table order and using LEFT JOIN. Being consistent with LEFT JOIN makes code easier to read.

### FULL OUTER JOIN [Slide 8]

FULL OUTER JOIN combines LEFT and RIGHT — you get all rows from both tables, NULLs on either side where no match exists.

It's used less often than LEFT JOIN, but it's valuable when you're auditing data quality. "Show me everything — every customer and every order — and I want to see the orphans on both sides." Orphaned orders — orders with no valid customer ID — would show NULL in the customer columns. Customers with no orders would show NULL in the order columns.

One important caveat on the slide: MySQL doesn't support FULL OUTER JOIN natively. The workaround is a LEFT JOIN combined with a RIGHT JOIN using UNION. If you're working in PostgreSQL or SQL Server, it works natively.

I want you to have this JOIN comparison table in your memory. INNER: matched rows only. LEFT: all left + matched right. RIGHT: all right + matched left. FULL OUTER: everything from both. Let's move.

### CROSS JOIN and Self JOIN [Slide 9]

Quick ones. CROSS JOIN returns every combination of every row in table A with every row in table B. Five rows times five rows is twenty-five rows. A thousand rows times a thousand rows is a million rows — be careful. Use case: generating all combinations for a product configurator, all matchups in a tournament, all date-interval pairs for a schedule.

Self JOIN is when a table joins to itself, using two different aliases. The classic case is a hierarchy — employees with a manager ID column that points back to the employee ID column in the same table. You write `FROM employees e LEFT JOIN employees m ON e.manager_id = m.employee_id`. Now `e` represents the employee and `m` represents their manager. One table, two roles, two aliases. LEFT JOIN because the top of the org chart has no manager — their manager_id is NULL, and an INNER JOIN would drop them from the result.

If you've ever worked with a family tree, a comment-reply thread, or a category hierarchy with parent IDs, that's all a self join.

---

## [34:00–44:00] Slide 10: Multiple JOINs

### Chaining Multiple JOINs [Slide 10]

Now let's put it all together with multiple joins in a single query. This is the most powerful pattern in SQL and also where students most often go wrong. Let me slow down here.

The strategy: treat each JOIN as one step in following a chain of relationships. You start from one table, and each JOIN is saying "now navigate to this next table through this column relationship."

Look at the four-table query on the slide. We start from `orders`. We JOIN `customers` to get the customer's name — connecting on `customer_id`. We JOIN `order_items` to get the line items on each order — connecting on `order_id`. We JOIN `products` to get the product name for each line item — connecting on `product_id`.

Notice the pattern: every JOIN adds one more table, and each `ON` clause specifies the two column references — one from a table already in the query, one from the table being added.

Let me count the columns in the SELECT: `order_id` is `o.order_id`. Customer name is constructed from `c.first_name` and `c.last_name`. Product is `p.name`. Quantity and unit price come from `oi`. Line total is a calculated expression: `oi.quantity * oi.unit_price`. All of those are qualified with their table alias. With four tables in a query, you always qualify columns. If you write just `name`, SQL doesn't know if you mean the product name or — in a hypothetical schema — a category name. Be explicit.

Please follow along and type this query yourself. I'll paste the schema reminder on the screen while you do. `FROM orders o JOIN customers c ON o.customer_id = c.customer_id JOIN order_items oi ON o.order_id = oi.order_id JOIN products p ON oi.product_id = p.product_id`. Add a few SELECT columns and run it.

When you get your first multi-table join working, there's a real satisfaction to it. You're pulling coherent information from four separate tables in one clean statement. That's a real engineering skill.

The best practices on the slide: use aliases consistently, qualify all column names with three or more tables, arrange your JOINs in logical order following the relationships in the data.

---

## [44:00–52:00] Slides 11–13: Subqueries, Correlated Subqueries, EXISTS

### Subqueries [Slide 11]

A subquery is a SELECT statement nested inside another SELECT. The inner query runs first, and its result is used by the outer query.

Three placements. First: in the WHERE clause. The classic use case — "products priced above the average." You can't write `WHERE price > AVG(price)` — you can't use an aggregate directly in a WHERE clause. But you can write `WHERE price > (SELECT AVG(price) FROM products)`. The inner query runs first, returns a single number — say, 49.99 — and then the outer query runs with `WHERE price > 49.99`. That's a single-row subquery, returning exactly one value.

Multi-row subquery: use IN with a subquery that returns multiple rows. "Customers who have placed at least one order" — inner query: `SELECT DISTINCT customer_id FROM orders`. Outer query: `WHERE customer_id IN (that list)`. The inner query returns a list of IDs and the outer query filters to only those.

"Products never ordered" — `WHERE product_id NOT IN (SELECT DISTINCT product_id FROM order_items)`. Careful: if `order_items` is empty, this NOT IN returns nothing because it produces an empty list. And if `product_id` is NULL in any row of `order_items`... we get the NOT IN NULL gotcha from Part 1 again. For NOT IN, always add `WHERE product_id IS NOT NULL` inside the subquery to be safe.

Third placement: in the FROM clause as a "derived table." You write a subquery and alias it like a table, then select from that alias. Look at the average-of-averages example — you can't do `AVG(COUNT(*))` directly. But you can first compute counts per customer in an inner query, wrap it in a FROM clause, and then average those counts in the outer query.

### Correlated Subqueries [Slide 12]

Correlated subqueries reference the outer query. They're more powerful but need care.

The example: employees who earn above the average salary in their own department. If you had five departments, you'd need five different averages to compare against. A correlated subquery handles this: the inner query references `e.department` from the outer row, which means the inner query re-runs with a different department value for each outer row.

The arrow-diagram on the slide shows the execution: for each employee row in the outer query, run the inner query filtering to that employee's department, get the average, and compare.

The performance warning is important. Correlated subqueries are clear to read but they execute once per row. On a table with fifty thousand employees, the inner query runs fifty thousand times. An equivalent solution using `JOIN` with `GROUP BY` is almost always faster for large tables. Prefer JOINs for performance-sensitive code. But for understanding data or writing a quick report, correlated subqueries are perfectly fine and very readable.

### EXISTS [Slide 13]

EXISTS is cleaner than IN for existence checks, especially when you're dealing with correlated logic.

`WHERE EXISTS (subquery)` is true if the subquery returns at least one row. It doesn't care about what the values are — just whether anything comes back. That's why you'll often see `SELECT 1` inside an EXISTS — the value one is a placeholder. You could write `SELECT *` or `SELECT 47` — it doesn't matter; EXISTS only checks for presence.

Look at the critical comparison table. NOT EXISTS is safer than NOT IN when NULLs might be in your subquery result. If your `NOT IN` subquery returns any NULL, the entire condition fails silently and returns zero rows — one of the most confusing bugs in SQL. NOT EXISTS handles NULLs correctly. In any new code you write, prefer NOT EXISTS over NOT IN.

---

## [52:00–60:00] Slides 14–17: CASE, Calculated Columns, Summary

### CASE Statements [Slides 14–15]

CASE is SQL's version of if-else. Two forms.

Simple CASE compares one column to a list of values: `CASE status WHEN 'pending' THEN ... WHEN 'shipped' THEN ... ELSE ... END`. Clean when you're mapping a column's values to labels.

Searched CASE evaluates any boolean condition: `CASE WHEN total_amount >= 500 THEN 'High Value' WHEN total_amount >= 100 THEN 'Standard' ELSE 'Small' END`. SQL evaluates conditions top to bottom and returns the first one that's true — so order your conditions from most specific to most general.

The advanced pattern on Slide 15 is conditional aggregation — `COUNT(CASE WHEN status = 'completed' THEN 1 END)`. When the condition is false, CASE returns NULL. And `COUNT` ignores NULLs. So this only counts rows where status is 'completed'. Pair this with `GROUP BY EXTRACT(MONTH FROM order_date)` and you get a pivot table — one row per month, separate columns for completed, cancelled, and pending counts. This is something SQL developers use constantly for dashboard reporting queries.

### Calculated Columns [Slide 16]

These are quick to show and often overlooked. You can do math directly in your SELECT. `quantity * unit_price` in the ORDER_ITEMS query gives you a line total without storing that column anywhere — it's computed on the fly. `price * 1.08` for price with tax. Always alias your calculated columns so the result has a clear name.

String functions — `UPPER`, `LOWER`, `LENGTH`, `SUBSTRING`, `TRIM` — are available in all major databases though the names vary slightly. Date functions like `EXTRACT`, `DATE_TRUNC`, `CURRENT_DATE` do the same. These are the building blocks of data transformation directly in SQL.

### Summary [Slide 17]

Let's recap the whole day.

Part 1: SELECT anatomy, execution order, WHERE filtering, logical operators, LIKE wildcards, IN, BETWEEN, ORDER BY, LIMIT/OFFSET, DISTINCT, NULL handling.

Part 2: Aggregates — COUNT, SUM, AVG, MIN, MAX. GROUP BY to group rows. HAVING to filter groups — after aggregation, not before. Table relationships — one-to-many, many-to-many with junction tables. JOIN types: INNER for matched-only, LEFT for all-from-left, FULL OUTER for everything, CROSS for combinations, self for hierarchies. Multiple JOINs chained. Subqueries in WHERE and FROM. Correlated subqueries. EXISTS and NOT EXISTS. CASE for conditional logic. Calculated columns.

That's a full day of SQL. Day 22 tomorrow shifts from reading data to defining schemas and writing data — CREATE TABLE, ALTER TABLE, INSERT, UPDATE, DELETE, indexes, and transactions. We go from querying data that exists to building the structures that hold data in the first place.

Good work today. Practice a few of these JOIN queries tonight — join two or three tables from our schema and make sure you can read the results without confusion. See you tomorrow.

---

*End of Part 2 — 60 minutes*
