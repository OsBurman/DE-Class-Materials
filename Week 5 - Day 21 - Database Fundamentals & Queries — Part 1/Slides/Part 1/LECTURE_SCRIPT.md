# Day 21 Part 1 — Database Fundamentals & SQL Basics
## Lecture Script

**Total time: 60 minutes**
**Slides: 16**
**Pace: ~165 words/minute**

---

### [00:00–03:00] Slide 1 — Opening

Good morning everyone. Welcome to Week 5. We've spent four weeks building full-stack web applications — frontend with React and Angular, state management, routing, HTTP calls, testing. Now we're going to the other side of all those HTTP calls: the database that stores and serves the data your applications need.

Today is Day 21, and we have two full hours of SQL. Part 1 covers the fundamentals — what a relational database is, and how to read data from it using SELECT queries with filtering, sorting, and pagination. Part 2 this afternoon gets into aggregation, JOINs, subqueries, and CASE statements. These two sessions together cover everything you need to query a real-world database effectively.

I want to set a tone for this week: SQL is one of the most durable skills in software engineering. Frameworks come and go. Languages go in and out of fashion. SQL has been the language of data since the 1970s and is not going anywhere. If you become genuinely comfortable with SQL, you will use it for the rest of your career. Let's build that comfort today.

---

### [03:00–11:00] Slides 2–3 — RDBMS Concepts and SQL Overview

**[Slide 2]**

Let's start with what a relational database actually is. The term "relational" comes from relational algebra — a mathematical model from the 1970s — but the practical meaning is simpler than that. A relational database stores data in tables, and tables can be related to each other through keys.

A table is a grid. Rows are records — one row is one thing: one customer, one order, one product. Columns are attributes — name, price, date created. Every table has a primary key: a column whose value uniquely identifies each row. If I know a customer's `customer_id`, I can look up exactly that customer. No two rows can share the same primary key.

Tables connect to each other through foreign keys. The orders table has a `customer_id` column. That column doesn't just hold a number — it holds a reference to a row in the customers table. When I see `customer_id = 7` in an order, it means "this order belongs to the customer with `customer_id = 7`." That connection is a relationship.

Look at the schema we'll be using all day. We have customers, products, categories, orders, order_items, and employees. This is a simplified e-commerce database, and it gives us everything we need to demonstrate every SQL concept today.

Popular RDBMS systems: PostgreSQL, MySQL/MariaDB, Oracle, SQL Server, SQLite. The SQL we write today is standard SQL — it runs on all of them. There are minor dialect differences, mostly in date functions and pagination syntax, and I'll call those out when they come up.

**[Slide 3]**

SQL has four sub-languages and I want you to know their names because they come up in conversation and on job interviews.

DQL — Data Query Language. The `SELECT` statement. Reading data. This is the entire focus of Part 1, and reading is honestly what you'll do most of the time as a developer — querying data for your application.

DDL — Data Definition Language. Creating and changing the structure of the database. `CREATE TABLE`, `ALTER TABLE`, `DROP TABLE`. This is Day 22 material.

DML — Data Manipulation Language. Writing data — `INSERT`, `UPDATE`, `DELETE`. Also Day 22.

DCL — Data Control Language. Permissions — `GRANT` access, `REVOKE` access. Usually handled by a DBA, not a developer, in most shops.

One style note: I write SQL keywords in uppercase and column/table names in lowercase. This is a convention — the database doesn't care, SQL keywords are case-insensitive. But the convention makes queries much easier to read when you come back to them later, and it's what you'll see in documentation and professional codebases.

---

### [11:00–19:00] Slides 4–5 — SELECT Structure and Columns

**[Slide 4]**

The anatomy of a SELECT statement. This slide is important enough to spend real time on, because understanding the structure — and specifically the execution order — will save you from confusing bugs later.

The clauses in writing order: SELECT, FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT. But the database doesn't execute them in that order. The execution order is different: FROM first — which table? WHERE second — which rows survive? GROUP BY third — collapse into groups. HAVING fourth — which groups survive? SELECT fifth — which columns do we return? ORDER BY sixth — how to sort? LIMIT seventh — how many rows?

