# Day 21 — Part 2 Walkthrough Script
# Aggregates, JOINs, Subqueries & CASE — Afternoon Session

**Files referenced:**
- `01-aggregates-and-grouping.sql`
- `02-joins.sql`
- `03-subqueries-and-case.sql`

**Total time:** ~90 minutes  
**Sections:** Aggregates (30 min) → JOINs (35 min) → Subqueries & CASE (25 min)

---

## Segment 1 — Welcome Back: The Power of SQL (3 min)

> "Welcome back everyone. This morning we covered reading data from single tables — filtering, sorting, and limiting results. That's important, but it's only half the picture."

> "This afternoon we get into what makes SQL genuinely powerful. Three things: aggregate functions and grouping, joining multiple tables, and subqueries with conditional logic."

> "By the end of this afternoon, you'll be able to write the kind of queries you'd see in a real analytics dashboard or REST API. Let's go."

---

## BLOCK A — AGGREGATE FUNCTIONS & GROUP BY

*Open `01-aggregates-and-grouping.sql`*

---

## Segment 2 — Aggregate Functions: Collapsing Rows (8 min)

*Navigate to Section 1 comment block*

> "Aggregate functions take a column of many values and collapse them into a single result. Instead of seeing each row, you get a summary."

> "The five you need to know: COUNT, SUM, AVG, MIN, MAX. Let's run each one."

*Run `SELECT COUNT(*) AS total_students FROM students;`*

> "COUNT-star counts rows. Every row, including those with NULL values. 12 students."

*Navigate to the COUNT comparison — 2c*

> "Here's the critical distinction. `COUNT(*)` counts every row. `COUNT(score)` counts only rows where score is NOT NULL. The difference gives us `COUNT(*) - COUNT(score)` — the number of enrollments with no score yet."

> "Run that three-way COUNT. See the numbers? Some students are enrolled but haven't received a score. COUNT(column) automatically skips NULLs — it doesn't count unknown values."

*Run Section 3 — all five aggregates together*

> "Look at this one — all five aggregates on the courses table in one query. Total courses, average price, sum of all prices, cheapest, most expensive. This is what a dashboard query looks like."

> "The `ROUND()` function wraps AVG to give us 2 decimal places. Always round currency — `AVG(price)` without rounding can give you 8 decimal places."

---

## Segment 3 — GROUP BY: Aggregating by Category (10 min)

*Navigate to Section 4*

> "GROUP BY is where aggregate functions get useful. Without GROUP BY, you get one aggregate for the entire table. With GROUP BY, you get one aggregate PER GROUP."

> "Think of it as: 'for each unique value of X, calculate this aggregate.'"

*Run 4a — count courses per category*

> "One row per category. How many courses are in each? Frontend has the most, Database has the fewest. That's useful information."

*Run 4b — avg price per category*

> "Now we can compare: which categories are the most expensive on average? Multiple aggregates in one GROUP BY query."

*Navigate to 4e — GROUP BY multiple columns*

> "You can GROUP BY multiple columns. This groups by the COMBINATION of level AND category — you get one row per unique pair. So 'Advanced Frontend' is a separate group from 'Beginner Frontend'."

*Run 4e*

> "Read across one row: Advanced Backend has 2 courses averaging $XX. This is the kind of pivot data you'd pass to a bar chart."

> "Question: if I add a column to the SELECT that's NOT in the GROUP BY and NOT an aggregate, what happens?"

*(Expected: error — every SELECT column must be in GROUP BY or wrapped in an aggregate function)*

> "Exactly. SQL will throw an error. Every column in SELECT must either be in GROUP BY (it's a grouping key) or wrapped in an aggregate (COUNT, SUM, AVG, etc.). This is a strict rule."

---

## Segment 4 — HAVING: Filtering Groups (8 min)

*Navigate to Section 5*

> "We can filter rows with WHERE. But what if we want to filter GROUPS? Like, 'only show me categories that have more than 2 courses'?"

