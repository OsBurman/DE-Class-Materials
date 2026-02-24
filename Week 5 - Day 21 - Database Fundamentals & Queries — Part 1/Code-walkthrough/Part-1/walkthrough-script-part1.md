# Day 21 — Part 1 Walkthrough Script
# Database Fundamentals & SELECT Queries — Morning Session

**File referenced:** `01-database-fundamentals-and-select.sql`  
**Total time:** ~90 minutes  
**Tool:** Run queries in psql, pgAdmin, DBeaver, or any SQL client connected to PostgreSQL

---

## Segment 1 — Welcome to Week 5: The Data Layer (8 min)

> "Good morning everyone! Welcome to Week 5. We've spent four weeks building frontend applications — Angular, React, JavaScript. Everything we've built so far has been stateless, or working with data that disappears when you close the browser."

> "This week we fix that. We're learning databases — specifically SQL and relational databases. By the end of today you'll be writing queries that pull real data from real tables."

> "Let me ask a quick question before we touch any code: what IS a database? Can someone give me a one-sentence definition?"

*(Take 1-2 answers)*

> "Good. Here's my definition: a database is an organised collection of structured data that you can query, update, and manage. The key word is 'organised'. It's not just files — it's data with structure, relationships, and rules."

> "The type of database we're using is called a Relational Database Management System — or RDBMS. The 'relational' part means data is stored in tables that can be related to each other."

> "Think of it like a set of connected spreadsheets. Your `students` table relates to your `enrollments` table — each enrollment record knows which student it belongs to. That relationship is what makes it powerful."

*Open `01-database-fundamentals-and-select.sql`, navigate to Section 1 comment block*

> "Let's read through the key terms together. Don't try to memorise all of this right now — they'll click as we use them."

> "Table: rows and columns. Row = one record. Column = one field. The most important thing: every table has a **Primary Key** — a column that uniquely identifies each row. No duplicates, never null."

> "A **Foreign Key** links one table to another. The `enrollments` table has a `student_id` column. That column references the `student_id` Primary Key in the `students` table. That's the relationship."

> "The big four RDBMS products: PostgreSQL, MySQL, Oracle, SQL Server. We're using PostgreSQL today. The SQL we write is mostly ANSI-standard — it'll work in MySQL with tiny changes."

---

## Segment 2 — Running the Setup (5 min)

*Navigate to the SETUP section*

> "Before we write any SELECT queries, we need data to query. I've written a setup script that creates four tables and loads them with realistic data."

> "Let's look at the schema quickly. We have an online learning platform with: students, instructors, courses, and enrollments. The enrollments table is what we call a bridge table — it connects students and courses in a many-to-many relationship. One student can enroll in many courses. One course can have many students. The enrollments table is how we record that."

> "Run everything from the top of the file down through the INSERT statements. Let me know when you see the INSERT messages."

*(Wait for students to run the setup)*

> "Now let's verify the data loaded correctly."

> "Run `SELECT * FROM students;`. You should see 12 rows. Run `SELECT * FROM courses;`. 14 rows. These are the tables we'll be querying all day."

---

## Segment 3 — SELECT Basics and DQL (10 min)

*Navigate to Section 2*

> "SQL is broken into sub-languages. There's DDL for creating tables, DML for inserting and updating data, and today we focus on **DQL — Data Query Language**. DQL is just SELECT. Reading data. That's it."

> "Here's the golden rule: SELECT queries NEVER change your data. They only read. You could run SELECT queries all day on a production database and nothing bad would happen. This is why learning SELECT first is safe — there's no risk."

*Point to the SELECT anatomy comment*

> "Read through this anatomy with me. SELECT what, FROM where, WHERE condition, ORDER BY sort, LIMIT count. The only required parts are SELECT and FROM. Everything else is optional."

*Run `SELECT * FROM students;`*

> "Star means 'all columns'. You'll see this in code reviews and tutorials, but in production you want to be more specific — selecting only the columns you need is faster because the database sends less data."

*Run the specific columns SELECT*

> "Much cleaner. Now look at this one — column aliases with AS. When I write `first_name AS "First Name"`, I'm telling the database what to call that column in the output. The double-quotes are needed when the alias has a space."

*Run the concat query*

> "This concatenates first and last name into a single column called `full_name`. The `||` operator joins strings in PostgreSQL. MySQL uses `CONCAT(first_name, ' ', last_name)` — I'll call out these differences as we go."

> "Question: what SQL sub-language is SELECT a part of, and what makes it different from INSERT or UPDATE?"

*(Expected: DQL — it only reads, never modifies)*

---

## Segment 4 — WHERE Clause and Filtering (10 min)

*Navigate to Section 3*

> "WHERE is how you filter rows. Without WHERE, you get every row in the table — that could be millions. WHERE is what makes queries useful."

*Run `WHERE country = 'USA'`*