Why does this matter? Because people often ask: "Why can't I use my SELECT alias in the WHERE clause?" And the answer is: WHERE runs before SELECT. When WHERE is evaluated, the alias doesn't exist yet. You'll see this come up when we do calculated columns.

For today, the relevant clauses are FROM, WHERE, ORDER BY, and LIMIT. GROUP BY and HAVING are Part 2.

**[Slide 5]**

SELECT — choosing what columns to return. `SELECT *` gives you all columns. It's great for quick exploration — running a query in a database tool to see what's in a table. But in application code, avoid it. It fetches data you don't need, it can break if someone adds or reorders columns, and it makes the query harder to understand at a glance.

Always name your columns. `SELECT first_name, last_name, email FROM customers`.

Column aliases with `AS`. You can rename any column in the output: `SELECT first_name AS "First Name"`. This is especially useful for calculated expressions — `SELECT price * 1.08 AS price_with_tax`. Without the alias, the column header in the result would literally be `price * 1.08`, which is ugly and hard to work with in application code.

Expressions. You can do math directly in a SELECT: `price * 0.9` for a discounted price. You can concatenate strings: `CONCAT(first_name, ' ', last_name) AS full_name`. These are computed columns — they don't exist in the table, they're calculated on the fly for each row.

---

### [19:00–29:00] Slides 6–7 — WHERE and Logical Operators

**[Slide 6]**

The WHERE clause filters which rows come back. Without WHERE, you get every row in the table. With WHERE, only rows that satisfy your condition are returned.

The comparison operators are exactly what you'd expect from any programming language: equals, not equals, less than, greater than, less-than-or-equal, greater-than-or-equal. One small SQL-ism: not equal can be written as either `!=` or `<>`. You'll see both in the wild.

String comparisons use single quotes — always single quotes in SQL, never double quotes. Most databases are case-sensitive for string comparisons, so `'USA'` and `'usa'` are different values unless you lowercase both sides.

The NULL operators are important: `IS NULL` and `IS NOT NULL`. This trips up a lot of new SQL writers. You might instinctively write `WHERE email = NULL` — but in SQL, comparing anything to NULL with `=` always returns UNKNOWN, which means the row is filtered out. The row never comes back. The correct syntax is `WHERE email IS NULL`. We'll talk more about NULL on Slide 13.

Date comparisons: you pass dates as strings in ISO format — `'2024-01-01'`. The database knows it's comparing to a date column and handles the conversion.

**[Slide 7]**

Logical operators: AND, OR, NOT.

AND: both conditions must be true. `WHERE price < 100 AND stock_qty > 0` — product must be both cheap AND in stock.

OR: either condition can be true. `WHERE country = 'USA' OR country = 'Canada'` — customers from either country.

NOT: inverts. `WHERE NOT status = 'cancelled'` is equivalent to `WHERE status != 'cancelled'`. NOT is more commonly used in front of IN and BETWEEN: `WHERE status NOT IN ('cancelled', 'refunded')`.

The important rule with AND and OR together: use parentheses. AND binds tighter than OR, just like multiplication binds tighter than addition in math. So `WHERE a = 1 AND b = 2 OR c = 3` is evaluated as `WHERE (a = 1 AND b = 2) OR c = 3`. If that's what you meant, fine. But if you meant "a equals 1, and either b equals 2 or c equals 3," you need `WHERE a = 1 AND (b = 2 OR c = 3)`. My rule: whenever I mix AND and OR, I always add parentheses. It costs you nothing and prevents bugs.

---

### [29:00–40:00] Slides 8–9 — LIKE, IN, BETWEEN

**[Slide 8]**

LIKE for pattern matching. Two wildcards. `%` matches zero or more of any character. `_` matches exactly one character.

`LIKE 'Al%'` — starts with Al. Returns Alice, Albert, Alex. `LIKE '%phone%'` — contains "phone" anywhere. Matches iPhone, Headphone, Smartphone. `LIKE '%.com'` — ends with .com.

The `_` wildcard is less commonly used but occasionally handy. `LIKE 'A_ice'` matches Alice but not Alce — there must be exactly one character between A and ice.