> "You can't use WHERE for this — the aggregate hasn't been calculated yet when WHERE runs. That's where HAVING comes in."

*Run 5a — categories with more than 2 courses*

> "HAVING filters groups AFTER aggregation. Think of it as WHERE for groups."

*Navigate to 5d — the full pipeline*

> "This is the complete SQL execution pipeline: WHERE first filters individual rows. Then GROUP BY groups those filtered rows. Then HAVING filters the groups. Then ORDER BY sorts the output."

> "Run this one. Active courses only, grouped by category, only categories with avg price over $60. Try to follow the logical flow step by step."

*Run 5d*

*Navigate to Section 6 — SQL Execution Order*

> "Let me show you something important. SQL clauses run in THIS order — not the order you write them."

*Point to the execution order list*

> "FROM, WHERE, GROUP BY, HAVING, SELECT, ORDER BY, LIMIT. This explains every 'strange' SQL behaviour you'll ever encounter. Can't use a WHERE on an aggregate? Because WHERE runs before GROUP BY. Can you ORDER BY an alias from SELECT? Yes — ORDER BY runs after SELECT."

*Run the failing vs working query*

> "The first query tries to use `discounted` in WHERE — that column doesn't exist yet when WHERE runs. The second repeats the expression in WHERE — correct. ORDER BY can use the alias because it runs last."

---

## BLOCK B — JOINS

*Open `02-joins.sql`*

---

## Segment 5 — Joins: Combining Tables (5 min)

*Navigate to Section 1 comment block with the schema diagram*

> "This is the biggest concept of the afternoon. JOINs let us combine data from multiple tables into a single result."

> "Look at our schema. Four tables, connected by foreign keys. On their own, each table is limited. JOINs unlock the relationships between them."

> "The central question for choosing a JOIN type: 'do I want to KEEP rows that have no match, or DROP them?' That's it. If you answer that question, you know which JOIN to use."

---

## Segment 6 — INNER JOIN (10 min)

*Navigate to Section 2*

> "INNER JOIN is the most common. It returns rows that have a match in BOTH tables. Unmatched rows from either side are dropped."

*Run 2a — students and their enrolled courses*

> "Three JOINs in one query. Students → enrollments → courses. Notice the aliases: `e` for enrollments, `s` for students, `c` for courses. The ON clause is the join condition — it tells SQL how the tables relate."

> "The notation `s.first_name` means 'the first_name column from the `s` table'. Required when multiple tables have columns with the same name."

*Run 2b — courses with instructor names*

> "Course catalog with instructor info. The result looks like a single table but it's pulling from two. This is what a GET /courses API endpoint would query in Spring Boot."

*Run 2d — count students per course with GROUP BY*

> "INNER JOIN + GROUP BY. Course names with enrollment counts. This is what an admin dashboard uses to show 'most popular courses.'"

---

## Segment 7 — LEFT JOIN (10 min)

*Navigate to Section 3*

> "LEFT JOIN keeps ALL rows from the LEFT table, even when there's no match in the right table. Unmatched right-side columns are filled with NULLs."

> "Classic use case: 'show me all courses, even the ones nobody has enrolled in.'"

*Run 3a — all courses including zero enrollments*

> "Notice the courses at the bottom of the list — enrollment_count is zero. INNER JOIN would have hidden those entirely. LEFT JOIN exposes them."

*Run 3b — courses with NO enrollments*

> "The pattern `WHERE right_table.id IS NULL` after a LEFT JOIN is a powerful trick. It finds rows in the left table that have NO match in the right table. We're using NULLs as a diagnostic tool."

*Run 3d — students who never enrolled*

> "Same pattern. LEFT JOIN students to enrollments, then WHERE enrollment side IS NULL. These are students who registered but never took a course. Business insight: target these people with a 're-engagement' email campaign."

> "Quick check: if I use INNER JOIN here instead of LEFT JOIN, what would I get?"

