-- =============================================================================
-- Day 21 — Part 2, File 3: Subqueries, CASE, and Calculated Columns
-- Database: PostgreSQL (compatible with MySQL with noted differences)
-- Schema: Online learning platform (students, instructors, courses, enrollments)
-- Run Part-1/01-database-fundamentals-and-select.sql SETUP section first
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: WHAT IS A SUBQUERY?
-- ─────────────────────────────────────────────────────────────────────────────

-- A subquery (inner query / nested query) is a SELECT statement embedded
-- inside another SQL statement.
--
-- Subqueries can appear in:
--   - WHERE clause   → filter rows based on dynamic values
--   - FROM clause    → treat a query result as a table (inline view)
--   - SELECT clause  → calculate a scalar value per row
--
-- Three key types:
--   1. Single-row subquery   → returns exactly ONE row, ONE column
--   2. Multi-row subquery    → returns multiple rows, ONE column
--   3. Correlated subquery   → references the outer query's current row

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: SINGLE-ROW SUBQUERIES
-- ─────────────────────────────────────────────────────────────────────────────

-- Used with comparison operators: =, <, >, <=, >=, !=
-- The subquery MUST return exactly one value.

-- 2a. Courses priced above the average price
SELECT title, category, price
FROM courses
WHERE price > (SELECT AVG(price) FROM courses)
ORDER BY price DESC;
-- The subquery (SELECT AVG(price) FROM courses) returns a single number.
-- The outer query uses that number as the filter threshold.

-- 2b. The most expensive course
SELECT title, category, price
FROM courses
WHERE price = (SELECT MAX(price) FROM courses);

-- 2c. Students who joined AFTER the average join date
SELECT
  first_name,
  last_name,
  join_date
FROM students
WHERE join_date > (SELECT AVG(join_date) FROM students)
ORDER BY join_date;
-- AVG on a date column calculates the average timestamp
-- Works in PostgreSQL; use different syntax in MySQL

-- 2d. Subquery in SELECT (scalar subquery) — one value per row
SELECT
  title,
  price,
  (SELECT ROUND(AVG(price), 2) FROM courses) AS platform_avg_price,
  ROUND(price - (SELECT AVG(price) FROM courses), 2) AS difference_from_avg
FROM courses
ORDER BY difference_from_avg DESC;
-- This subquery runs once and the same value appears in every row.
-- Useful for comparison columns.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: MULTI-ROW SUBQUERIES
-- ─────────────────────────────────────────────────────────────────────────────

-- Used with: IN, NOT IN, ANY, ALL
-- The subquery returns a column of values (a list).

-- 3a. Find students who are enrolled in at least one course
SELECT first_name, last_name, email
FROM students
WHERE student_id IN (
  SELECT DISTINCT student_id FROM enrollments
)
ORDER BY last_name;
-- The subquery returns a list of student_id values that appear in enrollments.
-- The outer query keeps only students whose ID is in that list.

-- 3b. Find students NOT enrolled in any course
SELECT first_name, last_name, email, join_date
FROM students
WHERE student_id NOT IN (
  SELECT DISTINCT student_id FROM enrollments
)
ORDER BY join_date;
-- WATCH OUT: NOT IN behaves unexpectedly when the subquery result contains NULLs.
-- If the subquery returns any NULL, NOT IN returns zero rows (due to NULL logic).
-- Safer alternative: use NOT EXISTS (shown in Section 5).

-- 3c. Find courses that have at least one enrollment above 90
SELECT title, category, level, price
FROM courses
WHERE course_id IN (
  SELECT DISTINCT course_id
  FROM enrollments
  WHERE score > 90
)
ORDER BY title;

-- 3d. Courses with NO scores above 90 — using NOT IN
SELECT title, category, price
FROM courses
WHERE course_id NOT IN (
  SELECT DISTINCT course_id
  FROM enrollments
  WHERE score > 90
    AND score IS NOT NULL     -- important: exclude NULLs from the subquery list
)
AND is_active = TRUE
ORDER BY title;

-- 3e. Subquery in FROM clause (inline view / derived table)
--     Treat the subquery result AS a table
SELECT
  student_summary.student_id,
  student_summary.total_courses,
  student_summary.avg_score
FROM (
  SELECT
    student_id,
    COUNT(*)            AS total_courses,
    ROUND(AVG(score), 2) AS avg_score
  FROM enrollments
  GROUP BY student_id
) AS student_summary
WHERE student_summary.avg_score > 80
ORDER BY student_summary.avg_score DESC;
-- The inner query (in parentheses) is the derived table "student_summary".
-- The outer query filters and selects from it like a regular table.

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 4: CORRELATED SUBQUERIES
-- ─────────────────────────────────────────────────────────────────────────────