Performance note that I want you to remember: when the wildcard is at the beginning — `LIKE '%search%'` or `LIKE '%search'` — the database cannot use an index. It has to read every single row and check the pattern. On a table with millions of rows, that's slow. When the wildcard is only at the end — `LIKE 'search%'` — the database can use a standard alphabetical index, which is fast. If you're implementing a search feature on a large table, this matters.

`NOT LIKE` excludes matches: `WHERE email NOT LIKE '%@gmail.com'` — everyone who didn't give a Gmail address.

**[Slide 9]**

IN and BETWEEN are shortcuts that make your SQL more readable.

IN is a cleaner way to write multiple OR conditions. Instead of `WHERE country = 'USA' OR country = 'Canada' OR country = 'Mexico'`, you write `WHERE country IN ('USA', 'Canada', 'Mexico')`. Same result, much easier to read, easier to add another country to the list. NOT IN excludes those values.

One thing to be careful about with NOT IN: if your list contains even one NULL, the result might not be what you expect. NULL propagates in strange ways through NOT IN. This is a known SQL gotcha — we'll mention it in the REVIEW file.

BETWEEN is a cleaner range check. `WHERE price BETWEEN 10 AND 50` is equivalent to `WHERE price >= 10 AND price <= 50`. Critical point: BETWEEN is inclusive on both ends. The values 10 and 50 themselves are included. Some developers assume it's exclusive because that's the convention in other programming languages — in SQL it's inclusive.

BETWEEN works great for dates: `WHERE order_date BETWEEN '2024-01-01' AND '2024-03-31'` gives you the first quarter of 2024, including both the first and last day.

---

### [40:00–50:00] Slides 10–12 — ORDER BY, LIMIT, DISTINCT

**[Slide 10]**

ORDER BY controls how results are sorted. Without ORDER BY, the database returns rows in whatever order is convenient — often insertion order, but not guaranteed. If your application displays a list of products and the order matters, you must explicitly sort.

`ORDER BY price ASC` — ascending, cheapest first. `ORDER BY price DESC` — descending, most expensive first. ASC is the default, so `ORDER BY price` and `ORDER BY price ASC` are equivalent.

Multiple column sort: `ORDER BY country ASC, last_name ASC`. This sorts first by country — all customers grouped by country. Within each country, sorted by last name. You can mix directions: `ORDER BY price DESC, name ASC` — highest price first, and for products at the same price, alphabetical by name.

You can sort by column aliases — `ORDER BY sale_price DESC` where sale_price is a calculated column in SELECT. ORDER BY is one of the few places where aliases work, because ORDER BY executes after SELECT.

NULL values: in most databases with ascending sort, NULLs appear last. With descending sort, they appear first. PostgreSQL lets you control this explicitly with `NULLS FIRST` or `NULLS LAST`.

**[Slide 11]**

LIMIT and OFFSET — pagination. This is something every web application needs.

LIMIT says "give me at most this many rows." `LIMIT 10` — return ten rows. OFFSET says "skip this many rows before starting." `LIMIT 10 OFFSET 10` — skip the first ten, return the next ten. That's page 2.

The formula: if your page size is 10, page 1 is `OFFSET 0`, page 2 is `OFFSET 10`, page 3 is `OFFSET 20`. Generally: `OFFSET = (page - 1) * pageSize`.

LIMIT with ORDER BY is almost always what you want. `SELECT name, price FROM products ORDER BY price DESC LIMIT 10` — the ten most expensive products. Without ORDER BY, the "top 10" is meaningless because you have no definition of what makes something top.

A performance note for when you're working on large systems: LIMIT/OFFSET starts to slow down as OFFSET gets large, because the database still has to count through all the rows you're skipping. For very large datasets — pagination page 10,000 — there are better approaches like keyset pagination. But for most applications LIMIT/OFFSET is perfectly fine.

**[Slide 12]**

DISTINCT removes duplicate rows from your result. `SELECT DISTINCT country FROM customers` — instead of getting one row per customer, you get one row per unique country.

When applied to multiple columns — `SELECT DISTINCT city, country` — it returns unique combinations. New York, USA and New York, Canada would both appear because the combination is different.