> "Exact match uses a single equals sign. In SQL, assignment and comparison both use `=`. No double-equals like JavaScript."

*Run `WHERE price > 60.00`*

> "Greater than, less than, not equal — all standard comparison operators. `!=` and `<>` both mean 'not equal'; they're interchangeable in most databases."

*Navigate to NULL checks*

> "**Watch out:** NULLs. NULL means 'unknown' or 'no value'. And NULL is special — you CANNOT compare it with `=`. If you write `WHERE score = NULL`, the query returns zero rows. Every time. NULL is never equal to anything, including itself."

> "The correct way: `IS NULL` and `IS NOT NULL`. These are the only operators that work with NULL."

*Run `WHERE score IS NULL`*

> "This finds incomplete enrollments — students who are enrolled but haven't received a score yet. NULL is how we represent 'no score assigned.'"

> "Quick test: if I run `WHERE score != NULL`, how many rows will I get?"

*(Expected: zero — you must use IS NOT NULL)*

---

## Segment 5 — Logical Operators (AND, OR, NOT) (10 min)

*Navigate to Section 4*

> "Let's combine conditions. Three logical operators: AND, OR, NOT. Same logic as programming."

*Run the AND query*

> "Level is Advanced AND price > 80. Both conditions must be true. We get the most expensive advanced courses."

*Run the OR query*

> "Category is Frontend OR Database. Either condition being true is enough. We get both categories."

> "Now here's the most important thing I'll say about logical operators: **use parentheses whenever you mix AND and OR**."

*Navigate to the combining example with and without parentheses*

> "Look at this query: `WHERE is_active = TRUE AND (country = 'USA' OR country = 'Canada')`. What does this return? Active students from USA, plus active students from Canada. Makes sense."

> "Now imagine removing the parentheses: `WHERE is_active = TRUE AND country = 'USA' OR country = 'Canada'`. SQL evaluates AND before OR — just like multiplication before addition in math. So this becomes: `(is_active AND USA) OR Canada`. We'd get ALL Canadian students regardless of active status."

> "That's a bug. Not a syntax error — SQL will happily run it and return wrong results. Parentheses are your protection."

*Run both versions to demonstrate*

> "See the difference? With parentheses: 5 rows, all active. Without: includes inactive Canadians. Same data, different results."

---

## Segment 6 — LIKE, IN, and BETWEEN (12 min)

*Navigate to Section 5*

> "Three special operators that each solve a specific filtering problem."

**LIKE:**

> "LIKE does pattern matching. Two wildcards: `%` means 'zero or more of any character'. `_` means 'exactly one character'."

*Run `WHERE title LIKE 'Java%'`*

> "Starts with 'Java'. Java Fundamentals, Spring Boot with Java — anything beginning with Java."

*Run `WHERE title LIKE '%React%'`*

> "Percent on both sides: contains 'React' anywhere in the title."

*Run the email query*

> "Ends with '.com'. The percent before .com matches everything up to the dot."

> "**Watch out:** LIKE is case-sensitive in PostgreSQL by default. If you search `LIKE 'java%'` (lowercase j), you won't find 'Java Fundamentals'. Use `ILIKE` in PostgreSQL for case-insensitive matching. In MySQL, LIKE is case-insensitive by default."

**IN:**

*Run the IN query*

> "IN is the elegant alternative to chaining ORs. `WHERE category IN ('Frontend', 'AI/ML', 'Database')` is cleaner than three OR conditions. The result is identical."

> "NOT IN works the same way — everything NOT in the list. Try the NOT IN students query — find students from countries we didn't list."

**BETWEEN:**

*Run the price BETWEEN query*

> "BETWEEN is inclusive. `BETWEEN 40.00 AND 70.00` includes rows where price = 40 and price = 70. The equivalent is `price >= 40 AND price <= 70`."

*Run the date BETWEEN query*

> "BETWEEN works beautifully with dates. 'Students who joined in Q1 2024.' Very readable."

> "Question: can you use NOT BETWEEN? What would it return?"

*(Expected: yes — everything outside the range. Run the duration NOT BETWEEN query to demonstrate)*

---

## Segment 7 — ORDER BY and Sorting (8 min)

*Navigate to Section 6*

> "ORDER BY controls how rows are sorted in your results. Without ORDER BY, the database returns rows in an undefined order — whatever order it feels like. Don't assume order."

*Run ORDER BY price ASC*

> "ASC is ascending — lowest to highest. This is the default, so `ORDER BY price` and `ORDER BY price ASC` are identical. I recommend always writing ASC or DESC explicitly to make your intent obvious to anyone reading the query."

*Run ORDER BY price DESC*

> "DESC is descending — highest to lowest. Most expensive courses first."

*Run the multi-column ORDER BY*

> "Multiple columns: sort by category alphabetically, then within each category sort by price descending. This is like 'sort by the primary thing, then break ties with the secondary thing.'"