-- A correlated subquery references a column from the OUTER query.
-- It runs ONCE PER ROW of the outer query — so it can be slow on large tables.
-- Use EXISTS (Section 5) or a JOIN where performance matters.

-- 4a. Find the enrollment score for each student that is ABOVE
--     that student's own average score (correlated on student_id)
SELECT
  e.student_id,
  e.course_id,
  e.score
FROM enrollments e
WHERE e.score > (
  SELECT AVG(e2.score)
  FROM enrollments e2
  WHERE e2.student_id = e.student_id   -- references the outer query's current row
)
ORDER BY e.student_id, e.score DESC;
-- For each row in the outer query (e), the subquery calculates that specific
-- student's average — then keeps only scores above their own average.

-- 4b. Courses priced above the average for their own category
SELECT
  c1.title,
  c1.category,
  c1.price
FROM courses c1
WHERE c1.price > (
  SELECT AVG(c2.price)
  FROM courses c2
  WHERE c2.category = c1.category    -- outer query's current row's category
)
ORDER BY c1.category, c1.price DESC;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 5: EXISTS AND NOT EXISTS
-- ─────────────────────────────────────────────────────────────────────────────

-- EXISTS returns TRUE if the subquery produces ANY rows.
-- NOT EXISTS returns TRUE if the subquery produces NO rows.
-- EXISTS does NOT care about the value — only whether rows exist.
-- Convention: write SELECT 1 (or SELECT *) inside EXISTS — the value is ignored.
--
-- EXISTS advantage: stops scanning as soon as the first match is found.
-- EXISTS is NULL-safe (unlike NOT IN — see Section 3b).

-- 5a. Students who have at least one enrollment (equivalent to IN example in 3a)
SELECT first_name, last_name, email
FROM students s
WHERE EXISTS (
  SELECT 1
  FROM enrollments e
  WHERE e.student_id = s.student_id
)
ORDER BY last_name;

-- 5b. Students with NO enrollments (safer than NOT IN — null-safe)
SELECT first_name, last_name, email, join_date
FROM students s
WHERE NOT EXISTS (
  SELECT 1
  FROM enrollments e
  WHERE e.student_id = s.student_id
)
ORDER BY join_date;

-- 5c. Courses that have at least one enrolled student with a score > 85
SELECT c.title, c.category, c.price
FROM courses c
WHERE EXISTS (
  SELECT 1
  FROM enrollments e
  WHERE e.course_id = c.course_id
    AND e.score > 85
)
ORDER BY c.category, c.title;

-- 5d. Instructors who have at least one active course
SELECT
  first_name || ' ' || last_name AS instructor_name,
  rating
FROM instructors i
WHERE EXISTS (
  SELECT 1
  FROM courses c
  WHERE c.instructor_id = i.instructor_id
    AND c.is_active = TRUE
)
ORDER BY rating DESC;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 6: CASE EXPRESSIONS
-- ─────────────────────────────────────────────────────────────────────────────

-- CASE is SQL's conditional expression — like if/else in programming.
-- Two forms: Simple CASE (equality checks) and Searched CASE (any condition).
-- CASE expressions can appear in SELECT, WHERE, ORDER BY, GROUP BY, and HAVING.

-- 6a. Searched CASE — general conditions (most common form)
SELECT
  title,
  price,
  CASE
    WHEN price < 40                THEN 'Budget'
    WHEN price >= 40 AND price < 70 THEN 'Mid-Range'
    WHEN price >= 70               THEN 'Premium'
    ELSE 'Unknown'
  END AS price_tier
FROM courses
ORDER BY price;
-- CASE evaluates conditions top to bottom and returns the first match.
-- ELSE is optional — if no condition matches and ELSE is omitted, returns NULL.

-- 6b. Simple CASE — equality checks on a single value
SELECT
  title,
  level,
  CASE level
    WHEN 'Beginner'     THEN '🟢 Beginner'
    WHEN 'Intermediate' THEN '🟡 Intermediate'
    WHEN 'Advanced'     THEN '🔴 Advanced'
    ELSE level
  END AS level_label
FROM courses
ORDER BY level;