Two common use cases I reach for DISTINCT regularly: first, exploration — when I first look at a new database I'll run `SELECT DISTINCT status FROM orders` to see what statuses actually exist. Second, counting unique values — `SELECT COUNT(DISTINCT customer_id) FROM orders` — how many distinct customers placed at least one order?

The caution: if you're using DISTINCT to fix unexpected duplicates that appear when you join tables, DISTINCT might be masking a deeper problem. If your join is producing duplicate rows that you didn't intend, examine your join conditions first. DISTINCT should be used intentionally, not as a band-aid.

---

### [50:00–60:00] Slides 13–16 — NULL, Full Examples, Formatting, Summary

**[Slide 13]**

NULL. This deserves its own slide because it's one of the most common sources of SQL bugs for developers coming from other languages.

NULL doesn't mean zero. It doesn't mean empty string. NULL means "the value is absent" or "the value is unknown." An order with `discount_code = NULL` didn't have a discount code. A customer with `email = NULL` didn't provide an email.

SQL uses three-valued logic: TRUE, FALSE, and UNKNOWN. Any comparison with NULL returns UNKNOWN. `5 = NULL` is UNKNOWN. `NULL = NULL` is UNKNOWN. `NULL != NULL` is UNKNOWN. Because UNKNOWN is not TRUE, rows with UNKNOWN comparisons are filtered out by WHERE.

This is why `WHERE email = NULL` doesn't work — even for rows where email is NULL, the comparison returns UNKNOWN, not TRUE, and the row is discarded.

COALESCE is your friend for handling NULLs in output: `COALESCE(email, 'no-email@unknown.com')` returns the email if it has one, or the default string if email is NULL.

NULLIF is useful for avoiding division by zero: `total / NULLIF(count, 0)` — if count is zero, NULLIF returns NULL, and dividing by NULL gives NULL instead of a runtime error.

**[Slide 14]**

Three real-world examples putting everything together. I want you to read each query and be able to trace through it: what table, what filter conditions, in what order.

First: a product search. Show me products that are in stock, priced between $10 and $100, with "pro" in the name, cheapest first, max 20 results. Every clause has a clear purpose.

Second: customer pagination. US or UK customers with a confirmed email, alphabetical, page 2 of 25-per-page results. Note that `IN ('USA', 'UK')` replaces two OR conditions. The OFFSET of 25 means we're on page 2.

Third: orders report. Non-cancelled orders from Q1 2024 worth more than $200, most recent first. The BETWEEN handles the date range, the `!=` excludes cancelled status, and the numeric filter keeps only significant orders.

These are the kinds of queries you write when your backend API needs to fetch data. Your Spring Boot controller receives query parameters — page number, filter values, sort column — and constructs a SQL query like these. Understanding what the SQL is doing is essential for writing a correct application.

**[Slide 15]**

Brief note on formatting. SQL doesn't care about whitespace. One long line and a neatly formatted multi-line query are identical to the database. But you're not writing SQL just for the database — you're writing it for the next developer who has to read it, debug it, and change it.

The conventions I use and recommend: keywords uppercase, one major clause per line, indent continuation lines slightly. The before-and-after example on this slide shows the same query written both ways. The formatted version is immediately readable. The compressed version requires you to parse it carefully to even know which table is being joined.

In a production codebase, SQL queries are often stored as strings in Java. Formatted, multi-line SQL strings with clear indentation are much easier to maintain than concatenated single-line strings. Use text blocks in modern Java — triple-quoted strings — to preserve formatting naturally.

**[Slide 16]**

Let me leave you with the complete syntax reference. Every clause we've covered, in order, with the optional clauses. When you're writing a query and forget the syntax, this is the shape to remember: SELECT what, FROM where, WHERE condition, ORDER BY sort, LIMIT count, OFFSET skip.

And the operator cheat sheet — every filtering operator we covered in one place.

Part 2 this afternoon: aggregates, GROUP BY, HAVING, all the JOIN types, subqueries, and CASE statements. We'll be using the same schema, so the tables will feel familiar. Take a ten minute break and I'll see you back here.