> "**Watch out:** NULL values in ORDER BY. PostgreSQL puts NULLs LAST when sorting ASC, FIRST when sorting DESC. MySQL and SQL Server behave slightly differently. If your data has NULLs in the sort column and order matters, test explicitly."

---

## Segment 8 — LIMIT and Pagination (8 min)

*Navigate to Section 7*

> "LIMIT answers the question: 'how many rows do I want?' It's essential for two things: safety and pagination."

*Run the top 5 most expensive courses*

> "Classic use case: 'show me the top N results.' Always pair LIMIT with ORDER BY — without ORDER BY, which 5 rows you get is arbitrary."

> "**Watch out:** never run `SELECT * FROM some_table` in production without a LIMIT. Some tables have millions of rows. `SELECT * FROM orders LIMIT 20` is the safe way to explore production data."

*Walk through the three pagination queries*

> "This is how every web app does pagination. You have a page size — let's say 3 courses per page. Page 1 gets rows 1-3: `LIMIT 3 OFFSET 0`. Page 2 gets rows 4-6: `LIMIT 3 OFFSET 3`. Page 3: `LIMIT 3 OFFSET 6`."

> "The formula: OFFSET = (page_number - 1) × page_size. If you're on page 4 with 10 per page: OFFSET = (4-1) × 10 = 30."

> "Your Spring Boot API will eventually use this to implement pageable REST endpoints. Bookmark this pattern."

---

## Segment 9 — DISTINCT Keyword (5 min)

*Navigate to Section 8*

> "DISTINCT removes duplicate rows from the result. Simple concept, very useful."

*Run `SELECT country FROM students ORDER BY country;` — show duplicates*

> "Multiple students per country — USA appears several times."

*Run `SELECT DISTINCT country FROM students ORDER BY country;`*

> "Now each country appears exactly once. DISTINCT gives us a unique list."

> "DISTINCT applies to the COMBINATION of columns when you select multiple. `SELECT DISTINCT country, is_active` gives you unique pairs — you might see USA/TRUE and USA/FALSE both appear."

> "One performance note: DISTINCT requires the database to sort and compare all rows to find duplicates — on large tables it can be slow. If you're doing `SELECT DISTINCT id` on a primary key, it's pointless — primary keys are already unique."

---

## Segment 10 — Putting It All Together (5 min)

*Navigate to Section 9*

> "Let's look at three realistic queries that combine everything we've learned."

*Run the Frontend courses query*

> "This is the kind of query you'd actually write in a real app: published Frontend courses under $70, sorted cheapest first. WHERE has two AND conditions plus a boolean check. ORDER BY at the end."

*Run the USA active students query*

> "Multiple conditions — country, active status, and a date range — sorted newest first, limited to 5. Think about where this query would come from: an admin dashboard showing 'recent US signups'."

> "Before I run the last one — what will `WHERE title LIKE '%Python%' OR title LIKE '%Machine%'` return?"

*(Expected: Machine Learning with Python — the only course matching either pattern)*

> "Right. One course. 'Machine Learning with Python' contains both 'Python' and 'Machine', so it matches either condition."

---

## Segment 11 — Part 1 Wrap-Up (5 min)

> "Let's review what we covered this morning:"

> "One: databases store structured data in tables. Primary keys identify rows, foreign keys link tables together. SQL is the language we use to talk to them."

> "Two: SELECT is the core of DQL. SELECT columns FROM table WHERE condition ORDER BY column LIMIT n."

> "Three: filter with WHERE using comparison operators, AND/OR/NOT with parentheses, NULL with IS NULL/IS NOT NULL, patterns with LIKE, lists with IN, ranges with BETWEEN."

> "Four: sort with ORDER BY ASC/DESC, control output size with LIMIT, paginate with LIMIT/OFFSET, deduplicate with DISTINCT."

> "After lunch we get into the real power of SQL: aggregating data with GROUP BY, joining multiple tables together, and writing subqueries. See you back here in an hour."

---

## Instructor Q&A Prompts

1. **"Why can't you write `WHERE score = NULL` to find null values?"**  
   *(Expected: NULL means unknown. NULL = NULL is unknown, not true. Only IS NULL and IS NOT NULL work with NULL.)*

2. **"What's the difference between `LIKE '%Java%'` and `LIKE 'Java%'`?"**  
   *(Expected: first finds 'Java' anywhere in the string. Second finds strings that START with 'Java'.)*

3. **"If I have 100 courses and run `LIMIT 10 OFFSET 90`, what will I get?"**  
   *(Expected: rows 91-100 — the last 10. OFFSET skips the first 90.)*

4. **"Can you ORDER BY a column you didn't SELECT? For example: `SELECT title FROM courses ORDER BY price`?"**  
   *(Expected: yes — you can order by any column in the table, not just the ones you selected)*