-- 6c. CASE in WHERE for conditional filtering
SELECT title, price, level, category
FROM courses
WHERE
  CASE category
    WHEN 'Frontend' THEN price < 65
    WHEN 'Backend'  THEN price < 80
    ELSE price < 55
  END;

-- 6d. CASE with aggregate functions — pivot-style summary
SELECT
  category,
  COUNT(*) AS total,
  SUM(CASE WHEN level = 'Beginner'     THEN 1 ELSE 0 END) AS beginner_count,
  SUM(CASE WHEN level = 'Intermediate' THEN 1 ELSE 0 END) AS intermediate_count,
  SUM(CASE WHEN level = 'Advanced'     THEN 1 ELSE 0 END) AS advanced_count
FROM courses
GROUP BY category
ORDER BY category;
-- Classic SQL pivot pattern: CASE inside SUM or COUNT
-- This creates a cross-tabulation in a single query

-- 6e. CASE for grading
SELECT
  student_id,
  course_id,
  score,
  CASE
    WHEN score >= 90 THEN 'A'
    WHEN score >= 80 THEN 'B'
    WHEN score >= 70 THEN 'C'
    WHEN score >= 60 THEN 'D'
    WHEN score IS NOT NULL THEN 'F'
    ELSE 'Not Graded'
  END AS letter_grade
FROM enrollments
ORDER BY student_id, score DESC NULLS LAST;

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 7: CALCULATED COLUMNS AND EXPRESSIONS
-- ─────────────────────────────────────────────────────────────────────────────

-- SQL SELECT can contain any expression: arithmetic, string functions,
-- date functions, and conditional logic. These become virtual columns.

-- 7a. Arithmetic in SELECT
SELECT
  title,
  price,
  price * 0.9                           AS discounted_price,    -- 10% off
  ROUND(price * 0.9, 2)                 AS discounted_rounded,
  price - ROUND(price * 0.9, 2)         AS discount_amount,
  duration_hours,
  ROUND(price / NULLIF(duration_hours, 0), 2) AS price_per_hour
FROM courses
WHERE is_active = TRUE
ORDER BY price_per_hour;
-- NULLIF(a, b) returns NULL if a = b, otherwise returns a.
-- Protects against division by zero — if duration_hours = 0, returns NULL.

-- 7b. COALESCE — return the first non-null value
SELECT
  student_id,
  course_id,
  score,
  COALESCE(score, 0)        AS score_or_zero,
  COALESCE(score::text, 'Pending') AS score_display
FROM enrollments
ORDER BY student_id;
-- COALESCE(value1, value2, ...) returns the first non-null argument.
-- Useful for displaying a default when a value is NULL.
-- In MySQL: IFNULL(score, 0) is the two-argument equivalent.

-- 7c. String manipulation
SELECT
  student_id,
  UPPER(first_name)                          AS first_upper,
  LOWER(last_name)                           AS last_lower,
  INITCAP(first_name || ' ' || last_name)    AS full_name_proper,  -- Title Case
  LENGTH(email)                              AS email_length,
  SUBSTRING(email FROM 1 FOR POSITION('@' IN email) - 1) AS username
FROM students
ORDER BY student_id;
-- INITCAP: PostgreSQL-specific Title Case function.
-- SUBSTRING / POSITION: standard string extraction.

-- 7d. Date expressions
SELECT
  student_id,
  first_name,
  join_date,
  CURRENT_DATE                                    AS today,
  CURRENT_DATE - join_date                        AS days_since_joining,
  DATE_PART('year', AGE(join_date))               AS years_as_member
FROM students
ORDER BY join_date;
-- DATE_PART extracts a component: 'year', 'month', 'day', 'hour', etc.
-- AGE() calculates the interval between two dates
-- MySQL equivalent: DATEDIFF(CURDATE(), join_date) for day difference

-- 7e. Full composite query — student report card
SELECT
  s.first_name || ' ' || s.last_name  AS student_name,
  s.country,
  c.title                             AS course,
  c.level,
  e.score,
  CASE
    WHEN e.score >= 90 THEN 'A'
    WHEN e.score >= 80 THEN 'B'
    WHEN e.score >= 70 THEN 'C'
    WHEN e.score >= 60 THEN 'D'
    WHEN e.score IS NOT NULL THEN 'F'
    ELSE 'Not Graded'
  END                                 AS grade,
  COALESCE(e.score::text, '—')        AS score_display
FROM students s
INNER JOIN enrollments e ON s.student_id = e.student_id
INNER JOIN courses     c ON e.course_id  = c.course_id
ORDER BY student_name, c.title;