*(Expected: zero rows — INNER JOIN only returns matches, and we're looking for non-matches)*

---

## Segment 8 — RIGHT JOIN, FULL OUTER JOIN, CROSS JOIN (6 min)

*Navigate to Sections 4, 5, 6*

> "RIGHT JOIN is the mirror image of LEFT JOIN. All rows from the RIGHT table, NULLs on the left for no match. I'll be honest — most developers almost never use RIGHT JOIN. You can always rewrite it as a LEFT JOIN by swapping table order. I'm showing you it exists but you'll see LEFT JOIN in 95% of real code."

*Point to 4a and 4b — same result different JOIN*

> "These produce identical output. One uses RIGHT JOIN, one rewrites as LEFT JOIN with swapped table order. Pick whichever reads more naturally."

*Navigate to Section 5 — FULL OUTER JOIN*

> "FULL OUTER JOIN returns all rows from both sides, with NULLs wherever there's no match. Rare but useful when comparing two datasets for discrepancies."

> "One critical note: **MySQL does not support FULL OUTER JOIN**. The comment in the file shows the MySQL workaround using UNION."

*Navigate to Section 6 — CROSS JOIN*

> "CROSS JOIN is the cartesian product. Every row from table A paired with every row from table B. 12 students × 14 courses = 168 rows."

> "Use cases: generating all possible combinations — size/colour grids, scheduling matrices, test data. Be careful — a CROSS JOIN on two large tables is explosive. 1,000 rows × 1,000 rows = 1,000,000 rows."

---

## Segment 9 — Self Joins and Multi-Table Joins (4 min)

*Navigate to Sections 7 and 8*

> "A self join joins a table to itself. You need aliases to distinguish the two copies. Classic use: finding pairs of things that share a property."

*Run 7a — courses taught by the same instructor*

> "Two copies of the courses table. c1 and c2. The ON condition matches courses with the same instructor_id. The `c1.course_id < c2.course_id` condition prevents duplicates — without it, you'd get both (A,B) and (B,A)."

*Navigate to 8b — four-table join*

> "This is the money query: student name, course title, instructor name, score — from four tables in one query. The chain is: students → enrollments → courses → instructors. Each JOIN links the next table in the chain."

*Run 8b*

> "This is exactly the kind of query a `/api/student-report` endpoint would run."

---

## BLOCK C — SUBQUERIES & CASE

*Open `03-subqueries-and-case.sql`*

---

## Segment 10 — Subqueries (10 min)

*Navigate to Section 1 comment block*

> "A subquery is a SELECT statement inside another SELECT statement. The inner query runs first and its result is used by the outer query."

> "Three types: single-row (returns one value), multi-row (returns a list), and correlated (references the outer query)."

*Navigate to Section 2 — single-row subqueries*

*Run 2a — courses priced above average*

> "The subquery `SELECT AVG(price) FROM courses` runs first and returns a single number — say, 62.50. The outer query then runs: `WHERE price > 62.50`. The subquery dynamically calculates the threshold. If prices change, the query automatically adapts."

*Navigate to Section 3 — multi-row subqueries*

*Run 3a — students enrolled in at least one course*

> "The subquery returns a LIST of student IDs. The outer query keeps only students whose ID is IN that list. Same result as INNER JOIN — but subquery style."

*Run 3b — students NOT enrolled*

> "NOT IN is useful but has a trap. If the subquery result contains any NULLs, NOT IN returns zero rows. This catches beginners off guard. The fix: add `AND column IS NOT NULL` in the subquery, or use NOT EXISTS."

*Navigate to Section 4 — correlated subquery*

*Run 4a — scores above a student's own average*

> "Correlated subqueries are clever. For each row in the outer query, the inner query runs using THAT ROW's values. Here: for each enrollment, we calculate the average score just for THAT student. Then we keep only scores above their personal average."

> "The trade-off: correlated subqueries run once per outer row, which can be slow on large datasets. For performance-critical queries, a JOIN with aggregation is usually faster."

---

## Segment 11 — EXISTS and NOT EXISTS (5 min)

*Navigate to Section 5*

> "EXISTS is a boolean check. 'Does any row match this condition?' If the subquery returns at least one row, EXISTS is TRUE."

> "The key difference from IN: EXISTS doesn't care about the values returned — only whether rows exist. So you write `SELECT 1` inside EXISTS — the value is meaningless."

*Run 5b — NOT EXISTS to find students with no enrollments*

> "NOT EXISTS is the NULL-safe alternative to NOT IN. Even if the subquery produces NULLs, NOT EXISTS behaves correctly. Prefer NOT EXISTS over NOT IN whenever looking for the absence of related data."

*Run 5c — courses with a high-scoring student*

> "EXISTS + correlated condition. For each course, check if any enrollment for that course has score > 85. EXISTS short-circuits — it stops scanning as soon as one matching row is found."

---

## Segment 12 — CASE Expressions (8 min)

*Navigate to Section 6*

> "CASE is SQL's if-else. It can appear anywhere in a query — SELECT, WHERE, ORDER BY, GROUP BY, even inside aggregate functions."

> "Two forms: Searched CASE (general conditions) and Simple CASE (equality checks on one value)."

*Run 6a — price tier CASE*

> "Searched CASE: `WHEN price < 40 THEN 'Budget'`. Each WHEN is evaluated top to bottom. The first match wins. ELSE handles anything that doesn't match — omitting ELSE returns NULL."

*Run 6d — CASE inside SUM for pivot*

> "This is a classic pattern: CASE inside SUM to create a pivot table in SQL. SUM of 1s and 0s counts matching rows per category. One row per category, columns for each difficulty level. This is what reporting queries look like."

*Navigate to Section 7 — Calculated Columns*

*Run 7a — discounted price with NULLIF*

> "`NULLIF(duration_hours, 0)` — this returns NULL if duration is zero, preventing a division-by-zero error. The result column is NULL rather than an error. Always protect divisions with NULLIF."

*Run 7b — COALESCE*

> "`COALESCE` returns the first non-null argument. `COALESCE(score, 0)` gives us zero instead of NULL for missing scores. Perfect for displaying defaults in a UI."

---

## Segment 13 — Part 2 Wrap-Up (5 min)

> "Let's recap the afternoon:"

> "One: aggregate functions — COUNT, SUM, AVG, MIN, MAX — collapse rows into summary values. GROUP BY creates groups. HAVING filters those groups."

> "Two: JOINs combine data from multiple tables. INNER JOIN = only matches. LEFT JOIN = all left rows plus matches. Choose based on whether you need unmatched rows."

> "Three: subqueries embed one SELECT inside another. Single-row subqueries use comparison operators. Multi-row use IN. Correlated subqueries reference the outer row. EXISTS checks for existence."

> "Four: CASE is SQL's conditional logic. It can appear anywhere in a query. Used for classifications, grading, and pivot-style aggregation."

> "Tomorrow we build on this foundation with SQL schema design — DDL, normalization, constraints, views, and transactions. Great work today everyone."

---

## Instructor Q&A Prompts

1. **"What's the difference between WHERE and HAVING?"**  
   *(Expected: WHERE filters individual rows before grouping; HAVING filters groups after aggregation. You can't use aggregate functions in WHERE.)*

2. **"If a LEFT JOIN returns NULL for right-side columns, how can I find rows where no match exists?"**  
   *(Expected: add `WHERE right_table.id IS NULL` after the LEFT JOIN — the null signals no match was found)*

3. **"When would you use EXISTS instead of IN?"**  
   *(Expected: EXISTS is NULL-safe and short-circuits; IN is simple for list comparisons. Prefer NOT EXISTS over NOT IN when the subquery could contain NULLs)*

4. **"Can you write a GROUP BY without an aggregate function?"**  
   *(Expected: yes — GROUP BY without aggregates is equivalent to SELECT DISTINCT on those columns)*

5. **"What does CROSS JOIN return and when would you actually use it?"**  
   *(Expected: cartesian product — every row from A paired with every row from B. Used for generating all combinations: size/colour matrices, test data, scheduling slots)*
